package org.playframework.playclipse.editors.html;

import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.playframework.playclipse.editors.Configuration;

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
