package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;

public final class NodeCounterGenerator
  extends ClassGenerator
{
  private Instruction _aloadTranslet;
  
  public NodeCounterGenerator(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString, Stylesheet paramStylesheet)
  {
    super(paramString1, paramString2, paramString3, paramInt, paramArrayOfString, paramStylesheet);
  }
  
  public void setTransletIndex(int paramInt)
  {
    _aloadTranslet = new ALOAD(paramInt);
  }
  
  public Instruction loadTranslet()
  {
    return _aloadTranslet;
  }
  
  public boolean isExternal()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\NodeCounterGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */