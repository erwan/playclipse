package org.playframework.playclipse.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.graphics.RGB;

public class RouteEditor extends Editor {

	String oldState = "default";

    @Override
	public String[] getTypes() {
		return new String[] {
		        "default",
		        "keyword",
		        "url",
		        "action",
		        "comment"
		};
	}

    @Override
    public String autoClose(char pc, char c, char nc) {
        return null;
    }

    @Override
    public IHyperlink detectHyperlink(ITextViewer textViewer, IRegion region) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TextAttribute getStyle(String type) {
		if(type.equals("keyword")) {
			return style(new RGB(0, 200, 0));
		}
		if(type.equals("url")) {
			return style(new RGB(0, 0, 200));
		}
		if(type.equals("comment")) {
			return style(new RGB(90, 90, 90));
		}
		if(type.equals("action")) {
			return style(new RGB(200, 0, 0));
		}
		return style(new RGB(0, 0, 0));
    }

    @Override
    public void openLink(IHyperlink link) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String scan() {
        if (isNext("\n")) {
            return found("start", 1);
        }
        if (state != "comment" && isNext("#")) {
            return found("comment", 0);
        }
        if (state == "start" && isNext("GET")) {
            return found("keyword", 0);
        }
        if (state == "start" && isNext("POST")) {
            return found("keyword", 0);
        }
        if (state == "start" && isNext("PUT")) {
            return found("keyword", 0);
        }
        if (state == "start" && isNext("DELETE")) {
            return found("keyword", 0);
        }
        if (state == "start" && isNext("*")) {
            return found("keyword", 0);
        }
        if ((state == "keyword" || state == "url") && nextIsSpace()) {
            oldState = state;
            return found("default", 1);
        }
        if (state == "default" && isNext("/")) {
            return found("url", 0);
        }
        if (state == "default" && oldState == "url" && nextIsSpace()) {
            return found("action", 0);
        }
        return null;
    }

    @Override
    public void templates(String contentType, String ctx) {
        // TODO Auto-generated method stub
        
    }

}
