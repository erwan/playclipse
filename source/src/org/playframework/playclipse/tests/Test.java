package org.playframework.playclipse.tests;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.graphics.Image;
import org.playframework.playclipse.PlayPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Test {

	public enum TestType {
		FOLDER,
		UNIT,
		FUNCTIONAL,
		SELENIUM
	}

	public enum TestResult {
		NOTRUN("test.gif"),
		SUCCESS("testok.gif"),
		FAILURE("testfail.gif"),
		ERROR("testerr.gif"),
		LOADING("testrun.gif");
		private final Image image;
		private TestResult(String imagePath) {
			image = PlayPlugin.getImageDescriptor("icons/tests/" + imagePath).createImage();
		}
		public Image image() {
			return image;
		}
	}

	public String name;
	public TestType type;
	public TestResult result;

	public Test(String name, TestType type) {
		this.name = name;
		this.type = type;
		this.result = TestResult.NOTRUN;
	}

	public Image getIcon() {
		return result.image();
	}

	public String toString() {
		return name;
	}

	public void start(final ITestListener listener) {
		System.out.println("start the thingy");
		result = TestResult.LOADING;
		IHttpListener httpListener = new IHttpListener() {
			public void onSuccess(String xmlresult, String callId) {
				System.out.println(result);
				Document doc = null;
				try {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder db;
					db = factory.newDocumentBuilder();
					InputSource inStream = new InputSource();

					inStream.setCharacterStream(new StringReader(xmlresult));
					doc = db.parse(inStream);
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(0, callId);
					}
					return;
				}
				Node results = doc.getElementsByTagName("results").item(0);
				NamedNodeMap attr = results.getAttributes();
				System.out.println("Result for " + name + " " + attr.getNamedItem("passed"));
				if (attr.getNamedItem("passed").getNodeValue().equals("1")) {
					result = TestResult.SUCCESS;
				} else {
					result = TestResult.FAILURE;
				}
				if (listener != null) {
					listener.onSuccess("success", callId);
				}
			}
			public void onError(int status, String callId) {
				// TODO
				System.out.println(name + " http Error "+status+"!");
				if (listener != null) {
					listener.onError(status, callId);
				}
			}
		};
		HttpThread testCall = new HttpThread(httpListener, getUrl(), null);
		testCall.start();
	}

	private String getUrl() {
		return "http://localhost:9000/@tests/" + name + ".class?format=xml";
	}

}
