/* Generated By:JJTree: Do not edit this line. ASTExplicitConstructorInvocation.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.core.javaManager.parser;

public class ASTExplicitConstructorInvocation extends SimpleNode {
	public ASTExplicitConstructorInvocation(int id) {
		super(id);
	}

	public ASTExplicitConstructorInvocation(JavaParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=8b240f21cf8fce692d712ea88dac5e7a (do not edit this
 * line)
 */