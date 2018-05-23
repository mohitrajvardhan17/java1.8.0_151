package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.DataInputStream;
import java.io.IOException;

public final class Field
  extends FieldOrMethod
{
  public Field(Field paramField)
  {
    super(paramField);
  }
  
  Field(DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException, ClassFormatException
  {
    super(paramDataInputStream, paramConstantPool);
  }
  
  public Field(int paramInt1, int paramInt2, int paramInt3, Attribute[] paramArrayOfAttribute, ConstantPool paramConstantPool)
  {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfAttribute, paramConstantPool);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitField(this);
  }
  
  public final ConstantValue getConstantValue()
  {
    for (int i = 0; i < attributes_count; i++) {
      if (attributes[i].getTag() == 1) {
        return (ConstantValue)attributes[i];
      }
    }
    return null;
  }
  
  public final String toString()
  {
    String str3 = Utility.accessToString(access_flags);
    str3 = str3 + " ";
    String str2 = Utility.signatureToString(getSignature());
    String str1 = getName();
    StringBuffer localStringBuffer = new StringBuffer(str3 + str2 + " " + str1);
    ConstantValue localConstantValue = getConstantValue();
    if (localConstantValue != null) {
      localStringBuffer.append(" = " + localConstantValue);
    }
    for (int i = 0; i < attributes_count; i++)
    {
      Attribute localAttribute = attributes[i];
      if (!(localAttribute instanceof ConstantValue)) {
        localStringBuffer.append(" [" + localAttribute.toString() + "]");
      }
    }
    return localStringBuffer.toString();
  }
  
  public final Field copy(ConstantPool paramConstantPool)
  {
    return (Field)copy_(paramConstantPool);
  }
  
  public Type getType()
  {
    return Type.getReturnType(getSignature());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Field.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */