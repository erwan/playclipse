package org.playframework.playclipse;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

public final class Editor {

	private ITextEditor textEditor;

	public Editor(ITextEditor textEditor) {
		this.textEditor = textEditor;
	}

	public static Editor getCurrent(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor instanceof ITextEditor) {
			return new Editor((ITextEditor)editor);
		} else {
			return null;
		}
	}

	public int getCurrentLineNo() {
		return this.getTextSelection().getStartLine();
	}

	public String getTitle() {
		return textEditor.getTitle();
	}

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

	private IDocument getDocument() {
		return textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
	}

}
