package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class FieldOrMethod
  extends AccessFlags
  implements Cloneable, Node
{
  protected int name_index;
  protected int signature_index;
  protected int attributes_count;
  protected Attribute[] attributes;
  protected ConstantPool constant_pool;
  
  FieldOrMethod() {}
  
  protected FieldOrMethod(FieldOrMethod paramFieldOrMethod)
  {
    this(paramFieldOrMethod.getAccessFlags(), paramFieldOrMethod.getNameIndex(), paramFieldOrMethod.getSignatureIndex(), paramFieldOrMethod.getAttributes(), paramFieldOrMethod.getConstantPool());
  }
  
  protected FieldOrMethod(DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException, ClassFormatException
  {
    this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), null, paramConstantPool);
    attributes_count = paramDataInputStream.readUnsignedShort();
    attributes = new Attribute[attributes_count];
    for (int i = 0; i < attributes_count; i++) {
      attributes[i] = Attribute.readAttribute(paramDataInputStream, paramConstantPool);
    }
  }
  
  protected FieldOrMethod(int paramInt1, int paramInt2, int paramInt3, Attribute[] paramArrayOfAttribute, ConstantPool paramConstantPool)
  {
    access_flags = paramInt1;
    name_index = paramInt2;
    signature_index = paramInt3;
    constant_pool = paramConstantPool;
    setAttributes(paramArrayOfAttribute);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(access_flags);
    paramDataOutputStream.writeShort(name_index);
    paramDataOutputStream.writeShort(signature_index);
    paramDataOutputStream.writeShort(attributes_count);
    for (int i = 0; i < attributes_count; i++) {
      attributes[i].dump(paramDataOutputStream);
    }
  }
  
  public final Attribute[] getAttributes()
  {
    return attributes;
  }
  
  public final void setAttributes(Attribute[] paramArrayOfAttribute)
  {
    attributes = paramArrayOfAttribute;
    attributes_count = (paramArrayOfAttribute == null ? 0 : paramArrayOfAttribute.length);
  }
  
  public final ConstantPool getConstantPool()
  {
    return constant_pool;
  }
  
  public final void setConstantPool(ConstantPool paramConstantPool)
  {
    constant_pool = paramConstantPool;
  }
  
  public final int getNameIndex()
  {
    return name_index;
  }
  
  public final void setNameIndex(int paramInt)
  {
    name_index = paramInt;
  }
  
  public final int getSignatureIndex()
  {
    return signature_index;
  }
  
  public final void setSignatureIndex(int paramInt)
  {
    signature_index = paramInt;
  }
  
  public final String getName()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(name_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  public final String getSignature()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(signature_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  protected FieldOrMethod copy_(ConstantPool paramConstantPool)
  {
    FieldOrMethod localFieldOrMethod = null;
    try
    {
      localFieldOrMethod = (FieldOrMethod)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    constant_pool = paramConstantPool;
    attributes = new Attribute[attributes_count];
    for (int i = 0; i < attributes_count; i++) {
      attributes[i] = attributes[i].copy(paramConstantPool);
    }
    return localFieldOrMethod;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\FieldOrMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */