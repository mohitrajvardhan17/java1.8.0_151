package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class LineNumberTable
  extends Attribute
{
  private int line_number_table_length;
  private LineNumber[] line_number_table;
  
  public LineNumberTable(LineNumberTable paramLineNumberTable)
  {
    this(paramLineNumberTable.getNameIndex(), paramLineNumberTable.getLength(), paramLineNumberTable.getLineNumberTable(), paramLineNumberTable.getConstantPool());
  }
  
  public LineNumberTable(int paramInt1, int paramInt2, LineNumber[] paramArrayOfLineNumber, ConstantPool paramConstantPool)
  {
    super((byte)4, paramInt1, paramInt2, paramConstantPool);
    setLineNumberTable(paramArrayOfLineNumber);
  }
  
  LineNumberTable(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (LineNumber[])null, paramConstantPool);
    line_number_table_length = paramDataInputStream.readUnsignedShort();
    line_number_table = new LineNumber[line_number_table_length];
    for (int i = 0; i < line_number_table_length; i++) {
      line_number_table[i] = new LineNumber(paramDataInputStream);
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitLineNumberTable(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(line_number_table_length);
    for (int i = 0; i < line_number_table_length; i++) {
      line_number_table[i].dump(paramDataOutputStream);
    }
  }
  
  public final LineNumber[] getLineNumberTable()
  {
    return line_number_table;
  }
  
  public final void setLineNumberTable(LineNumber[] paramArrayOfLineNumber)
  {
    line_number_table = paramArrayOfLineNumber;
    line_number_table_length = (paramArrayOfLineNumber == null ? 0 : paramArrayOfLineNumber.length);
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer1 = new StringBuffer();
    StringBuffer localStringBuffer2 = new StringBuffer();
    for (int i = 0; i < line_number_table_length; i++)
    {
      localStringBuffer2.append(line_number_table[i].toString());
      if (i < line_number_table_length - 1) {
        localStringBuffer2.append(", ");
      }
      if (localStringBuffer2.length() > 72)
      {
        localStringBuffer2.append('\n');
        localStringBuffer1.append(localStringBuffer2);
        localStringBuffer2.setLength(0);
      }
    }
    localStringBuffer1.append(localStringBuffer2);
    return localStringBuffer1.toString();
  }
  
  public int getSourceLine(int paramInt)
  {
    int i = 0;
    int j = line_number_table_length - 1;
    if (j < 0) {
      return -1;
    }
    int k = -1;
    int m = -1;
    do
    {
      int n = (i + j) / 2;
      int i1 = line_number_table[n].getStartPC();
      if (i1 == paramInt) {
        return line_number_table[n].getLineNumber();
      }
      if (paramInt < i1) {
        j = n - 1;
      } else {
        i = n + 1;
      }
      if ((i1 < paramInt) && (i1 > m))
      {
        m = i1;
        k = n;
      }
    } while (i <= j);
    if (k < 0) {
      return -1;
    }
    return line_number_table[k].getLineNumber();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    LineNumberTable localLineNumberTable = (LineNumberTable)clone();
    line_number_table = new LineNumber[line_number_table_length];
    for (int i = 0; i < line_number_table_length; i++) {
      line_number_table[i] = line_number_table[i].copy();
    }
    constant_pool = paramConstantPool;
    return localLineNumberTable;
  }
  
  public final int getTableLength()
  {
    return line_number_table_length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LineNumberTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */