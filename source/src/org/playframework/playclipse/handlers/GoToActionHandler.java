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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.playframework.playclipse.Editor;
import org.playframework.playclipse.FilesAccess;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class GoToActionHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public GoToActionHandler() {
	}

	private String fromView(Editor editor) {
		String line = editor.getLine(editor.getCurrentLineNo());
		String controller = editor.enclosingDirectory();
		String action;
		Pattern pt = Pattern.compile("@\\{[^\\(]+\\(\\)\\}");
		Matcher m = pt.matcher(line);
		if (m.find()) {
			action = m.group().replace("@{", "").replace("()}", "");
			if (action.contains(".")) {
				controller = action.split("\\.")[0];
			}
		} else {
			action = editor.getTitle().replace(".html", "");
		}
		return controller + "." + action;
	}

	private String fromRoutes(Editor editor) {
		String line = editor.getLine(editor.getCurrentLineNo());
		String[] lineArr = line.trim().split("\\s+");
		return lineArr[lineArr.length - 1];
	}

	/**
	 * the command has been executed, so let's extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		String action = null;
		Editor editor = Editor.getCurrent(event);
		IProject project = editor.getProject();
		if (editor.isView()) {
			action = fromView(editor);
		} else if (editor.isRoutes()) {
			action = fromRoutes(editor);
		}
		String controller = action.split("\\.")[0];
		String method = action.split("\\.")[1];
		String path = "app/controllers/" + controller + ".java";
		IFile file = project.getFile(path);
		if (file.exists()) {
			IEditorPart newEditorPart;
			try {
				newEditorPart = FilesAccess.openFile(file, window);
				Editor newEditor = new Editor((ITextEditor)newEditorPart);
				int lineNo = -1;
				int i = 0;
				int length = newEditor.lineCount();
				String line;
				IDocument doc = newEditor.getDocument();
				while (i < length && lineNo < 0) {
					line = doc.get(doc.getLineOffset(i), doc.getLineLength(i));
					if (line.contains("public") &&
						line.contains("static") &&
						line.contains("void") &&
						line.contains(method))
					{
						lineNo = i;
					}
					i++;
				}
				FilesAccess.goToLine(newEditorPart, i);
			} catch (CoreException e) {
				// Should never happen
				e.printStackTrace();
			} catch (BadLocationException e) {
				// Should never happen
				e.printStackTrace();
			}
		} else {
			if (MessageDialog.openConfirm(
					window.getShell(),
					"Playclipse",
					"The file " + path + " can't be found, do you want to create it?")) {
				FilesAccess.createAndOpen(file, "org.eclipse.ui.DefaultTextEditor");
			}
		}
		return null;
	}
}
