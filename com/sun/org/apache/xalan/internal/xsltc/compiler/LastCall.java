package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;

final class LastCall
  extends FunctionCall
{
  public LastCall(QName paramQName)
  {
    super(paramQName);
  }
  
  public boolean hasPositionCall()
  {
    return true;
  }
  
  public boolean hasLastCall()
  {
    return true;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if ((paramMethodGenerator instanceof CompareGenerator))
    {
      localInstructionList.append(((CompareGenerator)paramMethodGenerator).loadLastNode());
    }
    else if ((paramMethodGenerator instanceof TestGenerator))
    {
      localInstructionList.append(new ILOAD(3));
    }
    else
    {
      ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
      int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "getLast", "()I");
      localInstructionList.append(paramMethodGenerator.loadIterator());
      localInstructionList.append(new INVOKEINTERFACE(i, 1));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LastCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */