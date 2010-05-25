package org.playframework.playclipse.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

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

}
