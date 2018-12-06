package en.inlinerefactoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineMethodRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import en.actions.undo.ActionsAboutUndo;
import en.entitys.Entity;
import en.inlinerefactoring.database.ActionsAboutDB;
import en.inlinerefactoring.database.ui.LongMethodInformations;
import en.inlinerefactoring.metrics.LocalityofDataAccesses;
import en.inlinerefactoring.metrics.MethodLinesOfCode;
import en.inlinerefactoring.metrics.OutgoingCouplingIntensity;
import en.inlinerefactoring.metrics.OutgoingDependencyonDelegators;
import en.inlinerefactoring.metrics.WMA;



@SuppressWarnings("restriction")
public class InlineMethodActions {
	public List<IMethod> allMethodsCanBeInlined = new ArrayList<IMethod>();
	//private RefactoringStatus fStatus;
	public List<MethodInvocation> methodInvocationsCanBeInlined = new ArrayList<MethodInvocation>();
	public List<String> MethodInvocationNames = new ArrayList<String>();
	public IProject iProject;
	
	public int SelectMethodToInline(IMethod method, List<String> srcPackages){
		init();
		int flag = 0;
		if(method.exists()){
			int MLOC = getMLOC(method);
			if(MLOC > 3){
				Entity entity = new Entity(method);
				TypeDeclaration typeDeclaration = entity.getTypeDeclaration();
				
				MethodDeclaration methodDeclaration = (MethodDeclaration) entity.getAssociatedNode();
				System.out.println("TypeDeclaration------"+typeDeclaration.resolveBinding().getQualifiedName());
				System.out.println("methodDeclaration-------"+methodDeclaration.resolveBinding().toString());
				System.out.println("methodDeclaration name ----:"+methodDeclaration.getName()+'\n');
				System.out.println("before----------------------------------------------------------------------");
				try {
					if(!methodDeclaration.isConstructor() && (method.getFlags() & Flags.AccAbstract) == 0){
				        //System.out.println("allMethodsCanBeInlined.isEmpty()-----"+allMethodsCanBeInlined.isEmpty()); 
				        VisitorForMethod visitor = new VisitorForMethod(methodDeclaration,srcPackages);
				        typeDeclaration.accept(visitor);
				        allMethodsCanBeInlined = visitor.iMethods;
				        methodInvocationsCanBeInlined = visitor.methodInvocationsCanBeInlined;
				        /*
				        if(!allMethodsCanBeInlined.isEmpty()){
				        	System.out.println("method name-----:"+method.getDeclaringType().getFullyQualifiedName()+"---"+method.getElementName());
				        }
				        */
				        
				        
				        
				        List<MethodInvocationNode> sortedAllMethodsCanBeInlined = new ArrayList<MethodInvocationNode>();
				        
				        for(int i=0;i<allMethodsCanBeInlined.size();i++) {
				        	IMethod imethod = allMethodsCanBeInlined.get(i);
				        	MethodInvocation inlineMethod = methodInvocationsCanBeInlined.get(i);
				        	
				        	int temp_MLOC = getMLOC(imethod);
				        	
				        	if(temp_MLOC<(MLOC/4)||temp_MLOC>2*MLOC) continue;
				        	
				        	MethodInvocationNode e = new MethodInvocationNode();
				        	e.methodInvocation=inlineMethod;
				        	e.MLOC=temp_MLOC;
				        	e.classname=imethod.getDeclaringType().getElementName();
				        	
				        	sortedAllMethodsCanBeInlined.add(e);
				        }
				        
				        String baseClassName=method.getDeclaringType().getElementName();
				        
				        Collections.sort(sortedAllMethodsCanBeInlined,new Comparator<MethodInvocationNode>() {
							public int compare(MethodInvocationNode a,MethodInvocationNode b) {
								if(a.MLOC==b.MLOC) {
									if(!a.classname.equals(baseClassName)&&b.classname.equals(baseClassName)) return -1;
									return 1;
								}
								
								if(a.MLOC>b.MLOC) return -1;
								
								return 1;
							}	
							
				        });
				        
				        
				        for(MethodInvocationNode min : sortedAllMethodsCanBeInlined) {
				        	
				        	MethodInvocation inlineMethod = min.methodInvocation;
				        	/*
				        	System.out.println("***************************************111");
				        	System.out.println(inlineMethod.getName().toString());
				        	System.out.println(min.MLOC);
				        	System.out.println(min.classname);
				        	System.out.println("***************************************222");
				        	*/
				        	boolean status = InlineMethod(inlineMethod);
							if(status == true) {
								flag = 1;
								break;
							}
				        }
				        
				        /*
						for(MethodInvocation inlineMethod : methodInvocationsCanBeInlined){
							//System.out.println("Inline Method name-----:"+inlineMethod.resolveTypeBinding().getQualifiedName()+"---"+inlineMethod.getName());
							
							boolean status = InlineMethod(inlineMethod);
							if(status == true)
								flag = 1;	
						}
						
						*/
				        
						System.out.println("flag-----:"+flag);
						metrics(method,flag);
					}
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(); 
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	public void init(){
		allMethodsCanBeInlined.clear();
		MethodInvocationNames.clear();
	}
	public void metrics(IMethod method, int flag){
		Entity entity = new Entity(method);
		MethodDeclaration methodDeclaration = (MethodDeclaration) entity.getAssociatedNode();
		LocalityofDataAccesses localityofDataAccesses = new LocalityofDataAccesses();
		int NOAV = localityofDataAccesses.calculate(method);
		//System.out.println("NOAV-----:"+NOAV);
		int MLOC = getMLOC(method);
		OutgoingCouplingIntensity outgoingCouplingIntensity = new OutgoingCouplingIntensity();
		double DR = outgoingCouplingIntensity.calculate(methodDeclaration);
		//System.out.println("DR-------:"+DR);
		OutgoingDependencyonDelegators outgoingDependencyonDelegators = new OutgoingDependencyonDelegators();
		int ODD = outgoingDependencyonDelegators.calculate(methodDeclaration);
		//System.out.println("ODD-----:"+ODD);
		WMA wMA = new WMA();
		double WMA = wMA.getWMA(methodDeclaration, method);
		//System.out.println("WMA------:"+WMA);
		try {
			insertLongMethodIntoDB(method,methodDeclaration,NOAV,MLOC,DR,ODD,WMA,flag,method.getJavaProject().getElementName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getMLOC(IMethod method){
		MethodLinesOfCode methodLinesOfCode = new MethodLinesOfCode();
		int MLOC = 0;
		try {
			MLOC = methodLinesOfCode.calculateNumberOfLines(method.getSource());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("MLOC------:"+MLOC);
		return MLOC;
	}
	public void insertLongMethodIntoDB(IMethod method,MethodDeclaration methodDeclaration, int NOAV, int MLOC, double DR, int ODD, double WMA,int isLongMethodOrNot, String projectName) throws Exception{
		String methodOfClass = method.getDeclaringType().getFullyQualifiedName();
		String methodName = method.getElementName();
		String methodParameters = methodParameters(methodDeclaration);
		String methodBody = method.getSource();
		ActionsAboutDB actionsAboutDB = new ActionsAboutDB();
		int keynum = actionsAboutDB.getTableMaxRow()+1;
		LongMethodInformations longMethodInformations = new LongMethodInformations(keynum,methodName,methodOfClass,
				methodParameters, methodBody, NOAV, MLOC, DR, ODD, WMA, isLongMethodOrNot, projectName);
		int status = actionsAboutDB.insertLongMethodInformations(longMethodInformations);
		System.out.println("insert into DB or not-----"+status);

	}

	public boolean InlineMethod(MethodInvocation methodInvocation){
		
		int offset = methodInvocation.getStartPosition();
		int length = methodInvocation.getLength();
		System.out.println("Class Name -----: "+methodInvocation.resolveMethodBinding().getDeclaringClass().getQualifiedName());
		System.out.println("Method Name -----: "+methodInvocation.getName());
		//System.out.println("methodInvocation start position-----"+methodInvocation.getStartPosition());
		//System.out.println("methodInvocation length----"+methodInvocation.getLength());
		if(methodInvocation.resolveMethodBinding().isRecovered())
			return false;
		IMethod method = (IMethod) methodInvocation.resolveMethodBinding().getJavaElement();
		boolean status = false;
		if(method.exists()){
			ITypeRoot typeRoot = method.getTypeRoot();
			//CompilationUnit node= setCompilationUnit(method);
			CompilationUnit node= RefactoringASTParser.parseWithASTProvider(typeRoot, true, null);
			
			status = startInlineMethodRefactoring(typeRoot, node, offset, length);
			System.out.println("status---"+status);
		}
		return status;	
	}

	public boolean startInlineMethodRefactoring(ITypeRoot typeRoot, CompilationUnit node, int offset, int length) {
		
		System.out.println("start inline");
		InlineMethodRefactoring refactoring= InlineMethodRefactoring.create(typeRoot, node, offset, length);
		//changeRefactoring(InlineMethodRefactoring.Mode.INLINE_SINGLE,refactoring);
		if (refactoring != null) {
			int flag = 0;
			IProgressMonitor pm = new NullProgressMonitor();
			try {
				refactoring.checkInitialConditions(pm);
				//refactoring.checkFinalConditions(pm);
				//int step = CheckConditionsOperation.FINAL_CONDITIONS;
				final PerformRefactoringOperation op = new PerformRefactoringOperation(refactoring, CheckConditionsOperation.ALL_CONDITIONS);
				op.run(pm);
				ActionsAboutUndo undoActions = new ActionsAboutUndo();
				if(undoActions.calculateCompilationErrorMarkers(typeRoot.getJavaProject(),pm)){
					System.out.println("----CompilationErrorDetectedException");
					undoActions.undo();
					flag = 1;
				}	
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(flag == 1)
				return false;
			else
				return true;
		}
		else
			return false;
	}
	public String methodParameters(MethodDeclaration methodDeclaration){
		ITypeBinding[] parameters = methodDeclaration.resolveBinding().getParameterTypes();
		List<String> parameterList = new ArrayList<String>();
		if(parameters.length!=0)
			for (ITypeBinding parameter : parameters){
				parameterList.add(parameter.getQualifiedName());
				//System.out.println("parameters------------"+parameter.getQualifiedName());
			}
		StringBuilder sb = new StringBuilder();
		if(!parameterList.isEmpty()){
			for(String parameter : parameterList)
				sb.append(parameter).append(",");
		}
		else
			sb.append("0");                          
		
		return sb.toString();
	}

//	private CompilationUnit setCompilationUnit(IMethod element){
//		IMethod method = (IMethod)element;
//		ICompilationUnit iUnit = method.getCompilationUnit();
//		//IType belongType = member.getDeclaringType();
//		ASTParser parser = ASTParser.newParser(AST.JLS4);
//		parser.setSource(iUnit);
//		parser.setResolveBindings(true);
//		parser.setBindingsRecovery(false);
//		parser.setKind(ASTParser.K_COMPILATION_UNIT);
//		CompilationUnit unit1 = (CompilationUnit) parser.createAST(null);
//		unit1.recordModifications();
//		
//		return unit1;
//	}
//	public void CompilationErrorDetected(IJavaProject iProject, IProgressMonitor pm){
//		ActionsAboutUndo undoActions = new ActionsAboutUndo();
//		//boolean status = undoActions.calculateCompilationErrorMarkers(iProject);
//		//System.out.println("status of project------"+status);
//		if(undoActions.calculateCompilationErrorMarkers(iProject,null)){
//			System.out.println("----CompilationErrorDetectedException");
//			undoActions.undo();
//		}
//	}
}



class MethodInvocationNode{
	public MethodInvocation methodInvocation;
	public int MLOC;
	public String classname;
	
	
	
}
