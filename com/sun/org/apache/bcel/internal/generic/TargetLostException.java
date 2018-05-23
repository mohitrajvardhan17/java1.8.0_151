package com.sun.org.apache.bcel.internal.generic;

public final class TargetLostException
  extends Exception
{
  private InstructionHandle[] targets;
  
  TargetLostException(InstructionHandle[] paramArrayOfInstructionHandle, String paramString)
  {
    super(paramString);
    targets = paramArrayOfInstructionHandle;
  }
  
  public InstructionHandle[] getTargets()
  {
    return targets;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\TargetLostException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */