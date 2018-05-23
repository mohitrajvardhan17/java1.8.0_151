package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.dtm.Axis;
import java.util.Vector;

final class UnionPathExpr
  extends Expression
{
  private final Expression _pathExpr;
  private final Expression _rest;
  private boolean _reverse = false;
  private Expression[] _components;
  
  public UnionPathExpr(Expression paramExpression1, Expression paramExpression2)
  {
    _pathExpr = paramExpression1;
    _rest = paramExpression2;
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    Vector localVector = new Vector();
    flatten(localVector);
    int i = localVector.size();
    _components = ((Expression[])localVector.toArray(new Expression[i]));
    for (int j = 0; j < i; j++)
    {
      _components[j].setParser(paramParser);
      _components[j].setParent(this);
      if ((_components[j] instanceof Step))
      {
        Step localStep = (Step)_components[j];
        int k = localStep.getAxis();
        int m = localStep.getNodeType();
        if ((k == 2) || (m == 2))
        {
          _components[j] = _components[0];
          _components[0] = localStep;
        }
        if (Axis.isReverse(k)) {
          _reverse = true;
        }
      }
    }
    if ((getParent() instanceof Expression)) {
      _reverse = false;
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    int i = _components.length;
    for (int j = 0; j < i; j++) {
      if (_components[j].typeCheck(paramSymbolTable) != Type.NodeSet) {
        _components[j] = new CastExpr(_components[j], Type.NodeSet);
      }
    }
    return _type = Type.NodeSet;
  }
  
  public String toString()
  {
    return "union(" + _pathExpr + ", " + _rest + ')';
  }
  
  private void flatten(Vector paramVector)
  {
    paramVector.addElement(_pathExpr);
    if (_rest != null) {
      if ((_rest instanceof UnionPathExpr)) {
        ((UnionPathExpr)_rest).flatten(paramVector);
      } else {
        paramVector.addElement(_rest);
      }
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.UnionIterator", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
    int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.UnionIterator", "addIterator", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/UnionIterator;");
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.UnionIterator")));
    localInstructionList.append(DUP);
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(new INVOKESPECIAL(i));
    int k = _components.length;
    for (int m = 0; m < k; m++)
    {
      _components[m].translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new INVOKEVIRTUAL(j));
    }
    if (_reverse)
    {
      m = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
      localInstructionList.append(paramMethodGenerator.loadContextNode());
      localInstructionList.append(new INVOKEINTERFACE(m, 3));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnionPathExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */