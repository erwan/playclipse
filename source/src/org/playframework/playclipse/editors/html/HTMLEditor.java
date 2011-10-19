package org.playframework.playclipse.editors.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.playframework.playclipse.PlayPlugin;
import org.playframework.playclipse.editors.PlayEditor;

public class HTMLEditor extends PlayEditor {

	public static final String DEFAULT_COLOR = "html_default_color";
	public static final String DOCTYPE_COLOR = "html_doctype_color";
	public static final String HTML_COLOR = "html_html_color";
	public static final String TAG_COLOR = "html_tag_color";
	public static final String EXPR_COLOR = "html_expr_color";
	public static final String ACTION_COLOR = "html_action_color";
	public static final String SKIPPED_COLOR = "html_skipped_color";
	public static final String KEYWORD_COLOR = "html_keyword_color";
	public static final String STRING_COLOR = "html_string_color";

	public static final String SOFT_TABS = "html_soft_tabs";
	public static final String SOFT_TABS_WIDTH = "html_soft_tabs_width";

	public static final String MISSING_ACTION = "html_missing_action";

	private ProjectionSupport projectionSupport;

	public HTMLEditor() {
		super();
		setSourceViewerConfiguration(new HTMLConfiguration(this));
		IPreferenceStore store = PlayPlugin.getDefault().getPreferenceStore();
		useSoftTabs = store.getBoolean(SOFT_TABS);
		softTabsWidth = store.getInt(SOFT_TABS_WIDTH);
	}

	public String[] getTypes() {
		return new String[] {"default", "doctype", "html", "string", "tag", "expression", "action", "skipped", "keyword"};
	}

	@Override
	public String getStylePref(String type) {
		if(type.equals("doctype")) {
			return DOCTYPE_COLOR;
		}
		if(type.equals("html")) {
			return HTML_COLOR;
		}
		if(type.equals("string")) {
			return STRING_COLOR;
		}
		if(type.equals("tag")) {
			return TAG_COLOR;
		}
		if(type.equals("expression")) {
			return EXPR_COLOR;
		}
		if(type.equals("action")) {
			return ACTION_COLOR;
		}
		if(type.equals("skipped")) {
			return SKIPPED_COLOR;
		}
		if(type.equals("keyword")) {
			return KEYWORD_COLOR;
		}
		return DEFAULT_COLOR;
	}

	// Auto-close

	public String autoClose(char pc, char c, char nc) {
		if(c == '<') {
			return ">";
		}
		if(c == '>' && nc == '>') {
			return SKIP;
		}
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
	};

	// Template

	public void templates(String contentType, String ctx) {
		if(contentType == "default" || contentType == "html" || contentType == "string") {
			template("$", "Insert dynamic expression", "$${${}}${cursor}");
			template("tag", "Insert tag without body", "#{${name} ${}/}${cursor}");
			template("action", "Insert action", "@{${}}${cursor}");
			template("tag", "Insert tag with body", "##{${name} ${}}${cursor}#{/${name}}");
		}
		if(contentType == "default") {
			template("if", "Insert a #if tag", "#{if ${}}\n    ${cursor}\n#{/if}");
			template("extends", "Insert a #extends tag", "#{extends '${}' /}${cursor}");
			template("list", "Insert a #list tag", "#{list ${}, as:'${i}'}\n    ${cursor}\n#{/list}");
			template("doctype", "Insert an HTML5 doctype element", "<!DOCTYPE html>");
		}
		// Magic
		Matcher isTag = Pattern.compile("<([a-zA-Z]+)>").matcher(ctx);
		if(isTag.matches()) {
			String closeTag = "</" + isTag.group(1) + ">";
			template(ctx, "Close the " + ctx + " HTML tag", "${cursor}"+closeTag);
		}
	}

	// Hyperlink

	Pattern extend_s = Pattern.compile("#\\{extends\\s+'([^']+)'");
	Pattern include = Pattern.compile("#\\{include\\s+'([^']+)'");
	Pattern action = Pattern.compile("@\\{([^}]+)\\}");
	Pattern action_in_tag = Pattern.compile("#\\{.+(@.+[)])");
	Pattern tag = Pattern.compile("#\\{([-a-zA-Z0-9.]+) ");

	public IHyperlink detectHyperlink(ITextViewer textViewer, IRegion region) {
		BestMatch match = findBestMatch(region.getOffset(), include, extend_s, action, action_in_tag, tag);
		if(match != null) {
			if(match.is(action)) {
				return match.hyperlink("action", 0, 0);
			}
			if(match.is(tag)) {
				return match.hyperlink("tag", 2, -1);
			}
			if(match.is(extend_s)) {
				return match.hyperlink("extends", match.matcher.start(1) - match.matcher.start(), -1);
			}
			if(match.is(include)) {
				return match.hyperlink("include", match.matcher.start(1) - match.matcher.start(), -1);
			}
			if(match.is(action_in_tag)) {
				return match.hyperlink("action_in_tag", match.matcher.start(1) - match.matcher.start(), 0);
			}	
		}
		return null;
	}

	// Scanner

	boolean consumeString = false;
	char openedString = ' ';
	String oldState = "default";
	String oldStringState = "default";

	@Override
	protected void reset() {
		super.reset();
		consumeString = false;
		oldState = "default";
	}

	@Override
	public String scan() {
		if(isNext("*{") && state != "skipped") {
			oldState = state;
			return found("skipped", 0);
		}
		if(state == "skipped") {
			if(isNext("}*")) {
				return found(oldState, 2);
			}
		}
		if(state == "default" || state == "html" || state == "string") {
			if(isNext("#{")) {
				oldState = state;
				return found("tag", 0);
			}
			if(isNext("${")) {
				oldState = state;
				return found("expression", 0);
			}
			if(isNext("@{") || isNext("@@{")) {
				oldState = state;
				return found("action", 0);
			}
		}
		if(state == "tag" || state == "expression" || state == "action") {
			if(isNext("}")) {
				return found(oldState, 1);
			}
		}
		if(state == "default") {
			if(isNext("<!DOCTYPE")) {
				return found("doctype", 0);
			}
			if(isNext("<")) {
				return found("html", 0);
			}
			if(isNext("var ")) {
				return found("keyword", 0);
			}
			if(isNext("def ")) {
				return found("keyword", 0);
			}
			if(isNext("return ")) {
				return found("keyword", 0);
			}
			if(isNext("function(")) {
				return found("keyword", 0);
			}
			if(isNext("function ")) {
				return found("keyword", 0);
			}
			if(isNext("if(")) {
				return found("keyword", 0);
			}
			if(isNext("if ")) {
				return found("keyword", 0);
			}
			if(isNext("else ")) {
				return found("keyword", 0);
			}
			if(isNext("switch(")) {
				return found("keyword", 0);
			}
			if(isNext("switch ")) {
				return found("keyword", 0);
			}
		}
		if(state == "keyword") {
			if(isNext(" ") || isNext("(")) {
				return found("default", 0);
			}
		}
		if(state == "doctype" || state == "html") {
			if(isNext(">")) {
				return found("default", 1);
			}
		}
		if(state == "html") {
			if(isNext("\"")) {
				openedString = '\"';
				consumeString = false;
				oldStringState = state;
				return found("string", 0);
			}
			if(isNext("'")) {
				openedString = '\'';
				consumeString = false;
				oldStringState = state;
				return found("string", 0);
			}
		}
		if(state == "string") {
			if(isNext(""+openedString) && consumeString) {
				return found(oldStringState, 1);
			}
			consumeString = true;
		}
		return null;
	}

	// Folding

	private Annotation[] oldAnnotations;
	private ProjectionAnnotationModel annotationModel;

	public void updateFoldingStructure(ArrayList<Position> positions) {
		Annotation[] annotations = new Annotation[positions.size()];

		//this will hold the new annotations along
		//with their corresponding positions
		HashMap<ProjectionAnnotation, Position> newAnnotations = new HashMap<ProjectionAnnotation, Position>();

		for (int i = 0; i < positions.size(); i++) {
			ProjectionAnnotation annotation = new ProjectionAnnotation();
			newAnnotations.put(annotation, positions.get(i));
			annotations[i] = annotation;
		}

		annotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);

		oldAnnotations = annotations;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
		projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
		projectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);
		annotationModel = viewer.getProjectionAnnotationModel();
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		getSourceViewerDecorationSupport(viewer);
		viewer.getTextWidget().addVerifyListener(this);
		return viewer;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String key = event.getProperty();
		if (key.equals(SOFT_TABS)) {
			useSoftTabs = ((Boolean)event.getNewValue()).booleanValue();
		}
		super.propertyChange(event);
	}

}
