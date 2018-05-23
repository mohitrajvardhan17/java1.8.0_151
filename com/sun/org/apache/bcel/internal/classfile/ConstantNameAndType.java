package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantNameAndType
  extends Constant
{
  private int name_index;
  private int signature_index;
  
  public ConstantNameAndType(ConstantNameAndType paramConstantNameAndType)
  {
    this(paramConstantNameAndType.getNameIndex(), paramConstantNameAndType.getSignatureIndex());
  }
  
  ConstantNameAndType(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort());
  }
  
  public ConstantNameAndType(int paramInt1, int paramInt2)
  {
    super((byte)12);
    name_index = paramInt1;
    signature_index = paramInt2;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantNameAndType(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(tag);
    paramDataOutputStream.writeShort(name_index);
    paramDataOutputStream.writeShort(signature_index);
  }
  
  public final int getNameIndex()
  {
    return name_index;
  }
  
  public final String getName(ConstantPool paramConstantPool)
  {
    return paramConstantPool.constantToString(getNameIndex(), (byte)1);
  }
  
  public final int getSignatureIndex()
  {
    return signature_index;
  }
  
  public final String getSignature(ConstantPool paramConstantPool)
  {
    return paramConstantPool.constantToString(getSignatureIndex(), (byte)1);
  }
  
  public final void setNameIndex(int paramInt)
  {
    name_index = paramInt;
  }
  
  public final void setSignatureIndex(int paramInt)
  {
    signature_index = paramInt;
  }
  
  public final String toString()
  {
    return super.toString() + "(name_index = " + name_index + ", signature_index = " + signature_index + ")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantNameAndType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */