package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class JSR_W
  extends JsrInstruction
{
  JSR_W() {}
  
  public JSR_W(InstructionHandle paramInstructionHandle)
  {
    super((short)201, paramInstructionHandle);
    length = 5;
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    index = getTargetOffset();
    paramDataOutputStream.writeByte(opcode);
    paramDataOutputStream.writeInt(index);
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    index = paramByteSequence.readInt();
    length = 5;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitJsrInstruction(this);
    paramVisitor.visitJSR_W(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\JSR_W.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */