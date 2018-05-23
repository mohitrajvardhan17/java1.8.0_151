package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantInteger
  extends Constant
  implements ConstantObject
{
  private int bytes;
  
  public ConstantInteger(int paramInt)
  {
    super((byte)3);
    bytes = paramInt;
  }
  
  public ConstantInteger(ConstantInteger paramConstantInteger)
  {
    this(paramConstantInteger.getBytes());
  }
  
  ConstantInteger(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readInt());
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantInteger(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(tag);
    paramDataOutputStream.writeInt(bytes);
  }
  
  public final int getBytes()
  {
    return bytes;
  }
  
  public final void setBytes(int paramInt)
  {
    bytes = paramInt;
  }
  
  public final String toString()
  {
    return super.toString() + "(bytes = " + bytes + ")";
  }
  
  public Object getConstantValue(ConstantPool paramConstantPool)
  {
    return new Integer(bytes);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */