package org.playframework.playclipse.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.playframework.playclipse.PlayPlugin;

public class PlayPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PlayPreferencePage() {
		super(GRID);
		setPreferenceStore(PlayPlugin.getDefault().getPreferenceStore());
	}

	@Override
	public void createFieldEditors() {
		addField(new RadioGroupFieldEditor(
				PlayPlugin.PREF_BROWSER,
				"Open Tests in",
				1,
				new String[][] {
						{
							"Eclipse internal browser", 
							PlayPlugin.PREF_BROWSER_INTERNAL
						},
						{
							"External browser, as defined in Eclipse general preferences", 
							PlayPlugin.PREF_BROWSER_EXTERNAL
						}
				},
				getFieldEditorParent(),
				true));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

}