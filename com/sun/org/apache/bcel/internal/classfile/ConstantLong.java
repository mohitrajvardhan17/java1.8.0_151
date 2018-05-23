package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantLong
  extends Constant
  implements ConstantObject
{
  private long bytes;
  
  public ConstantLong(long paramLong)
  {
    super((byte)5);
    bytes = paramLong;
  }
  
  public ConstantLong(ConstantLong paramConstantLong)
  {
    this(paramConstantLong.getBytes());
  }
  
  ConstantLong(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readLong());
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantLong(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(tag);
    paramDataOutputStream.writeLong(bytes);
  }
  
  public final long getBytes()
  {
    return bytes;
  }
  
  public final void setBytes(long paramLong)
  {
    bytes = paramLong;
  }
  
  public final String toString()
  {
    return super.toString() + "(bytes = " + bytes + ")";
  }
  
  public Object getConstantValue(ConstantPool paramConstantPool)
  {
    return new Long(bytes);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */