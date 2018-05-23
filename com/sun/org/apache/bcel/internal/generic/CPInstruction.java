package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class CPInstruction
  extends Instruction
  implements TypedInstruction, IndexedInstruction
{
  protected int index;
  
  CPInstruction() {}
  
  protected CPInstruction(short paramShort, int paramInt)
  {
    super(paramShort, (short)3);
    setIndex(paramInt);
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(opcode);
    paramDataOutputStream.writeShort(index);
  }
  
  public String toString(boolean paramBoolean)
  {
    return super.toString(paramBoolean) + " " + index;
  }
  
  public String toString(ConstantPool paramConstantPool)
  {
    Constant localConstant = paramConstantPool.getConstant(index);
    String str = paramConstantPool.constantToString(localConstant);
    if ((localConstant instanceof ConstantClass)) {
      str = str.replace('.', '/');
    }
    return com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[opcode] + " " + str;
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    setIndex(paramByteSequence.readUnsignedShort());
    length = 3;
  }
  
  public final int getIndex()
  {
    return index;
  }
  
  public void setIndex(int paramInt)
  {
    if (paramInt < 0) {
      throw new ClassGenException("Negative index value: " + paramInt);
    }
    index = paramInt;
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    ConstantPool localConstantPool = paramConstantPoolGen.getConstantPool();
    String str = localConstantPool.getConstantString(index, (byte)7);
    if (!str.startsWith("[")) {
      str = "L" + str + ";";
    }
    return Type.getType(str);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\CPInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */