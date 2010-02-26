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

		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_ACTION_COLOR, new RGB(200, 0, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_KEYWORD_COLOR, new RGB(0, 200, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_URL_COLOR, new RGB(0, 0, 200));
		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_COMMENT_COLOR, new RGB(90, 90, 90));
		PreferenceConverter.setDefault(store, PreferenceConstants.ROUTE_DEFAULT_COLOR, new RGB(0, 0, 0));

		PreferenceConverter.setDefault(store, PreferenceConstants.CONF_COMMENT_COLOR, new RGB(90, 90, 90));
		PreferenceConverter.setDefault(store, PreferenceConstants.CONF_KEY_COLOR, new RGB(150, 0, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.CONF_DEFAULT_COLOR, new RGB(0, 0, 0));

		PreferenceConverter.setDefault(store, PreferenceConstants.HTML_ACTION_COLOR, new RGB(255, 0, 192));
		PreferenceConverter.setDefault(store, PreferenceConstants.HTML_DEFAULT_COLOR, new RGB(0, 0, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.HTML_DOCTYPE_COLOR, new RGB(127, 127, 127));
		PreferenceConverter.setDefault(store, PreferenceConstants.HTML_EXPR_COLOR, new RGB(255, 144, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.HTML_HTML_COLOR, new RGB(0, 0, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.HTML_KEYWORD_COLOR, new RGB(0, 0, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.HTML_SKIPPED_COLOR, new RGB(90, 90, 90));
		PreferenceConverter.setDefault(store, PreferenceConstants.HTML_TAG_COLOR, new RGB(129, 0, 153));
		PreferenceConverter.setDefault(store, PreferenceConstants.HTML_STRING_COLOR, new RGB(5, 152, 220));
	}

}
