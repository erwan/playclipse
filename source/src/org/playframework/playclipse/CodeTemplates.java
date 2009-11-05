package org.playframework.playclipse;

public final class CodeTemplates {

	public static String controller(String name) {
		StringBuilder builder = new StringBuilder();
		builder.append("package controllers;\n\n");
		builder.append("import play.mvc.*;\n\n");
		builder.append("public class ");
		builder.append(name);
		builder.append(" extends Controller {\n\n");
		builder.append("    public static void index() {\n");
		builder.append("        render();\n");
		builder.append("    }\n\n");
		builder.append("}\n");
		return builder.toString();
	}

}
