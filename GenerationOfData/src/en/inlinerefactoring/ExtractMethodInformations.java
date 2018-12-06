package en.inlinerefactoring;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import en.entitys.Entity;
import en.inlinerefactoring.database.ActionsAboutDB;

public class ExtractMethodInformations {
	
	public void getInformationsFromDB(IMethod method){
		Entity entity = new Entity(method);
		MethodDeclaration methodDeclaration = (MethodDeclaration) entity.getAssociatedNode();
		String methodOfClass = method.getDeclaringType().getFullyQualifiedName();
		String methodName = method.getElementName();
		String methodParameters = methodParameters(methodDeclaration);
		
		ActionsAboutDB actionsAboutDB = new ActionsAboutDB();
		int isLongMethodOrNot = 0;
		try {
			isLongMethodOrNot = actionsAboutDB.selectIsLongMethodOrNot(methodName, methodOfClass, methodParameters);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WriteIntoFiles(method,isLongMethodOrNot);	
	}
	
	public void WriteIntoFiles(IMethod method,int isLongMethodOrNot){
		BufferedWriter writer = null;
		String methodInformation = getMethodInformations(method);
		String project = method.getDeclaringType().getJavaProject().getElementName();
		try {
			File txtFile = getTxtFile(project,isLongMethodOrNot);
			//System.out.println("Writing file: " + txtFile.getPath());
	        writer = new BufferedWriter(new FileWriter(txtFile, true));
	        writer.write(methodInformation);
			writer.newLine();
			writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
	        	if(writer != null)
	        		try{
	        			writer.close();
	        		}catch(IOException e){
	        			e.printStackTrace();
	        		}
	        }
	}
	public File getTxtFile(String project,int index){
		File txtFile = new File("D:\\Longmethod\\compare\\OursNew\\"+project+"--"+index+".txt");
		try {
			if(!txtFile.exists())
				txtFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return txtFile; 
	}
	
	
	public String getMethodInformations(IMethod method){
		Entity entity = new Entity(method);
		TypeDeclaration typeDeclaration = entity.getTypeDeclaration();
		
		MethodDeclaration methodDeclaration = (MethodDeclaration) entity.getAssociatedNode();
		//System.out.println("TypeDeclaration------"+typeDeclaration.resolveBinding().getQualifiedName());
		//System.out.println("methodDeclaration-------"+methodDeclaration.resolveBinding().toString());
		String methodInformation = typeDeclaration.resolveBinding().getQualifiedName()+"\t"+methodDeclaration.resolveBinding().toString();
		//System.out.println("methodInformation-------****------"+methodInformation);
		return methodInformation;
	}
	public String methodParameters(MethodDeclaration methodDeclaration){
		ITypeBinding[] parameters = null;
		try{
			parameters = methodDeclaration.resolveBinding().getParameterTypes();
		}catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

}
