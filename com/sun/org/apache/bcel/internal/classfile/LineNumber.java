package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public final class LineNumber
  implements Cloneable, Node, Serializable
{
  private int start_pc;
  private int line_number;
  
  public LineNumber(LineNumber paramLineNumber)
  {
    this(paramLineNumber.getStartPC(), paramLineNumber.getLineNumber());
  }
  
  LineNumber(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort());
  }
  
  public LineNumber(int paramInt1, int paramInt2)
  {
    start_pc = paramInt1;
    line_number = paramInt2;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitLineNumber(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(start_pc);
    paramDataOutputStream.writeShort(line_number);
  }
  
  public final int getLineNumber()
  {
    return line_number;
  }
  
  public final int getStartPC()
  {
    return start_pc;
  }
  
  public final void setLineNumber(int paramInt)
  {
    line_number = paramInt;
  }
  
  public final void setStartPC(int paramInt)
  {
    start_pc = paramInt;
  }
  
  public final String toString()
  {
    return "LineNumber(" + start_pc + ", " + line_number + ")";
  }
  
  public LineNumber copy()
  {
    try
    {
      return (LineNumber)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\LineNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */