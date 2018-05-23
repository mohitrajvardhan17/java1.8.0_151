package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.DataInputStream;
import java.io.IOException;

public final class Method
  extends FieldOrMethod
{
  public Method() {}
  
  public Method(Method paramMethod)
  {
    super(paramMethod);
  }
  
  Method(DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException, ClassFormatException
  {
    super(paramDataInputStream, paramConstantPool);
  }
  
  public Method(int paramInt1, int paramInt2, int paramInt3, Attribute[] paramArrayOfAttribute, ConstantPool paramConstantPool)
  {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfAttribute, paramConstantPool);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitMethod(this);
  }
  
  public final Code getCode()
  {
    for (int i = 0; i < attributes_count; i++) {
      if ((attributes[i] instanceof Code)) {
        return (Code)attributes[i];
      }
    }
    return null;
  }
  
  public final ExceptionTable getExceptionTable()
  {
    for (int i = 0; i < attributes_count; i++) {
      if ((attributes[i] instanceof ExceptionTable)) {
        return (ExceptionTable)attributes[i];
      }
    }
    return null;
  }
  
  public final LocalVariableTable getLocalVariableTable()
  {
    Code localCode = getCode();
    if (localCode != null) {
      return localCode.getLocalVariableTable();
    }
    return null;
  }
  
  public final LineNumberTable getLineNumberTable()
  {
    Code localCode = getCode();
    if (localCode != null) {
      return localCode.getLineNumberTable();
    }
    return null;
  }
  
  public final String toString()
  {
    String str3 = Utility.accessToString(access_flags);
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(signature_index, (byte)1);
    String str2 = localConstantUtf8.getBytes();
    localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(name_index, (byte)1);
    String str1 = localConstantUtf8.getBytes();
    str2 = Utility.methodSignatureToString(str2, str1, str3, true, getLocalVariableTable());
    StringBuffer localStringBuffer = new StringBuffer(str2);
    Object localObject;
    for (int i = 0; i < attributes_count; i++)
    {
      localObject = attributes[i];
      if ((!(localObject instanceof Code)) && (!(localObject instanceof ExceptionTable))) {
        localStringBuffer.append(" [" + ((Attribute)localObject).toString() + "]");
      }
    }
    ExceptionTable localExceptionTable = getExceptionTable();
    if (localExceptionTable != null)
    {
      localObject = localExceptionTable.toString();
      if (!((String)localObject).equals("")) {
        localStringBuffer.append("\n\t\tthrows " + (String)localObject);
      }
    }
    return localStringBuffer.toString();
  }
  
  public final Method copy(ConstantPool paramConstantPool)
  {
    return (Method)copy_(paramConstantPool);
  }
  
  public Type getReturnType()
  {
    return Type.getReturnType(getSignature());
  }
  
  public Type[] getArgumentTypes()
  {
    return Type.getArgumentTypes(getSignature());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Method.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */