package sun.security.x509;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KeyIdentifier
{
  private byte[] octetString;
  
  public KeyIdentifier(byte[] paramArrayOfByte)
  {
    octetString = ((byte[])paramArrayOfByte.clone());
  }
  
  public KeyIdentifier(DerValue paramDerValue)
    throws IOException
  {
    octetString = paramDerValue.getOctetString();
  }
  
  public KeyIdentifier(PublicKey paramPublicKey)
    throws IOException
  {
    DerValue localDerValue = new DerValue(paramPublicKey.getEncoded());
    if (tag != 48) {
      throw new IOException("PublicKey value is not a valid X.509 public key");
    }
    AlgorithmId localAlgorithmId = AlgorithmId.parse(data.getDerValue());
    byte[] arrayOfByte = data.getUnalignedBitString().toByteArray();
    MessageDigest localMessageDigest = null;
    try
    {
      localMessageDigest = MessageDigest.getInstance("SHA1");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new IOException("SHA1 not supported");
    }
    localMessageDigest.update(arrayOfByte);
    octetString = localMessageDigest.digest();
  }
  
  public byte[] getIdentifier()
  {
    return (byte[])octetString.clone();
  }
  
  public String toString()
  {
    String str = "KeyIdentifier [\n";
    HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
    str = str + localHexDumpEncoder.encodeBuffer(octetString);
    str = str + "]\n";
    return str;
  }
  
  void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    paramDerOutputStream.putOctetString(octetString);
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < octetString.length; j++) {
      i += octetString[j] * j;
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof KeyIdentifier)) {
      return false;
    }
    byte[] arrayOfByte = octetString;
    return Arrays.equals(octetString, arrayOfByte);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\KeyIdentifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */