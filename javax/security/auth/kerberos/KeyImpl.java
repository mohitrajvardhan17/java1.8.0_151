package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.util.DerValue;

class KeyImpl
  implements SecretKey, Destroyable, Serializable
{
  private static final long serialVersionUID = -7889313790214321193L;
  private transient byte[] keyBytes;
  private transient int keyType;
  private volatile transient boolean destroyed = false;
  
  public KeyImpl(byte[] paramArrayOfByte, int paramInt)
  {
    keyBytes = ((byte[])paramArrayOfByte.clone());
    keyType = paramInt;
  }
  
  public KeyImpl(KerberosPrincipal paramKerberosPrincipal, char[] paramArrayOfChar, String paramString)
  {
    try
    {
      PrincipalName localPrincipalName = new PrincipalName(paramKerberosPrincipal.getName());
      EncryptionKey localEncryptionKey = new EncryptionKey(paramArrayOfChar, localPrincipalName.getSalt(), paramString);
      keyBytes = localEncryptionKey.getBytes();
      keyType = localEncryptionKey.getEType();
    }
    catch (KrbException localKrbException)
    {
      throw new IllegalArgumentException(localKrbException.getMessage());
    }
  }
  
  public final int getKeyType()
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    return keyType;
  }
  
  public final String getAlgorithm()
  {
    return getAlgorithmName(keyType);
  }
  
  private String getAlgorithmName(int paramInt)
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    switch (paramInt)
    {
    case 1: 
    case 3: 
      return "DES";
    case 16: 
      return "DESede";
    case 23: 
      return "ArcFourHmac";
    case 17: 
      return "AES128";
    case 18: 
      return "AES256";
    case 0: 
      return "NULL";
    }
    throw new IllegalArgumentException("Unsupported encryption type: " + paramInt);
  }
  
  public final String getFormat()
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    return "RAW";
  }
  
  public final byte[] getEncoded()
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    return (byte[])keyBytes.clone();
  }
  
  public void destroy()
    throws DestroyFailedException
  {
    if (!destroyed)
    {
      destroyed = true;
      Arrays.fill(keyBytes, (byte)0);
    }
  }
  
  public boolean isDestroyed()
  {
    return destroyed;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (destroyed) {
      throw new IOException("This key is no longer valid");
    }
    try
    {
      paramObjectOutputStream.writeObject(new EncryptionKey(keyType, keyBytes).asn1Encode());
    }
    catch (Asn1Exception localAsn1Exception)
    {
      throw new IOException(localAsn1Exception.getMessage());
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    try
    {
      EncryptionKey localEncryptionKey = new EncryptionKey(new DerValue((byte[])paramObjectInputStream.readObject()));
      keyType = localEncryptionKey.getEType();
      keyBytes = localEncryptionKey.getBytes();
    }
    catch (Asn1Exception localAsn1Exception)
    {
      throw new IOException(localAsn1Exception.getMessage());
    }
  }
  
  public String toString()
  {
    HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
    return "EncryptionKey: keyType=" + keyType + " keyBytes (hex dump)=" + ((keyBytes == null) || (keyBytes.length == 0) ? " Empty Key" : new StringBuilder().append('\n').append(localHexDumpEncoder.encodeBuffer(keyBytes)).append('\n').toString());
  }
  
  public int hashCode()
  {
    int i = 17;
    if (isDestroyed()) {
      return i;
    }
    i = 37 * i + Arrays.hashCode(keyBytes);
    return 37 * i + keyType;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof KeyImpl)) {
      return false;
    }
    KeyImpl localKeyImpl = (KeyImpl)paramObject;
    if ((isDestroyed()) || (localKeyImpl.isDestroyed())) {
      return false;
    }
    return (keyType == localKeyImpl.getKeyType()) && (Arrays.equals(keyBytes, localKeyImpl.getEncoded()));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\kerberos\KeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */