package org.playframework.playclipse.wizards;

import org.eclipse.jface.viewers.ISelection;

public class ControllerWizardPage extends PlayWizardPage {

	protected String description() { return "Create a new controller for your Play project."; }
	protected String defaultName() { return "MyController"; }
	
	public ControllerWizardPage(ISelection selection) {
		super(selection);
	}

}
