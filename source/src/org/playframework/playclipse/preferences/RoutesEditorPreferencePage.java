package org.playframework.playclipse.preferences;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.playframework.playclipse.editors.route.RouteEditor;

public class RoutesEditorPreferencePage extends PlayEditorPreferencePage {

	public RoutesEditorPreferencePage() {
		super();
		setDescription("Play Routes Editor");
	}

	@Override
	public Map<String, String> getColorFields() {
		Map<String, String> fields = new LinkedHashMap<String, String>();
		fields.put(RouteEditor.KEYWORD_COLOR, "Keyword Color");
		fields.put(RouteEditor.URL_COLOR, "URL Color");
		fields.put(RouteEditor.ACTION_COLOR, "Action Color");
		fields.put(RouteEditor.COMMENT_COLOR, "Comment Color");
		fields.put(RouteEditor.DEFAULT_COLOR, "Default Color");
		return fields;
	}

	@Override
	public void createFieldEditors() {
		super.createFieldEditors();
		String[][] missingRouteKeyValues = {
				{"Ignore", "ignore"},
				{"Warning", "warning"},
				{"Error", "error"}
		};
		addField(new ComboFieldEditor(RouteEditor.MISSING_ROUTE, "When a route is missing", missingRouteKeyValues, getFieldEditorParent()));
		addField(new BooleanFieldEditor(RouteEditor.SOFT_TABS, "Use soft tabs", getFieldEditorParent()));
		addField(new IntegerFieldEditor(RouteEditor.SOFT_TABS_WIDTH, "Soft tabs length", getFieldEditorParent()));
	}

}
