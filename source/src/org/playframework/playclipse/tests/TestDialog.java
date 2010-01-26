package org.playframework.playclipse.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.google.gson.Gson;

public class TestDialog extends Dialog {

	public Label message;

	private String LISTURL = "http://localhost:9000/@tests?format=json";

	public TestDialog(Shell parentShell) {
		super(parentShell);
		getTestList();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(500, 500);
		super.configureShell(newShell);
	}

	@Override
	public void create() {
		super.create();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = (Composite) super.createDialogArea(parent);
		Label label = new Label(c, SWT.BOLD);
		label.setText("Play Tests");

		return c;
	}

	@SuppressWarnings("unchecked")
	private void buildTestList(String inputJson) {
		System.out.println("buildTestList");
		Gson gson = new Gson();
		Map<String, List<String>> tests = new HashMap<String, List<String>>();
		tests = gson.fromJson(inputJson, tests.getClass());
		System.out.println(tests);
		// Control c = getContents();
	}

	private void getTestList() {
		IHttpListener listener = new IHttpListener() {
			public void onSuccess(String result, String callId) {
				buildTestList(result);
			}
			public void onError(int status, String callId) {
				// TODO
				System.out.println("http Error!");
			}
		};
		HttpThread listCall = new HttpThread(listener, LISTURL, null);
		listCall.start();
	}

}
