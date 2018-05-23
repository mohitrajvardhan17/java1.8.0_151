package com.sun.org.apache.bcel.internal.generic;

public abstract class IfInstruction
  extends BranchInstruction
  implements StackConsumer
{
  IfInstruction() {}
  
  protected IfInstruction(short paramShort, InstructionHandle paramInstructionHandle)
  {
    super(paramShort, paramInstructionHandle);
  }
  
  public abstract IfInstruction negate();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IfInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */