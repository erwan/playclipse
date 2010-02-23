package org.playframework.playclipse.editors.route;

import org.eclipse.jface.text.rules.*;

import fr.zenexity.pdt.editors.ColorManager;
import fr.zenexity.pdt.editors.WhitespaceDetector;

public class Scanner extends RuleBasedScanner {

	public Scanner(ColorManager manager) {
		IRule[] rules = new IRule[1];
		rules[0] = new WhitespaceRule(new WhitespaceDetector());

		setRules(rules);
	}
}
