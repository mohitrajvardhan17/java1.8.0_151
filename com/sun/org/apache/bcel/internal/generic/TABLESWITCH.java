package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class TABLESWITCH
  extends Select
{
  TABLESWITCH() {}
  
  public TABLESWITCH(int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle)
  {
    super((short)170, paramArrayOfInt, paramArrayOfInstructionHandle, paramInstructionHandle);
    length = ((short)(13 + match_length * 4));
    fixed_length = length;
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    int i = match_length > 0 ? match[0] : 0;
    paramDataOutputStream.writeInt(i);
    int j = match_length > 0 ? match[(match_length - 1)] : 0;
    paramDataOutputStream.writeInt(j);
    for (int k = 0; k < match_length; k++) {
      paramDataOutputStream.writeInt(indices[k] = getTargetOffset(targets[k]));
    }
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    super.initFromFile(paramByteSequence, paramBoolean);
    int i = paramByteSequence.readInt();
    int j = paramByteSequence.readInt();
    match_length = (j - i + 1);
    fixed_length = ((short)(13 + match_length * 4));
    length = ((short)(fixed_length + padding));
    match = new int[match_length];
    indices = new int[match_length];
    targets = new InstructionHandle[match_length];
    for (int k = i; k <= j; k++) {
      match[(k - i)] = k;
    }
    for (k = 0; k < match_length; k++) {
      indices[k] = paramByteSequence.readInt();
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitVariableLengthInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitSelect(this);
    paramVisitor.visitTABLESWITCH(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\TABLESWITCH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */