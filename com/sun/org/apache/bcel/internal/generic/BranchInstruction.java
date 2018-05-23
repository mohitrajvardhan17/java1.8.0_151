package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class BranchInstruction
  extends Instruction
  implements InstructionTargeter
{
  protected int index;
  protected InstructionHandle target;
  protected int position;
  
  BranchInstruction() {}
  
  protected BranchInstruction(short paramShort, InstructionHandle paramInstructionHandle)
  {
    super(paramShort, (short)3);
    setTarget(paramInstructionHandle);
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(opcode);
    index = getTargetOffset();
    if (Math.abs(index) >= 32767) {
      throw new ClassGenException("Branch target offset too large for short");
    }
    paramDataOutputStream.writeShort(index);
  }
  
  protected int getTargetOffset(InstructionHandle paramInstructionHandle)
  {
    if (paramInstructionHandle == null) {
      throw new ClassGenException("Target of " + super.toString(true) + " is invalid null handle");
    }
    int i = paramInstructionHandle.getPosition();
    if (i < 0) {
      throw new ClassGenException("Invalid branch target position offset for " + super.toString(true) + ":" + i + ":" + paramInstructionHandle);
    }
    return i - position;
  }
  
  protected int getTargetOffset()
  {
    return getTargetOffset(target);
  }
  
  protected int updatePosition(int paramInt1, int paramInt2)
  {
    position += paramInt1;
    return 0;
  }
  
  public String toString(boolean paramBoolean)
  {
    String str1 = super.toString(paramBoolean);
    String str2 = "null";
    if (paramBoolean)
    {
      if (target != null) {
        if (target.getInstruction() == this) {
          str2 = "<points to itself>";
        } else if (target.getInstruction() == null) {
          str2 = "<null instruction!!!?>";
        } else {
          str2 = target.getInstruction().toString(false);
        }
      }
    }
    else if (target != null)
    {
      index = getTargetOffset();
      str2 = "" + (index + position);
    }
    return str1 + " -> " + str2;
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    length = 3;
    index = paramByteSequence.readShort();
  }
  
  public final int getIndex()
  {
    return index;
  }
  
  public InstructionHandle getTarget()
  {
    return target;
  }
  
  public final void setTarget(InstructionHandle paramInstructionHandle)
  {
    notifyTargetChanging(target, this);
    target = paramInstructionHandle;
    notifyTargetChanged(target, this);
  }
  
  static void notifyTargetChanging(InstructionHandle paramInstructionHandle, InstructionTargeter paramInstructionTargeter)
  {
    if (paramInstructionHandle != null) {
      paramInstructionHandle.removeTargeter(paramInstructionTargeter);
    }
  }
  
  static void notifyTargetChanged(InstructionHandle paramInstructionHandle, InstructionTargeter paramInstructionTargeter)
  {
    if (paramInstructionHandle != null) {
      paramInstructionHandle.addTargeter(paramInstructionTargeter);
    }
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    if (target == paramInstructionHandle1) {
      setTarget(paramInstructionHandle2);
    } else {
      throw new ClassGenException("Not targeting " + paramInstructionHandle1 + ", but " + target);
    }
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle)
  {
    return target == paramInstructionHandle;
  }
  
  void dispose()
  {
    setTarget(null);
    index = -1;
    position = -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\BranchInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */