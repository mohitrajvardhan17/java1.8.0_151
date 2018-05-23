package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantFloat
  extends Constant
  implements ConstantObject
{
  private float bytes;
  
  public ConstantFloat(float paramFloat)
  {
    super((byte)4);
    bytes = paramFloat;
  }
  
  public ConstantFloat(ConstantFloat paramConstantFloat)
  {
    this(paramConstantFloat.getBytes());
  }
  
  ConstantFloat(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readFloat());
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantFloat(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(tag);
    paramDataOutputStream.writeFloat(bytes);
  }
  
  public final float getBytes()
  {
    return bytes;
  }
  
  public final void setBytes(float paramFloat)
  {
    bytes = paramFloat;
  }
  
  public final String toString()
  {
    return super.toString() + "(bytes = " + bytes + ")";
  }
  
  public Object getConstantValue(ConstantPool paramConstantPool)
  {
    return new Float(bytes);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */