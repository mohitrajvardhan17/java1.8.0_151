package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class DocumentCall
  extends FunctionCall
{
  private Expression _arg1 = null;
  private Expression _arg2 = null;
  private Type _arg1Type;
  
  public DocumentCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    int i = argumentCount();
    Object localObject;
    if ((i < 1) || (i > 2))
    {
      localObject = new ErrorMsg("ILLEGAL_ARG_ERR", this);
      throw new TypeCheckError((ErrorMsg)localObject);
    }
    if (getStylesheet() == null)
    {
      localObject = new ErrorMsg("ILLEGAL_ARG_ERR", this);
      throw new TypeCheckError((ErrorMsg)localObject);
    }
    _arg1 = argument(0);
    if (_arg1 == null)
    {
      localObject = new ErrorMsg("DOCUMENT_ARG_ERR", this);
      throw new TypeCheckError((ErrorMsg)localObject);
    }
    _arg1Type = _arg1.typeCheck(paramSymbolTable);
    if ((_arg1Type != Type.NodeSet) && (_arg1Type != Type.String)) {
      _arg1 = new CastExpr(_arg1, Type.String);
    }
    if (i == 2)
    {
      _arg2 = argument(1);
      if (_arg2 == null)
      {
        localObject = new ErrorMsg("DOCUMENT_ARG_ERR", this);
        throw new TypeCheckError((ErrorMsg)localObject);
      }
      localObject = _arg2.typeCheck(paramSymbolTable);
      if (((Type)localObject).identicalTo(Type.Node))
      {
        _arg2 = new CastExpr(_arg2, Type.NodeSet);
      }
      else if (!((Type)localObject).identicalTo(Type.NodeSet))
      {
        ErrorMsg localErrorMsg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
        throw new TypeCheckError(localErrorMsg);
      }
    }
    return _type = Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = argumentCount();
    int j = localConstantPoolGen.addFieldref(paramClassGenerator.getClassName(), "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    String str = null;
    if (i == 1) {
      str = "(Ljava/lang/Object;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
    } else {
      str = "(Ljava/lang/Object;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
    }
    int k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.LoadDocument", "documentF", str);
    _arg1.translate(paramClassGenerator, paramMethodGenerator);
    if (_arg1Type == Type.NodeSet) {
      _arg1.startIterator(paramClassGenerator, paramMethodGenerator);
    }
    if (i == 2)
    {
      _arg2.translate(paramClassGenerator, paramMethodGenerator);
      _arg2.startIterator(paramClassGenerator, paramMethodGenerator);
    }
    localInstructionList.append(new PUSH(localConstantPoolGen, getStylesheet().getSystemId()));
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(DUP);
    localInstructionList.append(new GETFIELD(j));
    localInstructionList.append(new INVOKESTATIC(k));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\DocumentCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */