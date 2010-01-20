package org.playframework.playclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.playframework.playclipse.wizards.PlayWizard;

public abstract class WizardHandler extends AbstractHandler {

	protected abstract PlayWizard getWizard();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = null;
		if (HandlerUtil.getCurrentSelection(event) instanceof IStructuredSelection) {
			selection = (IStructuredSelection)HandlerUtil.getCurrentSelection(event);
		}
		PlayWizard wizard = getWizard();
		wizard.init(HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench(), selection);
		WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		dialog.create();
		dialog.open();
		return null;
	}

}
