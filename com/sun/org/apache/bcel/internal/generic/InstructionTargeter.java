package com.sun.org.apache.bcel.internal.generic;

public abstract interface InstructionTargeter
{
  public abstract boolean containsTarget(InstructionHandle paramInstructionHandle);
  
  public abstract void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\InstructionTargeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */