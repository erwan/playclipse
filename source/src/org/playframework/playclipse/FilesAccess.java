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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import fr.zenexity.pdt.editors.EditorHelper;

public class FilesAccess {
	public enum FileType {
		JAVA("org.eclipse.jdt.ui.CompilationUnitEditor"),
		HTML("tk.eclipse.plugin.htmleditor.editors.HTMLEditor"),
		CSS("tk.eclipse.plugin.htmleditor.editors.HTMLEditor"),
		JS("tk.eclipse.plugin.htmleditor.editors.HTMLEditor");
		private String editorID;
		FileType(String editorID) {
			this.editorID = editorID;
		}
	}

	public static IEditorPart openFile(IFile file, IWorkbenchWindow window) throws CoreException {
		IEditorPart result = null;
		IWorkbenchPage page = getCurrentPage();
		IMarker marker;
		marker = file.createMarker(IMarker.TEXT);
		result = IDE.openEditor(page, marker);
		marker.delete();
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

	public static void goToCharacter(IEditorPart editor, int character) {
		IMarker marker = null;
		try {
			marker = getFile(editor).createMarker(IMarker.TEXT);
			marker.setAttribute(IMarker.CHAR_START, character);
		} catch (CoreException e) {
			// Never happens! We got the file from the editor.
		}
		IDE.gotoMarker(editor, marker);
	}

	public static void goToLineContaining(IEditorPart editorPart, String text) {
		EditorHelper editor = new EditorHelper((ITextEditor)editorPart);
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

	public static void createAndOpen(IFile file, String content, FileType type) {
		IWorkbenchPage page = getCurrentPage();
		InputStream source = new ByteArrayInputStream(content.getBytes());
		try {
			file.create(source, false, null);
			page.openEditor(
					new FileEditorInput(file),
					type.editorID,
					true,
					IWorkbenchPage.MATCH_INPUT);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static IFile getFile(IEditorPart editorPart) {
		return ((IFileEditorInput)editorPart.getEditorInput()).getFile();
	}

	private static IWorkbenchPage getCurrentPage() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		return page;
	}

}
