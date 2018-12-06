package en.inlinerefactoring.metrics;

//NOAV: Number of Accessed Variables 度量一个method的中局部变量，参数，类的成员变量的个数之和。

import org.eclipse.jdt.core.IField;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.FieldAccess;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import en.entitys.Entity;


public class LocalityofDataAccesses{
	
	int methodDataNum ;
	int NumOfParameter;
	int NumOfattribute;
	int NumberOfLocal;
	public int calculate(IMethod method){
		// TODO Auto-generated method stub
		
	    methodDataNum = 0;
	    NumberOfLocal=0;
	    NumOfParameter=0;
	    NumOfattribute=0;
	    double totalFields = 0;
	   
	    Entity entity = new Entity(method);
	    MethodDeclaration astNode = (MethodDeclaration) entity.getAssociatedNode();
	    System.out.println("methodDeclaration----------------"+astNode.getName());
		IType type = method.getDeclaringType();
		try {
	//		System.out.print(astNode.getChildren().length + method.getElementName()+method.toString()+"@@@@@@@@@@@@@@@@@@@@@@@@@@"+'\n');
			
			IField[] fields = type.getFields();
		    totalFields =fields.length;
			MethodFieldVisitor v = new MethodFieldVisitor();
			AccessDataVisitor v2=new AccessDataVisitor();
			astNode.accept(v);
			if(!Modifier.isAbstract(astNode.getModifiers()))
			   astNode.accept(v2);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	//	double dam = (methodDataNum+totalFields)>0?methodDataNum/(methodDataNum+totalFields):0.0; 
		int dataAccess=methodDataNum+NumOfParameter+NumOfattribute;
		//System.out.println("NOAV-----"+dataAccess);
		return dataAccess;
	//	source.setValue(new Metric(LOCALITY_OF_DATA_ACCESSES, dam));
		//source.setValue(new Metric(NOAV,dataAccess));
		
	}
	//lc
	private class MethodFieldVisitor extends ASTVisitor {
		public boolean visit(VariableDeclarationFragment  node){
	//		System.out.print(node.toString()+"@@@@@@@@@@@@@@@@@@@@@@@@@@"+'\n');
		    methodDataNum++;
			return true;
		}
	}
	//zjj
	private class AccessDataVisitor extends ASTVisitor {
		public boolean visit(VariableDeclarationFragment  node){
	//		System.out.print(node.toString()+"@@@@@@@@@@@@@@@@@@@@@@@@@@"+'\n');
			NumberOfLocal++;
			return true;
		}
		public boolean visit(SingleVariableDeclaration  node){
			//System.out.print("zjj^^^^^SingleVariableDeclaration^^^^^^^^^^^^^^^^^"+node.toString());
			NumOfParameter++;
					return true;
				}
		public boolean visit(FieldAccess  node){
			//System.out.print("zjj^^^^^FieldAccess^^^^^^^^^^^^^^^^^"+node.toString());
			NumOfattribute++;
			return true;
	   }

	}
	
}

