package org.playframework.playclipse.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class PlayBuilder extends IncrementalProjectBuilder {

	class ResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			System.out.println("Visit: " + resource.getName());
			if (resource instanceof IFile && resource.getName().equals("routes")) {
				checkRoute((IFile)resource);
				System.out.println("end reading routes");
				return false;
			} else  return true;
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
		deleteMarkers(file);
		(new RouteChecker(file)).check();
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

}
