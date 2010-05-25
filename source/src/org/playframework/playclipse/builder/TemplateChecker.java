package org.playframework.playclipse.builder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import fr.zenexity.pdt.editors.IO;

public class TemplateChecker extends ErrorChecker {

	public TemplateChecker(IFile file) {
		super(file);
	}

	
	private static Pattern action = Pattern.compile("@\\{([^}]+)\\}");
	private static Pattern action_in_tag = Pattern.compile("#\\{.+(@.+[)])");

	public static boolean isTemplate(IPath path) {
		for (String segment: path.segments()) {
			if (segment.equals("views")) return true;
		}
		return false;
	}

	@Override
	public void check() {
		System.out.println("CHECK: " + file.getName());
		String content = "";
		try {
			content = IO.readContentAsString(file);
		} catch (Exception e) {
			return;
		}
		Matcher matcher = action.matcher(content);
		while (matcher.find()) {
			String action = matcher.group(1);
			int offset = matcher.start() + 2;
			checkAction(action, lineNumberOf(content, offset), offset);
		}
		matcher = action_in_tag.matcher(content);
		while (matcher.find()) {
			String action = matcher.group(1).substring(1);
			int offset = content.indexOf("@", matcher.start()) + 1;
			checkAction(action, lineNumberOf(content, offset), offset);
		}
	}

	private void checkAction(String action, int lineNo, int offset) {
		if (action.indexOf('/') > -1) {
			checkStaticAction(action, lineNo, offset);
			return;
		}
		if (action.indexOf('(') > -1) {
			action = action.substring(0, action.indexOf('('));
		}
		if (action.indexOf('.') == -1) {
			// TODO: Check relative actions, e.g. @index instead of @Application.index
			return;
		}
		if (getInspector().resolveAction(action) == null) {
			try {
				addMarker("Missing route: " + action, lineNo, IMarker.SEVERITY_ERROR, offset, offset + action.length());
			} catch (CoreException e) {
				// Never happens
			}
		}
	}

	private void checkStaticAction(String action, int lineNo, int offset) {
		// TODO
	}

	private static int lineNumberOf(String content, int offset) {
		int lineNo = 1;
		String region = content.substring(0, offset);
		while (region.indexOf("\n") > -1) {
			lineNo++;
			region = region.substring(region.indexOf("\n") + 1);
		}
		return lineNo;
	}

}
