package com.sun.org.apache.bcel.internal.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public final class Signature
  extends Attribute
{
  private int signature_index;
  
  public Signature(Signature paramSignature)
  {
    this(paramSignature.getNameIndex(), paramSignature.getLength(), paramSignature.getSignatureIndex(), paramSignature.getConstantPool());
  }
  
  Signature(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, paramDataInputStream.readUnsignedShort(), paramConstantPool);
  }
  
  public Signature(int paramInt1, int paramInt2, int paramInt3, ConstantPool paramConstantPool)
  {
    super((byte)10, paramInt1, paramInt2, paramConstantPool);
    signature_index = paramInt3;
  }
  
  public void accept(Visitor paramVisitor)
  {
    System.err.println("Visiting non-standard Signature object");
    paramVisitor.visitSignature(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(signature_index);
  }
  
  public final int getSignatureIndex()
  {
    return signature_index;
  }
  
  public final void setSignatureIndex(int paramInt)
  {
    signature_index = paramInt;
  }
  
  public final String getSignature()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constant_pool.getConstant(signature_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  private static boolean identStart(int paramInt)
  {
    return (paramInt == 84) || (paramInt == 76);
  }
  
  private static boolean identPart(int paramInt)
  {
    return (paramInt == 47) || (paramInt == 59);
  }
  
  private static final void matchIdent(MyByteArrayInputStream paramMyByteArrayInputStream, StringBuffer paramStringBuffer)
  {
    if ((i = paramMyByteArrayInputStream.read()) == -1) {
      throw new RuntimeException("Illegal signature: " + paramMyByteArrayInputStream.getData() + " no ident, reaching EOF");
    }
    if (!identStart(i))
    {
      localStringBuffer = new StringBuffer();
      int j = 1;
      while (Character.isJavaIdentifierPart((char)i))
      {
        localStringBuffer.append((char)i);
        j++;
        i = paramMyByteArrayInputStream.read();
      }
      if (i == 58)
      {
        paramMyByteArrayInputStream.skip("Ljava/lang/Object".length());
        paramStringBuffer.append(localStringBuffer);
        i = paramMyByteArrayInputStream.read();
        paramMyByteArrayInputStream.unread();
      }
      else
      {
        for (int k = 0; k < j; k++) {
          paramMyByteArrayInputStream.unread();
        }
      }
      return;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramMyByteArrayInputStream.read();
    do
    {
      localStringBuffer.append((char)i);
      i = paramMyByteArrayInputStream.read();
    } while ((i != -1) && ((Character.isJavaIdentifierPart((char)i)) || (i == 47)));
    paramStringBuffer.append(localStringBuffer.toString().replace('/', '.'));
    if (i != -1) {
      paramMyByteArrayInputStream.unread();
    }
  }
  
  private static final void matchGJIdent(MyByteArrayInputStream paramMyByteArrayInputStream, StringBuffer paramStringBuffer)
  {
    matchIdent(paramMyByteArrayInputStream, paramStringBuffer);
    int i = paramMyByteArrayInputStream.read();
    if ((i == 60) || (i == 40))
    {
      paramStringBuffer.append((char)i);
      matchGJIdent(paramMyByteArrayInputStream, paramStringBuffer);
      while (((i = paramMyByteArrayInputStream.read()) != 62) && (i != 41))
      {
        if (i == -1) {
          throw new RuntimeException("Illegal signature: " + paramMyByteArrayInputStream.getData() + " reaching EOF");
        }
        paramStringBuffer.append(", ");
        paramMyByteArrayInputStream.unread();
        matchGJIdent(paramMyByteArrayInputStream, paramStringBuffer);
      }
      paramStringBuffer.append((char)i);
    }
    else
    {
      paramMyByteArrayInputStream.unread();
    }
    i = paramMyByteArrayInputStream.read();
    if (identStart(i))
    {
      paramMyByteArrayInputStream.unread();
      matchGJIdent(paramMyByteArrayInputStream, paramStringBuffer);
    }
    else
    {
      if (i == 41)
      {
        paramMyByteArrayInputStream.unread();
        return;
      }
      if (i != 59) {
        throw new RuntimeException("Illegal signature: " + paramMyByteArrayInputStream.getData() + " read " + (char)i);
      }
    }
  }
  
  public static String translate(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    matchGJIdent(new MyByteArrayInputStream(paramString), localStringBuffer);
    return localStringBuffer.toString();
  }
  
  public static final boolean isFormalParameterList(String paramString)
  {
    return (paramString.startsWith("<")) && (paramString.indexOf(':') > 0);
  }
  
  public static final boolean isActualParameterList(String paramString)
  {
    return (paramString.startsWith("L")) && (paramString.endsWith(">;"));
  }
  
  public final String toString()
  {
    String str = getSignature();
    return "Signature(" + str + ")";
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    return (Signature)clone();
  }
  
  private static final class MyByteArrayInputStream
    extends ByteArrayInputStream
  {
    MyByteArrayInputStream(String paramString)
    {
      super();
    }
    
    final int mark()
    {
      return pos;
    }
    
    final String getData()
    {
      return new String(buf);
    }
    
    final void reset(int paramInt)
    {
      pos = paramInt;
    }
    
    final void unread()
    {
      if (pos > 0) {
        pos -= 1;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Signature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */