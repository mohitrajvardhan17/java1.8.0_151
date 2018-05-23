package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class NEWARRAY
  extends Instruction
  implements AllocationInstruction, ExceptionThrower, StackProducer
{
  private byte type;
  
  NEWARRAY() {}
  
  public NEWARRAY(byte paramByte)
  {
    super((short)188, (short)2);
    type = paramByte;
  }
  
  public NEWARRAY(BasicType paramBasicType)
  {
    this(paramBasicType.getType());
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(opcode);
    paramDataOutputStream.writeByte(type);
  }
  
  public final byte getTypecode()
  {
    return type;
  }
  
  public final Type getType()
  {
    return new ArrayType(BasicType.getType(type), 1);
  }
  
  public String toString(boolean paramBoolean)
  {
    return super.toString(paramBoolean) + " " + com.sun.org.apache.bcel.internal.Constants.TYPE_NAMES[type];
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    type = paramByteSequence.readByte();
    length = 2;
  }
  
  public Class[] getExceptions()
  {
    return new Class[] { ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION };
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitAllocationInstruction(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitNEWARRAY(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\NEWARRAY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */