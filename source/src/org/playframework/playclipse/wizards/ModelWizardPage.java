package org.playframework.playclipse.wizards;


import org.eclipse.jface.viewers.ISelection;

public class ModelWizardPage extends PlayWizardPage {

	public ModelWizardPage(ISelection selection) {
		super(selection);
	}

	@Override
	protected String defaultName() { return "MyModel"; }

	@Override
	protected String description() { return "Create a new model for your Play project."; }

}
