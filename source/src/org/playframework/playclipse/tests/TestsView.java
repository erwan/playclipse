package org.playframework.playclipse.tests;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import com.google.gson.Gson;


public class TestsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.playframework.playclipse.views.TestsView";

	// TODO: Use the real port rather than assume it's 9000
	private String LISTURL = "http://localhost:9000/@tests?format=json";

	private TableViewer viewer;
	private Action refreshAction;
	private Action runAction;
	private Action doubleClickAction;

	class ViewContentProvider implements IStructuredContentProvider {
		private String[] elements = new String[0];
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return elements;
		}
		public void setElements(String[] elements) {
			this.elements = elements;
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public TestsView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "org.playframework.playclipse.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TestsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
		manager.add(new Separator());
		manager.add(runAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(refreshAction);
		manager.add(runAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
		manager.add(runAction);
	}

	private void makeActions() {
		refreshAction = new Action() {
			public void run() {
				getTestList();
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh the Tests List");
		refreshAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		runAction = new Action() {
			public void run() {
				showMessage("Run the tests!");
			}
		};
		runAction.setText("Run");
		runAction.setToolTipText("Run Selected Tests");
		runAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Play Tests",
			message);
	}

	private void getTestList() {
		IHttpListener listener = new IHttpListener() {
			public void onSuccess(String result, String callId) {
				buildTestList(result);
			}
			public void onError(int status, String callId) {
				showMessage("Error connecting to the server - make sure it's running.");
			}
		};
		HttpThread listCall = new HttpThread(listener, LISTURL, null);
		listCall.start();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void buildTestList(String inputJson) {
		Gson gson = new Gson();
		AllTests tests = gson.fromJson(inputJson, AllTests.class);

		List<String> unitTests = tests.getUnitTests();
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < unitTests.size(); i++) {
			names.add(unitTests.get(i));
		}

		List<String> functionalTests = tests.getFunctionalTests();
		for (int i = 0; i < functionalTests.size(); i++) {
			names.add(functionalTests.get(i));
		}

		((ViewContentProvider) viewer.getContentProvider()).setElements(names.toArray(new String[names.size()]));
		viewer.refresh();
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