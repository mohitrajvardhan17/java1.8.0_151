package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public final class Synthetic
  extends Attribute
{
  private byte[] bytes;
  
  public Synthetic(Synthetic paramSynthetic)
  {
    this(paramSynthetic.getNameIndex(), paramSynthetic.getLength(), paramSynthetic.getBytes(), paramSynthetic.getConstantPool());
  }
  
  public Synthetic(int paramInt1, int paramInt2, byte[] paramArrayOfByte, ConstantPool paramConstantPool)
  {
    super((byte)7, paramInt1, paramInt2, paramConstantPool);
    bytes = paramArrayOfByte;
  }
  
  Synthetic(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (byte[])null, paramConstantPool);
    if (paramInt2 > 0)
    {
      bytes = new byte[paramInt2];
      paramDataInputStream.readFully(bytes);
      System.err.println("Synthetic attribute with length > 0");
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitSynthetic(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    if (length > 0) {
      paramDataOutputStream.write(bytes, 0, length);
    }
  }
  
  public final byte[] getBytes()
  {
    return bytes;
  }
  
  public final void setBytes(byte[] paramArrayOfByte)
  {
    bytes = paramArrayOfByte;
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("Synthetic");
    if (length > 0) {
      localStringBuffer.append(" " + Utility.toHexString(bytes));
    }
    return localStringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    Synthetic localSynthetic = (Synthetic)clone();
    if (bytes != null) {
      bytes = ((byte[])bytes.clone());
    }
    constant_pool = paramConstantPool;
    return localSynthetic;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Synthetic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */