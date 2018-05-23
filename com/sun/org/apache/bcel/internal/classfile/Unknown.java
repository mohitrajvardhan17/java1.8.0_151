package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public final class Unknown
  extends Attribute
{
  private byte[] bytes;
  private String name;
  private static HashMap unknown_attributes = new HashMap();
  
  static Unknown[] getUnknownAttributes()
  {
    Unknown[] arrayOfUnknown = new Unknown[unknown_attributes.size()];
    Iterator localIterator = unknown_attributes.values().iterator();
    for (int i = 0; localIterator.hasNext(); i++) {
      arrayOfUnknown[i] = ((Unknown)localIterator.next());
    }
    unknown_attributes.clear();
    return arrayOfUnknown;
  }
  
  public Unknown(Unknown paramUnknown)
  {
    this(paramUnknown.getNameIndex(), paramUnknown.getLength(), paramUnknown.getBytes(), paramUnknown.getConstantPool());
  }
  
  public Unknown(int paramInt1, int paramInt2, byte[] paramArrayOfByte, ConstantPool paramConstantPool)
  {
    super((byte)-1, paramInt1, paramInt2, paramConstantPool);
    bytes = paramArrayOfByte;
    name = ((ConstantUtf8)paramConstantPool.getConstant(paramInt1, (byte)1)).getBytes();
    unknown_attributes.put(name, this);
  }
  
  Unknown(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (byte[])null, paramConstantPool);
    if (paramInt2 > 0)
    {
      bytes = new byte[paramInt2];
      paramDataInputStream.readFully(bytes);
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitUnknown(this);
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
  
  public final String getName()
  {
    return name;
  }
  
  public final void setBytes(byte[] paramArrayOfByte)
  {
    bytes = paramArrayOfByte;
  }
  
  public final String toString()
  {
    if ((length == 0) || (bytes == null)) {
      return "(Unknown attribute " + name + ")";
    }
    String str;
    if (length > 10)
    {
      byte[] arrayOfByte = new byte[10];
      System.arraycopy(bytes, 0, arrayOfByte, 0, 10);
      str = Utility.toHexString(arrayOfByte) + "... (truncated)";
    }
    else
    {
      str = Utility.toHexString(bytes);
    }
    return "(Unknown attribute " + name + ": " + str + ")";
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    Unknown localUnknown = (Unknown)clone();
    if (bytes != null) {
      bytes = ((byte[])bytes.clone());
    }
    constant_pool = paramConstantPool;
    return localUnknown;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Unknown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */