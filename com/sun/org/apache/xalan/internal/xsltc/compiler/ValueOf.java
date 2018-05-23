package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ValueOf
  extends Instruction
{
  private Expression _select;
  private boolean _escaping = true;
  private boolean _isString = false;
  
  ValueOf() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("ValueOf");
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
    String str = getAttribute("disable-output-escaping");
    if ((str != null) && (str.equals("yes"))) {
      _escaping = false;
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType = _select.typeCheck(paramSymbolTable);
    if ((localType != null) && (!localType.identicalTo(Type.Node))) {
      if (localType.identicalTo(Type.NodeSet))
      {
        _select = new CastExpr(_select, Type.Node);
      }
      else
      {
        _isString = true;
        if (!localType.identicalTo(Type.String)) {
          _select = new CastExpr(_select, Type.String);
        }
        _isString = true;
      }
    }
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "setEscaping", "(Z)Z");
    if (!_escaping)
    {
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(new PUSH(localConstantPoolGen, false));
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
    }
    int j;
    if (_isString)
    {
      j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      localInstructionList.append(paramClassGenerator.loadTranslet());
      _select.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(new INVOKEVIRTUAL(j));
    }
    else
    {
      j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "characters", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      _select.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(new INVOKEINTERFACE(j, 3));
    }
    if (!_escaping)
    {
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(SWAP);
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
      localInstructionList.append(POP);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ValueOf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */