package org.playframework.playclipse.handlers;

import org.playframework.playclipse.wizards.ModelWizard;
import org.playframework.playclipse.wizards.PlayWizard;

public class NewModelHandler extends WizardHandler {

	@Override
	protected PlayWizard getWizard() {
		return new ModelWizard();
	}

}
