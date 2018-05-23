package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantClass
  extends Constant
  implements ConstantObject
{
  private int name_index;
  
  public ConstantClass(ConstantClass paramConstantClass)
  {
    this(paramConstantClass.getNameIndex());
  }
  
  ConstantClass(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readUnsignedShort());
  }
  
  public ConstantClass(int paramInt)
  {
    super((byte)7);
    name_index = paramInt;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantClass(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(tag);
    paramDataOutputStream.writeShort(name_index);
  }
  
  public final int getNameIndex()
  {
    return name_index;
  }
  
  public final void setNameIndex(int paramInt)
  {
    name_index = paramInt;
  }
  
  public Object getConstantValue(ConstantPool paramConstantPool)
  {
    Constant localConstant = paramConstantPool.getConstant(name_index, (byte)1);
    return ((ConstantUtf8)localConstant).getBytes();
  }
  
  public String getBytes(ConstantPool paramConstantPool)
  {
    return (String)getConstantValue(paramConstantPool);
  }
  
  public final String toString()
  {
    return super.toString() + "(name_index = " + name_index + ")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */