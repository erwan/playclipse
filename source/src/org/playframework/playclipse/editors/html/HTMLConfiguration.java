package org.playframework.playclipse.editors.html;

import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

import fr.zenexity.pdt.editors.Configuration;
import fr.zenexity.pdt.editors.XMLReconcilingStrategy;

public class HTMLConfiguration extends Configuration {

	public HTMLConfiguration(HTMLEditor editor) {
		super(editor);
	}

	public IReconciler getReconciler(ISourceViewer sourceViewer)
	{
		XMLReconcilingStrategy strategy = new XMLReconcilingStrategy();
		strategy.setEditor((HTMLEditor)editor);
		MonoReconciler reconciler = new MonoReconciler(strategy,false);
		return reconciler;
	}
}
