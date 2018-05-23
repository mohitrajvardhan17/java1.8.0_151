package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ConstantCP
  extends Constant
{
  protected int class_index;
  protected int name_and_type_index;
  
  public ConstantCP(ConstantCP paramConstantCP)
  {
    this(paramConstantCP.getTag(), paramConstantCP.getClassIndex(), paramConstantCP.getNameAndTypeIndex());
  }
  
  ConstantCP(byte paramByte, DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramByte, paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort());
  }
  
  protected ConstantCP(byte paramByte, int paramInt1, int paramInt2)
  {
    super(paramByte);
    class_index = paramInt1;
    name_and_type_index = paramInt2;
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(tag);
    paramDataOutputStream.writeShort(class_index);
    paramDataOutputStream.writeShort(name_and_type_index);
  }
  
  public final int getClassIndex()
  {
    return class_index;
  }
  
  public final int getNameAndTypeIndex()
  {
    return name_and_type_index;
  }
  
  public final void setClassIndex(int paramInt)
  {
    class_index = paramInt;
  }
  
  public String getClass(ConstantPool paramConstantPool)
  {
    return paramConstantPool.constantToString(class_index, (byte)7);
  }
  
  public final void setNameAndTypeIndex(int paramInt)
  {
    name_and_type_index = paramInt;
  }
  
  public final String toString()
  {
    return super.toString() + "(class_index = " + class_index + ", name_and_type_index = " + name_and_type_index + ")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantCP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */