package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StackMapType
  implements Cloneable
{
  private byte type;
  private int index = -1;
  private ConstantPool constant_pool;
  
  StackMapType(DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramDataInputStream.readByte(), -1, paramConstantPool);
    if (hasIndex()) {
      setIndex(paramDataInputStream.readShort());
    }
    setConstantPool(paramConstantPool);
  }
  
  public StackMapType(byte paramByte, int paramInt, ConstantPool paramConstantPool)
  {
    setType(paramByte);
    setIndex(paramInt);
    setConstantPool(paramConstantPool);
  }
  
  public void setType(byte paramByte)
  {
    if ((paramByte < 0) || (paramByte > 8)) {
      throw new RuntimeException("Illegal type for StackMapType: " + paramByte);
    }
    type = paramByte;
  }
  
  public byte getType()
  {
    return type;
  }
  
  public void setIndex(int paramInt)
  {
    index = paramInt;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(type);
    if (hasIndex()) {
      paramDataOutputStream.writeShort(getIndex());
    }
  }
  
  public final boolean hasIndex()
  {
    return (type == 7) || (type == 8);
  }
  
  private String printIndex()
  {
    if (type == 7) {
      return ", class=" + constant_pool.constantToString(index, (byte)7);
    }
    if (type == 8) {
      return ", offset=" + index;
    }
    return "";
  }
  
  public final String toString()
  {
    return "(type=" + com.sun.org.apache.bcel.internal.Constants.ITEM_NAMES[type] + printIndex() + ")";
  }
  
  public StackMapType copy()
  {
    try
    {
      return (StackMapType)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public final ConstantPool getConstantPool()
  {
    return constant_pool;
  }
  
  public final void setConstantPool(ConstantPool paramConstantPool)
  {
    constant_pool = paramConstantPool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\StackMapType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */