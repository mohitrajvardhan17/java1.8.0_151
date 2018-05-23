package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;

public abstract interface AttributeReader
{
  public abstract Attribute createAttribute(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\AttributeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */