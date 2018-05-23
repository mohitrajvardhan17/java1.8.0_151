package java.security.spec;

public abstract class EncodedKeySpec
  implements KeySpec
{
  private byte[] encodedKey;
  
  public EncodedKeySpec(byte[] paramArrayOfByte)
  {
    encodedKey = ((byte[])paramArrayOfByte.clone());
  }
  
  public byte[] getEncoded()
  {
    return (byte[])encodedKey.clone();
  }
  
  public abstract String getFormat();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\EncodedKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */