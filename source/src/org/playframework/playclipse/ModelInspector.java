package org.playframework.playclipse;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class ModelInspector {

	private IJavaProject javaProject;

	public ModelInspector(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public IMethod resolveAction(String input) {
		if (input.contains("@@{")) input = input.substring(input.indexOf("@@{") + 3);
		if (input.contains("@{")) input = input.substring(input.indexOf("@{") + 2);

		if (input.indexOf('.') == -1)
			return null;

		String typeName = input.substring(0, input.lastIndexOf('.'));
		String query = input.substring(input.lastIndexOf('.') + 1);
		return getAction("controllers." + typeName, query);
	}

	private IMethod getAction(String fullClassName, String query) {
		try {
			IType parent = javaProject.findType(fullClassName);
			// Look for package fragments
			if (parent == null) {
				return null;
			}
			IJavaElement[] children = parent.getChildren();
			for (int i = 0; i < children.length; i++) {
				IJavaElement child = children[i];
				if (child instanceof IMethod) {
					IMethod method = (IMethod)child;
					int flags = method.getFlags();
					if ((query.isEmpty() || method.getElementName().startsWith(query))
							&& Flags.isPublic(flags)
							&& Flags.isStatic(flags)
							&& method.getReturnType().equals("V")) {
						return method;
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return null;
	}

}

