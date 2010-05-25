package org.playframework.playclipse.builder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.playframework.playclipse.PlayPlugin;
import org.playframework.playclipse.editors.html.HTMLEditor;
import org.playframework.playclipse.editors.route.RouteEditor;

public class PlayBuilder extends IncrementalProjectBuilder implements IPropertyChangeListener {

	public PlayBuilder() {
		super();
		PlayPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	class ResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			if (!(resource instanceof IFile)) return true;
			IFile file = (IFile)resource;
			if (resource.getName().equals("routes")) {
				deleteMarkers(file);
				(new RouteChecker(file, RouteEditor.MISSING_ROUTE)).check();
				return false;
			}
			if (TemplateChecker.isTemplate(resource.getFullPath())) {
				deleteMarkers(file);
				(new TemplateChecker(file, HTMLEditor.MISSING_ACTION)).check();
				return false;
			}
			return true;
		}
	}

	public static final String BUILDER_ID = "org.playframework.playclipse.PlayBuilder";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		// TODO: Make incremental
		fullBuild(monitor);
		return null;
	}

	void checkRoute(IFile file) {
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		System.out.println("Play full build...");
		try {
			getProject().accept(new ResourceVisitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private static Set<String> observedProperties = new HashSet<String>();
	static {
		observedProperties.add(RouteEditor.MISSING_ROUTE);
		observedProperties.add(HTMLEditor.MISSING_ACTION);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		for (String property: observedProperties) {
			if (event.getProperty().equals(property)) {
				try {
					fullBuild(null);
				} catch (CoreException e) {}
				return;
			}
		}
	}

}
