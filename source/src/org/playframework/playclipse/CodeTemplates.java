package org.playframework.playclipse;

public final class CodeTemplates {

	public static String controller(String name, String packageName) {
		StringBuilder builder = new StringBuilder();
		builder.append("package ");
		builder.append(packageName);
		builder.append(";\n\n");
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

	public static String view(String title) {
		StringBuilder builder = new StringBuilder();
		builder.append("#{extends 'main.html' /}\n");
		builder.append("#{set title:'");
		builder.append(title);
		builder.append("' /}\n\n");
		builder.append("Here goes your content.");
		return builder.toString();
	}

	public static String model(String modelName, String packageName) {
		StringBuilder builder = new StringBuilder();
		builder.append("package ");
		builder.append(packageName);
		builder.append(";\n\n");
		builder.append("import play.*;\n");
		builder.append("import play.db.jpa.*;\n\n");
		builder.append("import javax.persistence.*;\n");
		builder.append("import java.util.*;\n\n");
		builder.append("@Entity\n");
		builder.append("public class ");
		builder.append(modelName);
		builder.append(" extends Model {\n    \n}\n");
		return builder.toString();
	}

}
