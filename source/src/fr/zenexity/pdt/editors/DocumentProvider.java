package fr.zenexity.pdt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.editors.text.FileDocumentProvider;


public class DocumentProvider extends FileDocumentProvider {

	Editor editor;
	IDocument document;

	public DocumentProvider(Editor editor) {
		this.editor = editor;
	}

	@Override
	public String getDefaultEncoding() {
		return "utf-8";
	}

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
			new IDocumentPartitioner() {

				ITypedRegion[] oldRegions = null;
				ITypedRegion[] regions = null;

				@Override
				public ITypedRegion getPartition(int offset) {
					// for(ITypedRegion region : computePartitioning(offset, 0)) {
					if (regions == null) computePartitioning(0, document.getLength());
					for(ITypedRegion region : regions) {
						if(region.getOffset() + region.getLength() >= offset) {
							return region;
						}
					}
					return null;
				}

				@Override
				public String[] getLegalContentTypes() {
					return editor.getTypes();
				}

				@Override
				public String getContentType(int offset) {
					return getPartition(offset).getType();
				}

				@Override
				public boolean documentChanged(DocumentEvent event) {
					System.out.println("documentChanged");
					regions = null;
					return true;
				}

				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
					System.out.println("documentAboutToBeChanged");
					oldRegions = regions;
				}

				@Override
				public void disconnect() {
				}

				@Override
				public void connect(IDocument document) {
					document.addPositionCategory(IDocument.DEFAULT_CATEGORY);
					document.setDocumentPartitioner(this);
				}

				@Override
				public ITypedRegion[] computePartitioning(int offset, int length) {
					System.out.println("COMPUTEPART " + offset + " " + length);
					List<ITypedRegion> rs = new ArrayList<ITypedRegion>();

					// Insert any region before the zone we recalculate
					if (regions != null)
						for (int i = 0; i < regions.length; i++) {
							if (regions[i].getOffset() < offset)
								rs.add(regions[i]);
						}

					// Calculate the regions for the requested zone
					int current = getPartitionIndex(offset);
					String state = "default";
					String lastState = "default";
					if (current > -1) {
						state = oldRegions[current].getType();
						if (current > 0) {
							lastState = oldRegions[current - 1].getType();
						}
					}
					System.out.println("It appears that state at " + offset + " is " + state + " last " + lastState);
					try {
						editor.reset(offset, length, state, lastState);
					} catch (BadLocationException e) {
						// We trust Eclipse won't call us with bad offset/length
					}
					while (!editor.eof) {
						rs.add(editor.nextToken(offset));
					}

					// Insert any region after the zone we recalculate
					if (regions != null)
						for (int i = 0; i < regions.length; i++) {
							if (regions[i].getOffset() > (offset + length))
								rs.add(regions[i]);
						}

					regions = rs.toArray(new ITypedRegion[rs.size()]);
					return regions;
				}

				public int getPartitionIndex(int offset) {
					if (oldRegions == null)
						return -1;
					for (int i = 0; i < oldRegions.length; i++) {
						ITypedRegion region = oldRegions[i];
						if(region.getOffset() + region.getLength() >= offset) {
							return i;
						}
					}
					return -1;
				}

			};
			partitioner.connect(document);
		}
		return document;
	}

}