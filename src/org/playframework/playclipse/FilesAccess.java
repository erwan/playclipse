package org.playframework.playclipse;

import java.util.HashMap;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.ide.IDE;

public class FilesAccess {
	private static IWorkbenchPage getCurrentPage() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		return page;
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

	public static void openFile(String filename, IWorkbenchWindow window) {
		IFile file = getFile(filename);
		IWorkbenchPage page = getCurrentPage();
		// TODO: jump to the right line (5 is just random)
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IMarker.LINE_NUMBER, new Integer(5));
		map.put(IDE.EDITOR_ID_ATTR, "org.eclipse.ui.DefaultTextEditor");
		IMarker marker;
		try {
			marker = file.createMarker(IMarker.TEXT);
			marker.setAttributes(map);
			IDE.openEditor(page, marker);
			marker.delete();
		} catch (CoreException e) {
			// TODO: if the file doesn't exist, create it (or at least prompt the user to create it)
			e.printStackTrace();
		}
	}
}
