package org.playframework.playclipse.tests;

public class Test {

	public enum TestType {
		FOLDER,
		UNIT,
		FUNCTIONAL,
		SELENIUM
	}

	public Test(String name, TestType type) {
		this.name = name;
		this.type = type;
	}

	public String name;

	public TestType type;

	public String toString() {
		return name;
	}

}
