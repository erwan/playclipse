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

public class HTMLEditorPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public HTMLEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(PlayPlugin.getDefault().getPreferenceStore());
		setDescription("Play HTML Template Editor");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new ColorFieldEditor(PreferenceConstants.HTML_DEFAULT_COLOR, "Default Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.HTML_STRING_COLOR, "String Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.HTML_ACTION_COLOR, "Action Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.HTML_DOCTYPE_COLOR, "Doctype Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.HTML_EXPR_COLOR, "Expr Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.HTML_HTML_COLOR, "HTML Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.HTML_KEYWORD_COLOR, "Keyword Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.HTML_SKIPPED_COLOR, "Skipped Color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.HTML_TAG_COLOR, "Tag Color", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}