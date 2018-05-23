package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class SIPUSH
  extends Instruction
  implements ConstantPushInstruction
{
  private short b;
  
  SIPUSH() {}
  
  public SIPUSH(short paramShort)
  {
    super((short)17, (short)3);
    b = paramShort;
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(b);
  }
  
  public String toString(boolean paramBoolean)
  {
    return super.toString(paramBoolean) + " " + b;
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    length = 3;
    b = paramByteSequence.readShort();
  }
  
  public Number getValue()
  {
    return new Integer(b);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.SHORT;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitConstantPushInstruction(this);
    paramVisitor.visitSIPUSH(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\SIPUSH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */