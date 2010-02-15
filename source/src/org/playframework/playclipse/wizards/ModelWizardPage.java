package org.playframework.playclipse.wizards;


import org.eclipse.jface.viewers.ISelection;

public class ModelWizardPage extends ClassWizardPage {

	public ModelWizardPage(ISelection selection) {
		super(selection);
		setTitle("Play Model");
	}

	@Override
	protected String defaultName() { return "MyModel"; }

	@Override
	protected String description() { return "Create a new model for your Play project."; }

	@Override
	protected String defaultPackage() {
		return "models";
	}

	@Override
	protected String nameLabel() {
		return "&Model name:";
	}

}
