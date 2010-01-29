package org.playframework.playclipse.tests;

import org.playframework.playclipse.PlayPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

class TestControl extends Composite {

	public enum TestResult {
		NOTRUN("norun.png"),
		SUCCESS("success.png"),
		FAILURE("failure.png"),
		LOADING("loading.png");
		private final Image image;
		private TestResult(String imagePath) {
			image = PlayPlugin.getImageDescriptor("icons/tests/" + imagePath).createImage();
		}
		public Image image() {
			return image;
		}
	}

	private Button check;
	private Label icon;
	private String testName;
	private TestResult result;

	public TestControl(Composite parent, String testName) {
		super(parent, SWT.NONE);
		this.testName = testName;
		icon = new Label(this, SWT.NO_REDRAW_RESIZE);
		check = new Button(this, SWT.CHECK);
		check.setText(testName);
		setResult(TestResult.NOTRUN);
		setLayout(new RowLayout(SWT.HORIZONTAL));
	}

	public void setResult(TestResult result) {
		this.result = result;
		icon.setImage(result.image());
		redraw();
	}

	public TestResult getResult() {
		return result;
	}

	public String getTestName() {
		return testName;
	}

	public boolean isChecked() {
		return check.getSelection();
	}

}
