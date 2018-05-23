package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class LOOKUPSWITCH
  extends Select
{
  LOOKUPSWITCH() {}
  
  public LOOKUPSWITCH(int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle)
  {
    super((short)171, paramArrayOfInt, paramArrayOfInstructionHandle, paramInstructionHandle);
    length = ((short)(9 + match_length * 8));
    fixed_length = length;
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeInt(match_length);
    for (int i = 0; i < match_length; i++)
    {
      paramDataOutputStream.writeInt(match[i]);
      paramDataOutputStream.writeInt(indices[i] = getTargetOffset(targets[i]));
    }
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    super.initFromFile(paramByteSequence, paramBoolean);
    match_length = paramByteSequence.readInt();
    fixed_length = ((short)(9 + match_length * 8));
    length = ((short)(fixed_length + padding));
    match = new int[match_length];
    indices = new int[match_length];
    targets = new InstructionHandle[match_length];
    for (int i = 0; i < match_length; i++)
    {
      match[i] = paramByteSequence.readInt();
      indices[i] = paramByteSequence.readInt();
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitVariableLengthInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitSelect(this);
    paramVisitor.visitLOOKUPSWITCH(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LOOKUPSWITCH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */