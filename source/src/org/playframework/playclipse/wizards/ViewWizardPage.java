package org.playframework.playclipse.wizards;

import org.eclipse.jface.viewers.ISelection;

public class ViewWizardPage extends PlayWizardPage {

	public ViewWizardPage(ISelection selection) {
		super(selection);
		setTitle("Play View");
	}

	@Override
	protected String description() { return "Create a new view for your Play project."; }

	@Override
	protected String defaultName() { return "myview.html"; }

}
