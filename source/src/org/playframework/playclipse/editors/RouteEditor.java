package org.playframework.playclipse.editors;

import java.util.regex.Pattern;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.playframework.playclipse.ModelInspector;

public class RouteEditor extends PlayEditor {

	public static final String KEYWORD_COLOR = "route_keyword_color";
	public static final String URL_COLOR = "route_url_color";
	public static final String COMMENT_COLOR = "route_comment_color";
	public static final String ACTION_COLOR = "route_action_color";
	public static final String DEFAULT_COLOR = "route_default_color";

	String oldState = "default";
	IJavaProject javaProject;
	ModelInspector inspector;

	public RouteEditor() {
		super();
		setSourceViewerConfiguration(new RouteConfiguration(this));
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
			// System.out.println(begin + " " + begin2 + " " + end + " " + end2 + " " + len);
			BestMatch match = findBestMatch(end, action);
			if (match != null)
				// System.out.println("Let's see " + match.text());
			if (match != null && getInspector().resolveAction(match.text()) == null) {
				try {
					addError(match.offset, match.text().length(), "I don't know this route!");
				} catch (BadLocationException e) {
					// Should never happen
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
