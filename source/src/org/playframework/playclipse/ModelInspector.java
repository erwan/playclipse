package org.playframework.playclipse;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class ModelInspector {

	private IJavaProject javaProject;

	public ModelInspector(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public IMethod resolveAction(String input) {
		if (input.indexOf('.') == -1)
			return null;

		String typeName = input.substring(0, input.lastIndexOf('.'));
		String query = input.substring(input.lastIndexOf('.') + 1);
		return getAction("controllers." + typeName, query);
	}

	private IMethod getAction(String fullClassName, String query) {
		IType parent = null;
		try {
			parent = javaProject.findType(fullClassName);
		} catch (JavaModelException e) {}
		if (parent == null) {
			return null;
		}
		try {
			IMethod method = findMethod(parent, query);
			if (method != null) return method;
			ITypeHierarchy hierarchy = parent.newTypeHierarchy(null);
			for (IType superclass: hierarchy.getAllSuperclasses(parent)) {
				method = findMethod(superclass, query);
				if (method != null) return method;
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	private IMethod findMethod(IType type, String query) throws JavaModelException {
		// We can't use IType.getMethod(name, parameterTypeSignature) because we usually don't know the parameters,
		// we only have the name.

		for (IMethod method: type.getMethods()) {
			int flags = method.getFlags();
			if (Flags.isPublic(flags)
					&& Flags.isStatic(flags)
					&& method.getReturnType().equals("V")) {
				if (method.getElementName().equals(query))
					return method;
			}
		}
		return null;
	}

}

