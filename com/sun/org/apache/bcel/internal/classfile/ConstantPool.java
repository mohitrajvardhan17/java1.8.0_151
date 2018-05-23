package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class ConstantPool
  implements Cloneable, Node, Serializable
{
  private int constant_pool_count;
  private Constant[] constant_pool;
  
  public ConstantPool(Constant[] paramArrayOfConstant)
  {
    setConstantPool(paramArrayOfConstant);
  }
  
  ConstantPool(DataInputStream paramDataInputStream)
    throws IOException, ClassFormatException
  {
    constant_pool_count = paramDataInputStream.readUnsignedShort();
    constant_pool = new Constant[constant_pool_count];
    for (int j = 1; j < constant_pool_count; j++)
    {
      constant_pool[j] = Constant.readConstant(paramDataInputStream);
      int i = constant_pool[j].getTag();
      if ((i == 6) || (i == 5)) {
        j++;
      }
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantPool(this);
  }
  
  public String constantToString(Constant paramConstant)
    throws ClassFormatException
  {
    int j = paramConstant.getTag();
    int i;
    String str;
    switch (j)
    {
    case 7: 
      i = ((ConstantClass)paramConstant).getNameIndex();
      paramConstant = getConstant(i, (byte)1);
      str = Utility.compactClassName(((ConstantUtf8)paramConstant).getBytes(), false);
      break;
    case 8: 
      i = ((ConstantString)paramConstant).getStringIndex();
      paramConstant = getConstant(i, (byte)1);
      str = "\"" + escape(((ConstantUtf8)paramConstant).getBytes()) + "\"";
      break;
    case 1: 
      str = ((ConstantUtf8)paramConstant).getBytes();
      break;
    case 6: 
      str = "" + ((ConstantDouble)paramConstant).getBytes();
      break;
    case 4: 
      str = "" + ((ConstantFloat)paramConstant).getBytes();
      break;
    case 5: 
      str = "" + ((ConstantLong)paramConstant).getBytes();
      break;
    case 3: 
      str = "" + ((ConstantInteger)paramConstant).getBytes();
      break;
    case 12: 
      str = constantToString(((ConstantNameAndType)paramConstant).getNameIndex(), (byte)1) + " " + constantToString(((ConstantNameAndType)paramConstant).getSignatureIndex(), (byte)1);
      break;
    case 9: 
    case 10: 
    case 11: 
      str = constantToString(((ConstantCP)paramConstant).getClassIndex(), (byte)7) + "." + constantToString(((ConstantCP)paramConstant).getNameAndTypeIndex(), (byte)12);
      break;
    case 2: 
    default: 
      throw new RuntimeException("Unknown constant type " + j);
    }
    return str;
  }
  
  private static final String escape(String paramString)
  {
    int i = paramString.length();
    StringBuffer localStringBuffer = new StringBuffer(i + 5);
    char[] arrayOfChar = paramString.toCharArray();
    for (int j = 0; j < i; j++) {
      switch (arrayOfChar[j])
      {
      case '\n': 
        localStringBuffer.append("\\n");
        break;
      case '\r': 
        localStringBuffer.append("\\r");
        break;
      case '\t': 
        localStringBuffer.append("\\t");
        break;
      case '\b': 
        localStringBuffer.append("\\b");
        break;
      case '"': 
        localStringBuffer.append("\\\"");
        break;
      default: 
        localStringBuffer.append(arrayOfChar[j]);
      }
    }
    return localStringBuffer.toString();
  }
  
  public String constantToString(int paramInt, byte paramByte)
    throws ClassFormatException
  {
    Constant localConstant = getConstant(paramInt, paramByte);
    return constantToString(localConstant);
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(constant_pool_count);
    for (int i = 1; i < constant_pool_count; i++) {
      if (constant_pool[i] != null) {
        constant_pool[i].dump(paramDataOutputStream);
      }
    }
  }
  
  public Constant getConstant(int paramInt)
  {
    if ((paramInt >= constant_pool.length) || (paramInt < 0)) {
      throw new ClassFormatException("Invalid constant pool reference: " + paramInt + ". Constant pool size is: " + constant_pool.length);
    }
    return constant_pool[paramInt];
  }
  
  public Constant getConstant(int paramInt, byte paramByte)
    throws ClassFormatException
  {
    Constant localConstant = getConstant(paramInt);
    if (localConstant == null) {
      throw new ClassFormatException("Constant pool at index " + paramInt + " is null.");
    }
    if (localConstant.getTag() == paramByte) {
      return localConstant;
    }
    throw new ClassFormatException("Expected class `" + com.sun.org.apache.bcel.internal.Constants.CONSTANT_NAMES[paramByte] + "' at index " + paramInt + " and got " + localConstant);
  }
  
  public Constant[] getConstantPool()
  {
    return constant_pool;
  }
  
  public String getConstantString(int paramInt, byte paramByte)
    throws ClassFormatException
  {
    Constant localConstant = getConstant(paramInt, paramByte);
    int i;
    switch (paramByte)
    {
    case 7: 
      i = ((ConstantClass)localConstant).getNameIndex();
      break;
    case 8: 
      i = ((ConstantString)localConstant).getStringIndex();
      break;
    default: 
      throw new RuntimeException("getConstantString called with illegal tag " + paramByte);
    }
    localConstant = getConstant(i, (byte)1);
    return ((ConstantUtf8)localConstant).getBytes();
  }
  
  public int getLength()
  {
    return constant_pool_count;
  }
  
  public void setConstant(int paramInt, Constant paramConstant)
  {
    constant_pool[paramInt] = paramConstant;
  }
  
  public void setConstantPool(Constant[] paramArrayOfConstant)
  {
    constant_pool = paramArrayOfConstant;
    constant_pool_count = (paramArrayOfConstant == null ? 0 : paramArrayOfConstant.length);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 1; i < constant_pool_count; i++) {
      localStringBuffer.append(i + ")" + constant_pool[i] + "\n");
    }
    return localStringBuffer.toString();
  }
  
  public ConstantPool copy()
  {
    ConstantPool localConstantPool = null;
    try
    {
      localConstantPool = (ConstantPool)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    constant_pool = new Constant[constant_pool_count];
    for (int i = 1; i < constant_pool_count; i++) {
      if (constant_pool[i] != null) {
        constant_pool[i] = constant_pool[i].copy();
      }
    }
    return localConstantPool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */