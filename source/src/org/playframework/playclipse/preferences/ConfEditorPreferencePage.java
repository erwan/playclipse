package org.playframework.playclipse.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.playframework.playclipse.PlayPlugin;

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

public class ConfEditorPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public ConfEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(PlayPlugin.getDefault().getPreferenceStore());
		setDescription("Play Configuration Editor");
	}
	
	public void createFieldEditors() {
		addField(new ColorFieldEditor(PreferenceConstants.CONF_DEFAULT_COLOR, "Default Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.CONF_KEY_COLOR, "Key Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.CONF_COMMENT_COLOR, "Comment Color", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}
	
}