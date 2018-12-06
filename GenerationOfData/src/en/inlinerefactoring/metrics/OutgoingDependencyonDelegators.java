package en.inlinerefactoring.metrics;
/*
 * ODD – Outgoing Dependency on Delegators度量一个方法中调用来自外部的get、set方法和只有一条转移语句的函数。
 */
import java.util.HashMap;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;


public class OutgoingDependencyonDelegators{

	public OutgoingDependencyonDelegators() {
		//super(ODD);
		// TODO Auto-generated constructor stub
	}
	private MethodDeclaration methodNode = null;
	private String methodPath = null;
	private int accessNum = 0;
	private HashMap<String, Integer> map = null;
	private HashMap<SimpleName, Integer> methods = null;

	public int calculate(MethodDeclaration methodDeclaration)  {
		// TODO Auto-generated method stub
		//if (source.getLevel() != METHOD) {
		//	throw new InvalidSourceException("OCIO only applicable to methods");
		//}
		// TODO Auto-generated method stub
//	    double totalFields = 0;
		accessNum = 0;
		map = new HashMap<String, Integer>();
		methods = new HashMap<SimpleName,Integer>();
		
		methodNode = methodDeclaration;
		
		//System.out.println("-^^^^^zjj^^^^^^^"+source.getValue(MLOC).intValue());
		if(methodNode != null){
			IMethodBinding methodBinding = methodNode.resolveBinding();
			if(methodBinding != null){
				ITypeBinding typeBinding = methodBinding.getDeclaringClass();
				if(typeBinding != null){
					methodPath = typeBinding.getQualifiedName();//path
					//System.out.println("-^^^^^zjj^^methodPath^^^^^"+methodPath);
				}
			}
		}
		//System.out.println("-^^^^^zjj^^^^^^^"+methodNode.getName());
		if(methodPath == null){
			return 0;
		}
//		System.out.println("Method:" + methodPath + "." + methodNode.getName());
//			IField[] fields = type.getFields();
		//		    totalFields =fields.length;
		//System.out.println("---1----Method:" + methodPath + "." + methodNode.getName());
		MethodFieldVisitor v = new MethodFieldVisitor();
		methodNode.accept(v);
		return accessNum;
		//source.setValue(new Metric(ODD, accessNum));
		
	}
	private class MethodFieldVisitor extends ASTVisitor {
		private int methodInvocationNum=0;
		public boolean visit(MethodInvocation  node){
			IMethodBinding imethodBinding = node.resolveMethodBinding();
			//System.out.println("methodInvocation name-------------"+node.getName());
			ITypeBinding iTypeBinding = null;
			try{
				iTypeBinding = node.resolveMethodBinding().getDeclaringClass();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if((iTypeBinding.getModifiers()& Modifier.ABSTRACT)!=0)
				return false;
			if(imethodBinding.isConstructor() || (imethodBinding.getModifiers() & Modifier.ABSTRACT) != 0||node.resolveTypeBinding().isEnum() || node.resolveMethodBinding().getDeclaringClass().isEnum())
				return false;
			methodInvocationNum=0;
			//System.out.println("-start^^^^^zjj^methodInvocationName^^^^^^"+node.getName());
			if(isGetterOrSetter(node)){
				//System.out.println("-^^^^^zjj^if^^^^^^");
				IMethodBinding methodBinding = node.resolveMethodBinding(); 
				if(methodBinding != null){
					IMethodBinding binding = methodBinding.getMethodDeclaration();
					   
					if(binding != null){
						ITypeBinding typeBinding = binding.getDeclaringClass();
						if(typeBinding != null){
							String path = typeBinding.getQualifiedName();//path
							if(!path.equals(methodPath)){
							//ystem.out.println("getsetMethod:" + methodPath + ". " + methodNode.getName() + " " + "callee:" + path + "." + node.getName()); 
								accessNum++;
								map.put(path, 1);
							} 
						}
					}
				}
			}else{
					IMethodBinding methodBinding = node.resolveMethodBinding();
					if(methodBinding != null){
						IMethodBinding binding = methodBinding.getMethodDeclaration();
						   
						if(binding != null){
							ITypeBinding typeBinding = binding.getDeclaringClass();
							if(typeBinding != null){
								String path = typeBinding.getQualifiedName();//path
								if(!path.equals(methodPath)){
									IMethod iMethod = (IMethod)methodBinding.getJavaElement();
									//MethodDeclaration Mnode=Imethod.getJ
									//System.out.println("-^^^^^zjj^ImethodName^^^^^^"+node.getName());
									//System.out.println("-^^^^^zjj^methodInvocationNum^^^^^^"+methodInvocationNum);
									try {
										if(iMethod.getSource()!=null){
											
											//System.out.println("-^^^^^zjj^calculateNumberOfLines^^^^^^"+calculateNumberOfLines(Imethod.getSource()));
											String source=iMethod.getSource();
											//System.out.println("-^^^^^zjj^source^^^^^^"+source);
											if(calculateNumberOfLines(iMethod.getSource())==3){
												source=source.replaceAll("\r|\n", "");
												//source=source.trim();
												//System.out.println("-^^^^^zjj^source^^^^^^"+source);
												int start=source.indexOf("{");
												int end=source.indexOf("}");
												String body = source.substring(start+1,end).trim();
												//System.out.println("---zjj----start----"+start);
												//System.out.println("---zjj----end----"+end);
												//System.out.println("---zjj----body----"+body);
												if(body.startsWith("return")&&body.contains("(")&&body.contains(")"))
												accessNum++;
												//System.out.println("---zjj---()()----");
											}
											//System.out.println("-^^^^^zjj^body^^^^^^"+Mmthod.getBody());
										}
									} catch (JavaModelException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (InvalidInputException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}
					}
			}
			return true;
		}
		/*public void ASTparser(String source){
			ASTParser astParser = ASTParser.newParser(AST.JLS3);  
	        astParser.setSource(new String(source).toCharArray());  
	        astParser.setKind(ASTParser.K_COMPILATION_UNIT);  
	  
	        CompilationUnit result = (CompilationUnit) (astParser.createAST(null)); 
	        TypeDeclaration ty=result.
	        MethodVisitor v=new MethodVisitor();
	        result.accept(v);
	       
		}*/
		private class MethodVisitor extends ASTVisitor {
			public boolean visit(MethodInvocation  node){
				methodInvocationNum++;
				//System.out.println("-^^^^^zjj^name^^^^^^"+node.getName());
				return true;
			}
		}

		public int calculateNumberOfLines(String a_source) throws InvalidInputException{
			//String l_srcToCount = filterSourceToProcess(a_source).trim();
			
			//Set<Integer> l_lineSet = new HashSet<Integer>();
			//System.out.println("进入 calculatenumberofline");
			IScanner l_scanner = ToolFactory.createScanner(false, false, true, true);
			l_scanner.setSource(a_source.toCharArray());
			
			int token = l_scanner.getNextToken();
			
			int l_startpos = l_scanner.getCurrentTokenStartPosition();
			int l_lineNb = l_scanner.getLineNumber(l_startpos);
				
		
			while (true) {
				if (token == ITerminalSymbols.TokenNameEOF) {
					break;
				}
				token = l_scanner.getNextToken();
			}
		
			int l_endpos = l_scanner.getCurrentTokenEndPosition();
				
			int l_endLine = l_scanner.getLineNumber(l_endpos);

			//System.out.println("endLine:"+l_endLine);
			//System.out.println("startLine"+l_lineNb);
			return l_endLine-l_lineNb+1;
		}

	}

	public static boolean isGetterOrSetter(MethodInvocation node){
		IMethodBinding binding = node.resolveMethodBinding();
		String methodName = binding.getName();
		if(methodName.length() <= 3)
			return false;
		if(methodName.startsWith("get")){
			String targetField = methodName.substring(3);
			//System.out.println("--zjj---get-----"+targetField);
			IVariableBinding[] fields = node.resolveMethodBinding().getDeclaringClass().getDeclaredFields();
			for(IVariableBinding field : fields){
				//System.out.println("--zjj---?????-----"+field.getName());
				if(field.getName().equalsIgnoreCase(targetField) && field.getType().equals(node.resolveMethodBinding().getReturnType())){
					//System.out.println("--zjj---getmethod-----"+field.getName());
					return true;
				}
			}
		}
		if(methodName.startsWith("set")){
			String targetField = methodName.substring(3);
			//System.out.println("--zjj---set-----"+targetField);
			IVariableBinding[] fields = node.resolveMethodBinding().getDeclaringClass().getDeclaredFields();
			for(IVariableBinding field : fields)
				if(field.getName().equalsIgnoreCase(targetField)){
					ITypeBinding[] parameterTypes = node.resolveMethodBinding().getParameterTypes();
					if(parameterTypes.length == 1 && field.getType().equals(parameterTypes[0])){
						//System.out.println("--zjj---setmethod-----"+field.getName());
						return true;
					}
				}
		}
		return false;
	}

}
