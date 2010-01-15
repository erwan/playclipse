package org.playframework.playclipse.editors;

import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.graphics.RGB;

public class RouteEditor extends Editor {

	String oldState = IDocument.DEFAULT_CONTENT_TYPE;

	@Override
	public String[] getTypes() {
		return new String[] {
				IDocument.DEFAULT_CONTENT_TYPE,
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

	Pattern action = Pattern.compile("\\s(\\w+\\.\\w+)");

	@Override
	public IHyperlink detectHyperlink(ITextViewer textViewer, IRegion region) {
		BestMatch match = findBestMatch(region.getOffset(), action);
		if(match != null) {
			if(match.is(action)) {
				return match.hyperlink("action", 0, 0);
			}
		}
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
	public String scan() {
		if (isNext("\n")) {
			return found(IDocument.DEFAULT_CONTENT_TYPE, 1);
		}
		if (state != "comment" && isNext("#")) {
			return found("comment", 0);
		}
		if (state == IDocument.DEFAULT_CONTENT_TYPE && isNext("GET")) {
			return found("keyword", 0);
		}
		if (state == IDocument.DEFAULT_CONTENT_TYPE && isNext("POST")) {
			return found("keyword", 0);
		}
		if (state == IDocument.DEFAULT_CONTENT_TYPE && isNext("PUT")) {
			return found("keyword", 0);
		}
		if (state == IDocument.DEFAULT_CONTENT_TYPE && isNext("DELETE")) {
			return found("keyword", 0);
		}
		if (state == IDocument.DEFAULT_CONTENT_TYPE && isNext("*")) {
			return found("keyword", 0);
		}
		if ((state == "keyword" || state == "url") && nextIsSpace()) {
			oldState = state;
			return found(IDocument.DEFAULT_CONTENT_TYPE, 0);
		}
		if (state == IDocument.DEFAULT_CONTENT_TYPE && isNext("/")) {
			return found("url", 0);
		}
		if (state == IDocument.DEFAULT_CONTENT_TYPE && oldState == "url" && !nextIsSpace()) {
			return found("action", 0);
		}
		return null;
	}

	@Override
	public void templates(String contentType, String ctx) {
		// TODO Auto-generated method stub

	}

}
