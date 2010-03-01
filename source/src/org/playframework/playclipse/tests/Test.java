package org.playframework.playclipse.tests;

import org.eclipse.swt.graphics.Image;
import org.playframework.playclipse.PlayPlugin;

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

}
