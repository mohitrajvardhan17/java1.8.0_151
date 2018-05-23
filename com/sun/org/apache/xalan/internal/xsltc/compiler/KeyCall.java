package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class KeyCall
  extends FunctionCall
{
  private Expression _name;
  private Expression _value;
  private Type _valueType;
  private QName _resolvedQName = null;
  
  public KeyCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
    switch (argumentCount())
    {
    case 1: 
      _name = null;
      _value = argument(0);
      break;
    case 2: 
      _name = argument(0);
      _value = argument(1);
      break;
    default: 
      _name = (_value = null);
    }
  }
  
  public void addParentDependency()
  {
    if (_resolvedQName == null) {
      return;
    }
    for (Object localObject = this; (localObject != null) && (!(localObject instanceof TopLevelElement)); localObject = ((SyntaxTreeNode)localObject).getParent()) {}
    TopLevelElement localTopLevelElement = (TopLevelElement)localObject;
    if (localTopLevelElement != null) {
      localTopLevelElement.addDependency(getSymbolTable().getKey(_resolvedQName));
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = super.typeCheck(paramSymbolTable);
    if (_name != null)
    {
      Type localType2 = _name.typeCheck(paramSymbolTable);
      if ((_name instanceof LiteralExpr))
      {
        LiteralExpr localLiteralExpr = (LiteralExpr)_name;
        _resolvedQName = getParser().getQNameIgnoreDefaultNs(localLiteralExpr.getValue());
      }
      else if (!(localType2 instanceof StringType))
      {
        _name = new CastExpr(_name, Type.String);
      }
    }
    _valueType = _value.typeCheck(paramSymbolTable);
    if ((_valueType != Type.NodeSet) && (_valueType != Type.Reference) && (_valueType != Type.String))
    {
      _value = new CastExpr(_value, Type.String);
      _valueType = _value.typeCheck(paramSymbolTable);
    }
    addParentDependency();
    return localType1;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex;");
    int j = localConstantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "setDom", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)V");
    int k = localConstantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "getKeyIndexIterator", "(" + _valueType.toSignature() + "Z)" + "Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex$KeyIndexIterator;");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    if (_name == null) {
      localInstructionList.append(new PUSH(localConstantPoolGen, "##id"));
    } else if (_resolvedQName != null) {
      localInstructionList.append(new PUSH(localConstantPoolGen, _resolvedQName.toString()));
    } else {
      _name.translate(paramClassGenerator, paramMethodGenerator);
    }
    localInstructionList.append(new INVOKEVIRTUAL(i));
    localInstructionList.append(DUP);
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(new INVOKEVIRTUAL(j));
    _value.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(_name != null ? ICONST_1 : ICONST_0);
    localInstructionList.append(new INVOKEVIRTUAL(k));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\KeyCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */