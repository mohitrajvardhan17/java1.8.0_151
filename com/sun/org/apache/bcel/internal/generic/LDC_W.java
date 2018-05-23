package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;

public class LDC_W
  extends LDC
{
  LDC_W() {}
  
  public LDC_W(int paramInt)
  {
    super(paramInt);
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    setIndex(paramByteSequence.readUnsignedShort());
    opcode = 19;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LDC_W.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */