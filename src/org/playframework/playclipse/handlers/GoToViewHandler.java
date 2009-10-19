package org.playframework.playclipse.handlers;

import org.playframework.playclipse.Editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class GoToViewHandler extends AbstractHandler {

	/**
	 * The constructor.
	 */
	public GoToViewHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		Editor editor = Editor.getCurrent(event);
		int lineNo = editor.getCurrentLineNo();
		String line = editor.getCurrentLine();

		MessageDialog.openInformation(
				window.getShell(),
				"Playclipse",
				line);
		return null;
	}
}
