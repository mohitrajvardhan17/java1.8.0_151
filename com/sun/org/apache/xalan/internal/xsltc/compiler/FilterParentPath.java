package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class FilterParentPath
  extends Expression
{
  private Expression _filterExpr;
  private Expression _path;
  private boolean _hasDescendantAxis = false;
  
  public FilterParentPath(Expression paramExpression1, Expression paramExpression2)
  {
    (_path = paramExpression2).setParent(this);
    (_filterExpr = paramExpression1).setParent(this);
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _filterExpr.setParser(paramParser);
    _path.setParser(paramParser);
  }
  
  public String toString()
  {
    return "FilterParentPath(" + _filterExpr + ", " + _path + ')';
  }
  
  public void setDescendantAxis()
  {
    _hasDescendantAxis = true;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = _filterExpr.typeCheck(paramSymbolTable);
    if (!(localType1 instanceof NodeSetType)) {
      if ((localType1 instanceof ReferenceType)) {
        _filterExpr = new CastExpr(_filterExpr, Type.NodeSet);
      } else if ((localType1 instanceof NodeType)) {
        _filterExpr = new CastExpr(_filterExpr, Type.NodeSet);
      } else {
        throw new TypeCheckError(this);
      }
    }
    Type localType2 = _path.typeCheck(paramSymbolTable);
    if (!(localType2 instanceof NodeSetType)) {
      _path = new CastExpr(_path, Type.NodeSet);
    }
    return _type = Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
    _filterExpr.translate(paramClassGenerator, paramMethodGenerator);
    LocalVariableGen localLocalVariableGen1 = paramMethodGenerator.addLocalVariable("filter_parent_path_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localLocalVariableGen1.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex())));
    _path.translate(paramClassGenerator, paramMethodGenerator);
    LocalVariableGen localLocalVariableGen2 = paramMethodGenerator.addLocalVariable("filter_parent_path_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localLocalVariableGen2.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen2.getIndex())));
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator")));
    localInstructionList.append(DUP);
    localLocalVariableGen1.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex())));
    localLocalVariableGen2.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen2.getIndex())));
    localInstructionList.append(new INVOKESPECIAL(i));
    if (_hasDescendantAxis)
    {
      int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase", "includeSelf", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(new INVOKEVIRTUAL(j));
    }
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    int k = ((localSyntaxTreeNode instanceof RelativeLocationPath)) || ((localSyntaxTreeNode instanceof FilterParentPath)) || ((localSyntaxTreeNode instanceof KeyCall)) || ((localSyntaxTreeNode instanceof CurrentCall)) || ((localSyntaxTreeNode instanceof DocumentCall)) ? 1 : 0;
    if (k == 0)
    {
      int m = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
      localInstructionList.append(paramMethodGenerator.loadContextNode());
      localInstructionList.append(new INVOKEINTERFACE(m, 3));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FilterParentPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */