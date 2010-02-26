package org.playframework.playclipse.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
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

public class RoutesEditorPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public RoutesEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(PlayPlugin.getDefault().getPreferenceStore());
		setDescription("Play Route Editor");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new ColorFieldEditor(PreferenceConstants.ROUTE_KEYWORD_COLOR, "Keyword Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.ROUTE_URL_COLOR, "URL Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.ROUTE_ACTION_COLOR, "Action Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.ROUTE_COMMENT_COLOR, "Comment Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.ROUTE_DEFAULT_COLOR, "Default Color", getFieldEditorParent()));

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}