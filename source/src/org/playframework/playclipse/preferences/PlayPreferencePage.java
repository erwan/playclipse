package org.playframework.playclipse.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.playframework.playclipse.PlayPlugin;

public class PlayPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PlayPreferencePage() {
		super(GRID);
		setPreferenceStore(PlayPlugin.getDefault().getPreferenceStore());
		setDescription("Preferences for the Play Framework Plugin.");
	}

	@Override
	public void createFieldEditors() {
	}

	@Override
	public void init(IWorkbench workbench) {
	}

}