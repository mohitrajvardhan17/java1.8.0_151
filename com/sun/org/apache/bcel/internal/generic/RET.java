package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class RET
  extends Instruction
  implements IndexedInstruction, TypedInstruction
{
  private boolean wide;
  private int index;
  
  RET() {}
  
  public RET(int paramInt)
  {
    super((short)169, (short)2);
    setIndex(paramInt);
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    if (wide) {
      paramDataOutputStream.writeByte(196);
    }
    paramDataOutputStream.writeByte(opcode);
    if (wide) {
      paramDataOutputStream.writeShort(index);
    } else {
      paramDataOutputStream.writeByte(index);
    }
  }
  
  private final void setWide()
  {
    if ((wide = index > 255 ? 1 : 0) != 0) {
      length = 4;
    } else {
      length = 2;
    }
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    wide = paramBoolean;
    if (paramBoolean)
    {
      index = paramByteSequence.readUnsignedShort();
      length = 4;
    }
    else
    {
      index = paramByteSequence.readUnsignedByte();
      length = 2;
    }
  }
  
  public final int getIndex()
  {
    return index;
  }
  
  public final void setIndex(int paramInt)
  {
    if (paramInt < 0) {
      throw new ClassGenException("Negative index value: " + paramInt);
    }
    index = paramInt;
    setWide();
  }
  
  public String toString(boolean paramBoolean)
  {
    return super.toString(paramBoolean) + " " + index;
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return ReturnaddressType.NO_TARGET;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitRET(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\RET.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */