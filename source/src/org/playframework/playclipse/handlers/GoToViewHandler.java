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

package org.playframework.playclipse.handlers;

import org.playframework.playclipse.Editor;
import org.playframework.playclipse.FilesAccess;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import java.util.regex.*;

/**
 * Go to the view (template) corresponding to the current action
 */
public class GoToViewHandler extends AbstractHandler {

	/**
	 * The constructor.
	 */
	public GoToViewHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		String line;
		String viewName = null;
		Editor editor = Editor.getCurrent(event);
		int lineNo = editor.getCurrentLineNo();
		line = editor.getLine(lineNo);
		if (line.contains("render")) {
			Pattern pt = Pattern.compile("\"(.*)\"");
			Matcher m = pt.matcher(line);
			if (m.find()) {
				// There is a custom view
				viewName = m.group().replace("\"", "");
			} else {
				// No custom view, let's go up until we get the action name
				while (lineNo > 0 && viewName == null) {
					line = editor.getLine(lineNo--);
					if (line.contains("public") && line.contains("static") && line.contains("void")) {
						Pattern pt2 = Pattern.compile("\\w+\\s*\\(");
						Matcher m2 = pt2.matcher(line);
						if (m2.find()) {
							String action = m2.group().replace("(", "").trim();
							String controllerName = editor.getTitle().replace(".java", "");
							viewName = controllerName + "/" + action + ".html";
						}
					}
				}
			}
		}
		if (viewName == null) {
			MessageDialog.openInformation(
			window.getShell(),
			"Playclipse",
			"Use this command in a controller, on a render() line");
		} else {
			FilesAccess.openFile("app/views/" + viewName, window);
		}
		return null;
	}
}
