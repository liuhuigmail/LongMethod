package en.inlinerefactoring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;

public class VisitorForMethod extends ASTVisitor {

	//public Set<String> methodInvocationNames = new HashSet<>();
	List<String> srcPackages = new ArrayList<String>();
	public Set<IMethod> methodAppeared = new HashSet<>();
	public List<IMethod> iMethods = new ArrayList<>();
	public List<MethodInvocation> methodInvocationsCanBeInlined = new ArrayList<MethodInvocation>();
	MethodDeclaration methodDeclaration;
	IJavaProject iJavaProject;
	
	VisitorForMethod(MethodDeclaration methodDeclaration, List<String> srcPackages){
		this.methodDeclaration = methodDeclaration;
		this.srcPackages = srcPackages;
		init();
	}
	void init(){
		//methodInvocationNames.clear();
		methodAppeared.clear();
		iMethods.clear();
	}
	public boolean visit(MethodDeclaration node){
		if(node.isConstructor() || (node.getModifiers() & Modifier.ABSTRACT) != 0 )
			return false;
		if(node.equals(methodDeclaration)){
			//System.out.println("visit methodDeclaration name--------"+methodDeclaration.getName());
			return true;
		}else{
			return false;
		}
		
	}

	public boolean visit(QualifiedName node){
		return true;
	}
	
	public boolean visit(EnumDeclaration node){
		return false;
	}
	
	public boolean visit(EnumConstantDeclaration node){
		return false;
	}
	
	public boolean visit(FieldAccess node){
		return false;
	}
	public boolean visit(FieldDeclaration node){

		return false;
	}


	public boolean visit(MethodInvocation node){
		IMethodBinding imethodBinding = node.resolveMethodBinding();
		//System.out.println("methodInvocation name-------------"+node.getName());
		ITypeBinding iTypeBinding = node.resolveMethodBinding().getDeclaringClass();
		
		if((iTypeBinding.getModifiers()& Modifier.ABSTRACT)!=0)
			return false;
		if(imethodBinding.isConstructor() || (imethodBinding.getModifiers() & Modifier.ABSTRACT) != 0)
			return false;
		if(isGetterOrSetter(node))
			return false;
		
		IMethod method = (IMethod)node.resolveMethodBinding().getJavaElement();

		if(!iMethods.contains(method)&&!methodAppeared.contains(method)){
//			System.out.println("Class Name -----: "+node.resolveMethodBinding().getDeclaringClass().getQualifiedName());
//			System.out.println("Method Name -----: "+node.getName());
			if(SystemClassDetection(node)){
				methodInvocationsCanBeInlined.add(node);
				iMethods.add(method);
			}
			
		}else{
			iMethods.remove(method);
			if(SystemClassDetection(node)){
				methodInvocationsCanBeInlined.add(node);
				//methodInvocationNames.remove(MethodInvocationName);
				methodAppeared.add(method);
			}
		}
//		for(IMethod method1 : iMethods){
//			System.out.println("method name------------"+method1.getElementName());
//		}
		return true;
	}
	public  boolean isGetterOrSetter(MethodInvocation node){
		IMethodBinding binding = node.resolveMethodBinding();
		String methodName = binding.getName();
	
		if(methodName.length() <= 3)
			return false;
		if(methodName.startsWith("get")){
			String targetField = methodName.substring(3);
			IVariableBinding[] fields = node.resolveMethodBinding().getDeclaringClass().getDeclaredFields();
			for(IVariableBinding field : fields){
				if(field.getName().equalsIgnoreCase(targetField) && field.getType().equals(node.resolveMethodBinding().getReturnType()))
				{
					return true;
				}
			}
		}
		if(methodName.startsWith("set")){
			String targetField = methodName.substring(3);
			IVariableBinding[] fields = node.resolveMethodBinding().getDeclaringClass().getDeclaredFields();
			for(IVariableBinding field : fields)
				if(field.getName().equalsIgnoreCase(targetField)){
					ITypeBinding[] parameterTypes = node.resolveMethodBinding().getParameterTypes();
					if(parameterTypes.length == 1 && field.getType().equals(parameterTypes[0]))
					{
						return true;
					}
				}
		}
		return false;
	}
	public boolean SystemClassDetection(MethodInvocation node){
		//iJavaProject.getAllPackageFragmentRoots()
		SimpleName methodName = node.getName();
		String classQualifiedName = node.resolveMethodBinding().getDeclaringClass().getQualifiedName();
		String className = node.resolveMethodBinding().getDeclaringClass().getName(); 
		//System.out.println("methodName------"+methodName);
		//System.out.println("classQualifiedName----**********---"+classQualifiedName);
		//System.out.println("className----**********---"+className);
		if(node.resolveTypeBinding().isEnum() || node.resolveMethodBinding().getDeclaringClass().isEnum())
			return false;
		if(classQualifiedName.startsWith("java.")||classQualifiedName.startsWith("javax.")||classQualifiedName.startsWith("org.apache"))
			return false;
		if(className.equals("Logger")||methodName.equals("delete")||methodName.equals("close")||className.equals("FileSystemManager")){
			//
			//System.out.println("methodName----*****#####*****---"+methodName);
			return false;
		}
			
		for(String srcPackage : srcPackages){
			//System.out.println("srcPackage------"+srcPackage);
			if(classQualifiedName.startsWith(srcPackage)){
				//System.out.println("className----*****#####*****---"+className);
				//System.out.println("src----*****####*****---"+srcPackage);
				return true;
			}
				
		}
		return false;
	}

}
