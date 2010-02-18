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
					if (regions == null) computePartitioning(offset, 0);
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
					ITypedRegion current = getCachedPartition(offset);
					String state = (current != null) ? current.getType() : "default";
					try {
						editor.reset(offset, length, state);
					} catch (BadLocationException e) {
						// We trust Eclipse won't call us with bad offset/length
					}
					while (!editor.eof) {
						rs.add(editor.nextToken(offset));
					}
					regions = rs.toArray(new ITypedRegion[rs.size()]);
					return regions;
				}

				public ITypedRegion getCachedPartition(int offset) {
					if (oldRegions == null)
						return null;
					for(ITypedRegion region : oldRegions) {
						if(region.getOffset() + region.getLength() >= offset) {
							return region;
						}
					}
					return null;
				}

			};
			partitioner.connect(document);
		}
		return document;
	}

}