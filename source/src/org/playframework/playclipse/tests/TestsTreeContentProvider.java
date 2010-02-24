package org.playframework.playclipse.tests;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.playframework.playclipse.tests.Test.TestType;

class TestsTreeContentProvider implements ITreeContentProvider {

	private Test[] elements = new Test[0];
	private Test[] unitTests = new Test[0];
	private Test[] functionalTests = new Test[0];

	public TestsTreeContentProvider() {
		elements = new Test[2];
		elements[0] = new Test("Unit Tests", TestType.FOLDER);
		elements[1] = new Test("Functional Tests", TestType.FOLDER);
	}

	public void setUnitTests(Test[] names) {
		unitTests = names;
	}

	public void setFunctionalTests(Test[] names) {
		functionalTests = names;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(Object parent) {
		return elements;
	}

	public void setElements(Test[] elements) {
		this.elements = elements;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (!(parentElement instanceof Test)) {
			return null;
		}
		Test t = (Test)parentElement;
		if (t.name.equals("Unit Tests")) {
			return unitTests;
		}
		if (t.name.equals("Functional Tests")) {
			return functionalTests;
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		Test t = (Test)element;
		if (t.type != TestType.FOLDER)
			return false;
		if (t.name.equals("Unit Tests"))
			return unitTests.length > 0;
		if (t.name.equals("Functional Tests"))
			return functionalTests.length > 0;
		return false;
	}

}
