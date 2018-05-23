package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Instruction;

class OutlineableChunkEnd
  extends MarkerInstruction
{
  public static final Instruction OUTLINEABLECHUNKEND = new OutlineableChunkEnd();
  
  private OutlineableChunkEnd() {}
  
  public String getName()
  {
    return OutlineableChunkEnd.class.getName();
  }
  
  public String toString()
  {
    return getName();
  }
  
  public String toString(boolean paramBoolean)
  {
    return getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\OutlineableChunkEnd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */