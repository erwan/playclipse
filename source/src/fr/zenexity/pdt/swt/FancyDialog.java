package fr.zenexity.pdt.swt;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.playframework.playclipse.PlayPlugin;

public class FancyDialog extends Dialog {

	private Composite outer;
	protected Composite pageComposite;
	private Image logo;

	public FancyDialog(Shell parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		outer = (Composite) super.createDialogArea(parent);
		RowLayout outerLayout = new RowLayout(SWT.VERTICAL);
		outerLayout.marginHeight = -3;
		outerLayout.marginWidth = -3;
		outer.setLayout(outerLayout);

		Label label = new Label(outer, SWT.NONE);
		logo = PlayPlugin.getImageDescriptor("icons/tests.png").createImage();
		label.setImage(logo);
		label.setBackground(new Color(Display.getCurrent(), 174, 174, 174));
		label.setSize(getShell().getSize().x, 64);

		pageComposite = new Composite(outer, SWT.NONE);
		// RowLayout layout = new RowLayout(0);
		pageComposite.setLayout(new RowLayout(SWT.VERTICAL));

		return pageComposite;
	}

	public void refresh() {
		outer.layout();
		pageComposite.layout();
	}

}
