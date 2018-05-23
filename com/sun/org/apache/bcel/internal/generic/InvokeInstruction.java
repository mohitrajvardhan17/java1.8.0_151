package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import java.util.StringTokenizer;

public abstract class InvokeInstruction
  extends FieldOrMethod
  implements ExceptionThrower, TypedInstruction, StackConsumer, StackProducer
{
  InvokeInstruction() {}
  
  protected InvokeInstruction(short paramShort, int paramInt)
  {
    super(paramShort, paramInt);
  }
  
  public String toString(ConstantPool paramConstantPool)
  {
    Constant localConstant = paramConstantPool.getConstant(index);
    StringTokenizer localStringTokenizer = new StringTokenizer(paramConstantPool.constantToString(localConstant));
    return com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[opcode] + " " + localStringTokenizer.nextToken().replace('.', '/') + localStringTokenizer.nextToken();
  }
  
  public int consumeStack(ConstantPoolGen paramConstantPoolGen)
  {
    String str = getSignature(paramConstantPoolGen);
    Type[] arrayOfType = Type.getArgumentTypes(str);
    int i;
    if (opcode == 184) {
      i = 0;
    } else {
      i = 1;
    }
    int j = arrayOfType.length;
    for (int k = 0; k < j; k++) {
      i += arrayOfType[k].getSize();
    }
    return i;
  }
  
  public int produceStack(ConstantPoolGen paramConstantPoolGen)
  {
    return getReturnType(paramConstantPoolGen).getSize();
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return getReturnType(paramConstantPoolGen);
  }
  
  public String getMethodName(ConstantPoolGen paramConstantPoolGen)
  {
    return getName(paramConstantPoolGen);
  }
  
  public Type getReturnType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.getReturnType(getSignature(paramConstantPoolGen));
  }
  
  public Type[] getArgumentTypes(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.getArgumentTypes(getSignature(paramConstantPoolGen));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\InvokeInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */