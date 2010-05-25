package org.playframework.playclipse.builder;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.playframework.playclipse.ModelInspector;

import fr.zenexity.pdt.editors.IO;
import fr.zenexity.pdt.editors.IO.LineReader;

public class RouteChecker extends ErrorChecker {

	private static Pattern comment = Pattern.compile("^\\s*#");
	private static Pattern empty = Pattern.compile("\\s*");

	public RouteChecker(IFile file) {
		super(file);
	}

	@Override
	public void check() {
		try {
			IO.readLines(file, new LineReader() {
				public void readLine(String line, int lineNumber, int offset) {
					System.out.println("" + lineNumber + " - " + line);
					if (comment.matcher(line).find()) {
						// commented line
						return;
					}
					if (line.isEmpty() || empty.matcher(line).matches()) {
						// empty line
						return;
					}
					try {
						checkLine(line, lineNumber, offset);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Pattern METHOD = Pattern.compile("(\\*|GET|POST|PUT|DELETE|UPDATE)");

	private void checkLine(String line, int lineNumber, int offset) throws CoreException {
		String[] rule = line.split("\\s+");
		if (rule.length != 3) {
			System.out.println("ERROR length with {" + line + "}");
			addMarker("Invalid route syntax", lineNumber, IMarker.SEVERITY_ERROR, offset, offset + line.length());
			return;
		}
		String method = rule[0];
		//String path = rule[1];
		String action = rule[2];
		System.out.println("Method = " + method + " - " + METHOD.matcher(method).matches());
		if (METHOD.matcher(method).matches() == false) {
			System.out.println("===> add marker line " + lineNumber);
			addMarker("Invalid method", lineNumber, IMarker.SEVERITY_ERROR, offset, offset + method.length());
		}

		
		if (action.indexOf(":") == -1 && // TODO: Check module routes
				action.indexOf("{") == -1 && // TODO: Check if it's valid?
				getInspector().resolveAction(action) == null) {
			int start = offset + line.indexOf(action);
			addMarker("Missing route: " + action, lineNumber, IMarker.SEVERITY_ERROR, start, start + action.length());
		}
	}

	private IJavaProject javaProject = null;
	private ModelInspector inspector = null;

	private IJavaProject getJavaProject() {
		if (javaProject == null) javaProject = JavaCore.create(getProject());
		return javaProject;
	}

	private ModelInspector getInspector() {
		if (inspector == null) inspector = new ModelInspector(getJavaProject());
		return inspector;
	}

	public IProject getProject() {
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
