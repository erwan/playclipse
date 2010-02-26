package org.playframework.playclipse.editors;

import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.playframework.playclipse.PlayPlugin;
import org.playframework.playclipse.preferences.PreferenceConstants;

public class RouteEditor extends PlayEditor {

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
	public TextAttribute getStyle(String type) {
		IPreferenceStore store = PlayPlugin.getDefault().getPreferenceStore();
		if(type.equals("keyword")) {
			return style(PreferenceConverter.getColor(store, PreferenceConstants.ROUTE_KEYWORD_COLOR));
		}
		if(type.equals("url")) {
			return style(PreferenceConverter.getColor(store, PreferenceConstants.ROUTE_URL_COLOR));
		}
		if(type.equals("comment")) {
			return style(PreferenceConverter.getColor(store, PreferenceConstants.ROUTE_COMMENT_COLOR));
		}
		if(type.equals("action")) {
			return style(PreferenceConverter.getColor(store, PreferenceConstants.ROUTE_ACTION_COLOR));
		}
		return style(PreferenceConverter.getColor(store, PreferenceConstants.ROUTE_DEFAULT_COLOR));
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
