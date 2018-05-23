package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantDouble;
import com.sun.org.apache.bcel.internal.classfile.ConstantLong;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;

public class LDC2_W
  extends CPInstruction
  implements PushInstruction, TypedInstruction
{
  LDC2_W() {}
  
  public LDC2_W(int paramInt)
  {
    super((short)20, paramInt);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    switch (paramConstantPoolGen.getConstantPool().getConstant(index).getTag())
    {
    case 5: 
      return Type.LONG;
    case 6: 
      return Type.DOUBLE;
    }
    throw new RuntimeException("Unknown constant type " + opcode);
  }
  
  public Number getValue(ConstantPoolGen paramConstantPoolGen)
  {
    Constant localConstant = paramConstantPoolGen.getConstantPool().getConstant(index);
    switch (localConstant.getTag())
    {
    case 5: 
      return new Long(((ConstantLong)localConstant).getBytes());
    case 6: 
      return new Double(((ConstantDouble)localConstant).getBytes());
    }
    throw new RuntimeException("Unknown or invalid constant type at " + index);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitLDC2_W(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LDC2_W.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */