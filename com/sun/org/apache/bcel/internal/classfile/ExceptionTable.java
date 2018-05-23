package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ExceptionTable
  extends Attribute
{
  private int number_of_exceptions;
  private int[] exception_index_table;
  
  public ExceptionTable(ExceptionTable paramExceptionTable)
  {
    this(paramExceptionTable.getNameIndex(), paramExceptionTable.getLength(), paramExceptionTable.getExceptionIndexTable(), paramExceptionTable.getConstantPool());
  }
  
  public ExceptionTable(int paramInt1, int paramInt2, int[] paramArrayOfInt, ConstantPool paramConstantPool)
  {
    super((byte)3, paramInt1, paramInt2, paramConstantPool);
    setExceptionIndexTable(paramArrayOfInt);
  }
  
  ExceptionTable(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (int[])null, paramConstantPool);
    number_of_exceptions = paramDataInputStream.readUnsignedShort();
    exception_index_table = new int[number_of_exceptions];
    for (int i = 0; i < number_of_exceptions; i++) {
      exception_index_table[i] = paramDataInputStream.readUnsignedShort();
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionTable(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(number_of_exceptions);
    for (int i = 0; i < number_of_exceptions; i++) {
      paramDataOutputStream.writeShort(exception_index_table[i]);
    }
  }
  
  public final int[] getExceptionIndexTable()
  {
    return exception_index_table;
  }
  
  public final int getNumberOfExceptions()
  {
    return number_of_exceptions;
  }
  
  public final String[] getExceptionNames()
  {
    String[] arrayOfString = new String[number_of_exceptions];
    for (int i = 0; i < number_of_exceptions; i++) {
      arrayOfString[i] = constant_pool.getConstantString(exception_index_table[i], 7).replace('/', '.');
    }
    return arrayOfString;
  }
  
  public final void setExceptionIndexTable(int[] paramArrayOfInt)
  {
    exception_index_table = paramArrayOfInt;
    number_of_exceptions = (paramArrayOfInt == null ? 0 : paramArrayOfInt.length);
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("");
    for (int i = 0; i < number_of_exceptions; i++)
    {
      String str = constant_pool.getConstantString(exception_index_table[i], (byte)7);
      localStringBuffer.append(Utility.compactClassName(str, false));
      if (i < number_of_exceptions - 1) {
        localStringBuffer.append(", ");
      }
    }
    return localStringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    ExceptionTable localExceptionTable = (ExceptionTable)clone();
    exception_index_table = ((int[])exception_index_table.clone());
    constant_pool = paramConstantPool;
    return localExceptionTable;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ExceptionTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */