package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.IOException;

public final class ConstantMethodref
  extends ConstantCP
{
  public ConstantMethodref(ConstantMethodref paramConstantMethodref)
  {
    super((byte)10, paramConstantMethodref.getClassIndex(), paramConstantMethodref.getNameAndTypeIndex());
  }
  
  ConstantMethodref(DataInputStream paramDataInputStream)
    throws IOException
  {
    super((byte)10, paramDataInputStream);
  }
  
  public ConstantMethodref(int paramInt1, int paramInt2)
  {
    super((byte)10, paramInt1, paramInt2);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantMethodref(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantMethodref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */