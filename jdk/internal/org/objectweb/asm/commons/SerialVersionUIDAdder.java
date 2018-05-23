package jdk.internal.org.objectweb.asm.commons;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class SerialVersionUIDAdder
  extends ClassVisitor
{
  private boolean computeSVUID;
  private boolean hasSVUID;
  private int access;
  private String name;
  private String[] interfaces;
  private Collection<Item> svuidFields = new ArrayList();
  private boolean hasStaticInitializer;
  private Collection<Item> svuidConstructors = new ArrayList();
  private Collection<Item> svuidMethods = new ArrayList();
  
  public SerialVersionUIDAdder(ClassVisitor paramClassVisitor)
  {
    this(327680, paramClassVisitor);
    if (getClass() != SerialVersionUIDAdder.class) {
      throw new IllegalStateException();
    }
  }
  
  protected SerialVersionUIDAdder(int paramInt, ClassVisitor paramClassVisitor)
  {
    super(paramInt, paramClassVisitor);
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    computeSVUID = ((paramInt2 & 0x200) == 0);
    if (computeSVUID)
    {
      name = paramString1;
      access = paramInt2;
      interfaces = new String[paramArrayOfString.length];
      System.arraycopy(paramArrayOfString, 0, interfaces, 0, paramArrayOfString.length);
    }
    super.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    if (computeSVUID)
    {
      if ("<clinit>".equals(paramString1)) {
        hasStaticInitializer = true;
      }
      int i = paramInt & 0xD3F;
      if ((paramInt & 0x2) == 0) {
        if ("<init>".equals(paramString1)) {
          svuidConstructors.add(new Item(paramString1, i, paramString2));
        } else if (!"<clinit>".equals(paramString1)) {
          svuidMethods.add(new Item(paramString1, i, paramString2));
        }
      }
    }
    return super.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    if (computeSVUID)
    {
      if ("serialVersionUID".equals(paramString1))
      {
        computeSVUID = false;
        hasSVUID = true;
      }
      if (((paramInt & 0x2) == 0) || ((paramInt & 0x88) == 0))
      {
        int i = paramInt & 0xDF;
        svuidFields.add(new Item(paramString1, i, paramString2));
      }
    }
    return super.visitField(paramInt, paramString1, paramString2, paramString3, paramObject);
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if ((name != null) && (name.equals(paramString1))) {
      access = paramInt;
    }
    super.visitInnerClass(paramString1, paramString2, paramString3, paramInt);
  }
  
  public void visitEnd()
  {
    if ((computeSVUID) && (!hasSVUID)) {
      try
      {
        addSVUID(computeSVUID());
      }
      catch (Throwable localThrowable)
      {
        throw new RuntimeException("Error while computing SVUID for " + name, localThrowable);
      }
    }
    super.visitEnd();
  }
  
  public boolean hasSVUID()
  {
    return hasSVUID;
  }
  
  protected void addSVUID(long paramLong)
  {
    FieldVisitor localFieldVisitor = super.visitField(24, "serialVersionUID", "J", null, Long.valueOf(paramLong));
    if (localFieldVisitor != null) {
      localFieldVisitor.visitEnd();
    }
  }
  
  protected long computeSVUID()
    throws IOException
  {
    DataOutputStream localDataOutputStream = null;
    long l = 0L;
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
      localDataOutputStream.writeUTF(name.replace('/', '.'));
      localDataOutputStream.writeInt(access & 0x611);
      Arrays.sort(interfaces);
      for (int i = 0; i < interfaces.length; i++) {
        localDataOutputStream.writeUTF(interfaces[i].replace('/', '.'));
      }
      writeItems(svuidFields, localDataOutputStream, false);
      if (hasStaticInitializer)
      {
        localDataOutputStream.writeUTF("<clinit>");
        localDataOutputStream.writeInt(8);
        localDataOutputStream.writeUTF("()V");
      }
      writeItems(svuidConstructors, localDataOutputStream, true);
      writeItems(svuidMethods, localDataOutputStream, true);
      localDataOutputStream.flush();
      byte[] arrayOfByte = computeSHAdigest(localByteArrayOutputStream.toByteArray());
      for (int j = Math.min(arrayOfByte.length, 8) - 1; j >= 0; j--) {
        l = l << 8 | arrayOfByte[j] & 0xFF;
      }
    }
    finally
    {
      if (localDataOutputStream != null) {
        localDataOutputStream.close();
      }
    }
    return l;
  }
  
  protected byte[] computeSHAdigest(byte[] paramArrayOfByte)
  {
    try
    {
      return MessageDigest.getInstance("SHA").digest(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new UnsupportedOperationException(localException.toString());
    }
  }
  
  private static void writeItems(Collection<Item> paramCollection, DataOutput paramDataOutput, boolean paramBoolean)
    throws IOException
  {
    int i = paramCollection.size();
    Item[] arrayOfItem = (Item[])paramCollection.toArray(new Item[i]);
    Arrays.sort(arrayOfItem);
    for (int j = 0; j < i; j++)
    {
      paramDataOutput.writeUTF(name);
      paramDataOutput.writeInt(access);
      paramDataOutput.writeUTF(paramBoolean ? desc.replace('/', '.') : desc);
    }
  }
  
  private static class Item
    implements Comparable<Item>
  {
    final String name;
    final int access;
    final String desc;
    
    Item(String paramString1, int paramInt, String paramString2)
    {
      name = paramString1;
      access = paramInt;
      desc = paramString2;
    }
    
    public int compareTo(Item paramItem)
    {
      int i = name.compareTo(name);
      if (i == 0) {
        i = desc.compareTo(desc);
      }
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Item)) {
        return compareTo((Item)paramObject) == 0;
      }
      return false;
    }
    
    public int hashCode()
    {
      return (name + desc).hashCode();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\SerialVersionUIDAdder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */