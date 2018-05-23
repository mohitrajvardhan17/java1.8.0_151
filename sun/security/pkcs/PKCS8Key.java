package sun.security.pkcs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyRep;
import java.security.KeyRep.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public class PKCS8Key
  implements PrivateKey
{
  private static final long serialVersionUID = -3836890099307167124L;
  protected AlgorithmId algid;
  protected byte[] key;
  protected byte[] encodedKey;
  public static final BigInteger version = BigInteger.ZERO;
  
  public PKCS8Key() {}
  
  private PKCS8Key(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
    throws InvalidKeyException
  {
    algid = paramAlgorithmId;
    key = paramArrayOfByte;
    encode();
  }
  
  public static PKCS8Key parse(DerValue paramDerValue)
    throws IOException
  {
    PrivateKey localPrivateKey = parseKey(paramDerValue);
    if ((localPrivateKey instanceof PKCS8Key)) {
      return (PKCS8Key)localPrivateKey;
    }
    throw new IOException("Provider did not return PKCS8Key");
  }
  
  public static PrivateKey parseKey(DerValue paramDerValue)
    throws IOException
  {
    if (tag != 48) {
      throw new IOException("corrupt private key");
    }
    BigInteger localBigInteger = data.getBigInteger();
    if (!version.equals(localBigInteger)) {
      throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(localBigInteger));
    }
    AlgorithmId localAlgorithmId = AlgorithmId.parse(data.getDerValue());
    PrivateKey localPrivateKey;
    try
    {
      localPrivateKey = buildPKCS8Key(localAlgorithmId, data.getOctetString());
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new IOException("corrupt private key");
    }
    if (data.available() != 0) {
      throw new IOException("excess private key");
    }
    return localPrivateKey;
  }
  
  protected void parseKeyBits()
    throws IOException, InvalidKeyException
  {
    encode();
  }
  
  static PrivateKey buildPKCS8Key(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
    throws IOException, InvalidKeyException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    encode(localDerOutputStream, paramAlgorithmId, paramArrayOfByte);
    PKCS8EncodedKeySpec localPKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(localDerOutputStream.toByteArray());
    try
    {
      KeyFactory localKeyFactory = KeyFactory.getInstance(paramAlgorithmId.getName());
      return localKeyFactory.generatePrivate(localPKCS8EncodedKeySpec);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}catch (InvalidKeySpecException localInvalidKeySpecException) {}
    String str = "";
    try
    {
      Provider localProvider = Security.getProvider("SUN");
      if (localProvider == null) {
        throw new InstantiationException();
      }
      str = localProvider.getProperty("PrivateKey.PKCS#8." + paramAlgorithmId.getName());
      if (str == null) {
        throw new InstantiationException();
      }
      Class localClass = null;
      Object localObject2;
      try
      {
        localClass = Class.forName(str);
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        localObject2 = ClassLoader.getSystemClassLoader();
        if (localObject2 != null) {
          localClass = ((ClassLoader)localObject2).loadClass(str);
        }
      }
      Object localObject1 = null;
      if (localClass != null) {
        localObject1 = localClass.newInstance();
      }
      if ((localObject1 instanceof PKCS8Key))
      {
        localObject2 = (PKCS8Key)localObject1;
        algid = paramAlgorithmId;
        key = paramArrayOfByte;
        ((PKCS8Key)localObject2).parseKeyBits();
        return (PrivateKey)localObject2;
      }
    }
    catch (ClassNotFoundException localClassNotFoundException1) {}catch (InstantiationException localInstantiationException) {}catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IOException(str + " [internal error]");
    }
    PKCS8Key localPKCS8Key = new PKCS8Key();
    algid = paramAlgorithmId;
    key = paramArrayOfByte;
    return localPKCS8Key;
  }
  
  public String getAlgorithm()
  {
    return algid.getName();
  }
  
  public AlgorithmId getAlgorithmId()
  {
    return algid;
  }
  
  public final void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    encode(paramDerOutputStream, algid, key);
  }
  
  public synchronized byte[] getEncoded()
  {
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = encode();
    }
    catch (InvalidKeyException localInvalidKeyException) {}
    return arrayOfByte;
  }
  
  public String getFormat()
  {
    return "PKCS#8";
  }
  
  public byte[] encode()
    throws InvalidKeyException
  {
    if (encodedKey == null) {
      try
      {
        DerOutputStream localDerOutputStream = new DerOutputStream();
        encode(localDerOutputStream);
        encodedKey = localDerOutputStream.toByteArray();
      }
      catch (IOException localIOException)
      {
        throw new InvalidKeyException("IOException : " + localIOException.getMessage());
      }
    }
    return (byte[])encodedKey.clone();
  }
  
  public void decode(InputStream paramInputStream)
    throws InvalidKeyException
  {
    try
    {
      DerValue localDerValue = new DerValue(paramInputStream);
      if (tag != 48) {
        throw new InvalidKeyException("invalid key format");
      }
      BigInteger localBigInteger = data.getBigInteger();
      if (!localBigInteger.equals(version)) {
        throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(localBigInteger));
      }
      algid = AlgorithmId.parse(data.getDerValue());
      key = data.getOctetString();
      parseKeyBits();
      if (data.available() != 0) {}
      return;
    }
    catch (IOException localIOException)
    {
      throw new InvalidKeyException("IOException : " + localIOException.getMessage());
    }
  }
  
  public void decode(byte[] paramArrayOfByte)
    throws InvalidKeyException
  {
    decode(new ByteArrayInputStream(paramArrayOfByte));
  }
  
  protected Object writeReplace()
    throws ObjectStreamException
  {
    return new KeyRep(KeyRep.Type.PRIVATE, getAlgorithm(), getFormat(), getEncoded());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException
  {
    try
    {
      decode(paramObjectInputStream);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      localInvalidKeyException.printStackTrace();
      throw new IOException("deserialized key is invalid: " + localInvalidKeyException.getMessage());
    }
  }
  
  static void encode(DerOutputStream paramDerOutputStream, AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putInteger(version);
    paramAlgorithmId.encode(localDerOutputStream);
    localDerOutputStream.putOctetString(paramArrayOfByte);
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Key))
    {
      byte[] arrayOfByte1;
      if (encodedKey != null) {
        arrayOfByte1 = encodedKey;
      } else {
        arrayOfByte1 = getEncoded();
      }
      byte[] arrayOfByte2 = ((Key)paramObject).getEncoded();
      return MessageDigest.isEqual(arrayOfByte1, arrayOfByte2);
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = 0;
    byte[] arrayOfByte = getEncoded();
    for (int j = 1; j < arrayOfByte.length; j++) {
      i += arrayOfByte[j] * j;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs\PKCS8Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */