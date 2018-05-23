package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public final class PMGClass
  extends Attribute
{
  private int pmg_class_index;
  private int pmg_index;
  
  public PMGClass(PMGClass paramPMGClass)
  {
    this(paramPMGClass.getNameIndex(), paramPMGClass.getLength(), paramPMGClass.getPMGIndex(), paramPMGClass.getPMGClassIndex(), paramPMGClass.getConstantPool());
  }
  
  PMGClass(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramConstantPool);
  }
  
  public PMGClass(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ConstantPool paramConstantPool)
  {
    super((byte)9, paramInt1, paramInt2, paramConstantPool);
    pmg_index = paramInt3;
    pmg_class_index = paramInt4;
  }
  
  public void accept(Visitor paramVisitor)
  {
    System.err.println("Visiting non-standard PMGClass object");
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(pmg_index);
    paramDataOutputStream.writeShort(pmg_class_index);
  }
  
  public final int getPMGClassIndex()
  {
    return pmg_class_index;
  }
  
  public final void setPMGClassIndex(int paramInt)
  {
    pmg_class_index = paramInt;
  }
  
  public final int getPMGIndex()
  {
    return pmg_index;
  }
  
  public final void setPMGIndex(int paramInt)
  {
    pmg_index = paramInt;
  }
  
  public final String getPMGName()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(pmg_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  public final String getPMGClassName()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(pmg_class_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  public final String toString()
  {
    return "PMGClass(" + getPMGName() + ", " + getPMGClassName() + ")";
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    return (PMGClass)clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\PMGClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */