package fr.zenexity.pdt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
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
				
				ITypedRegion[] regions = null;
				
				@Override
				public ITypedRegion getPartition(int offset) {
					computePartitioning(offset, 0);
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
					regions = null;
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
					if(regions == null || true) {
						List<ITypedRegion> rs = new ArrayList<ITypedRegion>();
						editor.reset();
						while (!editor.eof) {
							rs.add(editor.nextToken());
						}
						regions = rs.toArray(new ITypedRegion[rs.size()]);
					}
					List<ITypedRegion> innerRegions = new ArrayList<ITypedRegion>();
					for (int i = 0; i < regions.length; i++) {
						int start = regions[i].getOffset();
						int stop = regions[i].getOffset() + regions[i].getLength();
						if ((start >= offset && start <= offset + length) ||
							(stop >= offset && stop <= offset + length)) {
							innerRegions.add(regions[i]);
						}
					}
					return innerRegions.toArray(new ITypedRegion[innerRegions.size()]);
				}
				
			};
			partitioner.connect(document);
		}
		return document;
	}

}