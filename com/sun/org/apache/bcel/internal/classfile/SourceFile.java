package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class SourceFile
  extends Attribute
{
  private int sourcefile_index;
  
  public SourceFile(SourceFile paramSourceFile)
  {
    this(paramSourceFile.getNameIndex(), paramSourceFile.getLength(), paramSourceFile.getSourceFileIndex(), paramSourceFile.getConstantPool());
  }
  
  SourceFile(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramConstantPool);
  }
  
  public SourceFile(int paramInt1, int paramInt2, int paramInt3, ConstantPool paramConstantPool)
  {
    super((byte)0, paramInt1, paramInt2, paramConstantPool);
    sourcefile_index = paramInt3;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitSourceFile(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(sourcefile_index);
  }
  
  public final int getSourceFileIndex()
  {
    return sourcefile_index;
  }
  
  public final void setSourceFileIndex(int paramInt)
  {
    sourcefile_index = paramInt;
  }
  
  public final String getSourceFileName()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(sourcefile_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  public final String toString()
  {
    return "SourceFile(" + getSourceFileName() + ")";
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    return (SourceFile)clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\SourceFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */