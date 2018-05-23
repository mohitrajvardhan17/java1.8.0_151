package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LocalVariableTypeTable
  extends Attribute
{
  private static final long serialVersionUID = -1082157891095177114L;
  private int local_variable_type_table_length;
  private LocalVariable[] local_variable_type_table;
  
  public LocalVariableTypeTable(LocalVariableTypeTable paramLocalVariableTypeTable)
  {
    this(paramLocalVariableTypeTable.getNameIndex(), paramLocalVariableTypeTable.getLength(), paramLocalVariableTypeTable.getLocalVariableTypeTable(), paramLocalVariableTypeTable.getConstantPool());
  }
  
  public LocalVariableTypeTable(int paramInt1, int paramInt2, LocalVariable[] paramArrayOfLocalVariable, ConstantPool paramConstantPool)
  {
    super((byte)12, paramInt1, paramInt2, paramConstantPool);
    setLocalVariableTable(paramArrayOfLocalVariable);
  }
  
  LocalVariableTypeTable(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (LocalVariable[])null, paramConstantPool);
    local_variable_type_table_length = paramDataInputStream.readUnsignedShort();
    local_variable_type_table = new LocalVariable[local_variable_type_table_length];
    for (int i = 0; i < local_variable_type_table_length; i++) {
      local_variable_type_table[i] = new LocalVariable(paramDataInputStream, paramConstantPool);
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitLocalVariableTypeTable(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(local_variable_type_table_length);
    for (int i = 0; i < local_variable_type_table_length; i++) {
      local_variable_type_table[i].dump(paramDataOutputStream);
    }
  }
  
  public final LocalVariable[] getLocalVariableTypeTable()
  {
    return local_variable_type_table;
  }
  
  public final LocalVariable getLocalVariable(int paramInt)
  {
    for (int i = 0; i < local_variable_type_table_length; i++) {
      if (local_variable_type_table[i].getIndex() == paramInt) {
        return local_variable_type_table[i];
      }
    }
    return null;
  }
  
  public final void setLocalVariableTable(LocalVariable[] paramArrayOfLocalVariable)
  {
    local_variable_type_table = paramArrayOfLocalVariable;
    local_variable_type_table_length = (paramArrayOfLocalVariable == null ? 0 : paramArrayOfLocalVariable.length);
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < local_variable_type_table_length; i++)
    {
      localStringBuilder.append(local_variable_type_table[i].toString());
      if (i < local_variable_type_table_length - 1) {
        localStringBuilder.append('\n');
      }
    }
    return localStringBuilder.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    LocalVariableTypeTable localLocalVariableTypeTable = (LocalVariableTypeTable)clone();
    local_variable_type_table = new LocalVariable[local_variable_type_table_length];
    for (int i = 0; i < local_variable_type_table_length; i++) {
      local_variable_type_table[i] = local_variable_type_table[i].copy();
    }
    constant_pool = paramConstantPool;
    return localLocalVariableTypeTable;
  }
  
  public final int getTableLength()
  {
    return local_variable_type_table_length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LocalVariableTypeTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */