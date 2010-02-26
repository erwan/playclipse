package org.playframework.playclipse.preferences;

import java.util.LinkedHashMap;
import java.util.Map;

import org.playframework.playclipse.editors.ConfEditor;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class ConfEditorPreferencePage extends PlayEditorPreferencePage {

	public ConfEditorPreferencePage() {
		super();
		setDescription("Play Configuration Editor");
	}

	@Override
	public Map<String, String> getFields() {
		Map<String, String> fields = new LinkedHashMap<String, String>();
		fields.put(ConfEditor.KEY_COLOR, "Key Color");
		fields.put(ConfEditor.DEFAULT_COLOR, "Default Color");
		fields.put(ConfEditor.COMMENT_COLOR, "Comment Color");
		return fields;
	}

}