package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Instruction;

class OutlineableChunkStart
  extends MarkerInstruction
{
  public static final Instruction OUTLINEABLECHUNKSTART = new OutlineableChunkStart();
  
  private OutlineableChunkStart() {}
  
  public String getName()
  {
    return OutlineableChunkStart.class.getName();
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\OutlineableChunkStart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */