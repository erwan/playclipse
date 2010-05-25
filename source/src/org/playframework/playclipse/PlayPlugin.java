/*
 * Playclipse - Eclipse plugin for the Play! Framework
 * Copyright 2009 Zenexity
 *
 * This file is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.playframework.playclipse;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PlayPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.playframework.playclipse";

	// The shared instance
	private static PlayPlugin plugin;

	// Preferences
	public static final String PREF_TMPL_IDENT = "_play_tmpl_ident";
	public static final String PREF_BROWSER = "pref_browser";
	public static final String PREF_BROWSER_INTERNAL = "internal";
	public static final String PREF_BROWSER_EXTERNAL = "external";

	/**
	 * The constructor
	 */
	public PlayPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
/*
		ICommandService commandService = (ICommandService)plugin.getWorkbench().getService(ICommandService.class);
		commandService.addExecutionListener(new IExecutionListener() {
			public void notHandled(final String commandId, final NotHandledException exception) {}
			public void postExecuteFailure(final String commandId, final ExecutionException exception) {}
			public void postExecuteSuccess(final String commandId, final Object returnValue) {}
			public void preExecute( final String commandId, final ExecutionEvent event ) {
				if (commandId.equals("org.eclipse.ui.file.save")) {
					IEditorPart editor = HandlerUtil.getActiveEditor(event);
					if (editor instanceof Editor) {
						((Editor)editor).updateMarkers();
					}
				}
			}
		});*/
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PlayPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
