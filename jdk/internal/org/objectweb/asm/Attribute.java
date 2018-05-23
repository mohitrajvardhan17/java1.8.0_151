package jdk.internal.org.objectweb.asm;

public class Attribute
{
  public final String type;
  byte[] value;
  Attribute next;
  
  protected Attribute(String paramString)
  {
    type = paramString;
  }
  
  public boolean isUnknown()
  {
    return true;
  }
  
  public boolean isCodeAttribute()
  {
    return false;
  }
  
  protected Label[] getLabels()
  {
    return null;
  }
  
  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    Attribute localAttribute = new Attribute(type);
    value = new byte[paramInt2];
    System.arraycopy(b, paramInt1, value, 0, paramInt2);
    return localAttribute;
  }
  
  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    ByteVector localByteVector = new ByteVector();
    data = value;
    length = value.length;
    return localByteVector;
  }
  
  final int getCount()
  {
    int i = 0;
    for (Attribute localAttribute = this; localAttribute != null; localAttribute = next) {
      i++;
    }
    return i;
  }
  
  final int getSize(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    Attribute localAttribute = this;
    int i = 0;
    while (localAttribute != null)
    {
      paramClassWriter.newUTF8(type);
      i += writelength + 6;
      localAttribute = next;
    }
    return i;
  }
  
  final void put(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, ByteVector paramByteVector)
  {
    for (Attribute localAttribute = this; localAttribute != null; localAttribute = next)
    {
      ByteVector localByteVector = localAttribute.write(paramClassWriter, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
      paramByteVector.putShort(paramClassWriter.newUTF8(type)).putInt(length);
      paramByteVector.putByteArray(data, 0, length);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */