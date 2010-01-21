package fr.zenexity.pdt.editors;

import org.eclipse.jface.text.templates.TemplateContextType;

public class ContextType extends TemplateContextType {

	public ContextType() {
		addGlobalResolvers();
	}

	public ContextType(String id) {
		super(id);
		addGlobalResolvers();
	}

	public ContextType(String id, String name) {
		super(id, name);
		addGlobalResolvers();
	}

	private void addGlobalResolvers() {
		addResolver(new org.eclipse.jface.text.templates.GlobalTemplateVariables.Cursor());
		addResolver(new org.eclipse.jface.text.templates.GlobalTemplateVariables.WordSelection());
		addResolver(new org.eclipse.jface.text.templates.GlobalTemplateVariables.LineSelection());
		addResolver(new org.eclipse.jface.text.templates.GlobalTemplateVariables.Dollar());
		addResolver(new org.eclipse.jface.text.templates.GlobalTemplateVariables.Date());
		addResolver(new org.eclipse.jface.text.templates.GlobalTemplateVariables.Year());
		addResolver(new org.eclipse.jface.text.templates.GlobalTemplateVariables.Time());
		addResolver(new org.eclipse.jface.text.templates.GlobalTemplateVariables.User());
	}

}
