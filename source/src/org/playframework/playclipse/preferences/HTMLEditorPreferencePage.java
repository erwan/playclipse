package org.playframework.playclipse.preferences;

import java.util.LinkedHashMap;
import java.util.Map;

import org.playframework.playclipse.editors.html.HTMLEditor;

public class HTMLEditorPreferencePage extends PlayEditorPreferencePage {

	public HTMLEditorPreferencePage() {
		super();
		setDescription("Play HTML Template Editor");
	}

	@Override
	public Map<String, String> getFields() {
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

}
