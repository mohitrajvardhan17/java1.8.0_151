package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class IINC
  extends LocalVariableInstruction
{
  private boolean wide;
  private int c;
  
  IINC() {}
  
  public IINC(int paramInt1, int paramInt2)
  {
    opcode = 132;
    length = 3;
    setIndex(paramInt1);
    setIncrement(paramInt2);
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    if (wide) {
      paramDataOutputStream.writeByte(196);
    }
    paramDataOutputStream.writeByte(opcode);
    if (wide)
    {
      paramDataOutputStream.writeShort(n);
      paramDataOutputStream.writeShort(c);
    }
    else
    {
      paramDataOutputStream.writeByte(n);
      paramDataOutputStream.writeByte(c);
    }
  }
  
  private final void setWide()
  {
    if ((wide = (n > 65535) || (Math.abs(c) > 127) ? 1 : 0) != 0) {
      length = 6;
    } else {
      length = 3;
    }
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    wide = paramBoolean;
    if (paramBoolean)
    {
      length = 6;
      n = paramByteSequence.readUnsignedShort();
      c = paramByteSequence.readShort();
    }
    else
    {
      length = 3;
      n = paramByteSequence.readUnsignedByte();
      c = paramByteSequence.readByte();
    }
  }
  
  public String toString(boolean paramBoolean)
  {
    return super.toString(paramBoolean) + " " + c;
  }
  
  public final void setIndex(int paramInt)
  {
    if (paramInt < 0) {
      throw new ClassGenException("Negative index value: " + paramInt);
    }
    n = paramInt;
    setWide();
  }
  
  public final int getIncrement()
  {
    return c;
  }
  
  public final void setIncrement(int paramInt)
  {
    c = paramInt;
    setWide();
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.INT;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitLocalVariableInstruction(this);
    paramVisitor.visitIINC(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IINC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */