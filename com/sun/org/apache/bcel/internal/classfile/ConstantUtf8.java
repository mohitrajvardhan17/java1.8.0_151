package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantUtf8
  extends Constant
{
  private String bytes;
  
  public ConstantUtf8(ConstantUtf8 paramConstantUtf8)
  {
    this(paramConstantUtf8.getBytes());
  }
  
  ConstantUtf8(DataInputStream paramDataInputStream)
    throws IOException
  {
    super((byte)1);
    bytes = paramDataInputStream.readUTF();
  }
  
  public ConstantUtf8(String paramString)
  {
    super((byte)1);
    if (paramString == null) {
      throw new IllegalArgumentException("bytes must not be null!");
    }
    bytes = paramString;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantUtf8(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(tag);
    paramDataOutputStream.writeUTF(bytes);
  }
  
  public final String getBytes()
  {
    return bytes;
  }
  
  public final void setBytes(String paramString)
  {
    bytes = paramString;
  }
  
  public final String toString()
  {
    return super.toString() + "(\"" + Utility.replace(bytes, "\n", "\\n") + "\")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantUtf8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */