package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantString
  extends Constant
  implements ConstantObject
{
  private int string_index;
  
  public ConstantString(ConstantString paramConstantString)
  {
    this(paramConstantString.getStringIndex());
  }
  
  ConstantString(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readUnsignedShort());
  }
  
  public ConstantString(int paramInt)
  {
    super((byte)8);
    string_index = paramInt;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantString(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(tag);
    paramDataOutputStream.writeShort(string_index);
  }
  
  public final int getStringIndex()
  {
    return string_index;
  }
  
  public final void setStringIndex(int paramInt)
  {
    string_index = paramInt;
  }
  
  public final String toString()
  {
    return super.toString() + "(string_index = " + string_index + ")";
  }
  
  public Object getConstantValue(ConstantPool paramConstantPool)
  {
    Constant localConstant = paramConstantPool.getConstant(string_index, (byte)1);
    return ((ConstantUtf8)localConstant).getBytes();
  }
  
  public String getBytes(ConstantPool paramConstantPool)
  {
    return (String)getConstantValue(paramConstantPool);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */