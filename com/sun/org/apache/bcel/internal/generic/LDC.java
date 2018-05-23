package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantFloat;
import com.sun.org.apache.bcel.internal.classfile.ConstantInteger;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class LDC
  extends CPInstruction
  implements PushInstruction, ExceptionThrower, TypedInstruction
{
  LDC() {}
  
  public LDC(int paramInt)
  {
    super((short)19, paramInt);
    setSize();
  }
  
  protected final void setSize()
  {
    if (index <= 255)
    {
      opcode = 18;
      length = 2;
    }
    else
    {
      opcode = 19;
      length = 3;
    }
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(opcode);
    if (length == 2) {
      paramDataOutputStream.writeByte(index);
    } else {
      paramDataOutputStream.writeShort(index);
    }
  }
  
  public final void setIndex(int paramInt)
  {
    super.setIndex(paramInt);
    setSize();
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    length = 2;
    index = paramByteSequence.readUnsignedByte();
  }
  
  public Object getValue(ConstantPoolGen paramConstantPoolGen)
  {
    Constant localConstant = paramConstantPoolGen.getConstantPool().getConstant(index);
    switch (localConstant.getTag())
    {
    case 8: 
      int i = ((ConstantString)localConstant).getStringIndex();
      localConstant = paramConstantPoolGen.getConstantPool().getConstant(i);
      return ((ConstantUtf8)localConstant).getBytes();
    case 4: 
      return new Float(((ConstantFloat)localConstant).getBytes());
    case 3: 
      return new Integer(((ConstantInteger)localConstant).getBytes());
    }
    throw new RuntimeException("Unknown or invalid constant type at " + index);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    switch (paramConstantPoolGen.getConstantPool().getConstant(index).getTag())
    {
    case 8: 
      return Type.STRING;
    case 4: 
      return Type.FLOAT;
    case 3: 
      return Type.INT;
    }
    throw new RuntimeException("Unknown or invalid constant type at " + index);
  }
  
  public Class[] getExceptions()
  {
    return ExceptionConstants.EXCS_STRING_RESOLUTION;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitLDC(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LDC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */