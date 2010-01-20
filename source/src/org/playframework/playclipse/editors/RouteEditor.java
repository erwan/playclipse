package org.playframework.playclipse.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.playframework.playclipse.handlers.PlayPlugin;

public class RouteEditor extends Editor {

	String oldState = "default";
	private Image controllerImage;

	@Override
	public String[] getTypes() {
		return new String[] {
				"default",
				"keyword",
				"url",
				"action",
				"comment"
		};
	}

	@Override
	public String autoClose(char pc, char c, char nc) {
		return null;
	}

	Pattern action = Pattern.compile("\\s(\\w[\\.\\w]+)");

	@Override
	public IHyperlink detectHyperlink(ITextViewer textViewer, IRegion region) {
		BestMatch match = findBestMatch(region.getOffset(), action);
		if(match != null) {
			if(match.is(action)) {
				return match.hyperlink("action", 0, 0);
			}
		}
		return null;
	}

	@Override
	public TextAttribute getStyle(String type) {
		if(type.equals("keyword")) {
			return style(new RGB(0, 200, 0));
		}
		if(type.equals("url")) {
			return style(new RGB(0, 0, 200));
		}
		if(type.equals("comment")) {
			return style(new RGB(90, 90, 90));
		}
		if(type.equals("action")) {
			return style(new RGB(200, 0, 0));
		}
		return style(new RGB(0, 0, 0));
	}

	@Override
	public String scan() {
		if (isNext("\n")) {
			return found("default", 1);
		}
		if (state != "comment" && isNext("#")) {
			return found("comment", 0);
		}
		if (state == "default" && isNext("GET")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("POST")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("PUT")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("DELETE")) {
			return found("keyword", 0);
		}
		if (state == "default" && isNext("*")) {
			return found("keyword", 0);
		}
		if ((state == "keyword" || state == "url") && nextIsSpace()) {
			oldState = state;
			return found("default", 0);
		}
		if (state == "default" && isNext("/")) {
			return found("url", 0);
		}
		if (state == "default" && oldState == "url" && !nextIsSpace()) {
			return found("action", 0);
		}
		return null;
	}

	@Override
	public Template[] getTemplates(String contentType, String ctx) {
		System.out.println("templates " + contentType + " - " + ctx);
		List<Template> result = new ArrayList<Template>();
		if (contentType.equals("action") && ctx.contains(".")) {
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
		return result.toArray(new Template[result.size()]);
	}

	private Template getTemplate(IMethod javaMethod) {
		String name = javaMethod.getElementName();
		String description = javaMethod.getCompilationUnit().getElementName();
		return new Template(name, description, getClass().getName(), name, true);
	}

	private List<IMethod> getMatchingMethods(String fullClassName, String query) {
		System.out.println("getMatchingMethods " + fullClassName + " - " + query);
		List<IMethod> result = new ArrayList<IMethod>();
		IJavaProject javaProject = JavaCore.create(getProject());
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
				System.out.println("Try to match " + allMethods[i].getElementName() + " and " + query);
				IMethod method = allMethods[i];
				if (query.isEmpty() || method.getElementName().startsWith(query)) {
					result.add(allMethods[i]);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public Image getImage(Template template) {
		if (controllerImage == null) {
			controllerImage = PlayPlugin.getImageDescriptor("icons/controller.png").createImage();
		}
		return controllerImage;
	}

	@Override
	public void templates(String contentType, String ctx) {
	}

}
