package org.playframework.playclipse;

import java.util.HashMap;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.ide.IDE;

public class FilesAccess {

	private static IWorkbenchPage getCurrentPage() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		return page;
	}

	public static void openFile(String filename) {
		// IFile file = <choose the file to open>;
		IWorkbenchPage page = getCurrentPage();
		HashMap<String, Object> map = new HashMap();
		map.put(IMarker.LINE_NUMBER, new Integer(5));
		map.put(IWorkbenchPage.EDITOR_ID_ATTR, "org.eclipse.ui.DefaultTextEditor");
		IMarker marker = file.createMarker(IMarker.TEXT);
		marker.setAttributes(map);
		IDE.openEditor(page, marker);
		marker.delete();
	}
}
