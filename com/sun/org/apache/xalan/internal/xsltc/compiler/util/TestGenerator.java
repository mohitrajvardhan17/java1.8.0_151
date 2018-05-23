package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class TestGenerator
  extends MethodGenerator
{
  private static int CONTEXT_NODE_INDEX = 1;
  private static int CURRENT_NODE_INDEX = 4;
  private static int ITERATOR_INDEX = 6;
  private Instruction _aloadDom;
  private final Instruction _iloadCurrent = new ILOAD(CURRENT_NODE_INDEX);
  private final Instruction _iloadContext = new ILOAD(CONTEXT_NODE_INDEX);
  private final Instruction _istoreCurrent = new ISTORE(CURRENT_NODE_INDEX);
  private final Instruction _istoreContext = new ILOAD(CONTEXT_NODE_INDEX);
  private final Instruction _astoreIterator = new ASTORE(ITERATOR_INDEX);
  private final Instruction _aloadIterator = new ALOAD(ITERATOR_INDEX);
  
  public TestGenerator(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen)
  {
    super(paramInt, paramType, paramArrayOfType, paramArrayOfString, paramString1, paramString2, paramInstructionList, paramConstantPoolGen);
  }
  
  public int getHandlerIndex()
  {
    return -1;
  }
  
  public int getIteratorIndex()
  {
    return ITERATOR_INDEX;
  }
  
  public void setDomIndex(int paramInt)
  {
    _aloadDom = new ALOAD(paramInt);
  }
  
  public Instruction loadDOM()
  {
    return _aloadDom;
  }
  
  public Instruction loadCurrentNode()
  {
    return _iloadCurrent;
  }
  
  public Instruction loadContextNode()
  {
    return _iloadContext;
  }
  
  public Instruction storeContextNode()
  {
    return _istoreContext;
  }
  
  public Instruction storeCurrentNode()
  {
    return _istoreCurrent;
  }
  
  public Instruction storeIterator()
  {
    return _astoreIterator;
  }
  
  public Instruction loadIterator()
  {
    return _aloadIterator;
  }
  
  public int getLocalIndex(String paramString)
  {
    if (paramString.equals("current")) {
      return CURRENT_NODE_INDEX;
    }
    return super.getLocalIndex(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\TestGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */