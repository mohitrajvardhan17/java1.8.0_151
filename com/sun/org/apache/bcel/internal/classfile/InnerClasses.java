package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class InnerClasses
  extends Attribute
{
  private InnerClass[] inner_classes;
  private int number_of_classes;
  
  public InnerClasses(InnerClasses paramInnerClasses)
  {
    this(paramInnerClasses.getNameIndex(), paramInnerClasses.getLength(), paramInnerClasses.getInnerClasses(), paramInnerClasses.getConstantPool());
  }
  
  public InnerClasses(int paramInt1, int paramInt2, InnerClass[] paramArrayOfInnerClass, ConstantPool paramConstantPool)
  {
    super((byte)6, paramInt1, paramInt2, paramConstantPool);
    setInnerClasses(paramArrayOfInnerClass);
  }
  
  InnerClasses(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (InnerClass[])null, paramConstantPool);
    number_of_classes = paramDataInputStream.readUnsignedShort();
    inner_classes = new InnerClass[number_of_classes];
    for (int i = 0; i < number_of_classes; i++) {
      inner_classes[i] = new InnerClass(paramDataInputStream);
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitInnerClasses(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(number_of_classes);
    for (int i = 0; i < number_of_classes; i++) {
      inner_classes[i].dump(paramDataOutputStream);
    }
  }
  
  public final InnerClass[] getInnerClasses()
  {
    return inner_classes;
  }
  
  public final void setInnerClasses(InnerClass[] paramArrayOfInnerClass)
  {
    inner_classes = paramArrayOfInnerClass;
    number_of_classes = (paramArrayOfInnerClass == null ? 0 : paramArrayOfInnerClass.length);
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < number_of_classes; i++) {
      localStringBuffer.append(inner_classes[i].toString(constant_pool) + "\n");
    }
    return localStringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    InnerClasses localInnerClasses = (InnerClasses)clone();
    inner_classes = new InnerClass[number_of_classes];
    for (int i = 0; i < number_of_classes; i++) {
      inner_classes[i] = inner_classes[i].copy();
    }
    constant_pool = paramConstantPool;
    return localInnerClasses;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\InnerClasses.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */