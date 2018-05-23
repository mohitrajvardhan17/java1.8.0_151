package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class RtMethodGenerator
  extends MethodGenerator
{
  private static final int HANDLER_INDEX = 2;
  private final Instruction _astoreHandler = new ASTORE(2);
  private final Instruction _aloadHandler = new ALOAD(2);
  
  public RtMethodGenerator(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen)
  {
    super(paramInt, paramType, paramArrayOfType, paramArrayOfString, paramString1, paramString2, paramInstructionList, paramConstantPoolGen);
  }
  
  public int getIteratorIndex()
  {
    return -1;
  }
  
  public final Instruction storeHandler()
  {
    return _astoreHandler;
  }
  
  public final Instruction loadHandler()
  {
    return _aloadHandler;
  }
  
  public int getLocalIndex(String paramString)
  {
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\RtMethodGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */