package org.playframework.playclipse.wizards;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.playframework.playclipse.CodeTemplates;

public class ViewWizard extends PlayWizard {

	@Override
	public void addPages() {
		page = new ViewWizardPage(selection);
		addPage(page);
	}

	@Override
	protected IFile getTargetFile(IContainer container, String name) {
		return container.getFile(new Path(name));
	}

	@Override
	protected String getContent(Map<String, String> parameters) {
		return CodeTemplates.view(parameters.get("name"));
	}

}
