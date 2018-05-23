package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.ConstantCP;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;

public abstract class FieldOrMethod
  extends CPInstruction
  implements LoadClass
{
  FieldOrMethod() {}
  
  protected FieldOrMethod(short paramShort, int paramInt)
  {
    super(paramShort, paramInt);
  }
  
  public String getSignature(ConstantPoolGen paramConstantPoolGen)
  {
    ConstantPool localConstantPool = paramConstantPoolGen.getConstantPool();
    ConstantCP localConstantCP = (ConstantCP)localConstantPool.getConstant(index);
    ConstantNameAndType localConstantNameAndType = (ConstantNameAndType)localConstantPool.getConstant(localConstantCP.getNameAndTypeIndex());
    return ((ConstantUtf8)localConstantPool.getConstant(localConstantNameAndType.getSignatureIndex())).getBytes();
  }
  
  public String getName(ConstantPoolGen paramConstantPoolGen)
  {
    ConstantPool localConstantPool = paramConstantPoolGen.getConstantPool();
    ConstantCP localConstantCP = (ConstantCP)localConstantPool.getConstant(index);
    ConstantNameAndType localConstantNameAndType = (ConstantNameAndType)localConstantPool.getConstant(localConstantCP.getNameAndTypeIndex());
    return ((ConstantUtf8)localConstantPool.getConstant(localConstantNameAndType.getNameIndex())).getBytes();
  }
  
  public String getClassName(ConstantPoolGen paramConstantPoolGen)
  {
    ConstantPool localConstantPool = paramConstantPoolGen.getConstantPool();
    ConstantCP localConstantCP = (ConstantCP)localConstantPool.getConstant(index);
    return localConstantPool.getConstantString(localConstantCP.getClassIndex(), (byte)7).replace('/', '.');
  }
  
  public ObjectType getClassType(ConstantPoolGen paramConstantPoolGen)
  {
    return new ObjectType(getClassName(paramConstantPoolGen));
  }
  
  public ObjectType getLoadClassType(ConstantPoolGen paramConstantPoolGen)
  {
    return getClassType(paramConstantPoolGen);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\FieldOrMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */