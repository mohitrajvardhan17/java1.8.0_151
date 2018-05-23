package com.sun.org.apache.bcel.internal.generic;

import java.io.DataOutputStream;
import java.io.IOException;

public class JSR
  extends JsrInstruction
  implements VariableLengthInstruction
{
  JSR() {}
  
  public JSR(InstructionHandle paramInstructionHandle)
  {
    super((short)168, paramInstructionHandle);
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    index = getTargetOffset();
    if (opcode == 168)
    {
      super.dump(paramDataOutputStream);
    }
    else
    {
      index = getTargetOffset();
      paramDataOutputStream.writeByte(opcode);
      paramDataOutputStream.writeInt(index);
    }
  }
  
  protected int updatePosition(int paramInt1, int paramInt2)
  {
    int i = getTargetOffset();
    position += paramInt1;
    if (Math.abs(i) >= 32767 - paramInt2)
    {
      opcode = 201;
      length = 5;
      return 2;
    }
    return 0;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitVariableLengthInstruction(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitJsrInstruction(this);
    paramVisitor.visitJSR(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\JSR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */