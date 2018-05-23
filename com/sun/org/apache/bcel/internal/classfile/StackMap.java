package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StackMap
  extends Attribute
  implements Node
{
  private int map_length;
  private StackMapEntry[] map;
  
  public StackMap(int paramInt1, int paramInt2, StackMapEntry[] paramArrayOfStackMapEntry, ConstantPool paramConstantPool)
  {
    super((byte)11, paramInt1, paramInt2, paramConstantPool);
    setStackMap(paramArrayOfStackMapEntry);
  }
  
  StackMap(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (StackMapEntry[])null, paramConstantPool);
    map_length = paramDataInputStream.readUnsignedShort();
    map = new StackMapEntry[map_length];
    for (int i = 0; i < map_length; i++) {
      map[i] = new StackMapEntry(paramDataInputStream, paramConstantPool);
    }
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(map_length);
    for (int i = 0; i < map_length; i++) {
      map[i].dump(paramDataOutputStream);
    }
  }
  
  public final StackMapEntry[] getStackMap()
  {
    return map;
  }
  
  public final void setStackMap(StackMapEntry[] paramArrayOfStackMapEntry)
  {
    map = paramArrayOfStackMapEntry;
    map_length = (paramArrayOfStackMapEntry == null ? 0 : paramArrayOfStackMapEntry.length);
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("StackMap(");
    for (int i = 0; i < map_length; i++)
    {
      localStringBuffer.append(map[i].toString());
      if (i < map_length - 1) {
        localStringBuffer.append(", ");
      }
    }
    localStringBuffer.append(')');
    return localStringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    StackMap localStackMap = (StackMap)clone();
    map = new StackMapEntry[map_length];
    for (int i = 0; i < map_length; i++) {
      map[i] = map[i].copy();
    }
    constant_pool = paramConstantPool;
    return localStackMap;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackMap(this);
  }
  
  public final int getMapLength()
  {
    return map_length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\StackMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */