package org.playframework.playclipse.editors.route;

import org.eclipse.ui.editors.text.TextEditor;

import fr.zenexity.pdt.editors.ColorManager;

public class RouteEditor2 extends TextEditor {

	private ColorManager colorManager;

	public RouteEditor2() {
		super();
		setSourceViewerConfiguration(new XMLConfiguration(this));
		setDocumentProvider(new XMLDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
