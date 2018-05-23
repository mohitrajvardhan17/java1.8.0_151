package sun.security.provider;

import java.io.IOException;

class NativeSeedGenerator
  extends SeedGenerator
{
  NativeSeedGenerator(String paramString)
    throws IOException
  {
    if (!nativeGenerateSeed(new byte[2])) {
      throw new IOException("Required native CryptoAPI features not  available on this machine");
    }
  }
  
  private static native boolean nativeGenerateSeed(byte[] paramArrayOfByte);
  
  void getSeedBytes(byte[] paramArrayOfByte)
  {
    if (!nativeGenerateSeed(paramArrayOfByte)) {
      throw new InternalError("Unexpected CryptoAPI failure generating seed");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\NativeSeedGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */