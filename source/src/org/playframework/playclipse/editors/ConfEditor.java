package org.playframework.playclipse.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.playframework.playclipse.preferences.PreferenceConstants;

public class ConfEditor extends PlayEditor {

	@Override
	public String autoClose(char pc, char c, char nc) {
		if(c == '{') {
			return "}";
		}
		if(c == '}' && nc == '}') {
			return SKIP;
		}
		if(c == '(') {
			return ")";
		}
		if(c == ')' && nc == ')') {
			return SKIP;
		}
		if(c == '[') {
			return "]";
		}
		if(c == ']' && nc == ']') {
			return SKIP;
		}
		if(c == '\'') {
			if(nc == '\'') {
				return SKIP;
			}
			return "\'";
		}
		if(c == '\"') {
			if(nc == '\"') {
				return SKIP;
			}
			return "\"";
		}
		return null;
	}

	@Override
	public IHyperlink detectHyperlink(ITextViewer textViewer, IRegion region) {
		return null;
	}

	@Override
	public String getStylePref(String type) {
		if(type.equals("comment")) {
			return PreferenceConstants.CONF_COMMENT_COLOR;
		}
		if(type.equals("key")) {
			return PreferenceConstants.CONF_KEY_COLOR;
		}
		return PreferenceConstants.HTML_DEFAULT_COLOR;
	}

	@Override
	public String[] getTypes() {
		return new String[] {
				"default",
				"key",
				"comment"
		};
	}

    @Override
    public String scan() {
        if (isNext("\n")) {
            return found("key", 1);
        }
        if (state != "comment" && isNext("#")) {
            return found("comment", 0);
        }
        if (state == "key" && isNext("=")) {
            return found("default", 0);
        }
        return null;
    }

    @Override
    public void templates(String contentType, String ctx) {
        // TODO Auto-generated method stub

    }

}
