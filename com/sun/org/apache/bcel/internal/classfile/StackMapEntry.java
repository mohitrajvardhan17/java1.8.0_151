package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StackMapEntry
  implements Cloneable
{
  private int byte_code_offset;
  private int number_of_locals;
  private StackMapType[] types_of_locals;
  private int number_of_stack_items;
  private StackMapType[] types_of_stack_items;
  private ConstantPool constant_pool;
  
  StackMapEntry(DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramDataInputStream.readShort(), paramDataInputStream.readShort(), null, -1, null, paramConstantPool);
    types_of_locals = new StackMapType[number_of_locals];
    for (int i = 0; i < number_of_locals; i++) {
      types_of_locals[i] = new StackMapType(paramDataInputStream, paramConstantPool);
    }
    number_of_stack_items = paramDataInputStream.readShort();
    types_of_stack_items = new StackMapType[number_of_stack_items];
    for (i = 0; i < number_of_stack_items; i++) {
      types_of_stack_items[i] = new StackMapType(paramDataInputStream, paramConstantPool);
    }
  }
  
  public StackMapEntry(int paramInt1, int paramInt2, StackMapType[] paramArrayOfStackMapType1, int paramInt3, StackMapType[] paramArrayOfStackMapType2, ConstantPool paramConstantPool)
  {
    byte_code_offset = paramInt1;
    number_of_locals = paramInt2;
    types_of_locals = paramArrayOfStackMapType1;
    number_of_stack_items = paramInt3;
    types_of_stack_items = paramArrayOfStackMapType2;
    constant_pool = paramConstantPool;
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(byte_code_offset);
    paramDataOutputStream.writeShort(number_of_locals);
    for (int i = 0; i < number_of_locals; i++) {
      types_of_locals[i].dump(paramDataOutputStream);
    }
    paramDataOutputStream.writeShort(number_of_stack_items);
    for (i = 0; i < number_of_stack_items; i++) {
      types_of_stack_items[i].dump(paramDataOutputStream);
    }
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("(offset=" + byte_code_offset);
    int i;
    if (number_of_locals > 0)
    {
      localStringBuffer.append(", locals={");
      for (i = 0; i < number_of_locals; i++)
      {
        localStringBuffer.append(types_of_locals[i]);
        if (i < number_of_locals - 1) {
          localStringBuffer.append(", ");
        }
      }
      localStringBuffer.append("}");
    }
    if (number_of_stack_items > 0)
    {
      localStringBuffer.append(", stack items={");
      for (i = 0; i < number_of_stack_items; i++)
      {
        localStringBuffer.append(types_of_stack_items[i]);
        if (i < number_of_stack_items - 1) {
          localStringBuffer.append(", ");
        }
      }
      localStringBuffer.append("}");
    }
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  
  public void setByteCodeOffset(int paramInt)
  {
    byte_code_offset = paramInt;
  }
  
  public int getByteCodeOffset()
  {
    return byte_code_offset;
  }
  
  public void setNumberOfLocals(int paramInt)
  {
    number_of_locals = paramInt;
  }
  
  public int getNumberOfLocals()
  {
    return number_of_locals;
  }
  
  public void setTypesOfLocals(StackMapType[] paramArrayOfStackMapType)
  {
    types_of_locals = paramArrayOfStackMapType;
  }
  
  public StackMapType[] getTypesOfLocals()
  {
    return types_of_locals;
  }
  
  public void setNumberOfStackItems(int paramInt)
  {
    number_of_stack_items = paramInt;
  }
  
  public int getNumberOfStackItems()
  {
    return number_of_stack_items;
  }
  
  public void setTypesOfStackItems(StackMapType[] paramArrayOfStackMapType)
  {
    types_of_stack_items = paramArrayOfStackMapType;
  }
  
  public StackMapType[] getTypesOfStackItems()
  {
    return types_of_stack_items;
  }
  
  public StackMapEntry copy()
  {
    try
    {
      return (StackMapEntry)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackMapEntry(this);
  }
  
  public final ConstantPool getConstantPool()
  {
    return constant_pool;
  }
  
  public final void setConstantPool(ConstantPool paramConstantPool)
  {
    constant_pool = paramConstantPool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\StackMapEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */