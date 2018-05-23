package sun.security.util;

import java.util.Comparator;

public class ByteArrayLexOrder
  implements Comparator<byte[]>
{
  public ByteArrayLexOrder() {}
  
  public final int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    for (int j = 0; (j < paramArrayOfByte1.length) && (j < paramArrayOfByte2.length); j++)
    {
      int i = (paramArrayOfByte1[j] & 0xFF) - (paramArrayOfByte2[j] & 0xFF);
      if (i != 0) {
        return i;
      }
    }
    return paramArrayOfByte1.length - paramArrayOfByte2.length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\ByteArrayLexOrder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */