package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;

public final class NodeSortRecordGenerator
  extends ClassGenerator
{
  private static final int TRANSLET_INDEX = 4;
  private final Instruction _aloadTranslet = new ALOAD(4);
  
  public NodeSortRecordGenerator(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString, Stylesheet paramStylesheet)
  {
    super(paramString1, paramString2, paramString3, paramInt, paramArrayOfString, paramStylesheet);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\NodeSortRecordGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */