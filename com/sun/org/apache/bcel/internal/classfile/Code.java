package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Code
  extends Attribute
{
  private int max_stack;
  private int max_locals;
  private int code_length;
  private byte[] code;
  private int exception_table_length;
  private CodeException[] exception_table;
  private int attributes_count;
  private Attribute[] attributes;
  
  public Code(Code paramCode)
  {
    this(paramCode.getNameIndex(), paramCode.getLength(), paramCode.getMaxStack(), paramCode.getMaxLocals(), paramCode.getCode(), paramCode.getExceptionTable(), paramCode.getAttributes(), paramCode.getConstantPool());
  }
  
  Code(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), (byte[])null, (CodeException[])null, (Attribute[])null, paramConstantPool);
    code_length = paramDataInputStream.readInt();
    code = new byte[code_length];
    paramDataInputStream.readFully(code);
    exception_table_length = paramDataInputStream.readUnsignedShort();
    exception_table = new CodeException[exception_table_length];
    for (int i = 0; i < exception_table_length; i++) {
      exception_table[i] = new CodeException(paramDataInputStream);
    }
    attributes_count = paramDataInputStream.readUnsignedShort();
    attributes = new Attribute[attributes_count];
    for (i = 0; i < attributes_count; i++) {
      attributes[i] = Attribute.readAttribute(paramDataInputStream, paramConstantPool);
    }
    length = paramInt2;
  }
  
  public Code(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, CodeException[] paramArrayOfCodeException, Attribute[] paramArrayOfAttribute, ConstantPool paramConstantPool)
  {
    super((byte)2, paramInt1, paramInt2, paramConstantPool);
    max_stack = paramInt3;
    max_locals = paramInt4;
    setCode(paramArrayOfByte);
    setExceptionTable(paramArrayOfCodeException);
    setAttributes(paramArrayOfAttribute);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitCode(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(max_stack);
    paramDataOutputStream.writeShort(max_locals);
    paramDataOutputStream.writeInt(code_length);
    paramDataOutputStream.write(code, 0, code_length);
    paramDataOutputStream.writeShort(exception_table_length);
    for (int i = 0; i < exception_table_length; i++) {
      exception_table[i].dump(paramDataOutputStream);
    }
    paramDataOutputStream.writeShort(attributes_count);
    for (i = 0; i < attributes_count; i++) {
      attributes[i].dump(paramDataOutputStream);
    }
  }
  
  public final Attribute[] getAttributes()
  {
    return attributes;
  }
  
  public LineNumberTable getLineNumberTable()
  {
    for (int i = 0; i < attributes_count; i++) {
      if ((attributes[i] instanceof LineNumberTable)) {
        return (LineNumberTable)attributes[i];
      }
    }
    return null;
  }
  
  public LocalVariableTable getLocalVariableTable()
  {
    for (int i = 0; i < attributes_count; i++) {
      if ((attributes[i] instanceof LocalVariableTable)) {
        return (LocalVariableTable)attributes[i];
      }
    }
    return null;
  }
  
  public final byte[] getCode()
  {
    return code;
  }
  
  public final CodeException[] getExceptionTable()
  {
    return exception_table;
  }
  
  public final int getMaxLocals()
  {
    return max_locals;
  }
  
  public final int getMaxStack()
  {
    return max_stack;
  }
  
  private final int getInternalLength()
  {
    return 8 + code_length + 2 + 8 * exception_table_length + 2;
  }
  
  private final int calculateLength()
  {
    int i = 0;
    for (int j = 0; j < attributes_count; j++) {
      i += attributes[j].length + 6;
    }
    return i + getInternalLength();
  }
  
  public final void setAttributes(Attribute[] paramArrayOfAttribute)
  {
    attributes = paramArrayOfAttribute;
    attributes_count = (paramArrayOfAttribute == null ? 0 : paramArrayOfAttribute.length);
    length = calculateLength();
  }
  
  public final void setCode(byte[] paramArrayOfByte)
  {
    code = paramArrayOfByte;
    code_length = (paramArrayOfByte == null ? 0 : paramArrayOfByte.length);
  }
  
  public final void setExceptionTable(CodeException[] paramArrayOfCodeException)
  {
    exception_table = paramArrayOfCodeException;
    exception_table_length = (paramArrayOfCodeException == null ? 0 : paramArrayOfCodeException.length);
  }
  
  public final void setMaxLocals(int paramInt)
  {
    max_locals = paramInt;
  }
  
  public final void setMaxStack(int paramInt)
  {
    max_stack = paramInt;
  }
  
  public final String toString(boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer("Code(max_stack = " + max_stack + ", max_locals = " + max_locals + ", code_length = " + code_length + ")\n" + Utility.codeToString(code, constant_pool, 0, -1, paramBoolean));
    int i;
    if (exception_table_length > 0)
    {
      localStringBuffer.append("\nException handler(s) = \nFrom\tTo\tHandler\tType\n");
      for (i = 0; i < exception_table_length; i++) {
        localStringBuffer.append(exception_table[i].toString(constant_pool, paramBoolean) + "\n");
      }
    }
    if (attributes_count > 0)
    {
      localStringBuffer.append("\nAttribute(s) = \n");
      for (i = 0; i < attributes_count; i++) {
        localStringBuffer.append(attributes[i].toString() + "\n");
      }
    }
    return localStringBuffer.toString();
  }
  
  public final String toString()
  {
    return toString(true);
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    Code localCode = (Code)clone();
    code = ((byte[])code.clone());
    constant_pool = paramConstantPool;
    exception_table = new CodeException[exception_table_length];
    for (int i = 0; i < exception_table_length; i++) {
      exception_table[i] = exception_table[i].copy();
    }
    attributes = new Attribute[attributes_count];
    for (i = 0; i < attributes_count; i++) {
      attributes[i] = attributes[i].copy(paramConstantPool);
    }
    return localCode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Code.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */