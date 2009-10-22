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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.playframework.playclipse.Editor;
import org.playframework.playclipse.FilesAccess;

/**
 * 
 * Open the routes files from the controller, and show the line corresponding to the current action
 *
 */
public class GoToRouteHandler extends AbstractHandler {

	/**
	 * The constructor.
	 */
	public GoToRouteHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		String line;
		String action = null;
		Editor editor = Editor.getCurrent(event);
		int lineNo = editor.getCurrentLineNo();
		line = editor.getLine(lineNo);
		// Let's go up until we get the action name
		while (lineNo > 0 && action == null) {
			line = editor.getLine(lineNo--);
			if (line.contains("public") && line.contains("static") && line.contains("void")) {
				Pattern pt2 = Pattern.compile("\\w+\\s*\\(");
				Matcher m2 = pt2.matcher(line);
				if (m2.find()) {
					String method = m2.group().replace("(", "").trim();
					String controller = editor.getTitle().replace(".java", "");
					action = controller + "." + method;
				}
			}
		}
		if (action == null) {
			MessageDialog.openInformation(
			window.getShell(),
			"Playclipse",
			"Use this command in a controller, in an action method");
		} else {
			IEditorPart editorPart = FilesAccess.openFile("conf/routes", window);
			FilesAccess.goToLineContaining(editorPart, action);
		}
		return null;
	}

}
