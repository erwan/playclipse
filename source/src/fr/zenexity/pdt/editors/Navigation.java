package fr.zenexity.pdt.editors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.playframework.playclipse.CodeTemplates;
import org.playframework.playclipse.FilesAccess;


public final class Navigation {
	private EditorHelper editorHelper;
	private IWorkbenchWindow window;
	private IProject project;
	private IJavaProject javaProject;

	public Navigation(EditorHelper editorHelper) {
		this.editorHelper = editorHelper;
		this.window = this.editorHelper.getWindow();
		this.project = this.editorHelper.getProject();
		this.javaProject = JavaCore.create(project);
	}

	private IType findType(String name) {
		try {
			IType type = javaProject.findType(name);
			System.out.println("Type for " + name + ": " + type);
			return type;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Open the requested action in an editor
	 * @param action the fully qualified action, such as namespace.className.method
	 */
	public void goToAction(String action) {
		String fullClassName = "controllers." + action.replaceFirst(".[^.]+$", "");
		System.out.println("goToAction for class: " + fullClassName);
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
	 * @throws JavaModelException
	 */
	private void focusOrCreateMethod(IEditorPart editorPart, IType type, String methodName) throws JavaModelException {
		// We can't just use IType.getMethod because we don't know the arguments
		ISourceRange sourceRange = null;
		IMethod[] methods;
		System.out.println("Looking for method: " + methodName);
			methods = type.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getElementName().equals(methodName)) {
					sourceRange = methods[i].getSourceRange();
				}
			}
		if (sourceRange != null) {
			FilesAccess.goToCharacter(editorPart, sourceRange.getOffset());
		} else if (MessageDialog.openConfirm(
				window.getShell(),
				"Playclipse",
				"The method " + methodName + " doesn't exist, do you want to create it?")) {
				IMethod newMethod = type.createMethod("public static void "+methodName+"() {\n\n}\n", null, false, null);
				FilesAccess.goToCharacter(editorPart, newMethod.getSourceRange().getOffset());
		}
	}

	public void goToView(String viewName) {
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

	public static IProject getProject(IStructuredSelection selection) {
		Object obj = selection.getFirstElement();
		if (obj instanceof IJavaElement) {
			obj = ((IJavaElement)obj).getResource();
		}
		if (obj instanceof IResource) {
			IContainer container;
			if (obj instanceof IContainer) {
				container = (IContainer) obj;
			} else {
				container = ((IResource) obj).getParent();
			}
			while (container != null) {
				if (container instanceof IProject) {
					return (IProject)container;
				}
				container = container.getParent();
			}
		}
		return null;
	}

}
