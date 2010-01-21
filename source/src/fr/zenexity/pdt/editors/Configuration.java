package fr.zenexity.pdt.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


public class Configuration extends SourceViewerConfiguration {

	protected Editor editor;
	
	public Configuration(Editor editor) {
		this.editor = editor;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return editor.getTypes();
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler presentationReconciler = new PresentationReconciler();		
		for(String type: editor.getTypes()) {
			NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(editor.getStyle(type));
			presentationReconciler.setDamager(ndr, type);
			presentationReconciler.setRepairer(ndr, type);
		}
		return presentationReconciler;
	}
	
	@Override
	public int getTabWidth(ISourceViewer sourceViewer) {
		return 4;
	}
	
	
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] parent = super.getHyperlinkDetectors(sourceViewer);
		IHyperlinkDetector mine = new IHyperlinkDetector() {
			
			@Override
			public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
				IHyperlink link = editor.detectHyperlink(textViewer, region);
				if(link == null) {
					return new IHyperlink[0];
				}
				return new IHyperlink[] {link};
			}
		};
		IHyperlinkDetector[] result = new IHyperlinkDetector[parent.length + 1];
		System.arraycopy(parent, 0, result, 0, parent.length);
		result[result.length-1] = mine;
		return result;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		for(String type : editor.getTypes()) {
			CompletionProcessor processor = new CompletionProcessor(type, sourceViewer, editor);
			assistant.setContentAssistProcessor(processor, type);
		}
		return assistant;
	}

}