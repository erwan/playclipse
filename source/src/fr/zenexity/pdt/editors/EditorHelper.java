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

package fr.zenexity.pdt.editors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A helper class to handle the Eclipse-specific heavy lifting to access and
 * manipulate editors and their associated documents.
 * 
 * While it looks like some of these methods could go to
 * org.playframework.playclipse.editors.Editor, we need them outside for the handlers
 * (toolbar icons and shortcuts)
 *
 */
public final class EditorHelper {

	public ITextEditor textEditor;

	public EditorHelper(ITextEditor textEditor) {
		this.textEditor = textEditor;
	}

	/**
	 * Static Factory Method: Creates an Editor corresponding to the ITextEditor
	 * the user is currently interacting with.
	 *
	 * @param event
	 * @throws ExecutionException
	 */
	public static EditorHelper getCurrent(ExecutionEvent event)
			throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor instanceof ITextEditor) {
			return new EditorHelper((ITextEditor) editor);
		} else {
			return null;
		}
	}

	public IProject getProject() {
		IFile curfile = ((IFileEditorInput)textEditor.getEditorInput()).getFile();
		IContainer container = curfile.getParent();
		while (container != null) {
			if (container instanceof IProject) {
				return (IProject)container;
			}
			container = container.getParent();
		}
		// Should not happen
		return null;
	}

	/**
	 * Return the first parent with the requested name, null if not found
	 * @param name 
	 * @return
	 */
	public IContainer getFirstParentFor(String name) {
		IFile curfile = ((IFileEditorInput)textEditor.getEditorInput()).getFile();
		IContainer container = curfile.getParent();
		while (container != null) {
			if (container.getName().equals(name)) {
				return container;
			}
			container = container.getParent();
		}
		return null;
	}

	public IWorkbenchWindow getWindow() {
		return this.textEditor.getSite().getWorkbenchWindow();
	}

	public int getCurrentLineNo() {
		return this.getTextSelection().getStartLine();
	}

	public int lineCount() {
		return getDocument().getNumberOfLines();
	}

	public String getTitle() {
		return textEditor.getTitle();
	}

	public String enclosingDirectory() {
		IPath path = getFilePath();
		return path.segment(path.segmentCount() - 2);
	}

	/**
	 * 
	 * @return the path of the file currently edited in the editor
	 */
	protected IPath getFilePath() {
		return ((IFileEditorInput) textEditor.getEditorInput()).getFile().getFullPath();
	}

	/**
	 * 
	 * @param lineNo the line number to get
	 * @return the text corresponding the the lines requested in the editor
	 */
	public String getLine(int lineNo) {
		IDocument doc = this.getDocument();
		try {
			return doc.get(doc.getLineOffset(lineNo), doc.getLineLength(lineNo));
		} catch (BadLocationException e) {
			return null;
		}
	}

	public String getCurrentLine() {
		return getLine(getCurrentLineNo());
	}

	private ITextSelection getTextSelection() {
		return ((ITextSelection) textEditor.getSelectionProvider().getSelection());
	}

	public IDocument getDocument() {
		return textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
	}

	public int getLineNumber(int offset) throws BadLocationException {
		IDocument doc = this.getDocument();
		return doc.getLineOfOffset(offset);
	}

}
