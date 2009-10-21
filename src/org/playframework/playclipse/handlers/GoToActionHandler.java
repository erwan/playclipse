package org.playframework.playclipse.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
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
		FilesAccess.openFile(path, window);
		return null;
	}
}
