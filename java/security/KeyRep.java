package java.security;

import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;
import javax.crypto.spec.SecretKeySpec;

public class KeyRep
  implements Serializable
{
  private static final long serialVersionUID = -4757683898830641853L;
  private static final String PKCS8 = "PKCS#8";
  private static final String X509 = "X.509";
  private static final String RAW = "RAW";
  private Type type;
  private String algorithm;
  private String format;
  private byte[] encoded;
  
  public KeyRep(Type paramType, String paramString1, String paramString2, byte[] paramArrayOfByte)
  {
    if ((paramType == null) || (paramString1 == null) || (paramString2 == null) || (paramArrayOfByte == null)) {
      throw new NullPointerException("invalid null input(s)");
    }
    type = paramType;
    algorithm = paramString1;
    format = paramString2.toUpperCase(Locale.ENGLISH);
    encoded = ((byte[])paramArrayOfByte.clone());
  }
  
  protected Object readResolve()
    throws ObjectStreamException
  {
    try
    {
      if ((type == Type.SECRET) && ("RAW".equals(format))) {
        return new SecretKeySpec(encoded, algorithm);
      }
      KeyFactory localKeyFactory;
      if ((type == Type.PUBLIC) && ("X.509".equals(format)))
      {
        localKeyFactory = KeyFactory.getInstance(algorithm);
        return localKeyFactory.generatePublic(new X509EncodedKeySpec(encoded));
      }
      if ((type == Type.PRIVATE) && ("PKCS#8".equals(format)))
      {
        localKeyFactory = KeyFactory.getInstance(algorithm);
        return localKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
      }
      throw new NotSerializableException("unrecognized type/format combination: " + type + "/" + format);
    }
    catch (NotSerializableException localNotSerializableException1)
    {
      throw localNotSerializableException1;
    }
    catch (Exception localException)
    {
      NotSerializableException localNotSerializableException2 = new NotSerializableException("java.security.Key: [" + type + "] [" + algorithm + "] [" + format + "]");
      localNotSerializableException2.initCause(localException);
      throw localNotSerializableException2;
    }
  }
  
  public static enum Type
  {
    SECRET,  PUBLIC,  PRIVATE;
    
    private Type() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\KeyRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */