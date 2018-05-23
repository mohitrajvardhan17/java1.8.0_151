package com.sun.org.apache.bcel.internal.generic;

import java.util.Objects;

public class ReturnaddressType
  extends Type
{
  public static final ReturnaddressType NO_TARGET = new ReturnaddressType();
  private InstructionHandle returnTarget;
  
  private ReturnaddressType()
  {
    super((byte)16, "<return address>");
  }
  
  public ReturnaddressType(InstructionHandle paramInstructionHandle)
  {
    super((byte)16, "<return address targeting " + paramInstructionHandle + ">");
    returnTarget = paramInstructionHandle;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(returnTarget);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ReturnaddressType)) {
      return false;
    }
    return returnTarget.equals(returnTarget);
  }
  
  public InstructionHandle getTarget()
  {
    return returnTarget;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ReturnaddressType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */