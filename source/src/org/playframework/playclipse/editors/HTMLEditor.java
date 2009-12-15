package org.playframework.playclipse.editors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.graphics.RGB;

public class HTMLEditor extends Editor {
	
	public String[] getTypes() {
		return new String[] {"default", "doctype", "html", "string", "tag", "expression", "action", "skipped", "keyword"};
	}
	
	public TextAttribute getStyle(String type) {
		if(type.equals("doctype")) {
			return style(new RGB(127, 127, 127));
		}
		if(type.equals("html")) {
			return style(new RGB(58, 147, 18));
		}
		if(type.equals("string")) {
			return style(new RGB(5, 152, 220));
		}
		if(type.equals("tag")) {
			return style(new RGB(129, 0, 153));
		}
		if(type.equals("expression")) {
			return style(new RGB(255, 144, 0));
		}
		if(type.equals("action")) {
			return style(new RGB(255, 0, 192));
		}
		if(type.equals("skipped")) {
			return style(new RGB(90, 90, 90));
		}
		if(type.equals("keyword")) {
			return style(new RGB(255, 0, 0));
		}
		return style(new RGB(0, 0, 0));
	}
	
	// Auto-close
	
	public String autoClose(char pc, char c, char nc) {
		if(c == '<') {
			return ">";
		}
		if(c == '>' && nc == '>') {
			return SKIP;
		}
		if(c == '{') {
			return "}";
		}
		if(c == '}' && nc == '}') {
			return SKIP;
		}
		if(c == '(') {
			return ")";
		}
		if(c == ')' && nc == ')') {
			return SKIP;
		}
		if(c == '[') {
			return "]";
		}
		if(c == ']' && nc == ']') {
			return SKIP;
		}
		if(c == '\'') {
			if(nc == '\'') {
				return SKIP;
			}
			return "\'";
		}
		if(c == '\"') {
			if(nc == '\"') {
				return SKIP;
			}
			return "\"";
		}
		return null;
	};
	
	// Template
	
	public void templates(String contentType, String ctx) {		
		if(contentType == "default" || contentType == "html" || contentType == "string") {
			template("$", "Insert dynamic expression", "$${${}}${cursor}");
			template("tag", "Insert tag without body", "#{${name} ${}/}${cursor}");
			template("action", "Insert action", "@{${}}${cursor}");
			template("tag", "Insert tag with body", "##{${name} ${}}${cursor}#{/${name}}");
		}
		if(contentType == "default") {
			template("if", "Insert a #if tag", "#{if ${}}\n    ${cursor}\n#{/if}");
			template("extends", "Insert a #extends tag", "#{extends '${}' /}${cursor}");
			template("list", "Insert a #list tag", "#{list ${}, as:'${i}'}\n    ${cursor}\n#{/list>");
			template("doctype", "Insert an HTML5 doctype element", "<!DOCTYPE html>");
		}
		// Magic
		Matcher isTag = Pattern.compile("<([a-zA-Z]+)>").matcher(ctx);
		if(isTag.matches()) {
			String closeTag = "</" + isTag.group(1) + ">";
			template(ctx, "Close the " + ctx + " HTML tag", "${cursor}"+closeTag);
		}
	}
	
	// Hyperlink
	
	Pattern extend_s = Pattern.compile("#\\{extends\\s+'([^']+)'");
	Pattern include = Pattern.compile("#\\{include\\s+'([^']+)'");
	Pattern action = Pattern.compile("@\\{([^}]+)\\}");
	Pattern action_in_tag = Pattern.compile("#\\{.+(@.+[)])");
	Pattern tag = Pattern.compile("#\\{([-a-zA-Z0-9.]+) ");
	
	public IHyperlink detectHyperlink(ITextViewer textViewer, IRegion region) {
		BestMatch match = findBestMatch(region.getOffset(), include, extend_s, action, action_in_tag, tag);		
		if(match != null) {
			if(match.is(action)) {
				return match.hyperlink("action", 0, 0);
			}
			if(match.is(tag)) {
				return match.hyperlink("tag", 2, -1);
			}
			if(match.is(extend_s)) {
				return match.hyperlink("extends", match.matcher.start(1) - match.matcher.start(), -1);
			}
			if(match.is(include)) {
				return match.hyperlink("include", match.matcher.start(1) - match.matcher.start(), -1);
			}
			if(match.is(action_in_tag)) {
				return match.hyperlink("action_in_tag", match.matcher.start(1) - match.matcher.start(), 0);
			}	
		}
		return null;
	}
	
	@Override
	public void openLink(IHyperlink link) {
	    getNav().openLink(link);
		// System.out.println("TODO; OPEN "+link);
	}
	
	// Scanner
	
	boolean consumeString = false;
	char openedString = ' ';
	String oldState = "default";
	String oldStringState = "default";
	
	@Override
	protected void reset() {
		super.reset();
		consumeString = false;
		oldState = "default";
	}
	
	@Override
	public String scan() {
		if(isNext("*{") && state != "skipped") {
			oldState = state;
			return found("skipped", 0);
		}
		if(state == "skipped") {
			if(isNext("}*")) {
				return found(oldState, 2);
			}
		}
		if(state == "default" || state == "html" || state == "string") {
			if(isNext("#{")) {
				oldState = state;
				return found("tag", 0);
			}
			if(isNext("${")) {
				oldState = state;
				return found("expression", 0);
			}
			if(isNext("@{")) {
				oldState = state;
				return found("action", 0);
			}
		}
		if(state == "tag" || state == "expression" || state == "action") {
			if(isNext("}")) {
				return found(oldState, 1);
			}
		}
		if(state == "default") {
			if(isNext("<!DOCTYPE")) {
				return found("doctype", 0);
			}
			if(isNext("<")) {
				return found("html", 0);
			}
			if(isNext("var ")) {
				return found("keyword", 0);
			}
			if(isNext("def ")) {
				return found("keyword", 0);
			}
			if(isNext("return ")) {
				return found("keyword", 0);
			}
			if(isNext("function(")) {
				return found("keyword", 0);
			}
			if(isNext("function ")) {
				return found("keyword", 0);
			}
			if(isNext("if(")) {
				return found("keyword", 0);
			}
			if(isNext("if ")) {
				return found("keyword", 0);
			}
			if(isNext("else ")) {
				return found("keyword", 0);
			}
			if(isNext("switch(")) {
				return found("keyword", 0);
			}
			if(isNext("switch ")) {
				return found("keyword", 0);
			}
		}
		if(state == "keyword") {
			if(isNext(" ") || isNext("(")) {
				return found("default", 0);
			}
		}
		if(state == "doctype" || state == "html") {
			if(isNext(">")) {
				return found("default", 1);
			}
		}
		if(state == "html" || state == "default") {
			if(isNext("\"")) {
				openedString = '\"';
				consumeString = false;
				oldStringState = state;
				return found("string", 0);
			}
			if(isNext("'")) {
				openedString = '\'';
				consumeString = false;
				oldStringState = state;
				return found("string", 0);
			}
		}
		if(state == "string") {
			if(isNext(""+openedString) && consumeString) {
				return found(oldStringState, 1);
			}
			consumeString = true;
		}
		return null;
	}

}
