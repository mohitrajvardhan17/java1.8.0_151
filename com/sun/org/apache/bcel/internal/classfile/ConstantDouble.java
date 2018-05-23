package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantDouble
  extends Constant
  implements ConstantObject
{
  private double bytes;
  
  public ConstantDouble(double paramDouble)
  {
    super((byte)6);
    bytes = paramDouble;
  }
  
  public ConstantDouble(ConstantDouble paramConstantDouble)
  {
    this(paramConstantDouble.getBytes());
  }
  
  ConstantDouble(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readDouble());
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantDouble(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(tag);
    paramDataOutputStream.writeDouble(bytes);
  }
  
  public final double getBytes()
  {
    return bytes;
  }
  
  public final void setBytes(double paramDouble)
  {
    bytes = paramDouble;
  }
  
  public final String toString()
  {
    return super.toString() + "(bytes = " + bytes + ")";
  }
  
  public Object getConstantValue(ConstantPool paramConstantPool)
  {
    return new Double(bytes);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */