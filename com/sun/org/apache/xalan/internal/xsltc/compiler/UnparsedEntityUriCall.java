package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class UnparsedEntityUriCall
  extends FunctionCall
{
  private Expression _entity = argument();
  
  public UnparsedEntityUriCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType = _entity.typeCheck(paramSymbolTable);
    if (!(localType instanceof StringType)) {
      _entity = new CastExpr(_entity, Type.String);
    }
    return _type = Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(paramMethodGenerator.loadDOM());
    _entity.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new INVOKEINTERFACE(localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getUnparsedEntityURI", "(Ljava/lang/String;)Ljava/lang/String;"), 2));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnparsedEntityUriCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */