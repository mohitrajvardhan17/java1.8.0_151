package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantValue
  extends Attribute
{
  private int constantvalue_index;
  
  public ConstantValue(ConstantValue paramConstantValue)
  {
    this(paramConstantValue.getNameIndex(), paramConstantValue.getLength(), paramConstantValue.getConstantValueIndex(), paramConstantValue.getConstantPool());
  }
  
  ConstantValue(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramConstantPool);
  }
  
  public ConstantValue(int paramInt1, int paramInt2, int paramInt3, ConstantPool paramConstantPool)
  {
    super((byte)1, paramInt1, paramInt2, paramConstantPool);
    constantvalue_index = paramInt3;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantValue(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(constantvalue_index);
  }
  
  public final int getConstantValueIndex()
  {
    return constantvalue_index;
  }
  
  public final void setConstantValueIndex(int paramInt)
  {
    constantvalue_index = paramInt;
  }
  
  public final String toString()
  {
    Constant localConstant = constant_pool.getConstant(constantvalue_index);
    String str;
    switch (localConstant.getTag())
    {
    case 5: 
      str = "" + ((ConstantLong)localConstant).getBytes();
      break;
    case 4: 
      str = "" + ((ConstantFloat)localConstant).getBytes();
      break;
    case 6: 
      str = "" + ((ConstantDouble)localConstant).getBytes();
      break;
    case 3: 
      str = "" + ((ConstantInteger)localConstant).getBytes();
      break;
    case 8: 
      int i = ((ConstantString)localConstant).getStringIndex();
      localConstant = constant_pool.getConstant(i, (byte)1);
      str = "\"" + Utility.convertString(((ConstantUtf8)localConstant).getBytes()) + "\"";
      break;
    case 7: 
    default: 
      throw new IllegalStateException("Type of ConstValue invalid: " + localConstant);
    }
    return str;
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    ConstantValue localConstantValue = (ConstantValue)clone();
    constant_pool = paramConstantPool;
    return localConstantValue;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */