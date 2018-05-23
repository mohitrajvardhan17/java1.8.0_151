package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.LineNumber;
import java.io.PrintStream;
import java.io.Serializable;

public class LineNumberGen
  implements InstructionTargeter, Cloneable, Serializable
{
  private InstructionHandle ih;
  private int src_line;
  
  public LineNumberGen(InstructionHandle paramInstructionHandle, int paramInt)
  {
    setInstruction(paramInstructionHandle);
    setSourceLine(paramInt);
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle)
  {
    return ih == paramInstructionHandle;
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    if (paramInstructionHandle1 != ih) {
      throw new ClassGenException("Not targeting " + paramInstructionHandle1 + ", but " + ih + "}");
    }
    setInstruction(paramInstructionHandle2);
  }
  
  public LineNumber getLineNumber()
  {
    return new LineNumber(ih.getPosition(), src_line);
  }
  
  public final void setInstruction(InstructionHandle paramInstructionHandle)
  {
    BranchInstruction.notifyTargetChanging(ih, this);
    ih = paramInstructionHandle;
    BranchInstruction.notifyTargetChanged(ih, this);
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      System.err.println(localCloneNotSupportedException);
    }
    return null;
  }
  
  public InstructionHandle getInstruction()
  {
    return ih;
  }
  
  public void setSourceLine(int paramInt)
  {
    src_line = paramInt;
  }
  
  public int getSourceLine()
  {
    return src_line;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LineNumberGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */