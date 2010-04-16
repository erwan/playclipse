package org.playframework.playclipse.editors.route;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.playframework.playclipse.ModelInspector;
import org.playframework.playclipse.PlayPlugin;
import org.playframework.playclipse.editors.PlayEditor;

public class RouteEditor extends PlayEditor {

	public static final String KEYWORD_COLOR = "route_keyword_color";
	public static final String URL_COLOR = "route_url_color";
	public static final String COMMENT_COLOR = "route_comment_color";
	public static final String ACTION_COLOR = "route_action_color";
	public static final String DEFAULT_COLOR = "route_default_color";

	/**
	 * Can be: "error", "warning" or "ignore"
	 */
	public static final String MISSING_ROUTE = "route_missing_route";
	public static final String SOFT_TABS = "route_soft_tabs";
	public static final String SOFT_TABS_WIDTH = "route_soft_tabs_width";

	String oldState = "default";
	IJavaProject javaProject;
	ModelInspector inspector;

	public RouteEditor() {
		super();
		setSourceViewerConfiguration(new RouteConfiguration(this));
		IPreferenceStore store = PlayPlugin.getDefault().getPreferenceStore();
		useSoftTabs = store.getBoolean(SOFT_TABS);
		softTabsWidth = store.getInt(SOFT_TABS_WIDTH);
	}

	private IJavaProject getJavaProject() {
		if (javaProject == null) javaProject = JavaCore.create(getProject());
		return javaProject;
	}

	private ModelInspector getInspector() {
		if (inspector == null) inspector = new ModelInspector(getJavaProject());
		return inspector;
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
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		String key = event.getProperty();
		System.out.println("handlePrefChanged: " + key);
		if (key.equals(SOFT_TABS)) {
			System.out.println("soft tabs!!");
			useSoftTabs = ((Boolean)event.getNewValue()).booleanValue();
		}

		super.handlePreferenceStoreChanged(event);
	}

	private void addMarker(int start, int end, String text) throws BadLocationException {
		String severityStr = PlayPlugin.getDefault().getPreferenceStore().getString(MISSING_ROUTE);
		int severity = 0;
		if (severityStr.equals("warning")) severity = IMarker.SEVERITY_WARNING;
		else if (severityStr.equals("error")) severity = IMarker.SEVERITY_ERROR;
		else return; // Ignore
		for (int i = 0; i < pendingMarkers.size(); i++) {
			if (((Integer)pendingMarkers.get(i).get(IMarker.CHAR_START)) == start) {
				pendingMarkers.set(i, getMarkerParameters(start, end, text, severity));
				return;
			}
		}
		pendingMarkers.add(getMarkerParameters(start, end, text, severity));
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
			BestMatch match = null;
			try {
				match = findBestMatch(end, action);
			} catch(StringIndexOutOfBoundsException ex) {
				// Happen when the user is typing, e.g. the action is just one character
			}
			if (match != null &&
				content.charAt(match.offset + match.matcher.end()) != ':' &&
				getInspector().resolveAction(match.text()) == null) {
				try {
					addMarker(match.offset + match.matcher.start() + 1,
							match.offset + match.matcher.end(),
							"Missing route: " + match.text());
				} catch (Exception e) {
					// Should never happen
					e.printStackTrace();
				}
			}
			return found("action", 0);
		}
		return null;
	}

	@Override
	public void templates(String contentType, String ctx) {
	}

}
