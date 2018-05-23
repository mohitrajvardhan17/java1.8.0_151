package com.sun.xml.internal.ws.org.objectweb.asm;

import java.io.IOException;
import java.io.InputStream;

public class ClassReader
{
  static final boolean SIGNATURES = true;
  static final boolean ANNOTATIONS = true;
  static final boolean FRAMES = true;
  static final boolean WRITER = true;
  static final boolean RESIZE = true;
  public static final int SKIP_CODE = 1;
  public static final int SKIP_DEBUG = 2;
  public static final int SKIP_FRAMES = 4;
  public static final int EXPAND_FRAMES = 8;
  public final byte[] b;
  private final int[] items;
  private final String[] strings;
  private final int maxStringLength;
  public final int header;
  
  public ClassReader(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public ClassReader(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    b = paramArrayOfByte;
    items = new int[readUnsignedShort(paramInt1 + 8)];
    int i = items.length;
    strings = new String[i];
    int j = 0;
    int k = paramInt1 + 10;
    for (int m = 1; m < i; m++)
    {
      items[m] = (k + 1);
      int n;
      switch (paramArrayOfByte[k])
      {
      case 3: 
      case 4: 
      case 9: 
      case 10: 
      case 11: 
      case 12: 
        n = 5;
        break;
      case 5: 
      case 6: 
        n = 9;
        m++;
        break;
      case 1: 
        n = 3 + readUnsignedShort(k + 1);
        if (n > j) {
          j = n;
        }
        break;
      case 2: 
      case 7: 
      case 8: 
      default: 
        n = 3;
      }
      k += n;
    }
    maxStringLength = j;
    header = k;
  }
  
  public int getAccess()
  {
    return readUnsignedShort(header);
  }
  
  public String getClassName()
  {
    return readClass(header + 2, new char[maxStringLength]);
  }
  
  public String getSuperName()
  {
    int i = items[readUnsignedShort(header + 4)];
    return i == 0 ? null : readUTF8(i, new char[maxStringLength]);
  }
  
  public String[] getInterfaces()
  {
    int i = header + 6;
    int j = readUnsignedShort(i);
    String[] arrayOfString = new String[j];
    if (j > 0)
    {
      char[] arrayOfChar = new char[maxStringLength];
      for (int k = 0; k < j; k++)
      {
        i += 2;
        arrayOfString[k] = readClass(i, arrayOfChar);
      }
    }
    return arrayOfString;
  }
  
  void copyPool(ClassWriter paramClassWriter)
  {
    char[] arrayOfChar = new char[maxStringLength];
    int i = items.length;
    Item[] arrayOfItem = new Item[i];
    for (int j = 1; j < i; j++)
    {
      int k = items[j];
      int m = b[(k - 1)];
      Item localItem = new Item(j);
      switch (m)
      {
      case 9: 
      case 10: 
      case 11: 
        int n = items[readUnsignedShort(k + 2)];
        localItem.set(m, readClass(k, arrayOfChar), readUTF8(n, arrayOfChar), readUTF8(n + 2, arrayOfChar));
        break;
      case 3: 
        localItem.set(readInt(k));
        break;
      case 4: 
        localItem.set(Float.intBitsToFloat(readInt(k)));
        break;
      case 12: 
        localItem.set(m, readUTF8(k, arrayOfChar), readUTF8(k + 2, arrayOfChar), null);
        break;
      case 5: 
        localItem.set(readLong(k));
        j++;
        break;
      case 6: 
        localItem.set(Double.longBitsToDouble(readLong(k)));
        j++;
        break;
      case 1: 
        String str = strings[j];
        if (str == null)
        {
          k = items[j];
          str = strings[j] = readUTF(k + 2, readUnsignedShort(k), arrayOfChar);
        }
        localItem.set(m, str, null, null);
        break;
      case 2: 
      case 7: 
      case 8: 
      default: 
        localItem.set(m, readUTF8(k, arrayOfChar), null, null);
      }
      int i1 = hashCode % arrayOfItem.length;
      next = arrayOfItem[i1];
      arrayOfItem[i1] = localItem;
    }
    j = items[1] - 1;
    pool.putByteArray(b, j, header - j);
    items = arrayOfItem;
    threshold = ((int)(0.75D * i));
    index = i;
  }
  
  public ClassReader(InputStream paramInputStream)
    throws IOException
  {
    this(readClass(paramInputStream));
  }
  
  public ClassReader(String paramString)
    throws IOException
  {
    this(ClassLoader.getSystemResourceAsStream(paramString.replace('.', '/') + ".class"));
  }
  
  private static byte[] readClass(InputStream paramInputStream)
    throws IOException
  {
    if (paramInputStream == null) {
      throw new IOException("Class not found");
    }
    Object localObject = new byte[paramInputStream.available()];
    int i = 0;
    for (;;)
    {
      int j = paramInputStream.read((byte[])localObject, i, localObject.length - i);
      byte[] arrayOfByte;
      if (j == -1)
      {
        if (i < localObject.length)
        {
          arrayOfByte = new byte[i];
          System.arraycopy(localObject, 0, arrayOfByte, 0, i);
          localObject = arrayOfByte;
        }
        return (byte[])localObject;
      }
      i += j;
      if (i == localObject.length)
      {
        arrayOfByte = new byte[localObject.length + 1000];
        System.arraycopy(localObject, 0, arrayOfByte, 0, i);
        localObject = arrayOfByte;
      }
    }
  }
  
  public void accept(ClassVisitor paramClassVisitor, int paramInt)
  {
    accept(paramClassVisitor, new Attribute[0], paramInt);
  }
  
  public void accept(ClassVisitor paramClassVisitor, Attribute[] paramArrayOfAttribute, int paramInt)
  {
    byte[] arrayOfByte = b;
    char[] arrayOfChar = new char[maxStringLength];
    int i3 = 0;
    int i4 = 0;
    Object localObject1 = null;
    int m = header;
    int i2 = readUnsignedShort(m);
    String str1 = readClass(m + 2, arrayOfChar);
    int n = items[readUnsignedShort(m + 4)];
    String str5 = n == 0 ? null : readUTF8(n, arrayOfChar);
    String[] arrayOfString1 = new String[readUnsignedShort(m + 6)];
    int i1 = 0;
    m += 8;
    for (int i = 0; i < arrayOfString1.length; i++)
    {
      arrayOfString1[i] = readClass(m, arrayOfChar);
      m += 2;
    }
    int i5 = (paramInt & 0x1) != 0 ? 1 : 0;
    int i6 = (paramInt & 0x2) != 0 ? 1 : 0;
    int i7 = (paramInt & 0x8) != 0 ? 1 : 0;
    n = m;
    i = readUnsignedShort(n);
    n += 2;
    int j;
    while (i > 0)
    {
      j = readUnsignedShort(n + 6);
      n += 8;
      while (j > 0)
      {
        n += 6 + readInt(n + 2);
        j--;
      }
      i--;
    }
    i = readUnsignedShort(n);
    n += 2;
    while (i > 0)
    {
      j = readUnsignedShort(n + 6);
      n += 8;
      while (j > 0)
      {
        n += 6 + readInt(n + 2);
        j--;
      }
      i--;
    }
    String str4 = null;
    String str6 = null;
    String str7 = null;
    String str8 = null;
    String str9 = null;
    String str10 = null;
    i = readUnsignedShort(n);
    n += 2;
    String str3;
    int i8;
    Attribute localAttribute;
    while (i > 0)
    {
      str3 = readUTF8(n, arrayOfChar);
      if ("SourceFile".equals(str3))
      {
        str6 = readUTF8(n + 6, arrayOfChar);
      }
      else if ("InnerClasses".equals(str3))
      {
        i1 = n + 6;
      }
      else if ("EnclosingMethod".equals(str3))
      {
        str8 = readClass(n + 6, arrayOfChar);
        i8 = readUnsignedShort(n + 8);
        if (i8 != 0)
        {
          str9 = readUTF8(items[i8], arrayOfChar);
          str10 = readUTF8(items[i8] + 2, arrayOfChar);
        }
      }
      else if ("Signature".equals(str3))
      {
        str4 = readUTF8(n + 6, arrayOfChar);
      }
      else if ("RuntimeVisibleAnnotations".equals(str3))
      {
        i3 = n + 6;
      }
      else if ("Deprecated".equals(str3))
      {
        i2 |= 0x20000;
      }
      else if ("Synthetic".equals(str3))
      {
        i2 |= 0x1000;
      }
      else if ("SourceDebugExtension".equals(str3))
      {
        i8 = readInt(n + 2);
        str7 = readUTF(n + 6, i8, new char[i8]);
      }
      else if ("RuntimeInvisibleAnnotations".equals(str3))
      {
        i4 = n + 6;
      }
      else
      {
        localAttribute = readAttribute(paramArrayOfAttribute, str3, n + 6, readInt(n + 2), arrayOfChar, -1, null);
        if (localAttribute != null)
        {
          next = ((Attribute)localObject1);
          localObject1 = localAttribute;
        }
      }
      n += 6 + readInt(n + 2);
      i--;
    }
    paramClassVisitor.visit(readInt(4), i2, str1, str4, str5, arrayOfString1);
    if ((i6 == 0) && ((str6 != null) || (str7 != null))) {
      paramClassVisitor.visitSource(str6, str7);
    }
    if (str8 != null) {
      paramClassVisitor.visitOuterClass(str8, str9, str10);
    }
    for (i = 1; i >= 0; i--)
    {
      n = i == 0 ? i4 : i3;
      if (n != 0)
      {
        j = readUnsignedShort(n);
        n += 2;
        while (j > 0)
        {
          n = readAnnotationValues(n + 2, arrayOfChar, true, paramClassVisitor.visitAnnotation(readUTF8(n, arrayOfChar), i != 0));
          j--;
        }
      }
    }
    while (localObject1 != null)
    {
      localAttribute = next;
      next = null;
      paramClassVisitor.visitAttribute((Attribute)localObject1);
      localObject1 = localAttribute;
    }
    if (i1 != 0)
    {
      i = readUnsignedShort(i1);
      i1 += 2;
      while (i > 0)
      {
        paramClassVisitor.visitInnerClass(readUnsignedShort(i1) == 0 ? null : readClass(i1, arrayOfChar), readUnsignedShort(i1 + 2) == 0 ? null : readClass(i1 + 2, arrayOfChar), readUnsignedShort(i1 + 4) == 0 ? null : readUTF8(i1 + 4, arrayOfChar), readUnsignedShort(i1 + 6));
        i1 += 8;
        i--;
      }
    }
    i = readUnsignedShort(m);
    m += 2;
    String str2;
    int k;
    while (i > 0)
    {
      i2 = readUnsignedShort(m);
      str1 = readUTF8(m + 2, arrayOfChar);
      str2 = readUTF8(m + 4, arrayOfChar);
      i8 = 0;
      str4 = null;
      i3 = 0;
      i4 = 0;
      localObject1 = null;
      j = readUnsignedShort(m + 6);
      m += 8;
      while (j > 0)
      {
        str3 = readUTF8(m, arrayOfChar);
        if ("ConstantValue".equals(str3))
        {
          i8 = readUnsignedShort(m + 6);
        }
        else if ("Signature".equals(str3))
        {
          str4 = readUTF8(m + 6, arrayOfChar);
        }
        else if ("Deprecated".equals(str3))
        {
          i2 |= 0x20000;
        }
        else if ("Synthetic".equals(str3))
        {
          i2 |= 0x1000;
        }
        else if ("RuntimeVisibleAnnotations".equals(str3))
        {
          i3 = m + 6;
        }
        else if ("RuntimeInvisibleAnnotations".equals(str3))
        {
          i4 = m + 6;
        }
        else
        {
          localAttribute = readAttribute(paramArrayOfAttribute, str3, m + 6, readInt(m + 2), arrayOfChar, -1, null);
          if (localAttribute != null)
          {
            next = ((Attribute)localObject1);
            localObject1 = localAttribute;
          }
        }
        m += 6 + readInt(m + 2);
        j--;
      }
      FieldVisitor localFieldVisitor = paramClassVisitor.visitField(i2, str1, str2, str4, i8 == 0 ? null : readConst(i8, arrayOfChar));
      if (localFieldVisitor != null)
      {
        for (j = 1; j >= 0; j--)
        {
          n = j == 0 ? i4 : i3;
          if (n != 0)
          {
            k = readUnsignedShort(n);
            n += 2;
            while (k > 0)
            {
              n = readAnnotationValues(n + 2, arrayOfChar, true, localFieldVisitor.visitAnnotation(readUTF8(n, arrayOfChar), j != 0));
              k--;
            }
          }
        }
        while (localObject1 != null)
        {
          localAttribute = next;
          next = null;
          localFieldVisitor.visitAttribute((Attribute)localObject1);
          localObject1 = localAttribute;
        }
        localFieldVisitor.visitEnd();
      }
      i--;
    }
    i = readUnsignedShort(m);
    m += 2;
    while (i > 0)
    {
      i8 = m + 6;
      i2 = readUnsignedShort(m);
      str1 = readUTF8(m + 2, arrayOfChar);
      str2 = readUTF8(m + 4, arrayOfChar);
      str4 = null;
      i3 = 0;
      i4 = 0;
      int i9 = 0;
      int i10 = 0;
      int i11 = 0;
      localObject1 = null;
      n = 0;
      i1 = 0;
      j = readUnsignedShort(m + 6);
      m += 8;
      while (j > 0)
      {
        str3 = readUTF8(m, arrayOfChar);
        int i12 = readInt(m + 2);
        m += 6;
        if ("Code".equals(str3))
        {
          if (i5 == 0) {
            n = m;
          }
        }
        else if ("Exceptions".equals(str3))
        {
          i1 = m;
        }
        else if ("Signature".equals(str3))
        {
          str4 = readUTF8(m, arrayOfChar);
        }
        else if ("Deprecated".equals(str3))
        {
          i2 |= 0x20000;
        }
        else if ("RuntimeVisibleAnnotations".equals(str3))
        {
          i3 = m;
        }
        else if ("AnnotationDefault".equals(str3))
        {
          i9 = m;
        }
        else if ("Synthetic".equals(str3))
        {
          i2 |= 0x1000;
        }
        else if ("RuntimeInvisibleAnnotations".equals(str3))
        {
          i4 = m;
        }
        else if ("RuntimeVisibleParameterAnnotations".equals(str3))
        {
          i10 = m;
        }
        else if ("RuntimeInvisibleParameterAnnotations".equals(str3))
        {
          i11 = m;
        }
        else
        {
          localAttribute = readAttribute(paramArrayOfAttribute, str3, m, i12, arrayOfChar, -1, null);
          if (localAttribute != null)
          {
            next = ((Attribute)localObject1);
            localObject1 = localAttribute;
          }
        }
        m += i12;
        j--;
      }
      String[] arrayOfString2;
      if (i1 == 0)
      {
        arrayOfString2 = null;
      }
      else
      {
        arrayOfString2 = new String[readUnsignedShort(i1)];
        i1 += 2;
        for (j = 0; j < arrayOfString2.length; j++)
        {
          arrayOfString2[j] = readClass(i1, arrayOfChar);
          i1 += 2;
        }
      }
      MethodVisitor localMethodVisitor = paramClassVisitor.visitMethod(i2, str1, str2, str4, arrayOfString2);
      int i14;
      if (localMethodVisitor != null)
      {
        Object localObject2;
        if ((localMethodVisitor instanceof MethodWriter))
        {
          localObject2 = (MethodWriter)localMethodVisitor;
          if ((cw.cr == this) && (str4 == signature))
          {
            i14 = 0;
            if (arrayOfString2 == null)
            {
              i14 = exceptionCount == 0 ? 1 : 0;
            }
            else if (arrayOfString2.length == exceptionCount)
            {
              i14 = 1;
              for (j = arrayOfString2.length - 1; j >= 0; j--)
              {
                i1 -= 2;
                if (exceptions[j] != readUnsignedShort(i1))
                {
                  i14 = 0;
                  break;
                }
              }
            }
            if (i14 != 0)
            {
              classReaderOffset = i8;
              classReaderLength = (m - i8);
              break label5472;
            }
          }
        }
        if (i9 != 0)
        {
          localObject2 = localMethodVisitor.visitAnnotationDefault();
          readAnnotationValue(i9, arrayOfChar, null, (AnnotationVisitor)localObject2);
          if (localObject2 != null) {
            ((AnnotationVisitor)localObject2).visitEnd();
          }
        }
        for (j = 1; j >= 0; j--)
        {
          i1 = j == 0 ? i4 : i3;
          if (i1 != 0)
          {
            k = readUnsignedShort(i1);
            i1 += 2;
            while (k > 0)
            {
              i1 = readAnnotationValues(i1 + 2, arrayOfChar, true, localMethodVisitor.visitAnnotation(readUTF8(i1, arrayOfChar), j != 0));
              k--;
            }
          }
        }
        if (i10 != 0) {
          readParameterAnnotations(i10, str2, arrayOfChar, true, localMethodVisitor);
        }
        if (i11 != 0) {
          readParameterAnnotations(i11, str2, arrayOfChar, false, localMethodVisitor);
        }
        while (localObject1 != null)
        {
          localAttribute = next;
          next = null;
          localMethodVisitor.visitAttribute((Attribute)localObject1);
          localObject1 = localAttribute;
        }
      }
      else
      {
        if ((localMethodVisitor != null) && (n != 0))
        {
          int i13 = readUnsignedShort(n);
          i14 = readUnsignedShort(n + 2);
          int i15 = readInt(n + 4);
          n += 8;
          int i16 = n;
          int i17 = n + i15;
          localMethodVisitor.visitCode();
          Label[] arrayOfLabel1 = new Label[i15 + 2];
          readLabel(i15 + 1, arrayOfLabel1);
          while (n < i17)
          {
            i1 = n - i16;
            int i19 = arrayOfByte[n] & 0xFF;
            switch (ClassWriter.TYPE[i19])
            {
            case 0: 
            case 4: 
              n++;
              break;
            case 8: 
              readLabel(i1 + readShort(n + 1), arrayOfLabel1);
              n += 3;
              break;
            case 9: 
              readLabel(i1 + readInt(n + 1), arrayOfLabel1);
              n += 5;
              break;
            case 16: 
              i19 = arrayOfByte[(n + 1)] & 0xFF;
              if (i19 == 132) {
                n += 6;
              } else {
                n += 4;
              }
              break;
            case 13: 
              n = n + 4 - (i1 & 0x3);
              readLabel(i1 + readInt(n), arrayOfLabel1);
              j = readInt(n + 8) - readInt(n + 4) + 1;
              n += 12;
            case 14: 
            case 1: 
            case 3: 
            case 10: 
            case 2: 
            case 5: 
            case 6: 
            case 11: 
            case 12: 
            case 7: 
            case 15: 
            default: 
              while (j > 0)
              {
                readLabel(i1 + readInt(n), arrayOfLabel1);
                n += 4;
                j--;
                continue;
                n = n + 4 - (i1 & 0x3);
                readLabel(i1 + readInt(n), arrayOfLabel1);
                j = readInt(n + 4);
                n += 8;
                while (j > 0)
                {
                  readLabel(i1 + readInt(n + 4), arrayOfLabel1);
                  n += 8;
                  j--;
                  continue;
                  n += 2;
                  break;
                  n += 3;
                  break;
                  n += 5;
                  break;
                  n += 4;
                }
              }
            }
          }
          j = readUnsignedShort(n);
          n += 2;
          while (j > 0)
          {
            Label localLabel1 = readLabel(readUnsignedShort(n), arrayOfLabel1);
            Label localLabel2 = readLabel(readUnsignedShort(n + 2), arrayOfLabel1);
            Label localLabel3 = readLabel(readUnsignedShort(n + 4), arrayOfLabel1);
            i23 = readUnsignedShort(n + 6);
            if (i23 == 0) {
              localMethodVisitor.visitTryCatchBlock(localLabel1, localLabel2, localLabel3, null);
            } else {
              localMethodVisitor.visitTryCatchBlock(localLabel1, localLabel2, localLabel3, readUTF8(items[i23], arrayOfChar));
            }
            n += 8;
            j--;
          }
          int i20 = 0;
          int i21 = 0;
          int i22 = 0;
          int i23 = 0;
          int i24 = 0;
          int i25 = 0;
          int i26 = 0;
          int i27 = 0;
          int i28 = 0;
          Object[] arrayOfObject1 = null;
          Object[] arrayOfObject2 = null;
          int i29 = 1;
          localObject1 = null;
          j = readUnsignedShort(n);
          n += 2;
          int i18;
          while (j > 0)
          {
            str3 = readUTF8(n, arrayOfChar);
            if ("LocalVariableTable".equals(str3))
            {
              if (i6 == 0)
              {
                i20 = n + 6;
                k = readUnsignedShort(n + 6);
                i1 = n + 8;
                while (k > 0)
                {
                  i18 = readUnsignedShort(i1);
                  if (arrayOfLabel1[i18] == null) {
                    readLabelstatus |= 0x1;
                  }
                  i18 += readUnsignedShort(i1 + 2);
                  if (arrayOfLabel1[i18] == null) {
                    readLabelstatus |= 0x1;
                  }
                  i1 += 10;
                  k--;
                }
              }
            }
            else if ("LocalVariableTypeTable".equals(str3)) {
              i21 = n + 6;
            } else if ("LineNumberTable".equals(str3))
            {
              if (i6 == 0)
              {
                k = readUnsignedShort(n + 6);
                i1 = n + 8;
                while (k > 0)
                {
                  i18 = readUnsignedShort(i1);
                  if (arrayOfLabel1[i18] == null) {
                    readLabelstatus |= 0x1;
                  }
                  line = readUnsignedShort(i1 + 2);
                  i1 += 4;
                  k--;
                }
              }
            }
            else if ("StackMapTable".equals(str3))
            {
              if ((paramInt & 0x4) == 0)
              {
                i22 = n + 8;
                i23 = readUnsignedShort(n + 6);
              }
            }
            else if ("StackMap".equals(str3))
            {
              if ((paramInt & 0x4) == 0)
              {
                i22 = n + 8;
                i23 = readUnsignedShort(n + 6);
                i29 = 0;
              }
            }
            else {
              for (k = 0; k < paramArrayOfAttribute.length; k++) {
                if (type.equals(str3))
                {
                  localAttribute = paramArrayOfAttribute[k].read(this, n + 6, readInt(n + 2), arrayOfChar, i16 - 8, arrayOfLabel1);
                  if (localAttribute != null)
                  {
                    next = ((Attribute)localObject1);
                    localObject1 = localAttribute;
                  }
                }
              }
            }
            n += 6 + readInt(n + 2);
            j--;
          }
          if (i22 != 0)
          {
            arrayOfObject1 = new Object[i14];
            arrayOfObject2 = new Object[i13];
            if (i7 != 0)
            {
              int i30 = 0;
              if ((i2 & 0x8) == 0) {
                if ("<init>".equals(str1)) {
                  arrayOfObject1[(i30++)] = Opcodes.UNINITIALIZED_THIS;
                } else {
                  arrayOfObject1[(i30++)] = readClass(header + 2, arrayOfChar);
                }
              }
              j = 1;
              for (;;)
              {
                k = j;
                switch (str2.charAt(j++))
                {
                case 'B': 
                case 'C': 
                case 'I': 
                case 'S': 
                case 'Z': 
                  arrayOfObject1[(i30++)] = Opcodes.INTEGER;
                  break;
                case 'F': 
                  arrayOfObject1[(i30++)] = Opcodes.FLOAT;
                  break;
                case 'J': 
                  arrayOfObject1[(i30++)] = Opcodes.LONG;
                  break;
                case 'D': 
                  arrayOfObject1[(i30++)] = Opcodes.DOUBLE;
                  break;
                case '[': 
                  while (str2.charAt(j) == '[') {
                    j++;
                  }
                  if (str2.charAt(j) == 'L')
                  {
                    j++;
                    while (str2.charAt(j) != ';') {
                      j++;
                    }
                  }
                  arrayOfObject1[(i30++)] = str2.substring(k, ++j);
                  break;
                case 'L': 
                  while (str2.charAt(j) != ';') {
                    j++;
                  }
                  arrayOfObject1[(i30++)] = str2.substring(k + 1, j++);
                }
              }
              i26 = i30;
            }
            i25 = -1;
          }
          n = i16;
          int i32;
          int i33;
          Object localObject3;
          while (n < i17)
          {
            i1 = n - i16;
            localLabel4 = arrayOfLabel1[i1];
            if (localLabel4 != null)
            {
              localMethodVisitor.visitLabel(localLabel4);
              if ((i6 == 0) && (line > 0)) {
                localMethodVisitor.visitLineNumber(line, localLabel4);
              }
            }
            while ((arrayOfObject1 != null) && ((i25 == i1) || (i25 == -1)))
            {
              if ((i29 == 0) || (i7 != 0)) {
                localMethodVisitor.visitFrame(-1, i26, arrayOfObject1, i28, arrayOfObject2);
              } else if (i25 != -1) {
                localMethodVisitor.visitFrame(i24, i27, arrayOfObject1, i28, arrayOfObject2);
              }
              if (i23 > 0)
              {
                if (i29 != 0)
                {
                  i31 = arrayOfByte[(i22++)] & 0xFF;
                }
                else
                {
                  i31 = 255;
                  i25 = -1;
                }
                i27 = 0;
                if (i31 < 64)
                {
                  i32 = i31;
                  i24 = 3;
                  i28 = 0;
                }
                else if (i31 < 128)
                {
                  i32 = i31 - 64;
                  i22 = readFrameType(arrayOfObject2, 0, i22, arrayOfChar, arrayOfLabel1);
                  i24 = 4;
                  i28 = 1;
                }
                else
                {
                  i32 = readUnsignedShort(i22);
                  i22 += 2;
                  if (i31 == 247)
                  {
                    i22 = readFrameType(arrayOfObject2, 0, i22, arrayOfChar, arrayOfLabel1);
                    i24 = 4;
                    i28 = 1;
                  }
                  else if ((i31 >= 248) && (i31 < 251))
                  {
                    i24 = 2;
                    i27 = 251 - i31;
                    i26 -= i27;
                    i28 = 0;
                  }
                  else if (i31 == 251)
                  {
                    i24 = 3;
                    i28 = 0;
                  }
                  else if (i31 < 255)
                  {
                    j = i7 != 0 ? i26 : 0;
                    for (k = i31 - 251; k > 0; k--) {
                      i22 = readFrameType(arrayOfObject1, j++, i22, arrayOfChar, arrayOfLabel1);
                    }
                    i24 = 1;
                    i27 = i31 - 251;
                    i26 += i27;
                    i28 = 0;
                  }
                  else
                  {
                    i24 = 0;
                    i33 = i27 = i26 = readUnsignedShort(i22);
                    i22 += 2;
                    j = 0;
                    while (i33 > 0)
                    {
                      i22 = readFrameType(arrayOfObject1, j++, i22, arrayOfChar, arrayOfLabel1);
                      i33--;
                    }
                    i33 = i28 = readUnsignedShort(i22);
                    i22 += 2;
                    j = 0;
                    while (i33 > 0)
                    {
                      i22 = readFrameType(arrayOfObject2, j++, i22, arrayOfChar, arrayOfLabel1);
                      i33--;
                    }
                  }
                }
                i25 += i32 + 1;
                readLabel(i25, arrayOfLabel1);
                i23--;
              }
              else
              {
                arrayOfObject1 = null;
              }
            }
            int i31 = arrayOfByte[n] & 0xFF;
            switch (ClassWriter.TYPE[i31])
            {
            case 0: 
              localMethodVisitor.visitInsn(i31);
              n++;
              break;
            case 4: 
              if (i31 > 54)
              {
                i31 -= 59;
                localMethodVisitor.visitVarInsn(54 + (i31 >> 2), i31 & 0x3);
              }
              else
              {
                i31 -= 26;
                localMethodVisitor.visitVarInsn(21 + (i31 >> 2), i31 & 0x3);
              }
              n++;
              break;
            case 8: 
              localMethodVisitor.visitJumpInsn(i31, arrayOfLabel1[(i1 + readShort(n + 1))]);
              n += 3;
              break;
            case 9: 
              localMethodVisitor.visitJumpInsn(i31 - 33, arrayOfLabel1[(i1 + readInt(n + 1))]);
              n += 5;
              break;
            case 16: 
              i31 = arrayOfByte[(n + 1)] & 0xFF;
              if (i31 == 132)
              {
                localMethodVisitor.visitIincInsn(readUnsignedShort(n + 2), readShort(n + 4));
                n += 6;
              }
              else
              {
                localMethodVisitor.visitVarInsn(i31, readUnsignedShort(n + 2));
                n += 4;
              }
              break;
            case 13: 
              n = n + 4 - (i1 & 0x3);
              i18 = i1 + readInt(n);
              i32 = readInt(n + 4);
              i33 = readInt(n + 8);
              n += 12;
              Label[] arrayOfLabel2 = new Label[i33 - i32 + 1];
              for (j = 0; j < arrayOfLabel2.length; j++)
              {
                arrayOfLabel2[j] = arrayOfLabel1[(i1 + readInt(n))];
                n += 4;
              }
              localMethodVisitor.visitTableSwitchInsn(i32, i33, arrayOfLabel1[i18], arrayOfLabel2);
              break;
            case 14: 
              n = n + 4 - (i1 & 0x3);
              i18 = i1 + readInt(n);
              j = readInt(n + 4);
              n += 8;
              localObject3 = new int[j];
              Label[] arrayOfLabel3 = new Label[j];
              for (j = 0; j < localObject3.length; j++)
              {
                localObject3[j] = readInt(n);
                arrayOfLabel3[j] = arrayOfLabel1[(i1 + readInt(n + 4))];
                n += 8;
              }
              localMethodVisitor.visitLookupSwitchInsn(arrayOfLabel1[i18], (int[])localObject3, arrayOfLabel3);
              break;
            case 3: 
              localMethodVisitor.visitVarInsn(i31, arrayOfByte[(n + 1)] & 0xFF);
              n += 2;
              break;
            case 1: 
              localMethodVisitor.visitIntInsn(i31, arrayOfByte[(n + 1)]);
              n += 2;
              break;
            case 2: 
              localMethodVisitor.visitIntInsn(i31, readShort(n + 1));
              n += 3;
              break;
            case 10: 
              localMethodVisitor.visitLdcInsn(readConst(arrayOfByte[(n + 1)] & 0xFF, arrayOfChar));
              n += 2;
              break;
            case 11: 
              localMethodVisitor.visitLdcInsn(readConst(readUnsignedShort(n + 1), arrayOfChar));
              n += 3;
              break;
            case 6: 
            case 7: 
              int i36 = items[readUnsignedShort(n + 1)];
              String str11 = readClass(i36, arrayOfChar);
              i36 = items[readUnsignedShort(i36 + 2)];
              String str12 = readUTF8(i36, arrayOfChar);
              String str13 = readUTF8(i36 + 2, arrayOfChar);
              if (i31 < 182) {
                localMethodVisitor.visitFieldInsn(i31, str11, str12, str13);
              } else {
                localMethodVisitor.visitMethodInsn(i31, str11, str12, str13);
              }
              if (i31 == 185) {
                n += 5;
              } else {
                n += 3;
              }
              break;
            case 5: 
              localMethodVisitor.visitTypeInsn(i31, readClass(n + 1, arrayOfChar));
              n += 3;
              break;
            case 12: 
              localMethodVisitor.visitIincInsn(arrayOfByte[(n + 1)] & 0xFF, arrayOfByte[(n + 2)]);
              n += 3;
              break;
            case 15: 
            default: 
              localMethodVisitor.visitMultiANewArrayInsn(readClass(n + 1, arrayOfChar), arrayOfByte[(n + 3)] & 0xFF);
              n += 4;
            }
          }
          Label localLabel4 = arrayOfLabel1[(i17 - i16)];
          if (localLabel4 != null) {
            localMethodVisitor.visitLabel(localLabel4);
          }
          if ((i6 == 0) && (i20 != 0))
          {
            int[] arrayOfInt = null;
            if (i21 != 0)
            {
              k = readUnsignedShort(i21) * 3;
              i1 = i21 + 2;
              arrayOfInt = new int[k];
              while (k > 0)
              {
                arrayOfInt[(--k)] = (i1 + 6);
                arrayOfInt[(--k)] = readUnsignedShort(i1 + 8);
                arrayOfInt[(--k)] = readUnsignedShort(i1);
                i1 += 10;
              }
            }
            k = readUnsignedShort(i20);
            i1 = i20 + 2;
            while (k > 0)
            {
              i32 = readUnsignedShort(i1);
              i33 = readUnsignedShort(i1 + 2);
              int i34 = readUnsignedShort(i1 + 8);
              localObject3 = null;
              if (arrayOfInt != null) {
                for (int i35 = 0; i35 < arrayOfInt.length; i35 += 3) {
                  if ((arrayOfInt[i35] == i32) && (arrayOfInt[(i35 + 1)] == i34))
                  {
                    localObject3 = readUTF8(arrayOfInt[(i35 + 2)], arrayOfChar);
                    break;
                  }
                }
              }
              localMethodVisitor.visitLocalVariable(readUTF8(i1 + 4, arrayOfChar), readUTF8(i1 + 6, arrayOfChar), (String)localObject3, arrayOfLabel1[i32], arrayOfLabel1[(i32 + i33)], i34);
              i1 += 10;
              k--;
            }
          }
          while (localObject1 != null)
          {
            localAttribute = next;
            next = null;
            localMethodVisitor.visitAttribute((Attribute)localObject1);
            localObject1 = localAttribute;
          }
          localMethodVisitor.visitMaxs(i13, i14);
        }
        if (localMethodVisitor != null) {
          localMethodVisitor.visitEnd();
        }
      }
      label5472:
      i--;
    }
    paramClassVisitor.visitEnd();
  }
  
  private void readParameterAnnotations(int paramInt, String paramString, char[] paramArrayOfChar, boolean paramBoolean, MethodVisitor paramMethodVisitor)
  {
    int j = b[(paramInt++)] & 0xFF;
    int k = Type.getArgumentTypes(paramString).length - j;
    AnnotationVisitor localAnnotationVisitor;
    for (int i = 0; i < k; i++)
    {
      localAnnotationVisitor = paramMethodVisitor.visitParameterAnnotation(i, "Ljava/lang/Synthetic;", false);
      if (localAnnotationVisitor != null) {
        localAnnotationVisitor.visitEnd();
      }
    }
    while (i < j + k)
    {
      int m = readUnsignedShort(paramInt);
      paramInt += 2;
      while (m > 0)
      {
        localAnnotationVisitor = paramMethodVisitor.visitParameterAnnotation(i, readUTF8(paramInt, paramArrayOfChar), paramBoolean);
        paramInt = readAnnotationValues(paramInt + 2, paramArrayOfChar, true, localAnnotationVisitor);
        m--;
      }
      i++;
    }
  }
  
  private int readAnnotationValues(int paramInt, char[] paramArrayOfChar, boolean paramBoolean, AnnotationVisitor paramAnnotationVisitor)
  {
    int i = readUnsignedShort(paramInt);
    paramInt += 2;
    if (paramBoolean) {
      while (i > 0)
      {
        paramInt = readAnnotationValue(paramInt + 2, paramArrayOfChar, readUTF8(paramInt, paramArrayOfChar), paramAnnotationVisitor);
        i--;
      }
    }
    while (i > 0)
    {
      paramInt = readAnnotationValue(paramInt, paramArrayOfChar, null, paramAnnotationVisitor);
      i--;
    }
    if (paramAnnotationVisitor != null) {
      paramAnnotationVisitor.visitEnd();
    }
    return paramInt;
  }
  
  private int readAnnotationValue(int paramInt, char[] paramArrayOfChar, String paramString, AnnotationVisitor paramAnnotationVisitor)
  {
    if (paramAnnotationVisitor == null)
    {
      switch (b[paramInt] & 0xFF)
      {
      case 101: 
        return paramInt + 5;
      case 64: 
        return readAnnotationValues(paramInt + 3, paramArrayOfChar, true, null);
      case 91: 
        return readAnnotationValues(paramInt + 1, paramArrayOfChar, false, null);
      }
      return paramInt + 3;
    }
    switch (b[(paramInt++)] & 0xFF)
    {
    case 68: 
    case 70: 
    case 73: 
    case 74: 
      paramAnnotationVisitor.visit(paramString, readConst(readUnsignedShort(paramInt), paramArrayOfChar));
      paramInt += 2;
      break;
    case 66: 
      paramAnnotationVisitor.visit(paramString, new Byte((byte)readInt(items[readUnsignedShort(paramInt)])));
      paramInt += 2;
      break;
    case 90: 
      paramAnnotationVisitor.visit(paramString, readInt(items[readUnsignedShort(paramInt)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
      paramInt += 2;
      break;
    case 83: 
      paramAnnotationVisitor.visit(paramString, new Short((short)readInt(items[readUnsignedShort(paramInt)])));
      paramInt += 2;
      break;
    case 67: 
      paramAnnotationVisitor.visit(paramString, new Character((char)readInt(items[readUnsignedShort(paramInt)])));
      paramInt += 2;
      break;
    case 115: 
      paramAnnotationVisitor.visit(paramString, readUTF8(paramInt, paramArrayOfChar));
      paramInt += 2;
      break;
    case 101: 
      paramAnnotationVisitor.visitEnum(paramString, readUTF8(paramInt, paramArrayOfChar), readUTF8(paramInt + 2, paramArrayOfChar));
      paramInt += 4;
      break;
    case 99: 
      paramAnnotationVisitor.visit(paramString, Type.getType(readUTF8(paramInt, paramArrayOfChar)));
      paramInt += 2;
      break;
    case 64: 
      paramInt = readAnnotationValues(paramInt + 2, paramArrayOfChar, true, paramAnnotationVisitor.visitAnnotation(paramString, readUTF8(paramInt, paramArrayOfChar)));
      break;
    case 91: 
      int j = readUnsignedShort(paramInt);
      paramInt += 2;
      if (j == 0) {
        return readAnnotationValues(paramInt - 2, paramArrayOfChar, false, paramAnnotationVisitor.visitArray(paramString));
      }
      int i;
      switch (b[(paramInt++)] & 0xFF)
      {
      case 66: 
        byte[] arrayOfByte = new byte[j];
        for (i = 0; i < j; i++)
        {
          arrayOfByte[i] = ((byte)readInt(items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfByte);
        paramInt--;
        break;
      case 90: 
        boolean[] arrayOfBoolean = new boolean[j];
        for (i = 0; i < j; i++)
        {
          arrayOfBoolean[i] = (readInt(items[readUnsignedShort(paramInt)]) != 0 ? 1 : false);
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfBoolean);
        paramInt--;
        break;
      case 83: 
        short[] arrayOfShort = new short[j];
        for (i = 0; i < j; i++)
        {
          arrayOfShort[i] = ((short)readInt(items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfShort);
        paramInt--;
        break;
      case 67: 
        char[] arrayOfChar = new char[j];
        for (i = 0; i < j; i++)
        {
          arrayOfChar[i] = ((char)readInt(items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfChar);
        paramInt--;
        break;
      case 73: 
        int[] arrayOfInt = new int[j];
        for (i = 0; i < j; i++)
        {
          arrayOfInt[i] = readInt(items[readUnsignedShort(paramInt)]);
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfInt);
        paramInt--;
        break;
      case 74: 
        long[] arrayOfLong = new long[j];
        for (i = 0; i < j; i++)
        {
          arrayOfLong[i] = readLong(items[readUnsignedShort(paramInt)]);
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfLong);
        paramInt--;
        break;
      case 70: 
        float[] arrayOfFloat = new float[j];
        for (i = 0; i < j; i++)
        {
          arrayOfFloat[i] = Float.intBitsToFloat(readInt(items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfFloat);
        paramInt--;
        break;
      case 68: 
        double[] arrayOfDouble = new double[j];
        for (i = 0; i < j; i++)
        {
          arrayOfDouble[i] = Double.longBitsToDouble(readLong(items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfDouble);
        paramInt--;
        break;
      case 69: 
      case 71: 
      case 72: 
      case 75: 
      case 76: 
      case 77: 
      case 78: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      default: 
        paramInt = readAnnotationValues(paramInt - 3, paramArrayOfChar, false, paramAnnotationVisitor.visitArray(paramString));
      }
      break;
    }
    return paramInt;
  }
  
  private int readFrameType(Object[] paramArrayOfObject, int paramInt1, int paramInt2, char[] paramArrayOfChar, Label[] paramArrayOfLabel)
  {
    int i = b[(paramInt2++)] & 0xFF;
    switch (i)
    {
    case 0: 
      paramArrayOfObject[paramInt1] = Opcodes.TOP;
      break;
    case 1: 
      paramArrayOfObject[paramInt1] = Opcodes.INTEGER;
      break;
    case 2: 
      paramArrayOfObject[paramInt1] = Opcodes.FLOAT;
      break;
    case 3: 
      paramArrayOfObject[paramInt1] = Opcodes.DOUBLE;
      break;
    case 4: 
      paramArrayOfObject[paramInt1] = Opcodes.LONG;
      break;
    case 5: 
      paramArrayOfObject[paramInt1] = Opcodes.NULL;
      break;
    case 6: 
      paramArrayOfObject[paramInt1] = Opcodes.UNINITIALIZED_THIS;
      break;
    case 7: 
      paramArrayOfObject[paramInt1] = readClass(paramInt2, paramArrayOfChar);
      paramInt2 += 2;
      break;
    default: 
      paramArrayOfObject[paramInt1] = readLabel(readUnsignedShort(paramInt2), paramArrayOfLabel);
      paramInt2 += 2;
    }
    return paramInt2;
  }
  
  protected Label readLabel(int paramInt, Label[] paramArrayOfLabel)
  {
    if (paramArrayOfLabel[paramInt] == null) {
      paramArrayOfLabel[paramInt] = new Label();
    }
    return paramArrayOfLabel[paramInt];
  }
  
  private Attribute readAttribute(Attribute[] paramArrayOfAttribute, String paramString, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    for (int i = 0; i < paramArrayOfAttribute.length; i++) {
      if (type.equals(paramString)) {
        return paramArrayOfAttribute[i].read(this, paramInt1, paramInt2, paramArrayOfChar, paramInt3, paramArrayOfLabel);
      }
    }
    return new Attribute(paramString).read(this, paramInt1, paramInt2, null, -1, null);
  }
  
  public int getItem(int paramInt)
  {
    return items[paramInt];
  }
  
  public int readByte(int paramInt)
  {
    return b[paramInt] & 0xFF;
  }
  
  public int readUnsignedShort(int paramInt)
  {
    byte[] arrayOfByte = b;
    return (arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[(paramInt + 1)] & 0xFF;
  }
  
  public short readShort(int paramInt)
  {
    byte[] arrayOfByte = b;
    return (short)((arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[(paramInt + 1)] & 0xFF);
  }
  
  public int readInt(int paramInt)
  {
    byte[] arrayOfByte = b;
    return (arrayOfByte[paramInt] & 0xFF) << 24 | (arrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (arrayOfByte[(paramInt + 2)] & 0xFF) << 8 | arrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  public long readLong(int paramInt)
  {
    long l1 = readInt(paramInt);
    long l2 = readInt(paramInt + 4) & 0xFFFFFFFF;
    return l1 << 32 | l2;
  }
  
  public String readUTF8(int paramInt, char[] paramArrayOfChar)
  {
    int i = readUnsignedShort(paramInt);
    String str = strings[i];
    if (str != null) {
      return str;
    }
    paramInt = items[i];
    return strings[i] = readUTF(paramInt + 2, readUnsignedShort(paramInt), paramArrayOfChar);
  }
  
  private String readUTF(int paramInt1, int paramInt2, char[] paramArrayOfChar)
  {
    int i = paramInt1 + paramInt2;
    byte[] arrayOfByte = b;
    int j = 0;
    while (paramInt1 < i)
    {
      int k = arrayOfByte[(paramInt1++)] & 0xFF;
      int m;
      switch (k >> 4)
      {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
        paramArrayOfChar[(j++)] = ((char)k);
        break;
      case 12: 
      case 13: 
        m = arrayOfByte[(paramInt1++)];
        paramArrayOfChar[(j++)] = ((char)((k & 0x1F) << 6 | m & 0x3F));
        break;
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      default: 
        m = arrayOfByte[(paramInt1++)];
        int n = arrayOfByte[(paramInt1++)];
        paramArrayOfChar[(j++)] = ((char)((k & 0xF) << 12 | (m & 0x3F) << 6 | n & 0x3F));
      }
    }
    return new String(paramArrayOfChar, 0, j);
  }
  
  public String readClass(int paramInt, char[] paramArrayOfChar)
  {
    return readUTF8(items[readUnsignedShort(paramInt)], paramArrayOfChar);
  }
  
  public Object readConst(int paramInt, char[] paramArrayOfChar)
  {
    int i = items[paramInt];
    switch (b[(i - 1)])
    {
    case 3: 
      return new Integer(readInt(i));
    case 4: 
      return new Float(Float.intBitsToFloat(readInt(i)));
    case 5: 
      return new Long(readLong(i));
    case 6: 
      return new Double(Double.longBitsToDouble(readLong(i)));
    case 7: 
      return Type.getObjectType(readUTF8(i, paramArrayOfChar));
    }
    return readUTF8(i, paramArrayOfChar);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\ClassReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */