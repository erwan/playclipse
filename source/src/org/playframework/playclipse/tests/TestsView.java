package org.playframework.playclipse.tests;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.playframework.playclipse.PlayPlugin;
import org.playframework.playclipse.tests.Test.TestType;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class TestsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.playframework.playclipse.views.TestsView";

	public static final ImageDescriptor RUN_ICON =  PlayPlugin.getImageDescriptor("icons/tests/RunIcon.png");
	public static final ImageDescriptor REFRESH_ICON =  PlayPlugin.getImageDescriptor("icons/tests/refresh.gif");

	// TODO: Use the real port rather than assume it's 9000
	private String LISTURL = "http://localhost:9000/@tests?format=xml";

	private TreeViewer viewer;
	private TestsTreeContentProvider testsTree;
	private Action refreshAction;
	private Action runAction;
	private Action doubleClickAction;

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
		testsTree = new TestsTreeContentProvider();
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(testsTree);
		viewer.setLabelProvider(new TestLabelProvider());
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
		refreshAction.setImageDescriptor(REFRESH_ICON);

		runAction = new Action() {
			public void run() {
				showMessage("Run the tests!");
			}
		};
		runAction.setText("Run");
		runAction.setToolTipText("Run Selected Tests");
		runAction.setImageDescriptor(RUN_ICON);
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
				try {
					buildTestList(result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

	private void buildTestList(String input) throws SAXException, IOException, ParserConfigurationException {
		Document tests = parseXML(input);

		List<Test> uTests = new ArrayList<Test>();
		List<Test> fTests = new ArrayList<Test>();
		NodeList testElts = tests.getElementsByTagName("test");
		for (int i = 0; i < testElts.getLength(); i++) {
			Node testElt = testElts.item(i);
			NamedNodeMap attr = testElt.getAttributes();
			String type = attr.getNamedItem("type").getTextContent();
			if (type.equals("unit")) {
				uTests.add(new Test(testElt.getTextContent(), TestType.UNIT));
			} else if (type.equals("functional")) {
				fTests.add(new Test(testElt.getTextContent(), TestType.FUNCTIONAL));
			}
		}
		testsTree.setUnitTests(uTests.toArray(new Test[uTests.size()]));
		testsTree.setFunctionalTests(fTests.toArray(new Test[fTests.size()]));

		viewer.refresh();
	}

	private static Document parseXML(String input) throws SAXException, IOException, ParserConfigurationException {
		javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder db = factory.newDocumentBuilder();
		org.xml.sax.InputSource inStream = new org.xml.sax.InputSource();

		inStream.setCharacterStream(new java.io.StringReader(input));
		return db.parse(inStream);
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