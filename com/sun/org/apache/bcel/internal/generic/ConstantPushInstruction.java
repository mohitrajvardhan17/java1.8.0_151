package com.sun.org.apache.bcel.internal.generic;

public abstract interface ConstantPushInstruction
  extends PushInstruction, TypedInstruction
{
  public abstract Number getValue();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ConstantPushInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */