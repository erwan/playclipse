package org.playframework.playclipse.editors.route;

import org.eclipse.jface.text.rules.*;

public class PartitionScanner extends RuleBasedPartitionScanner {
	public final static String ROUTE_COMMENT = "__route_comment";
	public final static String ROUTE_METHOD = "__route_method";

	public PartitionScanner() {

		IToken routeComment = new Token(ROUTE_COMMENT);

		IPredicateRule[] rules = new IPredicateRule[1];

		rules[0] = new SingleLineRule("#", "\n", routeComment);

		setPredicateRules(rules);
	}
}
