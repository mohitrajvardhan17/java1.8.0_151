package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public final class Deprecated
  extends Attribute
{
  private byte[] bytes;
  
  public Deprecated(Deprecated paramDeprecated)
  {
    this(paramDeprecated.getNameIndex(), paramDeprecated.getLength(), paramDeprecated.getBytes(), paramDeprecated.getConstantPool());
  }
  
  public Deprecated(int paramInt1, int paramInt2, byte[] paramArrayOfByte, ConstantPool paramConstantPool)
  {
    super((byte)8, paramInt1, paramInt2, paramConstantPool);
    bytes = paramArrayOfByte;
  }
  
  Deprecated(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (byte[])null, paramConstantPool);
    if (paramInt2 > 0)
    {
      bytes = new byte[paramInt2];
      paramDataInputStream.readFully(bytes);
      System.err.println("Deprecated attribute with length > 0");
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitDeprecated(this);
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
    return com.sun.org.apache.bcel.internal.Constants.ATTRIBUTE_NAMES[8];
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    Deprecated localDeprecated = (Deprecated)clone();
    if (bytes != null) {
      bytes = ((byte[])bytes.clone());
    }
    constant_pool = paramConstantPool;
    return localDeprecated;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Deprecated.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */