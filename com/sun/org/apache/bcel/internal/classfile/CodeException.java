package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public final class CodeException
  implements Cloneable, Constants, Node, Serializable
{
  private int start_pc;
  private int end_pc;
  private int handler_pc;
  private int catch_type;
  
  public CodeException(CodeException paramCodeException)
  {
    this(paramCodeException.getStartPC(), paramCodeException.getEndPC(), paramCodeException.getHandlerPC(), paramCodeException.getCatchType());
  }
  
  CodeException(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort());
  }
  
  public CodeException(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    start_pc = paramInt1;
    end_pc = paramInt2;
    handler_pc = paramInt3;
    catch_type = paramInt4;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitCodeException(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(start_pc);
    paramDataOutputStream.writeShort(end_pc);
    paramDataOutputStream.writeShort(handler_pc);
    paramDataOutputStream.writeShort(catch_type);
  }
  
  public final int getCatchType()
  {
    return catch_type;
  }
  
  public final int getEndPC()
  {
    return end_pc;
  }
  
  public final int getHandlerPC()
  {
    return handler_pc;
  }
  
  public final int getStartPC()
  {
    return start_pc;
  }
  
  public final void setCatchType(int paramInt)
  {
    catch_type = paramInt;
  }
  
  public final void setEndPC(int paramInt)
  {
    end_pc = paramInt;
  }
  
  public final void setHandlerPC(int paramInt)
  {
    handler_pc = paramInt;
  }
  
  public final void setStartPC(int paramInt)
  {
    start_pc = paramInt;
  }
  
  public final String toString()
  {
    return "CodeException(start_pc = " + start_pc + ", end_pc = " + end_pc + ", handler_pc = " + handler_pc + ", catch_type = " + catch_type + ")";
  }
  
  public final String toString(ConstantPool paramConstantPool, boolean paramBoolean)
  {
    String str;
    if (catch_type == 0) {
      str = "<Any exception>(0)";
    } else {
      str = Utility.compactClassName(paramConstantPool.getConstantString(catch_type, (byte)7), false) + (paramBoolean ? "(" + catch_type + ")" : "");
    }
    return start_pc + "\t" + end_pc + "\t" + handler_pc + "\t" + str;
  }
  
  public final String toString(ConstantPool paramConstantPool)
  {
    return toString(paramConstantPool, true);
  }
  
  public CodeException copy()
  {
    try
    {
      return (CodeException)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\CodeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */