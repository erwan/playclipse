package org.playframework.playclipse.editors.route;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class RouteEditorScanner extends RuleBasedScanner {
	private static final Color COLOR_METHOD;
	private static final Color COLOR_PATH;
	private static final Color COLOR_ACTION;
	private static final Color COLOR_COMMENT;
	private static final Color COLOR_OTHER;

	private static final String [] METHODS = {
		"GET", "POST", "DELETE", "PUT",
		"get", "post", "delete", "put"
	};

	static {
		Display d = PlatformUI.getWorkbench().getDisplay();
		COLOR_METHOD = new Color(d, 0, 0, 128);
		COLOR_PATH = new Color(d, 0, 128, 0);
		COLOR_ACTION = new Color(d, 128, 0, 0);
		COLOR_COMMENT = new Color(d, 128, 128, 128);
		COLOR_OTHER = new Color(d, 0, 0, 0);
	}

	public RouteEditorScanner() {
		Token method = new Token(new TextAttribute(COLOR_METHOD, null, SWT.BOLD));
		Token path = new Token(new TextAttribute(COLOR_PATH));
		Token action = new Token(new TextAttribute(COLOR_ACTION));
		Token comment = new Token(new TextAttribute(COLOR_COMMENT));
		Token other = new Token(new TextAttribute(COLOR_OTHER));

		WordRule rule = new WordRule(new IWordDetector() {
			@Override
			public boolean isWordPart(char c) {
				return true;
			}
			@Override
			public boolean isWordStart(char c) {
				return true;
			}
		});
		
		for (String k : METHODS) {
			rule.addWord(k, method);
		}

		IRule[] rules = {
			new SingleLineRule("#", null, comment)
		};
		setRules(rules);
	}
	
}
