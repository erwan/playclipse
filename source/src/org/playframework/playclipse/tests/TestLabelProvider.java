package org.playframework.playclipse.tests;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.playframework.playclipse.PlayPlugin;

class TestLabelProvider extends LabelProvider implements ITableLabelProvider {

	private Image junit = PlayPlugin.getImageDescriptor("icons/tests/junit.gif").createImage();

	public String getColumnText(Object obj, int index) {
		return getText(obj);
	}
	public Image getColumnImage(Object obj, int index) {
		return getImage(obj);
	}
	public Image getImage(Object obj) {
		return junit;
	}
}
