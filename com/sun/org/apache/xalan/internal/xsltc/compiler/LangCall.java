package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.FilterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class LangCall
  extends FunctionCall
{
  private Expression _lang = argument(0);
  private Type _langType;
  
  public LangCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    _langType = _lang.typeCheck(paramSymbolTable);
    if (!(_langType instanceof StringType)) {
      _lang = new CastExpr(_lang, Type.String);
    }
    return Type.Boolean;
  }
  
  public Type getType()
  {
    return Type.Boolean;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "testLanguage", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)Z");
    _lang.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(paramMethodGenerator.loadDOM());
    if ((paramClassGenerator instanceof FilterGenerator)) {
      localInstructionList.append(new ILOAD(1));
    } else {
      localInstructionList.append(paramMethodGenerator.loadContextNode());
    }
    localInstructionList.append(new INVOKESTATIC(i));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LangCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */