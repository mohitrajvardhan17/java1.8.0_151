package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public final class LocalVariable
  implements Constants, Cloneable, Node, Serializable
{
  private int start_pc;
  private int length;
  private int name_index;
  private int signature_index;
  private int index;
  private ConstantPool constant_pool;
  
  public LocalVariable(LocalVariable paramLocalVariable)
  {
    this(paramLocalVariable.getStartPC(), paramLocalVariable.getLength(), paramLocalVariable.getNameIndex(), paramLocalVariable.getSignatureIndex(), paramLocalVariable.getIndex(), paramLocalVariable.getConstantPool());
  }
  
  LocalVariable(DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramConstantPool);
  }
  
  public LocalVariable(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, ConstantPool paramConstantPool)
  {
    start_pc = paramInt1;
    length = paramInt2;
    name_index = paramInt3;
    signature_index = paramInt4;
    index = paramInt5;
    constant_pool = paramConstantPool;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitLocalVariable(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(start_pc);
    paramDataOutputStream.writeShort(length);
    paramDataOutputStream.writeShort(name_index);
    paramDataOutputStream.writeShort(signature_index);
    paramDataOutputStream.writeShort(index);
  }
  
  public final ConstantPool getConstantPool()
  {
    return constant_pool;
  }
  
  public final int getLength()
  {
    return length;
  }
  
  public final String getName()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(name_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  public final int getNameIndex()
  {
    return name_index;
  }
  
  public final String getSignature()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(signature_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  public final int getSignatureIndex()
  {
    return signature_index;
  }
  
  public final int getIndex()
  {
    return index;
  }
  
  public final int getStartPC()
  {
    return start_pc;
  }
  
  public final void setConstantPool(ConstantPool paramConstantPool)
  {
    constant_pool = paramConstantPool;
  }
  
  public final void setLength(int paramInt)
  {
    length = paramInt;
  }
  
  public final void setNameIndex(int paramInt)
  {
    name_index = paramInt;
  }
  
  public final void setSignatureIndex(int paramInt)
  {
    signature_index = paramInt;
  }
  
  public final void setIndex(int paramInt)
  {
    index = paramInt;
  }
  
  public final void setStartPC(int paramInt)
  {
    start_pc = paramInt;
  }
  
  public final String toString()
  {
    String str1 = getName();
    String str2 = Utility.signatureToString(getSignature());
    return "LocalVariable(start_pc = " + start_pc + ", length = " + length + ", index = " + index + ":" + str2 + " " + str1 + ")";
  }
  
  public LocalVariable copy()
  {
    try
    {
      return (LocalVariable)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LocalVariable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */