package org.playframework.playclipse;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;
import org.playframework.playclipse.builder.PlayNature;

public final class Navigation {
	private EditorHelper editorHelper;
	private IWorkbenchWindow window;

	public Navigation(EditorHelper editorHelper) {
		this.editorHelper = editorHelper;
		this.window = this.editorHelper.getWindow();
	}

	private IType findType(String name) {
		IProject project = this.editorHelper.getProject();
		try {
			PlayNature nature = (PlayNature)project.getNature("org.playframework.playclipse.playNature");
			IJavaProject javaProj = nature.getJavaProject();
			IType type = javaProj.findType(name);
			System.out.println("Type for " + name + ": " + type);
			return type;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Open the requested action in an editor
	 * @param action the fully qualified action, such as namespace.className.method
	 */
	public void goToAction(String action) {
		String fullClassName = action.replaceFirst(".[^.]+$", "");
		String method = action.substring(action.lastIndexOf('.') + 1);
		IType type = findType(fullClassName);
		IFile file;
		try {
			file = (IFile)type.getCompilationUnit().getCorrespondingResource();
		} catch (JavaModelException e1) {
			// Should not happen
			e1.printStackTrace();
			return;
		}
		IEditorPart newEditorPart;
		try {
			newEditorPart = FilesAccess.openFile(file, this.window);
			focusOrCreateMethod(newEditorPart, type, method);
		} catch (CoreException e) {
			// Should never happen
			e.printStackTrace();
		}
	}

	/**
	 * In an open editor, move the cursor to the line corresponding to the method if
	 * it exists, offer the user to create it otherwise.
	 * @param editorPart
	 * @param type
	 * @param methodName
	 */
	private void focusOrCreateMethod(IEditorPart editorPart, IType type, String methodName) {
		// We can't just use IType.getMethod because we don't know the arguments
		ISourceRange sourceRange = null;
		IMethod[] methods;
		System.out.println("Looking for method: " + methodName);
		try {
			methods = type.getMethods();
			for (int i = 0; i < methods.length; i++) {
				System.out.println("Checking with " + methods[i].getElementName());
				if (methods[i].getElementName().equals(methodName)) {
					sourceRange = methods[i].getSourceRange();
				}
			}
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (sourceRange != null) {
			FilesAccess.goToCharacter(editorPart, sourceRange.getOffset());
		} else if (MessageDialog.openConfirm(
				window.getShell(),
				"Playclipse",
				"The method " + methodName + " doesn't exist, do you want to create it?")) {
			try {
				IMethod newMethod = type.createMethod("public static void "+methodName+"() {\n\n}\n", null, false, null);
				FilesAccess.goToCharacter(editorPart, newMethod.getSourceRange().getOffset());
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void goToView(String viewName) {
		System.out.println("goToView");
		IProject project = editorHelper.getProject();
		try {
			PlayNature nature = (PlayNature)project.getNature("org.playframework.playclipse.playNature");
			System.out.println("Nature: " + nature);
			System.out.println(nature.getModules());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		IFile file = project.getFile("app/views/" + viewName);
		if (!file.exists()) {
			// The file doesn't exist from the absolute path, let's try from the relative path
			IContainer cont = editorHelper.getFirstParentFor("views");
			file = project.getFile(cont.getProjectRelativePath() + "/" + viewName);
		}
		openOrCreate(file);
	}

	public void openOrCreate(String path) {
		IFile file = editorHelper.getProject().getFile(path);
		openOrCreate(file);
	}

	private void openOrCreate(IFile file) {
		if (file.exists()) {
			try {
				FilesAccess.openFile(file, window);
			} catch (CoreException e) {
				// Should never happen (we checked for file.exist())
				e.printStackTrace();
			}
			return;
		}
		String path = file.getFullPath().toString();
		if (MessageDialog.openConfirm(
				window.getShell(),
				"Playclipse",
				"The file " + path + " can't be found, do you want to create it?")) {
			String[] titleArr = path.split("/");
			String title = titleArr[titleArr.length - 1].replace(".html", "");
			String content = CodeTemplates.view(title);
			FilesAccess.createAndOpen(file, content, FilesAccess.FileType.HTML);
		}
	}

}
