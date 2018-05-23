package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class NameCall
  extends NameBase
{
  public NameCall(QName paramQName)
  {
    super(paramQName);
  }
  
  public NameCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeNameX", "(I)Ljava/lang/String;");
    super.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new INVOKEINTERFACE(i, 2));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\NameCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */