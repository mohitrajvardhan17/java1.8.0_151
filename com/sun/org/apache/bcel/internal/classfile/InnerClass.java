package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class InnerClass
  implements Cloneable, Node
{
  private int inner_class_index;
  private int outer_class_index;
  private int inner_name_index;
  private int inner_access_flags;
  
  public InnerClass(InnerClass paramInnerClass)
  {
    this(paramInnerClass.getInnerClassIndex(), paramInnerClass.getOuterClassIndex(), paramInnerClass.getInnerNameIndex(), paramInnerClass.getInnerAccessFlags());
  }
  
  InnerClass(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort());
  }
  
  public InnerClass(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    inner_class_index = paramInt1;
    outer_class_index = paramInt2;
    inner_name_index = paramInt3;
    inner_access_flags = paramInt4;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitInnerClass(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(inner_class_index);
    paramDataOutputStream.writeShort(outer_class_index);
    paramDataOutputStream.writeShort(inner_name_index);
    paramDataOutputStream.writeShort(inner_access_flags);
  }
  
  public final int getInnerAccessFlags()
  {
    return inner_access_flags;
  }
  
  public final int getInnerClassIndex()
  {
    return inner_class_index;
  }
  
  public final int getInnerNameIndex()
  {
    return inner_name_index;
  }
  
  public final int getOuterClassIndex()
  {
    return outer_class_index;
  }
  
  public final void setInnerAccessFlags(int paramInt)
  {
    inner_access_flags = paramInt;
  }
  
  public final void setInnerClassIndex(int paramInt)
  {
    inner_class_index = paramInt;
  }
  
  public final void setInnerNameIndex(int paramInt)
  {
    inner_name_index = paramInt;
  }
  
  public final void setOuterClassIndex(int paramInt)
  {
    outer_class_index = paramInt;
  }
  
  public final String toString()
  {
    return "InnerClass(" + inner_class_index + ", " + outer_class_index + ", " + inner_name_index + ", " + inner_access_flags + ")";
  }
  
  public final String toString(ConstantPool paramConstantPool)
  {
    String str1 = paramConstantPool.getConstantString(inner_class_index, (byte)7);
    str1 = Utility.compactClassName(str1);
    String str2;
    if (outer_class_index != 0)
    {
      str2 = paramConstantPool.getConstantString(outer_class_index, (byte)7);
      str2 = Utility.compactClassName(str2);
    }
    else
    {
      str2 = "<not a member>";
    }
    String str3;
    if (inner_name_index != 0) {
      str3 = ((ConstantUtf8)paramConstantPool.getConstant(inner_name_index, (byte)1)).getBytes();
    } else {
      str3 = "<anonymous>";
    }
    String str4 = Utility.accessToString(inner_access_flags, true);
    str4 = str4 + " ";
    return "InnerClass:" + str4 + str1 + "(\"" + str2 + "\", \"" + str3 + "\")";
  }
  
  public InnerClass copy()
  {
    try
    {
      return (InnerClass)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\InnerClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */