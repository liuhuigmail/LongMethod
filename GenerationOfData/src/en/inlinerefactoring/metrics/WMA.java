package en.inlinerefactoring.metrics;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;

//import com.google.common.base.Preconditions;

public class WMA {
	// method weight
	public WMA(){
		
	}
	public int getWMA(MethodDeclaration astNode, IMethod method){
		//Preconditions.checkNotNull(astNode);
		//Preconditions.checkNotNull(method);

		Block body = astNode.getBody();
		if (body == null) {
			return -1;
		}
		String sourceCode = null;
		try {
			sourceCode = method.getCompilationUnit().getSource();
		} catch (JavaModelException e) {
			System.out.println(e.getMessage());
		}
		McCabeVisitor mcb = new McCabeVisitor(sourceCode);
		astNode.accept(mcb);
		return mcb.cyclomatic;
	}

	private static class McCabeVisitor extends ASTVisitor {

		private int cyclomatic = 1;
		private String source;

		McCabeVisitor(String source) {
			this.source = source;
		}

		// McCabe CC is computed as method level. there fore while parsing code
		// if we found TypeDeclaration, AnnotationTypeDeclaration,
		// EnumDeclaration or AnonymousClassDeclaration
		@Override
		public boolean visit(AnonymousClassDeclaration node) {
			return false; // XXX
		}

		@Override
		public boolean visit(TypeDeclaration node) {
			return false; // XXX same as above
		}

		@Override
		public boolean visit(AnnotationTypeDeclaration node) {
			return false; // XXX same as above
		}

		@Override
		public boolean visit(EnumDeclaration node) {
			return false; // XXX same as above
		}

		// catch
		@Override
		public boolean visit(CatchClause node) {
			cyclomatic++;
			return true;
		}

		// ?: and maybe include &&/||
		@Override
		public boolean visit(ConditionalExpression node) {
			cyclomatic++;
			// System.out.println("luohui conditionalExpression");
			inspectExpression(node.getExpression());
			return true;
		}

		// do-while and maybe include &&/||
		@Override
		public boolean visit(DoStatement node) {
			cyclomatic++;
			inspectExpression(node.getExpression());
			return true;
		}

		// for ( FormalParameter : Expression ) and maybe include &&/||
		@Override
		public boolean visit(EnhancedForStatement node) {
			cyclomatic++;
			inspectExpression(node.getExpression());
			return true;
		}

		// for( : : ) and maybe include &&/||
		@Override
		public boolean visit(ForStatement node) {
			cyclomatic++;
			inspectExpression(node.getExpression());
			return true;
		}

		// if and maybe include &&/||
		@Override
		public boolean visit(IfStatement node) {
			cyclomatic++;
			inspectExpression(node.getExpression());
			return true;
		}

		// case
		@Override
		public boolean visit(SwitchCase node) {
			if (!node.isDefault()) {
				cyclomatic++;
			}
			return true;
		}

		// while and maybe include &&/||
		@Override
		public boolean visit(WhileStatement node) {
			cyclomatic++;
			inspectExpression(node.getExpression());
			return true;
		}

		// maybe include &&/||
		@Override
		public boolean visit(ExpressionStatement node) {
			inspectExpression(node.getExpression());
			return true; // change true from false
		}

		// maybe include &&/||
		@Override
		public boolean visit(VariableDeclarationFragment node) {
			inspectExpression(node.getInitializer());
			return true;
		}

		/**
		 * Count occurrences of && and || (conditional and or) Fix for BUG
		 * 740253
		 * 
		 * @param ex
		 */
		private void inspectExpression(Expression ex) {
			if ((ex != null) && (source != null)) {
				int start = ex.getStartPosition();
				int end = start + ex.getLength();
				String expression = source.substring(start, end);
				char[] chars = expression.toCharArray();
				for (int i = 0; i < chars.length - 1; i++) {
					char next = chars[i];
					if ((next == '&' || next == '|') && (next == chars[i + 1])) {
						cyclomatic++;
					}
				}
			}
		}
	}
}
