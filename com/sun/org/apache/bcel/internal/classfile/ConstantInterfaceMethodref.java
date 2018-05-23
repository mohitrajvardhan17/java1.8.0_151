package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.IOException;

public final class ConstantInterfaceMethodref
  extends ConstantCP
{
  public ConstantInterfaceMethodref(ConstantInterfaceMethodref paramConstantInterfaceMethodref)
  {
    super((byte)11, paramConstantInterfaceMethodref.getClassIndex(), paramConstantInterfaceMethodref.getNameAndTypeIndex());
  }
  
  ConstantInterfaceMethodref(DataInputStream paramDataInputStream)
    throws IOException
  {
    super((byte)11, paramDataInputStream);
  }
  
  public ConstantInterfaceMethodref(int paramInt1, int paramInt2)
  {
    super((byte)11, paramInt1, paramInt2);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantInterfaceMethodref(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantInterfaceMethodref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */