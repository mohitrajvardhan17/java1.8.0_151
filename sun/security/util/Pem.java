package sun.security.util;

import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;

public class Pem
{
  public Pem() {}
  
  public static byte[] decode(String paramString)
    throws IOException
  {
    byte[] arrayOfByte = paramString.replaceAll("\\s+", "").getBytes();
    try
    {
      return Base64.getDecoder().decode(arrayOfByte);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IOException(localIllegalArgumentException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\Pem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */