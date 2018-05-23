package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public abstract class ReturnInstruction
  extends Instruction
  implements ExceptionThrower, TypedInstruction, StackConsumer
{
  ReturnInstruction() {}
  
  protected ReturnInstruction(short paramShort)
  {
    super(paramShort, (short)1);
  }
  
  public Type getType()
  {
    switch (opcode)
    {
    case 172: 
      return Type.INT;
    case 173: 
      return Type.LONG;
    case 174: 
      return Type.FLOAT;
    case 175: 
      return Type.DOUBLE;
    case 176: 
      return Type.OBJECT;
    case 177: 
      return Type.VOID;
    }
    throw new ClassGenException("Unknown type " + opcode);
  }
  
  public Class[] getExceptions()
  {
    return new Class[] { ExceptionConstants.ILLEGAL_MONITOR_STATE };
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return getType();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ReturnInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */