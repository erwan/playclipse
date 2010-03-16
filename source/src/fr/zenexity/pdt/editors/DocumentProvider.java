package fr.zenexity.pdt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;
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
			IDocumentPartitioner partitioner = new IDocumentPartitioner() {

				List<ITypedRegion> regions;

				@Override
				public ITypedRegion getPartition(int offset) {
					if (regions == null) {
						computePartitioning(0, 0);
					}
					for (ITypedRegion region: regions) {
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
					computePartitioning(0, 0);
					return true;
				}

				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
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
					System.out.println("computePartitioning " + offset + " - " + length);
					List<ITypedRegion> inner = new ArrayList<ITypedRegion>();
					regions = new ArrayList<ITypedRegion>();
					editor.reset();
					while (!editor.eof) {
						ITypedRegion current = editor.nextToken();
						regions.add(current);
						int start = current.getOffset();
						int stop = current.getOffset() + current.getLength();
						if (start >= offset && stop <= offset + length) {
							// Region included in the zone
							inner.add(current);
						} else if (start < offset && stop >= offset) {
							// Overlap on the beginning of the zone
							inner.add(new TypedRegion(offset, (stop - offset), current.getType()));
						} else if (start <= offset && stop > offset + length) {
							// Overlap on the end of the zone
							inner.add(new TypedRegion(start, (offset + length - start), current.getType()));
						}
					}
					return inner.toArray(new ITypedRegion[inner.size()]);
				}

			};
			partitioner.connect(document);
		}
		return document;
	}

}