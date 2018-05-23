package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class MatchGenerator
  extends MethodGenerator
{
  private static int CURRENT_INDEX = 1;
  private int _iteratorIndex = -1;
  private final Instruction _iloadCurrent = new ILOAD(CURRENT_INDEX);
  private final Instruction _istoreCurrent = new ISTORE(CURRENT_INDEX);
  private Instruction _aloadDom;
  
  public MatchGenerator(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen)
  {
    super(paramInt, paramType, paramArrayOfType, paramArrayOfString, paramString1, paramString2, paramInstructionList, paramConstantPoolGen);
  }
  
  public Instruction loadCurrentNode()
  {
    return _iloadCurrent;
  }
  
  public Instruction storeCurrentNode()
  {
    return _istoreCurrent;
  }
  
  public int getHandlerIndex()
  {
    return -1;
  }
  
  public Instruction loadDOM()
  {
    return _aloadDom;
  }
  
  public void setDomIndex(int paramInt)
  {
    _aloadDom = new ALOAD(paramInt);
  }
  
  public int getIteratorIndex()
  {
    return _iteratorIndex;
  }
  
  public void setIteratorIndex(int paramInt)
  {
    _iteratorIndex = paramInt;
  }
  
  public int getLocalIndex(String paramString)
  {
    if (paramString.equals("current")) {
      return CURRENT_INDEX;
    }
    return super.getLocalIndex(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\MatchGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */