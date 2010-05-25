package org.playframework.playclipse.preferences;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.playframework.playclipse.editors.html.HTMLEditor;

public class HTMLEditorPreferencePage extends PlayEditorPreferencePage {

	public HTMLEditorPreferencePage() {
		super();
		setDescription("Play HTML Template Editor");
	}

	@Override
	public Map<String, String> getColorFields() {
		Map<String, String> fields = new LinkedHashMap<String, String>();
		fields.put(HTMLEditor.DEFAULT_COLOR, "Default Color");
		fields.put(HTMLEditor.STRING_COLOR, "String Color");
		fields.put(HTMLEditor.ACTION_COLOR, "Action Color");
		fields.put(HTMLEditor.DOCTYPE_COLOR, "Doctype Color");
		fields.put(HTMLEditor.EXPR_COLOR, "Expr Color");
		fields.put(HTMLEditor.HTML_COLOR, "HTML Color");
		fields.put(HTMLEditor.KEYWORD_COLOR, "Keyword Color");
		fields.put(HTMLEditor.SKIPPED_COLOR, "Skipped Color");
		fields.put(HTMLEditor.TAG_COLOR, "Tag Color");
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
		addField(new ComboFieldEditor(HTMLEditor.MISSING_ACTION, "When an action is missing", missingRouteKeyValues, getFieldEditorParent()));
		addField(new BooleanFieldEditor(HTMLEditor.SOFT_TABS, "Indent with spaces (soft tabs)", getFieldEditorParent()));
		addField(new IntegerFieldEditor(HTMLEditor.SOFT_TABS_WIDTH, "Soft tabs length", getFieldEditorParent()));
	}

}
