package en.inlinerefactoring.metrics;

/*
 * DR - Dispersion Ratio 
 * OCIO:  统计被度量方法调用外部方法的数量
 * OCDO: 统计被度量方法调用外部方法所属类的数量
 */
import java.util.HashSet;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import swr.metrics.core.Constants;
import swr.metrics.core.Metric;
import swr.metrics.core.sources.AbstractMetricSource;

public class OutgoingCouplingIntensity {
	private MethodDeclaration methodNode = null;
	private String methodPath = null;
	private int calleeNum = 0;
	
	private HashSet<String> packagesName = null;

	private HashSet<String> set = null;
	private HashSet<String> methodSet = null;
	public OutgoingCouplingIntensity() {
		//super(OCIO);
		// TODO Auto-generated constructor stub
	}

	public double calculate(MethodDeclaration methodDeclaration) {
		// TODO Auto-generated method stub
		//if (source.getLevel() != METHOD) {
			//throw new InvalidSourceException("OCIO only applicable to methods");
		//}
		
		// TODO Auto-generated method stub
			
//	    double totalFields = 0;
		if(packagesName == null){
			packagesName = new HashSet<String>();
			this.getPackages();
		}
		calleeNum = 0;

		set = new HashSet<String>();
		methodSet = new HashSet<String>();
		methodNode = methodDeclaration;
		if(methodNode != null){
			IMethodBinding methodBinding = methodNode.resolveBinding();
			if(methodBinding != null){
				ITypeBinding typeBinding = methodBinding.getDeclaringClass();
				if(typeBinding != null){
					methodPath = typeBinding.getQualifiedName();//path
				}
			}
		}
		if(methodPath == null){
			return 0.0;
		}
//		System.out.println("Method:" + methodPath + "." + methodNode.getName());
//			IField[] fields = type.getFields();
		//		    totalFields =fields.length;
		MethodFieldVisitor v = new MethodFieldVisitor();
		methodNode.accept(v);
		calleeNum = methodSet.size();
		double dr=0.0;
		if(calleeNum!=0){

			dr=(double)set.size()/(double)calleeNum;
		}
		return dr;
		/*System.out.println("----zjj-----methodNode---"+ methodNode.getName());
		System.out.println("----zjj-----calleeNum---"+ calleeNum);
		System.out.println("----zjj-----set.size()---"+ set.size());
		System.out.println("----zjj-----dr---"+ dr);*/
		//source.setValue(new Metric(OCIO, calleeNum));
		//source.setValue(new Metric(OCDO, set.size()));
		//source.setValue(new Metric(DR, dr));
	}
	
	private void getPackages(){
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			if (workspace == null) {
				return;
			}
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IJavaModel jmodel = JavaCore.create(root);
			if (jmodel == null) {
				return;
			}
			try {
				IJavaProject[] projects = jmodel.getJavaProjects();
				for (int j = 0; j < projects.length; j++) {
	
					IPackageFragment[] packageFragments = projects[j].getPackageFragments();
	
					for (IPackageFragment packageFragment : packageFragments) {
						if (packageFragment.getKind() != IPackageFragmentRoot.K_SOURCE)
							continue;
						if (!packageFragment.containsJavaResources())
							continue;
						//System.out.println(packageFragment.getElementName());
						packagesName.add(packageFragment.getElementName());
					}
				}
			}
			catch (Exception e1) {
				System.out.println(e1);
			}
			} catch (Exception e1) {
				System.out.println(e1);
			}
	}
	
	private class MethodFieldVisitor extends ASTVisitor {
		public boolean visit(ClassInstanceCreation node){
			IMethodBinding methodBinding = node.resolveConstructorBinding();
			if(methodBinding != null){
				//System.out.println("luohui test " + methodBinding.getName());
				IMethodBinding binding = methodBinding.getMethodDeclaration();
				if(binding != null){
					ITypeBinding typeBinding = binding.getDeclaringClass();
					if(typeBinding != null){
						String path = typeBinding.getQualifiedName();//path
						
						for(String name: packagesName){
							if( path.startsWith(name) ){
//								System.out.println("luohui test " + path);
								if(!path.equals(methodPath)){
			//						System.out.println("Method:" + methodPath + "." + methodNode.getName() + " " + "callee:" + path + "." + node.getName()); 
									methodSet.add(path + "." + methodBinding.getName());
									//calleeNum++;
									set.add(path);
								}
								return true;
							}
						}
					}	
				}
			}			
			return true;	
		}
		
		public boolean visit(MethodInvocation  node){

			IMethodBinding methodBinding = node.resolveMethodBinding();
			if(methodBinding != null){
				IMethodBinding binding = methodBinding.getMethodDeclaration();
			
				if(binding != null){
					ITypeBinding typeBinding = binding.getDeclaringClass();
					if(typeBinding != null){
						String path = typeBinding.getQualifiedName();//path
						
						for(String name: packagesName){
							if( path.startsWith(name) ){
								
//								System.out.println("luohui test " + path);
								if(!path.equals(methodPath)){
//									System.out.println("Method:" + methodPath + "." + methodNode.getName() + " " + "callee:" + path + "." + node.getName()); 
									methodSet.add(path + "." + methodBinding.getName());
									//calleeNum++;
									set.add(path);						
								}
								
								return true;
							}
						}	
					}
				}
			}
			return true;
		}
	}	
}
