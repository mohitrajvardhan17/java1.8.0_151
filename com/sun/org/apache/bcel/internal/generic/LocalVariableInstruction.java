package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class LocalVariableInstruction
  extends Instruction
  implements TypedInstruction, IndexedInstruction
{
  protected int n = -1;
  private short c_tag = -1;
  private short canon_tag = -1;
  
  private final boolean wide()
  {
    return n > 255;
  }
  
  LocalVariableInstruction(short paramShort1, short paramShort2)
  {
    canon_tag = paramShort1;
    c_tag = paramShort2;
  }
  
  LocalVariableInstruction() {}
  
  protected LocalVariableInstruction(short paramShort1, short paramShort2, int paramInt)
  {
    super(paramShort1, (short)2);
    c_tag = paramShort2;
    canon_tag = paramShort1;
    setIndex(paramInt);
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    if (wide()) {
      paramDataOutputStream.writeByte(196);
    }
    paramDataOutputStream.writeByte(opcode);
    if (length > 1) {
      if (wide()) {
        paramDataOutputStream.writeShort(n);
      } else {
        paramDataOutputStream.writeByte(n);
      }
    }
  }
  
  public String toString(boolean paramBoolean)
  {
    if (((opcode >= 26) && (opcode <= 45)) || ((opcode >= 59) && (opcode <= 78))) {
      return super.toString(paramBoolean);
    }
    return super.toString(paramBoolean) + " " + n;
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean)
    {
      n = paramByteSequence.readUnsignedShort();
      length = 4;
    }
    else if (((opcode >= 21) && (opcode <= 25)) || ((opcode >= 54) && (opcode <= 58)))
    {
      n = paramByteSequence.readUnsignedByte();
      length = 2;
    }
    else if (opcode <= 45)
    {
      n = ((opcode - 26) % 4);
      length = 1;
    }
    else
    {
      n = ((opcode - 59) % 4);
      length = 1;
    }
  }
  
  public final int getIndex()
  {
    return n;
  }
  
  public void setIndex(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 65535)) {
      throw new ClassGenException("Illegal value: " + paramInt);
    }
    n = paramInt;
    if ((paramInt >= 0) && (paramInt <= 3))
    {
      opcode = ((short)(c_tag + paramInt));
      length = 1;
    }
    else
    {
      opcode = canon_tag;
      if (wide()) {
        length = 4;
      } else {
        length = 2;
      }
    }
  }
  
  public short getCanonicalTag()
  {
    return canon_tag;
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    switch (canon_tag)
    {
    case 21: 
    case 54: 
      return Type.INT;
    case 22: 
    case 55: 
      return Type.LONG;
    case 24: 
    case 57: 
      return Type.DOUBLE;
    case 23: 
    case 56: 
      return Type.FLOAT;
    case 25: 
    case 58: 
      return Type.OBJECT;
    }
    throw new ClassGenException("Oops: unknown case in switch" + canon_tag);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LocalVariableInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */