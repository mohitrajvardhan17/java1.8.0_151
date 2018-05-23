package jdk.internal.org.objectweb.asm;

final class AnnotationWriter
  extends AnnotationVisitor
{
  private final ClassWriter cw;
  private int size;
  private final boolean named;
  private final ByteVector bv;
  private final ByteVector parent;
  private final int offset;
  AnnotationWriter next;
  AnnotationWriter prev;
  
  AnnotationWriter(ClassWriter paramClassWriter, boolean paramBoolean, ByteVector paramByteVector1, ByteVector paramByteVector2, int paramInt)
  {
    super(327680);
    cw = paramClassWriter;
    named = paramBoolean;
    bv = paramByteVector1;
    parent = paramByteVector2;
    offset = paramInt;
  }
  
  public void visit(String paramString, Object paramObject)
  {
    size += 1;
    if (named) {
      bv.putShort(cw.newUTF8(paramString));
    }
    if ((paramObject instanceof String))
    {
      bv.put12(115, cw.newUTF8((String)paramObject));
    }
    else if ((paramObject instanceof Byte))
    {
      bv.put12(66, cw.newInteger(((Byte)paramObject).byteValue()).index);
    }
    else if ((paramObject instanceof Boolean))
    {
      int i = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      bv.put12(90, cw.newInteger(i).index);
    }
    else if ((paramObject instanceof Character))
    {
      bv.put12(67, cw.newInteger(((Character)paramObject).charValue()).index);
    }
    else if ((paramObject instanceof Short))
    {
      bv.put12(83, cw.newInteger(((Short)paramObject).shortValue()).index);
    }
    else if ((paramObject instanceof Type))
    {
      bv.put12(99, cw.newUTF8(((Type)paramObject).getDescriptor()));
    }
    else
    {
      Object localObject;
      int j;
      if ((paramObject instanceof byte[]))
      {
        localObject = (byte[])paramObject;
        bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          bv.put12(66, cw.newInteger(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof boolean[]))
      {
        localObject = (boolean[])paramObject;
        bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          bv.put12(90, cw.newInteger(localObject[j] != 0 ? 1 : 0).index);
        }
      }
      else if ((paramObject instanceof short[]))
      {
        localObject = (short[])paramObject;
        bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          bv.put12(83, cw.newInteger(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof char[]))
      {
        localObject = (char[])paramObject;
        bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          bv.put12(67, cw.newInteger(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof int[]))
      {
        localObject = (int[])paramObject;
        bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          bv.put12(73, cw.newInteger(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof long[]))
      {
        localObject = (long[])paramObject;
        bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          bv.put12(74, cw.newLong(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof float[]))
      {
        localObject = (float[])paramObject;
        bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          bv.put12(70, cw.newFloat(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof double[]))
      {
        localObject = (double[])paramObject;
        bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          bv.put12(68, cw.newDouble(localObject[j]).index);
        }
      }
      else
      {
        localObject = cw.newConstItem(paramObject);
        bv.put12(".s.IFJDCS".charAt(type), index);
      }
    }
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    size += 1;
    if (named) {
      bv.putShort(cw.newUTF8(paramString1));
    }
    bv.put12(101, cw.newUTF8(paramString2)).putShort(cw.newUTF8(paramString3));
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
  {
    size += 1;
    if (named) {
      bv.putShort(cw.newUTF8(paramString1));
    }
    bv.put12(64, cw.newUTF8(paramString2)).putShort(0);
    return new AnnotationWriter(cw, true, bv, bv, bv.length - 2);
  }
  
  public AnnotationVisitor visitArray(String paramString)
  {
    size += 1;
    if (named) {
      bv.putShort(cw.newUTF8(paramString));
    }
    bv.put12(91, 0);
    return new AnnotationWriter(cw, false, bv, bv, bv.length - 2);
  }
  
  public void visitEnd()
  {
    if (parent != null)
    {
      byte[] arrayOfByte = parent.data;
      arrayOfByte[offset] = ((byte)(size >>> 8));
      arrayOfByte[(offset + 1)] = ((byte)size);
    }
  }
  
  int getSize()
  {
    int i = 0;
    for (AnnotationWriter localAnnotationWriter = this; localAnnotationWriter != null; localAnnotationWriter = next) {
      i += bv.length;
    }
    return i;
  }
  
  void put(ByteVector paramByteVector)
  {
    int i = 0;
    int j = 2;
    Object localObject1 = this;
    Object localObject2 = null;
    while (localObject1 != null)
    {
      i++;
      j += bv.length;
      ((AnnotationWriter)localObject1).visitEnd();
      prev = ((AnnotationWriter)localObject2);
      localObject2 = localObject1;
      localObject1 = next;
    }
    paramByteVector.putInt(j);
    paramByteVector.putShort(i);
    for (localObject1 = localObject2; localObject1 != null; localObject1 = prev) {
      paramByteVector.putByteArray(bv.data, 0, bv.length);
    }
  }
  
  static void put(AnnotationWriter[] paramArrayOfAnnotationWriter, int paramInt, ByteVector paramByteVector)
  {
    int i = 1 + 2 * (paramArrayOfAnnotationWriter.length - paramInt);
    for (int j = paramInt; j < paramArrayOfAnnotationWriter.length; j++) {
      i += (paramArrayOfAnnotationWriter[j] == null ? 0 : paramArrayOfAnnotationWriter[j].getSize());
    }
    paramByteVector.putInt(i).putByte(paramArrayOfAnnotationWriter.length - paramInt);
    for (j = paramInt; j < paramArrayOfAnnotationWriter.length; j++)
    {
      Object localObject1 = paramArrayOfAnnotationWriter[j];
      Object localObject2 = null;
      int k = 0;
      while (localObject1 != null)
      {
        k++;
        ((AnnotationWriter)localObject1).visitEnd();
        prev = ((AnnotationWriter)localObject2);
        localObject2 = localObject1;
        localObject1 = next;
      }
      paramByteVector.putShort(k);
      for (localObject1 = localObject2; localObject1 != null; localObject1 = prev) {
        paramByteVector.putByteArray(bv.data, 0, bv.length);
      }
    }
  }
  
  static void putTarget(int paramInt, TypePath paramTypePath, ByteVector paramByteVector)
  {
    switch (paramInt >>> 24)
    {
    case 0: 
    case 1: 
    case 22: 
      paramByteVector.putShort(paramInt >>> 16);
      break;
    case 19: 
    case 20: 
    case 21: 
      paramByteVector.putByte(paramInt >>> 24);
      break;
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
      paramByteVector.putInt(paramInt);
      break;
    default: 
      paramByteVector.put12(paramInt >>> 24, (paramInt & 0xFFFF00) >> 8);
    }
    if (paramTypePath == null)
    {
      paramByteVector.putByte(0);
    }
    else
    {
      int i = b[offset] * 2 + 1;
      paramByteVector.putByteArray(b, offset, i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\AnnotationWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */