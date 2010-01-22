package org.playframework.playclipse.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
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
		templateImages = new HashMap<String, Image>();
		templateImages.put("Package", PlayPlugin.getImageDescriptor("icons/package_obj.gif").createImage());
		templateImages.put("Controller", PlayPlugin.getImageDescriptor("icons/class_obj.gif").createImage());
		templateImages.put("Action", PlayPlugin.getImageDescriptor("icons/controller.png").createImage());
	}

	@Override
	public Template[] getTemplates(String contextTypeId) {
		String ctx = getCtx();
		if (ctx.startsWith("@@{")) ctx = ctx.replace("@@{", "");
		if (ctx.startsWith("@{")) ctx = ctx.replace("@{", "");
		System.out.println("templates " + contextTypeId + " - " + ctx);
		List<Template> result = new ArrayList<Template>();
		IJavaProject javaProject = JavaCore.create(editor.getProject());
		if (ctx.isEmpty()) {
			try {
				IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
				for (int i = 0; i < roots.length; i++) {
					if (!roots[i].isArchive())
						result.add(getTemplate(contextTypeId, roots[i]));
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
				return result.toArray(new Template[result.size()]);
			}
		} else if (ctx.contains(".")) {
/*			System.out.println("ALL PACKAGE FRAGMENTS");
			try {
				IPackageFragment[] frags = javaProject.getPackageFragments();
				for (int i = 0; i < frags.length; i++) {
					System.out.println(frags[i].getElementName());
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
				return result.toArray(new Template[result.size()]);
			}
			System.out.println("/ ALL PACKAGE FRAGMENTS");*/
			String typeName = ctx.substring(0, ctx.lastIndexOf('.'));
			String query = ctx.substring(ctx.lastIndexOf('.') + 1);
			result.addAll(getMatchingTemplates(typeName, query, contextTypeId));
		}
		return result.toArray(new Template[result.size()]);
	}

	private Template getTemplate(String contextTypeId, IPackageFragmentRoot root) {
		String name = root.getElementName();
		String description = "Package";
		return new Template(name, description, contextTypeId, name, true);
	}

	private Template getTemplate(String contextTypeId, IMethod javaMethod) {
		String name = javaMethod.getElementName();
		String description = "Action";
		return new Template(name, description, contextTypeId, name, true);
	}

	private Template getTemplate(String contextTypeId, IPackageFragment packageFragment) {
		String name = packageFragment.getElementName();
		String description = "Package";
		return new Template(name, description, contextTypeId, name, true);
	}

	private Template getTemplate(String contextTypeId, ICompilationUnit compilationUnit) {
		String name = compilationUnit.getElementName().replace(".java", "");
		String description = "Controller";
		return new Template(name, description, contextTypeId, name, true);
	}

	private List<Template> getMatchingTemplates(String fullClassName, String query, String contextTypeId) {
		System.out.println("getMatchingMethods {" + fullClassName + "}.{" + query + "}");
		List<Template> result = new ArrayList<Template>();
		IJavaProject javaProject = JavaCore.create(editor.getProject());
		try {
			IParent parent;
			// Look for classes
			parent = javaProject.findType("controllers." + fullClassName);
			if (parent == null) {
				parent = javaProject.findType(fullClassName);
			}
			// Look for package fragments
			if (parent == null) {
				parent = getPackageFragment(javaProject, "controllers." + fullClassName);
			}
			if (parent == null) {
				parent = getPackageFragment(javaProject, fullClassName);
			}
			if (parent == null) {
				System.out.println("Can't find anything!");
				return null;
			}
			IJavaElement[] children = parent.getChildren();
			for (int i = 0; i < children.length; i++) {
				IJavaElement child = children[i];
				System.out.println(child.getElementName() + "?");
				if (child instanceof IMethod) {
					IMethod method = (IMethod)child;
					int flags = method.getFlags();
					if ((query.isEmpty() || method.getElementName().startsWith(query))
							&& Flags.isPublic(flags)
							&& Flags.isStatic(flags)
							&& method.getReturnType().equals("V")) {
						result.add(getTemplate(contextTypeId, method));
					}
				} else if (child instanceof IPackageFragment) {
					result.add(getTemplate(contextTypeId, (IPackageFragment)child));
				} else if (child instanceof ICompilationUnit) { // Java class
					System.out.println(child.getElementName() + " is a ICompilationUnit");
					result.add(getTemplate(contextTypeId, (ICompilationUnit)child));
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return result;
	}

	private IPackageFragment getPackageFragment(IJavaProject project, String fragmentName) {
		IPackageFragment[] frags;
		try {
			frags = project.getPackageFragments();
			for (int i = 0; i < frags.length; i++) {
				if (frags[i].getElementName().equals(fragmentName)) {
					return frags[i];
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Image> templateImages;

	@Override
	protected Image getImage(Template template) {
		if (templateImages.containsKey(template.getDescription())) {
			return templateImages.get(template.getDescription());
		}
		return null;
	}

}
