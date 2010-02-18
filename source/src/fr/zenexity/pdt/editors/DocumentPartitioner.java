package fr.zenexity.pdt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;

public class DocumentPartitioner implements IDocumentPartitioner {

	ITypedRegion[] cachedRegions = null;
	ITypedRegion[] regions = null;
	Editor editor = null;

	public DocumentPartitioner(Editor editor) {
		this.editor = editor;
	}

	@Override
	public ITypedRegion getPartition(int offset) {
		for(ITypedRegion region : computePartitioning(offset, 0)) {
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

	public String getCachedContentType(int offset) {
		if (cachedRegions == null) return "default";
		for (ITypedRegion region: cachedRegions) {
			if(region.getOffset() + region.getLength() >= offset) {
				return region.getType();
			}
		}
		return "default";
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
			editor.reset(offset, length);
			while (!editor.eof) {
				rs.add(editor.nextToken());
			}
			regions = rs.toArray(new ITypedRegion[rs.size()]);
		}
		cachedRegions = regions;
		return regions;
	}

}
