package org.playframework.playclipse;

import org.eclipse.ui.ide.IDE;
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
