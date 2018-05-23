package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LocalVariableTable
  extends Attribute
{
  private int local_variable_table_length;
  private LocalVariable[] local_variable_table;
  
  public LocalVariableTable(LocalVariableTable paramLocalVariableTable)
  {
    this(paramLocalVariableTable.getNameIndex(), paramLocalVariableTable.getLength(), paramLocalVariableTable.getLocalVariableTable(), paramLocalVariableTable.getConstantPool());
  }
  
  public LocalVariableTable(int paramInt1, int paramInt2, LocalVariable[] paramArrayOfLocalVariable, ConstantPool paramConstantPool)
  {
    super((byte)5, paramInt1, paramInt2, paramConstantPool);
    setLocalVariableTable(paramArrayOfLocalVariable);
  }
  
  LocalVariableTable(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (LocalVariable[])null, paramConstantPool);
    local_variable_table_length = paramDataInputStream.readUnsignedShort();
    local_variable_table = new LocalVariable[local_variable_table_length];
    for (int i = 0; i < local_variable_table_length; i++) {
      local_variable_table[i] = new LocalVariable(paramDataInputStream, paramConstantPool);
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitLocalVariableTable(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(local_variable_table_length);
    for (int i = 0; i < local_variable_table_length; i++) {
      local_variable_table[i].dump(paramDataOutputStream);
    }
  }
  
  public final LocalVariable[] getLocalVariableTable()
  {
    return local_variable_table;
  }
  
  public final LocalVariable getLocalVariable(int paramInt)
  {
    for (int i = 0; i < local_variable_table_length; i++) {
      if (local_variable_table[i].getIndex() == paramInt) {
        return local_variable_table[i];
      }
    }
    return null;
  }
  
  public final void setLocalVariableTable(LocalVariable[] paramArrayOfLocalVariable)
  {
    local_variable_table = paramArrayOfLocalVariable;
    local_variable_table_length = (paramArrayOfLocalVariable == null ? 0 : paramArrayOfLocalVariable.length);
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("");
    for (int i = 0; i < local_variable_table_length; i++)
    {
      localStringBuffer.append(local_variable_table[i].toString());
      if (i < local_variable_table_length - 1) {
        localStringBuffer.append('\n');
      }
    }
    return localStringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    LocalVariableTable localLocalVariableTable = (LocalVariableTable)clone();
    local_variable_table = new LocalVariable[local_variable_table_length];
    for (int i = 0; i < local_variable_table_length; i++) {
      local_variable_table[i] = local_variable_table[i].copy();
    }
    constant_pool = paramConstantPool;
    return localLocalVariableTable;
  }
  
  public final int getTableLength()
  {
    return local_variable_table_length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LocalVariableTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */