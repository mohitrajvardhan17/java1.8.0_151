package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

class NameBase
  extends FunctionCall
{
  private Expression _param = null;
  private Type _paramType = Type.Node;
  
  public NameBase(QName paramQName)
  {
    super(paramQName);
  }
  
  public NameBase(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
    _param = argument(0);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    switch (argumentCount())
    {
    case 0: 
      _paramType = Type.Node;
      break;
    case 1: 
      _paramType = _param.typeCheck(paramSymbolTable);
      break;
    default: 
      throw new TypeCheckError(this);
    }
    if ((_paramType != Type.NodeSet) && (_paramType != Type.Node) && (_paramType != Type.Reference)) {
      throw new TypeCheckError(this);
    }
    return _type = Type.String;
  }
  
  public Type getType()
  {
    return _type;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(paramMethodGenerator.loadDOM());
    if (argumentCount() == 0)
    {
      localInstructionList.append(paramMethodGenerator.loadContextNode());
    }
    else if (_paramType == Type.Node)
    {
      _param.translate(paramClassGenerator, paramMethodGenerator);
    }
    else if (_paramType == Type.Reference)
    {
      _param.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new INVOKESTATIC(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeSet", "(Ljava/lang/Object;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;")));
      localInstructionList.append(paramMethodGenerator.nextNode());
    }
    else
    {
      _param.translate(paramClassGenerator, paramMethodGenerator);
      _param.startIterator(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.nextNode());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\NameBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */