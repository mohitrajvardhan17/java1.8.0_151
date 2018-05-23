package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public abstract class Attribute
  implements Cloneable, Node, Serializable
{
  protected int name_index;
  protected int length;
  protected byte tag;
  protected ConstantPool constant_pool;
  private static HashMap readers = new HashMap();
  
  protected Attribute(byte paramByte, int paramInt1, int paramInt2, ConstantPool paramConstantPool)
  {
    tag = paramByte;
    name_index = paramInt1;
    length = paramInt2;
    constant_pool = paramConstantPool;
  }
  
  public abstract void accept(Visitor paramVisitor);
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(name_index);
    paramDataOutputStream.writeInt(length);
  }
  
  public static void addAttributeReader(String paramString, AttributeReader paramAttributeReader)
  {
    readers.put(paramString, paramAttributeReader);
  }
  
  public static void removeAttributeReader(String paramString)
  {
    readers.remove(paramString);
  }
  
  public static final Attribute readAttribute(DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException, ClassFormatException
  {
    int k = -1;
    int i = paramDataInputStream.readUnsignedShort();
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)paramConstantPool.getConstant(i, (byte)1);
    String str = localConstantUtf8.getBytes();
    int j = paramDataInputStream.readInt();
    for (int m = 0; m < 13; m = (byte)(m + 1)) {
      if (str.equals(com.sun.org.apache.bcel.internal.Constants.ATTRIBUTE_NAMES[m]))
      {
        k = m;
        break;
      }
    }
    switch (k)
    {
    case -1: 
      AttributeReader localAttributeReader = (AttributeReader)readers.get(str);
      if (localAttributeReader != null) {
        return localAttributeReader.createAttribute(i, j, paramDataInputStream, paramConstantPool);
      }
      return new Unknown(i, j, paramDataInputStream, paramConstantPool);
    case 1: 
      return new ConstantValue(i, j, paramDataInputStream, paramConstantPool);
    case 0: 
      return new SourceFile(i, j, paramDataInputStream, paramConstantPool);
    case 2: 
      return new Code(i, j, paramDataInputStream, paramConstantPool);
    case 3: 
      return new ExceptionTable(i, j, paramDataInputStream, paramConstantPool);
    case 4: 
      return new LineNumberTable(i, j, paramDataInputStream, paramConstantPool);
    case 5: 
      return new LocalVariableTable(i, j, paramDataInputStream, paramConstantPool);
    case 12: 
      return new LocalVariableTypeTable(i, j, paramDataInputStream, paramConstantPool);
    case 6: 
      return new InnerClasses(i, j, paramDataInputStream, paramConstantPool);
    case 7: 
      return new Synthetic(i, j, paramDataInputStream, paramConstantPool);
    case 8: 
      return new Deprecated(i, j, paramDataInputStream, paramConstantPool);
    case 9: 
      return new PMGClass(i, j, paramDataInputStream, paramConstantPool);
    case 10: 
      return new Signature(i, j, paramDataInputStream, paramConstantPool);
    case 11: 
      return new StackMap(i, j, paramDataInputStream, paramConstantPool);
    }
    throw new IllegalStateException("Ooops! default case reached.");
  }
  
  public final int getLength()
  {
    return length;
  }
  
  public final void setLength(int paramInt)
  {
    length = paramInt;
  }
  
  public final void setNameIndex(int paramInt)
  {
    name_index = paramInt;
  }
  
  public final int getNameIndex()
  {
    return name_index;
  }
  
  public final byte getTag()
  {
    return tag;
  }
  
  public final ConstantPool getConstantPool()
  {
    return constant_pool;
  }
  
  public final void setConstantPool(ConstantPool paramConstantPool)
  {
    constant_pool = paramConstantPool;
  }
  
  public Object clone()
  {
    Object localObject = null;
    try
    {
      localObject = super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
    }
    return localObject;
  }
  
  public abstract Attribute copy(ConstantPool paramConstantPool);
  
  public String toString()
  {
    return com.sun.org.apache.bcel.internal.Constants.ATTRIBUTE_NAMES[tag];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */