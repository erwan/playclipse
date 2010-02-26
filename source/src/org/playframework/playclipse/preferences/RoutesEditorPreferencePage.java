package org.playframework.playclipse.preferences;

import java.util.LinkedHashMap;
import java.util.Map;

import org.playframework.playclipse.editors.RouteEditor;

public class RoutesEditorPreferencePage extends PlayEditorPreferencePage {

	public RoutesEditorPreferencePage() {
		super();
		setDescription("Play Routes Editor");
	}

	@Override
	public Map<String, String> getFields() {
		Map<String, String> fields = new LinkedHashMap<String, String>();
		fields.put(RouteEditor.KEYWORD_COLOR, "Keyword Color");
		fields.put(RouteEditor.URL_COLOR, "URL Color");
		fields.put(RouteEditor.ACTION_COLOR, "Action Color");
		fields.put(RouteEditor.COMMENT_COLOR, "Comment Color");
		fields.put(RouteEditor.DEFAULT_COLOR, "Default Color");
		return fields;
	}

}
