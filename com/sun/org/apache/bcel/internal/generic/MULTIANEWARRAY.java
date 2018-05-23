package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class MULTIANEWARRAY
  extends CPInstruction
  implements LoadClass, AllocationInstruction, ExceptionThrower
{
  private short dimensions;
  
  MULTIANEWARRAY() {}
  
  public MULTIANEWARRAY(int paramInt, short paramShort)
  {
    super((short)197, paramInt);
    if (paramShort < 1) {
      throw new ClassGenException("Invalid dimensions value: " + paramShort);
    }
    dimensions = paramShort;
    length = 4;
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(opcode);
    paramDataOutputStream.writeShort(index);
    paramDataOutputStream.writeByte(dimensions);
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    super.initFromFile(paramByteSequence, paramBoolean);
    dimensions = ((short)paramByteSequence.readByte());
    length = 4;
  }
  
  public final short getDimensions()
  {
    return dimensions;
  }
  
  public String toString(boolean paramBoolean)
  {
    return super.toString(paramBoolean) + " " + index + " " + dimensions;
  }
  
  public String toString(ConstantPool paramConstantPool)
  {
    return super.toString(paramConstantPool) + " " + dimensions;
  }
  
  public int consumeStack(ConstantPoolGen paramConstantPoolGen)
  {
    return dimensions;
  }
  
  public Class[] getExceptions()
  {
    Class[] arrayOfClass = new Class[2 + ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length);
    arrayOfClass[(ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length + 1)] = ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION;
    arrayOfClass[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length] = ExceptionConstants.ILLEGAL_ACCESS_ERROR;
    return arrayOfClass;
  }
  
  public ObjectType getLoadClassType(ConstantPoolGen paramConstantPoolGen)
  {
    Type localType = getType(paramConstantPoolGen);
    if ((localType instanceof ArrayType)) {
      localType = ((ArrayType)localType).getBasicType();
    }
    return (localType instanceof ObjectType) ? (ObjectType)localType : null;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitAllocationInstruction(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitMULTIANEWARRAY(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\MULTIANEWARRAY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */