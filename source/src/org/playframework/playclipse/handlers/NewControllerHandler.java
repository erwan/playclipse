package org.playframework.playclipse.handlers;

import org.playframework.playclipse.wizards.ControllerWizard;
import org.playframework.playclipse.wizards.PlayWizard;

public class NewControllerHandler extends WizardHandler {

	@Override
	protected PlayWizard getWizard() {
		return new ControllerWizard();
	}

}
