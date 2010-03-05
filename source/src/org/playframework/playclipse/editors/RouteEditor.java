package org.playframework.playclipse.editors;

import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class RouteEditor extends PlayEditor {

	public static final String KEYWORD_COLOR = "route_keyword_color";
	public static final String URL_COLOR = "route_url_color";
	public static final String COMMENT_COLOR = "route_comment_color";
	public static final String ACTION_COLOR = "route_action_color";
	public static final String DEFAULT_COLOR = "route_default_color";

	String oldState = "default";

	public RouteEditor() {
		super();
		setSourceViewerConfiguration(new RouteConfiguration(this));
	}

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

	Pattern action = Pattern.compile("\\s(\\w[\\.\\w]+)");

	@Override
	public IHyperlink detectHyperlink(ITextViewer textViewer, IRegion region) {
		BestMatch match = findBestMatch(region.getOffset(), action);
		if(match != null) {
			if(match.is(action)) {
				return match.hyperlink("action", 1, 0);
			}
		}
		return null;
	}

	@Override
	public String getStylePref(String type) {
		if(type.equals("keyword")) {
			return KEYWORD_COLOR;
		}
		if(type.equals("url")) {
			return URL_COLOR;
		}
		if(type.equals("comment")) {
			return COMMENT_COLOR;
		}
		if(type.equals("action")) {
			return ACTION_COLOR;
		}
		return DEFAULT_COLOR;
	}

	@Override
	public String scan() {
		if (isNext("\n")) {
			return found("default", 1);
		}
		if (state != "comment" && isNext("#")) {
			return found("comment", 0);
		}
		if (state == "default" && isNext("GET")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("POST")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("PUT")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("DELETE")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("OPTIONS")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("HEAD")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("*")) {
			return found("keyword", 0);
		}
		if ((state == "keyword" || state == "url") && nextIsSpace()) {
			oldState = state;
			return found("default", 0);
		}
		if (state == "default" && isNext("/")) {
			return found("url", 0);
		}
		if (state == "default" && oldState == "url" && !nextIsSpace()) {
			return found("action", 0);
		}
		return null;
	}

	@Override
	public void templates(String contentType, String ctx) {
	}

}
