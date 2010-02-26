package org.playframework.playclipse.preferences;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.playframework.playclipse.PlayPlugin;

public abstract class PlayEditorPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PlayEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(PlayPlugin.getDefault().getPreferenceStore());
	}

	public void createFieldEditors() {
		Iterator<Map.Entry<String, String>> it = getFields().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = it.next();
			addField(new ColorFieldEditor(pairs.getKey(), pairs.getValue(), getFieldEditorParent()));
		}
	}

	public void init(IWorkbench workbench) {
	}

	public abstract Map<String, String> getFields();
}
