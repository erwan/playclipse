package org.playframework.playclipse.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.playframework.playclipse.PlayPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PlayPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.ROUTE_ACTION_COLOR, true);
		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_ACTION_COLOR, new RGB(200, 0, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_KEYWORD_COLOR, new RGB(0, 200, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_URL_COLOR, new RGB(0, 0, 200));
		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_COMMENT_COLOR, new RGB(90, 90, 90));
		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_DEFAULT_COLOR, new RGB(0, 0, 0));
	}

}
