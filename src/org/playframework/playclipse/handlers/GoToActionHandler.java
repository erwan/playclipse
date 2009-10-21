package org.playframework.playclipse.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		String action = null;
		String controller = null;
		String path = null;

		Editor editor = Editor.getCurrent(event);
		String line = editor.getLine(editor.getCurrentLineNo());
		controller = editor.enclosingDirectory();
		Pattern pt = Pattern.compile("@\\{[^\\(]+\\(\\)\\}");
		Matcher m = pt.matcher(line);
		if (m.find()) {
			// There is a custom view
			action = m.group().replace("@{", "").replace("()}", "");
			if (action.contains(".")) {
				controller = action.split(".")[0];
			}
		} else {
			action = editor.getTitle().replace(".html", "");
		}

		path = "/app/controllers/" + controller + ".java";
		IEditorPart newEditorPart = FilesAccess.openFile(path, window);
		Editor newEditor = new Editor((ITextEditor)newEditorPart);
		int lineNo = -1;
		int i = 0;
		int length = newEditor.lineCount();
		IDocument doc = newEditor.getDocument();
		try {
			while (i < length && lineNo < 0) {
				line = doc.get(doc.getLineOffset(i), doc.getLineLength(i));
				if (line.contains("public") &&
					line.contains("static") &&
					line.contains("void") &&
					line.contains(action))
				{
					lineNo = i;
				}
				i++;
			}
		} catch (BadLocationException e) {
			// Should never happen
			e.printStackTrace();
		}
		FilesAccess.goToLine(newEditorPart, i);
		return null;
	}
}
