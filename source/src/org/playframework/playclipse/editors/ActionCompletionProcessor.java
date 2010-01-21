package org.playframework.playclipse.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.graphics.Image;
import org.playframework.playclipse.handlers.PlayPlugin;

import fr.zenexity.pdt.editors.CompletionProcessor;
import fr.zenexity.pdt.editors.Editor;

public class ActionCompletionProcessor extends CompletionProcessor {

	public ActionCompletionProcessor(ISourceViewer sourceViewer, Editor editor) {
		super("action", sourceViewer, editor);
	}

	@Override
	public Template[] getTemplates(String contextTypeId) {
		String ctx = getCtx();
		System.out.println("templates " + contextTypeId + " - " + ctx);
		List<Template> result = new ArrayList<Template>();
		if (ctx.contains(".")) {
			String[] splitted = ctx.split("\\.");
			String query = "";
			if (splitted.length > 1) {
				query = splitted[1];
			}
			List<IMethod> methods = getMatchingMethods(splitted[0], query);
			for (int i = 0; i < methods.size(); i++) {
				result.add(getTemplate(methods.get(i)));
			}
		}
		System.out.println("RETURN: "+result);
		return result.toArray(new Template[result.size()]);
	}

	private Template getTemplate(IMethod javaMethod) {
		String name = javaMethod.getElementName();
		String description = javaMethod.getCompilationUnit().getElementName();
		return new Template(name, description, editor.getClass().getName(), name, true);
	}

	private List<IMethod> getMatchingMethods(String fullClassName, String query) {
		System.out.println("getMatchingMethods " + fullClassName + " - " + query);
		List<IMethod> result = new ArrayList<IMethod>();
		IJavaProject javaProject = JavaCore.create(editor.getProject());
		try {
			IType type = javaProject.findType("controllers." + fullClassName);
			if (type == null) {
				type = javaProject.findType(fullClassName);
			}
			if (type == null) {
				return result;
			}
			IMethod[] allMethods = type.getMethods();
			for (int i = 0; i < allMethods.length; i++) {
				IMethod method = allMethods[i];
				int flags = method.getFlags();
				if ((query.isEmpty() || method.getElementName().startsWith(query))
						&& Flags.isPublic(flags)
						&& Flags.isStatic(flags)
						&& method.getReturnType().equals("V")
						) {
					result.add(allMethods[i]);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return result;
	}

	private Image controllerImage;

	@Override
	protected Image getImage(Template template) {
		if (controllerImage == null) {
			controllerImage = PlayPlugin.getImageDescriptor("icons/controller.png").createImage();
		}
		return controllerImage;
	}

}
