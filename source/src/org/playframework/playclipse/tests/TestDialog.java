package org.playframework.playclipse.tests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.playframework.playclipse.tests.TestControl.TestResult;

import com.google.gson.Gson;

import fr.zenexity.pdt.swt.FancyDialog;

public class TestDialog extends FancyDialog {

	private String LISTURL = "http://localhost:9000/@tests?format=json";
	private Button start;
	private List<TestControl> testControls;

	public TestDialog(Shell parentShell) {
		super(parentShell, "icons/tests.png");
		getTestList();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(300, 300);
		super.configureShell(newShell);
	}

	private void buildTestList(String inputJson) {
		Gson gson = new Gson();
		AllTests tests = gson.fromJson(inputJson, AllTests.class);

		testControls = new ArrayList<TestControl>();
		List<String> unitTests = tests.getUnitTests();
		(new Label(pageComposite, SWT.NONE)).setText("Unit tests");
		for (int i = 0; i < unitTests.size(); i++) {
			testControls.add(new TestControl(pageComposite, unitTests.get(i)));
		}

		List<String> functionalTests = tests.getFunctionalTests();
		(new Label(pageComposite, SWT.NONE)).setText("Functional tests");
		for (int i = 0; i < functionalTests.size(); i++) {
			testControls.add(new TestControl(pageComposite, functionalTests.get(i)));
		}

		// TODO: Make sure this is grayed when no test is selected
		start = new Button(pageComposite, SWT.NONE);
		start.setText("Start tests");
		start.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				startTests();
			}
			public void widgetDefaultSelected(SelectionEvent event) {}
		});
		refresh();
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

	private void startTests() {
		System.out.println("START TESTS!!");
		for (int i = 0; i < testControls.size(); i++) {
			TestControl test = testControls.get(i);
			System.out.println(test.getTestName() + ": " + test.isChecked());
			if (test.isChecked()) {
				startTest(test);
			}
		}
	}

	private void startTest(final TestControl test) {
		System.out.println("start the thingy");
		test.setResult(TestResult.LOADING);
		IHttpListener listener = new IHttpListener() {
			public void onSuccess(String result, String callId) {
				System.out.println(result);
				Gson gson = new Gson();
				GlobalTestResult results = gson.fromJson(result, GlobalTestResult.class);
				if (results.isPassed()) {
					test.setResult(TestResult.SUCCESS);
				} else {
					test.setResult(TestResult.FAILURE);
				}
				refresh();
			}
			public void onError(int status, String callId) {
				// TODO
				System.out.println("http Error "+status+"!");
				test.setResult(TestResult.FAILURE);
			}
		};
		HttpThread testCall = new HttpThread(listener, getTestUrl(test.getTestName() + ".class"), null);
		testCall.start();
	}

	private String getTestUrl(String testName) {
		return "http://localhost:9000/@tests/" + testName + "?format=json";
	}

	// Classes for Gson deserialization

	static class AllTests {
		public AllTests() {}

		private List<String> unitTests;
		private List<String> functionalTests;
		private List<String> seleniumTests;

		public List<String> getUnitTests() {
			return unitTests;
		}
		public List<String> getFunctionalTests() {
			return functionalTests;
		}
		public List<String> getSeleniumTests() {
			return seleniumTests;
		}

		public String toString() {
			return "unitTests: " + unitTests + "; "
				  + "functionalTests: " + functionalTests + "; "
				  + "seleniumTests: " + seleniumTests + "; ";
		}
	}

	// {"results":{"results":[{"name":"aVeryImportantThingToTest","passed":true,"time":2,"sourceLine":0}],"passed":true},"test":"BasicTest.class"}

	static class SingleResult {
		public SingleResult() {}
		private String name;
		private boolean passed;
		private int time;
		private int sourceLine;

		public String getName() {
			return name;
		}
		public boolean isPassed() {
			return passed;
		}
		public int getTime() {
			return time;
		}
		public int getSourceLine() {
			return sourceLine;
		}
	}

	static class GlobalTestResult {
		private List<SingleResult> results;
		private boolean passed;

		public List<SingleResult> getResults() {
			return results;
		}
		public boolean isPassed() {
			return passed;
		}
	}

}
