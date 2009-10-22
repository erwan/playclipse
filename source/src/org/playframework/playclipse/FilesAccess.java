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

import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class FilesAccess {
	public static IEditorPart openFile(String filename, IWorkbenchWindow window) {
		IEditorPart result = null;

		IFile file = getFile(filename);
		IWorkbenchPage page = getCurrentPage();
		IMarker marker;
		try {
			marker = file.createMarker(IMarker.TEXT);
			marker.setAttribute(IDE.EDITOR_ID_ATTR, "org.eclipse.ui.DefaultTextEditor");
			result = IDE.openEditor(page, marker);
			marker.delete();
		} catch (CoreException e) {
			// TODO: if the file doesn't exist, create it (or at least prompt the user to create it)
			e.printStackTrace();
		}
		return result;
	}

	public static void goToLine(IEditorPart editor, int line) {
		IMarker marker = null;
		try {
			marker = getFile(editor).createMarker(IMarker.TEXT);
			marker.setAttribute(IMarker.LINE_NUMBER, line);
		} catch (CoreException e) {
			// Never happens! We got the file from the editor.
		}
		IDE.gotoMarker(editor, marker);
	}

	public static void goToLineContaining(IEditorPart editorPart, String text) {
		Editor editor = new Editor((ITextEditor)editorPart);
		String line;
		int lineNo = -1;
		int i = 0;
		int length = editor.lineCount();
		IDocument doc = editor.getDocument();
		try {
			while (i < length && lineNo < 0) {
				line = doc.get(doc.getLineOffset(i), doc.getLineLength(i));
				if (line.contains(text)) {
					lineNo = i;
				}
				i++;
			}
		} catch (BadLocationException e) {
			// Should never happen
			e.printStackTrace();
		}
		FilesAccess.goToLine(editorPart, i);
	}

	private static IFile getFile(IEditorPart editorPart) {
		return ((IFileEditorInput)editorPart.getEditorInput()).getFile();
	}

	private static IFile getFile(String filename) {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		IFile file = null;
		for (IProject project: projects) {
			file = project.getFile(filename);
			if (file != null)
				return file;
		}
		return null;
	}

	private static IWorkbenchPage getCurrentPage() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		return page;
	}

}
