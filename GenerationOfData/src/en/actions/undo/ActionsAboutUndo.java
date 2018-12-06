package en.actions.undo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.UndoActionHandler;

import en.actionsofproject.RelatedClass;

public class ActionsAboutUndo {

	
	public void undo(){
		
		IUndoContext context= (IUndoContext)ResourcesPlugin.getWorkspace().getAdapter(IUndoContext.class);
		
		//IWorkbenchPartSite workbenchpartSite = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getSite();
		
		//IOperationHistory operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
		IWorkbenchPartSite workbenchpartSite = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
		
		IOperationHistory operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
		UndoActionHandler undoAction = new UndoActionHandler(workbenchpartSite, context);
		 DefaultOperationHistory defaultOperationHistory = (DefaultOperationHistory)operationHistory;
		
		IUndoableOperation[] operationss = operationHistory
				.getUndoHistory(IOperationHistory.GLOBAL_UNDO_CONTEXT); 
		 
		if(operationss.length > 0)
			if(operationss[operationss.length - 1].canUndo()){
				System.out.println("undo once-----------");
				try {
					operationHistory.undoOperation(operationss[operationss.length - 1], null, undoAction);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		 
	}
	public boolean calculateCompilationErrorMarkers(IJavaProject iJavaProject, IProgressMonitor pm){//judging whether have compilation error
	    ArrayList <IMarker> result = new ArrayList <IMarker>();
	    //IMarker[] markers = null;
	    
	    try {
	    	IProject project = iJavaProject.getProject();
	    	project.refreshLocal(IResource.DEPTH_INFINITE, pm);	
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, pm);
			IMarker[] markers = null;
			markers = project.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
			for (IMarker marker: markers) {
				Integer severityType = (Integer) marker.getAttribute(IMarker.SEVERITY);
				if (severityType.intValue() == IMarker.SEVERITY_ERROR) {
					result.add(marker);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println("markers number------"+result.size()+"------"+result.isEmpty());
	    if(!result.isEmpty())
	    	return true;
	    else 
	    	return false;
	}
	public boolean calculateCompilationErrorMarkers(IProject project, IProgressMonitor pm){//judging whether have compilation error
	    ArrayList <IMarker> result = new ArrayList <IMarker>();
	    //IMarker[] markers = null;
	    
	    try {
	    	//IProject project = iJavaProject.getProject();
	    	project.refreshLocal(IResource.DEPTH_INFINITE, pm);	
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, pm);
			IMarker[] markers = null;
			markers = project.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
			for (IMarker marker: markers) {
				Integer severityType = (Integer) marker.getAttribute(IMarker.SEVERITY);
				if (severityType.intValue() == IMarker.SEVERITY_ERROR) {
					result.add(marker);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println("markers number------"+result.size()+"------"+result.isEmpty());
	    if(!result.isEmpty())
	    	return true;
	    else 
	    	return false;
	}
	private List<IMarker> buildProject(IJavaProject iJavaProject, IProgressMonitor pm) {
		ArrayList<IMarker> result = new ArrayList<IMarker>();
		try {
			IProject project = iJavaProject.getProject();
			project.refreshLocal(IResource.DEPTH_INFINITE, pm);	
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, pm);
			IMarker[] markers = null;
			markers = project.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
			for (IMarker marker: markers) {
				Integer severityType = (Integer) marker.getAttribute(IMarker.SEVERITY);
				if (severityType.intValue() == IMarker.SEVERITY_ERROR) {
					result.add(marker);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return result;
	}
}
