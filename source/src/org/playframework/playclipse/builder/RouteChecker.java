package org.playframework.playclipse.builder;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.playframework.playclipse.PlayPlugin;
import org.playframework.playclipse.editors.route.RouteEditor;

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
		String severityStr = PlayPlugin.getDefault().getPreferenceStore().getString(RouteEditor.MISSING_ROUTE);
		if (severityStr.equals("ignore")) return;
		System.out.println("Severity => " + severityStr);
		final int severity =  severityStr.equals("warning")
				? IMarker.SEVERITY_WARNING
				: IMarker.SEVERITY_ERROR;

		try {
			IO.readLines(file, new LineReader() {
				public void readLine(String line, int lineNumber, int offset) {
					if (comment.matcher(line).find()) {
						// commented line
						return;
					}
					if (line.isEmpty() || empty.matcher(line).matches()) {
						// empty line
						return;
					}
					try {
						checkLine(line, lineNumber, offset, severity);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
		}
	}

	private static Pattern METHOD = Pattern.compile("(\\*|GET|POST|PUT|DELETE|UPDATE|HEAD)");

	private void checkLine(String line, int lineNumber, int offset, int severity) throws CoreException {
		String[] rule = line.split("\\s+");
		if (rule.length != 3) {
			addMarker("Invalid route syntax", lineNumber, severity, offset, offset + line.length());
			return;
		}
		String method = rule[0];
		//String path = rule[1];
		String action = rule[2];
		if (METHOD.matcher(method).matches() == false) {
			addMarker("Invalid method", lineNumber, severity, offset, offset + method.length());
		}

		if (action.indexOf(":") == -1 && // TODO: Check module routes
				action.indexOf("{") == -1 && // TODO: Check if it's valid?
				getInspector().resolveAction(action) == null) {
			int start = offset + line.indexOf(action);
			addMarker("Missing route: " + action, lineNumber, severity, start, start + action.length());
		}
	}

}
