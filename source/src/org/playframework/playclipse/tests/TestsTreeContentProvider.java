package org.playframework.playclipse.tests;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class TestsTreeContentProvider implements ITreeContentProvider {

	private String[] elements = new String[0];
	private String[] unitTests = new String[0];
	private String[] functionalTests = new String[0];

	public TestsTreeContentProvider() {
		elements = new String[2];
		elements[0] = "Unit Tests";
		elements[1] = "Functional Tests";
	}

	public void setUnitTests(String[] names) {
		unitTests = names;
	}

	public void setFunctionalTests(String[] names) {
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

	public void setElements(String[] elements) {
		this.elements = elements;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (!(parentElement instanceof String)) {
			return null;
		}
		String p = (String)parentElement;
		if (p.equals("Unit Tests")) {
			return unitTests;
		}
		if (p.equals("Functional Tests")) {
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
		String p = (String)element;
		return (p.equals("Unit Tests") || p.equals("Functional Tests"));
	}

}
