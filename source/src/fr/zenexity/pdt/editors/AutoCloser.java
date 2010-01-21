package fr.zenexity.pdt.editors;


import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;


public class AutoCloser implements VerifyKeyListener {

	Editor editor;
	SourceViewer sourceViewer;
	
	public AutoCloser(Editor editor, SourceViewer sourceViewer) {
		this.editor = editor;
		this.sourceViewer = sourceViewer;
	}

	protected void moveCursor(int cursorDelta) {
		int widgetCursorPos = sourceViewer.getTextWidget().getCaretOffset();
		int docCursorPos = sourceViewer.widgetOffset2ModelOffset(widgetCursorPos);
		sourceViewer.setSelectedRange(docCursorPos + cursorDelta, 0);
	}
	
	@Override
	public void verifyKey(VerifyEvent event) {
		try {
			IDocument document = sourceViewer.getDocument();
	        Point selection = sourceViewer.getSelectedRange();
	        int cursorOffset = selection.x;
	        int selectionLength = selection.y;
	        char c = event.character;
	        char pc = document.getChar(cursorOffset - 1);
	        char nc = 0;
	        try {
	        	nc = document.getChar(cursorOffset);
	        } catch(Exception e) {
	        	// Now don't care
	        }
	        String insert = editor.autoClose(pc, c, nc);
	        if(insert != null && insert.equals(Editor.SKIP)) {
	        	event.doit = false;
	        	moveCursor(1);
	        } else if(insert != null) {
	        	insert = c + insert;
	        	document.replace(cursorOffset, selectionLength, insert);
                event.doit = false;
                moveCursor(1);
	        }
		} catch(BadLocationException e) {
			// Don't care
		}
	}

	protected static final char BACKSPACE = 8;
	protected static final char CARRAIGE_RETURN = 13;
	protected static final char NEWLINE = 10;
	protected static final char TAB = 9;
	protected static final char SPACE = 32;

}
