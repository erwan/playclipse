package org.playframework.playclipse.builder;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.playframework.playclipse.ModelInspector;

public abstract class ErrorChecker {

	protected IFile file;

	public ErrorChecker() {}

	public ErrorChecker(IFile file) {
		this.file = file;
	}

	protected IMarker addMarker(String message, int lineNumber, int severity) throws CoreException {
		IMarker marker = file.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		if (lineNumber == -1) {
			lineNumber = 1;
		}
		marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		return marker;
	}

	protected IMarker addMarker(String message, int lineNumber, int severity, int begin, int end) throws CoreException {
		IMarker marker = addMarker(message, lineNumber, severity);
		marker.setAttribute(IMarker.CHAR_START, begin);
		marker.setAttribute(IMarker.CHAR_END, end);
		return marker;
	}

	public abstract void check();

	private IJavaProject javaProject = null;
	private ModelInspector inspector = null;

	protected IJavaProject getJavaProject() {
		if (javaProject == null) javaProject = JavaCore.create(getProject());
		return javaProject;
	}

	protected ModelInspector getInspector() {
		if (inspector == null) inspector = new ModelInspector(getJavaProject());
		return inspector;
	}

	protected IProject getProject() {
		IContainer container = file.getParent();
		while (container != null) {
			if (container instanceof IProject) {
				return (IProject)container;
			}
			container = container.getParent();
		}
		// Should not happen
		return null;
	}

}
