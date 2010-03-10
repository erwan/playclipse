package org.playframework.playclipse.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

public class TestsHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public TestsHandler() {
	}

	/**
	 * the command has been executed, so let's extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWebBrowser browser;
		try {
			browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser("testbrowser");
			browser.openURL(new URL("http://localhost:9000/@tests?select=all&auto=yes"));
		} catch (PartInitException e) {
		} catch (MalformedURLException e) {
		}
		return null;
	}
}
