package org.playframework.playclipse.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.graphics.RGB;

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
    public TextAttribute getStyle(String type) {
		if(type.equals("comment")) {
			return style(new RGB(90, 90, 90));
		}
		if(type.equals("key")) {
			return style(new RGB(150, 0, 0));
		}
		return style(new RGB(0, 0, 0));
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
