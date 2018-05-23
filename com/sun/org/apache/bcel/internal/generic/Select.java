package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Select
  extends BranchInstruction
  implements VariableLengthInstruction, StackProducer
{
  protected int[] match;
  protected int[] indices;
  protected InstructionHandle[] targets;
  protected int fixed_length;
  protected int match_length;
  protected int padding = 0;
  
  Select() {}
  
  Select(short paramShort, int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle)
  {
    super(paramShort, paramInstructionHandle);
    targets = paramArrayOfInstructionHandle;
    for (int i = 0; i < paramArrayOfInstructionHandle.length; i++) {
      BranchInstruction.notifyTargetChanged(paramArrayOfInstructionHandle[i], this);
    }
    match = paramArrayOfInt;
    if ((match_length = paramArrayOfInt.length) != paramArrayOfInstructionHandle.length) {
      throw new ClassGenException("Match and target array have not the same length");
    }
    indices = new int[match_length];
  }
  
  protected int updatePosition(int paramInt1, int paramInt2)
  {
    position += paramInt1;
    int i = length;
    padding = ((4 - (position + 1) % 4) % 4);
    length = ((short)(fixed_length + padding));
    return length - i;
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(opcode);
    for (int i = 0; i < padding; i++) {
      paramDataOutputStream.writeByte(0);
    }
    index = getTargetOffset();
    paramDataOutputStream.writeInt(index);
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    padding = ((4 - paramByteSequence.getIndex() % 4) % 4);
    for (int i = 0; i < padding; i++) {
      paramByteSequence.readByte();
    }
    index = paramByteSequence.readInt();
  }
  
  public String toString(boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder(super.toString(paramBoolean));
    if (paramBoolean) {
      for (int i = 0; i < match_length; i++)
      {
        String str = "null";
        if (targets[i] != null) {
          str = targets[i].getInstruction().toString();
        }
        localStringBuilder.append("(").append(match[i]).append(", ").append(str).append(" = {").append(indices[i]).append("})");
      }
    } else {
      localStringBuilder.append(" ...");
    }
    return localStringBuilder.toString();
  }
  
  public final void setTarget(int paramInt, InstructionHandle paramInstructionHandle)
  {
    notifyTargetChanging(targets[paramInt], this);
    targets[paramInt] = paramInstructionHandle;
    notifyTargetChanged(targets[paramInt], this);
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    int i = 0;
    if (target == paramInstructionHandle1)
    {
      i = 1;
      setTarget(paramInstructionHandle2);
    }
    for (int j = 0; j < targets.length; j++) {
      if (targets[j] == paramInstructionHandle1)
      {
        i = 1;
        setTarget(j, paramInstructionHandle2);
      }
    }
    if (i == 0) {
      throw new ClassGenException("Not targeting " + paramInstructionHandle1);
    }
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle)
  {
    if (target == paramInstructionHandle) {
      return true;
    }
    for (int i = 0; i < targets.length; i++) {
      if (targets[i] == paramInstructionHandle) {
        return true;
      }
    }
    return false;
  }
  
  void dispose()
  {
    super.dispose();
    for (int i = 0; i < targets.length; i++) {
      targets[i].removeTargeter(this);
    }
  }
  
  public int[] getMatchs()
  {
    return match;
  }
  
  public int[] getIndices()
  {
    return indices;
  }
  
  public InstructionHandle[] getTargets()
  {
    return targets;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\Select.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */