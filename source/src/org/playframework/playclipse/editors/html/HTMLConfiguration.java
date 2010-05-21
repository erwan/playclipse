package org.playframework.playclipse.editors.html;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.playframework.playclipse.editors.ActionCompletionProcessor;

import fr.zenexity.pdt.editors.CompletionProcessor;
import fr.zenexity.pdt.editors.Configuration;
import fr.zenexity.pdt.editors.XMLReconcilingStrategy;

public class HTMLConfiguration extends Configuration {

	public HTMLConfiguration(HTMLEditor editor) {
		super(editor);
	}

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		XMLReconcilingStrategy strategy = new XMLReconcilingStrategy();
		strategy.setEditor((HTMLEditor)editor);
		MonoReconciler reconciler = new MonoReconciler(strategy,false);
		return reconciler;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		for(String type : editor.getTypes()) {
			if (!type.equals("action")) {
				CompletionProcessor processor = new CompletionProcessor(type, sourceViewer, editor);
				assistant.setContentAssistProcessor(processor, type);
			}
		}
		ActionCompletionProcessor processor = new ActionCompletionProcessor(sourceViewer, editor);
		assistant.setContentAssistProcessor(processor, "action");
		return assistant;
	}

}
