package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.ConstantPool;

public abstract class FieldInstruction
  extends FieldOrMethod
  implements TypedInstruction
{
  FieldInstruction() {}
  
  protected FieldInstruction(short paramShort, int paramInt)
  {
    super(paramShort, paramInt);
  }
  
  public String toString(ConstantPool paramConstantPool)
  {
    return com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[opcode] + " " + paramConstantPool.constantToString(index, (byte)9);
  }
  
  protected int getFieldSize(ConstantPoolGen paramConstantPoolGen)
  {
    return getType(paramConstantPoolGen).getSize();
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return getFieldType(paramConstantPoolGen);
  }
  
  public Type getFieldType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.getType(getSignature(paramConstantPoolGen));
  }
  
  public String getFieldName(ConstantPoolGen paramConstantPoolGen)
  {
    return getName(paramConstantPoolGen);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\FieldInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */