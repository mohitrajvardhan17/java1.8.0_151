package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class CopyOf
  extends Instruction
{
  private Expression _select;
  
  CopyOf() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("CopyOf");
    indent(paramInt + 4);
    Util.println("select " + _select.toString());
  }
  
  public void parseContents(Parser paramParser)
  {
    _select = paramParser.parseExpression(this, "select", null);
    if (_select.isDummy())
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "select");
      return;
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType = _select.typeCheck(paramSymbolTable);
    if ((!(localType instanceof NodeType)) && (!(localType instanceof NodeSetType)) && (!(localType instanceof ReferenceType)) && (!(localType instanceof ResultTreeType))) {
      _select = new CastExpr(_select, Type.String);
    }
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    Type localType = _select.getType();
    String str1 = "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "copy", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    String str2 = "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
    int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "copy", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    String str3 = "()I";
    int k = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getDocument", "()I");
    if ((localType instanceof NodeSetType))
    {
      localInstructionList.append(paramMethodGenerator.loadDOM());
      _select.translate(paramClassGenerator, paramMethodGenerator);
      _select.startIterator(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(new INVOKEINTERFACE(i, 3));
    }
    else if ((localType instanceof NodeType))
    {
      localInstructionList.append(paramMethodGenerator.loadDOM());
      _select.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(new INVOKEINTERFACE(j, 3));
    }
    else if ((localType instanceof ResultTreeType))
    {
      _select.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(DUP);
      localInstructionList.append(new INVOKEINTERFACE(k, 1));
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(new INVOKEINTERFACE(j, 3));
    }
    else if ((localType instanceof ReferenceType))
    {
      _select.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(paramMethodGenerator.loadCurrentNode());
      localInstructionList.append(paramMethodGenerator.loadDOM());
      int m = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "copy", "(Ljava/lang/Object;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;ILcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
      localInstructionList.append(new INVOKESTATIC(m));
    }
    else
    {
      localInstructionList.append(paramClassGenerator.loadTranslet());
      _select.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V")));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CopyOf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */