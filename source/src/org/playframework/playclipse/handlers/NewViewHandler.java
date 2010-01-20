package org.playframework.playclipse.handlers;

import org.playframework.playclipse.wizards.PlayWizard;
import org.playframework.playclipse.wizards.ViewWizard;

public class NewViewHandler extends WizardHandler {

	@Override
	protected PlayWizard getWizard() {
		return new ViewWizard();
	}

}
