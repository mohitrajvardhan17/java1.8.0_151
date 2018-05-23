package jdk.internal.org.objectweb.asm;

public class TypePath
{
  public static final int ARRAY_ELEMENT = 0;
  public static final int INNER_TYPE = 1;
  public static final int WILDCARD_BOUND = 2;
  public static final int TYPE_ARGUMENT = 3;
  byte[] b;
  int offset;
  
  TypePath(byte[] paramArrayOfByte, int paramInt)
  {
    b = paramArrayOfByte;
    offset = paramInt;
  }
  
  public int getLength()
  {
    return b[offset];
  }
  
  public int getStep(int paramInt)
  {
    return b[(offset + 2 * paramInt + 1)];
  }
  
  public int getStepArgument(int paramInt)
  {
    return b[(offset + 2 * paramInt + 2)];
  }
  
  public static TypePath fromString(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return null;
    }
    int i = paramString.length();
    ByteVector localByteVector = new ByteVector(i);
    localByteVector.putByte(0);
    int j = 0;
    while (j < i)
    {
      int k = paramString.charAt(j++);
      if (k == 91)
      {
        localByteVector.put11(0, 0);
      }
      else if (k == 46)
      {
        localByteVector.put11(1, 0);
      }
      else if (k == 42)
      {
        localByteVector.put11(2, 0);
      }
      else if ((k >= 48) && (k <= 57))
      {
        int m = k - 48;
        while ((j < i) && ((k = paramString.charAt(j)) >= '0') && (k <= 57))
        {
          m = m * 10 + k - 48;
          j++;
        }
        if ((j < i) && (paramString.charAt(j) == ';')) {
          j++;
        }
        localByteVector.put11(3, m);
      }
    }
    data[0] = ((byte)(length / 2));
    return new TypePath(data, 0);
  }
  
  public String toString()
  {
    int i = getLength();
    StringBuilder localStringBuilder = new StringBuilder(i * 2);
    for (int j = 0; j < i; j++) {
      switch (getStep(j))
      {
      case 0: 
        localStringBuilder.append('[');
        break;
      case 1: 
        localStringBuilder.append('.');
        break;
      case 2: 
        localStringBuilder.append('*');
        break;
      case 3: 
        localStringBuilder.append(getStepArgument(j)).append(';');
        break;
      default: 
        localStringBuilder.append('_');
      }
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\TypePath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */