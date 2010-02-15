package org.playframework.playclipse.wizards;

import org.eclipse.jface.viewers.ISelection;

public class ControllerWizardPage extends ClassWizardPage {

	public ControllerWizardPage(ISelection selection) {
		super(selection);
		setTitle("Play Controller");
	}

	@Override
	protected String description() { return "Create a new controller for your Play project."; }

	@Override
	protected String defaultName() { return "MyController"; }

	@Override
	protected String defaultPackage() {
		return "controllers";
	}

	@Override
	protected String nameLabel() {
		return "&Controller name:";
	}

}
