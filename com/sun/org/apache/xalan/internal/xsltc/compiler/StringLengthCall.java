package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class StringLengthCall
  extends FunctionCall
{
  public StringLengthCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (argumentCount() > 0)
    {
      argument().translate(paramClassGenerator, paramMethodGenerator);
    }
    else
    {
      localInstructionList.append(paramMethodGenerator.loadContextNode());
      Type.Node.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    }
    localInstructionList.append(new INVOKESTATIC(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "getStringLength", "(Ljava/lang/String;)I")));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\StringLengthCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */