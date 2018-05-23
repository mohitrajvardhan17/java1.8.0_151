package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class ProcessingInstruction
  extends Instruction
{
  private AttributeValue _name;
  private boolean _isLiteral = false;
  
  ProcessingInstruction() {}
  
  public void parseContents(Parser paramParser)
  {
    String str = getAttribute("name");
    if (str.length() > 0)
    {
      _isLiteral = Util.isLiteral(str);
      if ((_isLiteral) && (!XML11Char.isXML11ValidNCName(str)))
      {
        ErrorMsg localErrorMsg = new ErrorMsg("INVALID_NCNAME_ERR", str, this);
        paramParser.reportError(3, localErrorMsg);
      }
      _name = AttributeValue.create(this, str, paramParser);
    }
    else
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
    }
    if (str.equals("xml")) {
      reportError(this, paramParser, "ILLEGAL_PI_ERR", "xml");
    }
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    _name.typeCheck(paramSymbolTable);
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (!_isLiteral)
    {
      LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), null);
      _name.translate(paramClassGenerator, paramMethodGenerator);
      localLocalVariableGen.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
      localInstructionList.append(new ALOAD(localLocalVariableGen.getIndex()));
      int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "checkNCName", "(Ljava/lang/String;)V");
      localInstructionList.append(new INVOKESTATIC(j));
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(DUP);
      localLocalVariableGen.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen.getIndex())));
    }
    else
    {
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(DUP);
      _name.translate(paramClassGenerator, paramMethodGenerator);
    }
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;")));
    localInstructionList.append(DUP);
    localInstructionList.append(paramMethodGenerator.storeHandler());
    translateContents(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValueOfPI", "()Ljava/lang/String;")));
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "processingInstruction", "(Ljava/lang/String;Ljava/lang/String;)V");
    localInstructionList.append(new INVOKEINTERFACE(i, 3));
    localInstructionList.append(paramMethodGenerator.storeHandler());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ProcessingInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */