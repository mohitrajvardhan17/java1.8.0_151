package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class BIPUSH
  extends Instruction
  implements ConstantPushInstruction
{
  private byte b;
  
  BIPUSH() {}
  
  public BIPUSH(byte paramByte)
  {
    super((short)16, (short)2);
    b = paramByte;
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeByte(b);
  }
  
  public String toString(boolean paramBoolean)
  {
    return super.toString(paramBoolean) + " " + b;
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    length = 2;
    b = paramByteSequence.readByte();
  }
  
  public Number getValue()
  {
    return new Integer(b);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.BYTE;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitConstantPushInstruction(this);
    paramVisitor.visitBIPUSH(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\BIPUSH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */