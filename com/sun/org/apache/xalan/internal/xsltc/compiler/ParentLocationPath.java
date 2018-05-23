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
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ParentLocationPath
  extends RelativeLocationPath
{
  private Expression _step;
  private final RelativeLocationPath _path;
  private Type stype;
  private boolean _orderNodes = false;
  private boolean _axisMismatch = false;
  
  public ParentLocationPath(RelativeLocationPath paramRelativeLocationPath, Expression paramExpression)
  {
    _path = paramRelativeLocationPath;
    _step = paramExpression;
    _path.setParent(this);
    _step.setParent(this);
    if ((_step instanceof Step)) {
      _axisMismatch = checkAxisMismatch();
    }
  }
  
  public void setAxis(int paramInt)
  {
    _path.setAxis(paramInt);
  }
  
  public int getAxis()
  {
    return _path.getAxis();
  }
  
  public RelativeLocationPath getPath()
  {
    return _path;
  }
  
  public Expression getStep()
  {
    return _step;
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _step.setParser(paramParser);
    _path.setParser(paramParser);
  }
  
  public String toString()
  {
    return "ParentLocationPath(" + _path + ", " + _step + ')';
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    stype = _step.typeCheck(paramSymbolTable);
    _path.typeCheck(paramSymbolTable);
    if (_axisMismatch) {
      enableNodeOrdering();
    }
    return _type = Type.NodeSet;
  }
  
  public void enableNodeOrdering()
  {
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    if ((localSyntaxTreeNode instanceof ParentLocationPath)) {
      ((ParentLocationPath)localSyntaxTreeNode).enableNodeOrdering();
    } else {
      _orderNodes = true;
    }
  }
  
  public boolean checkAxisMismatch()
  {
    int i = _path.getAxis();
    int j = ((Step)_step).getAxis();
    if (((i == 0) || (i == 1)) && ((j == 3) || (j == 4) || (j == 5) || (j == 10) || (j == 11) || (j == 12))) {
      return true;
    }
    if (((i == 3) && (j == 0)) || (j == 1) || (j == 10) || (j == 11)) {
      return true;
    }
    if ((i == 4) || (i == 5)) {
      return true;
    }
    if (((i == 6) || (i == 7)) && ((j == 6) || (j == 10) || (j == 11) || (j == 12))) {
      return true;
    }
    if (((i == 11) || (i == 12)) && ((j == 4) || (j == 5) || (j == 6) || (j == 7) || (j == 10) || (j == 11) || (j == 12))) {
      return true;
    }
    if ((j == 6) && (i == 3) && ((_path instanceof Step)))
    {
      int k = ((Step)_path).getNodeType();
      if (k == 2) {
        return true;
      }
    }
    return false;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    _path.translate(paramClassGenerator, paramMethodGenerator);
    translateStep(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateStep(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localLocalVariableGen1 = paramMethodGenerator.addLocalVariable("parent_location_path_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localLocalVariableGen1.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex())));
    _step.translate(paramClassGenerator, paramMethodGenerator);
    LocalVariableGen localLocalVariableGen2 = paramMethodGenerator.addLocalVariable("parent_location_path_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    localLocalVariableGen2.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen2.getIndex())));
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator")));
    localInstructionList.append(DUP);
    localLocalVariableGen1.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex())));
    localLocalVariableGen2.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen2.getIndex())));
    localInstructionList.append(new INVOKESPECIAL(i));
    Expression localExpression = _step;
    if ((localExpression instanceof ParentLocationPath)) {
      localExpression = ((ParentLocationPath)localExpression).getStep();
    }
    int j;
    if (((_path instanceof Step)) && ((localExpression instanceof Step)))
    {
      j = ((Step)_path).getAxis();
      int k = ((Step)localExpression).getAxis();
      if (((j == 5) && (k == 3)) || ((j == 11) && (k == 10)))
      {
        int m = localConstantPoolGen.addMethodref("com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase", "includeSelf", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        localInstructionList.append(new INVOKEVIRTUAL(m));
      }
    }
    if (_orderNodes)
    {
      j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
      localInstructionList.append(paramMethodGenerator.loadContextNode());
      localInstructionList.append(new INVOKEINTERFACE(j, 3));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ParentLocationPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */