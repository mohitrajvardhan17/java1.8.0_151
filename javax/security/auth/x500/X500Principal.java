package javax.security.auth.x500;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import sun.security.util.DerValue;
import sun.security.util.ResourcesMgr;
import sun.security.x509.X500Name;

public final class X500Principal
  implements Principal, Serializable
{
  private static final long serialVersionUID = -500463348111345721L;
  public static final String RFC1779 = "RFC1779";
  public static final String RFC2253 = "RFC2253";
  public static final String CANONICAL = "CANONICAL";
  private transient X500Name thisX500Name;
  
  X500Principal(X500Name paramX500Name)
  {
    thisX500Name = paramX500Name;
  }
  
  public X500Principal(String paramString)
  {
    this(paramString, Collections.emptyMap());
  }
  
  public X500Principal(String paramString, Map<String, String> paramMap)
  {
    if (paramString == null) {
      throw new NullPointerException(ResourcesMgr.getString("provided.null.name"));
    }
    if (paramMap == null) {
      throw new NullPointerException(ResourcesMgr.getString("provided.null.keyword.map"));
    }
    try
    {
      thisX500Name = new X500Name(paramString, paramMap);
    }
    catch (Exception localException)
    {
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("improperly specified input name: " + paramString);
      localIllegalArgumentException.initCause(localException);
      throw localIllegalArgumentException;
    }
  }
  
  public X500Principal(byte[] paramArrayOfByte)
  {
    try
    {
      thisX500Name = new X500Name(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("improperly specified input name");
      localIllegalArgumentException.initCause(localException);
      throw localIllegalArgumentException;
    }
  }
  
  public X500Principal(InputStream paramInputStream)
  {
    if (paramInputStream == null) {
      throw new NullPointerException("provided null input stream");
    }
    try
    {
      if (paramInputStream.markSupported()) {
        paramInputStream.mark(paramInputStream.available() + 1);
      }
      DerValue localDerValue = new DerValue(paramInputStream);
      thisX500Name = new X500Name(data);
    }
    catch (Exception localException)
    {
      if (paramInputStream.markSupported()) {
        try
        {
          paramInputStream.reset();
        }
        catch (IOException localIOException)
        {
          IllegalArgumentException localIllegalArgumentException2 = new IllegalArgumentException("improperly specified input stream and unable to reset input stream");
          localIllegalArgumentException2.initCause(localException);
          throw localIllegalArgumentException2;
        }
      }
      IllegalArgumentException localIllegalArgumentException1 = new IllegalArgumentException("improperly specified input stream");
      localIllegalArgumentException1.initCause(localException);
      throw localIllegalArgumentException1;
    }
  }
  
  public String getName()
  {
    return getName("RFC2253");
  }
  
  public String getName(String paramString)
  {
    if (paramString != null)
    {
      if (paramString.equalsIgnoreCase("RFC1779")) {
        return thisX500Name.getRFC1779Name();
      }
      if (paramString.equalsIgnoreCase("RFC2253")) {
        return thisX500Name.getRFC2253Name();
      }
      if (paramString.equalsIgnoreCase("CANONICAL")) {
        return thisX500Name.getRFC2253CanonicalName();
      }
    }
    throw new IllegalArgumentException("invalid format specified");
  }
  
  public String getName(String paramString, Map<String, String> paramMap)
  {
    if (paramMap == null) {
      throw new NullPointerException(ResourcesMgr.getString("provided.null.OID.map"));
    }
    if (paramString != null)
    {
      if (paramString.equalsIgnoreCase("RFC1779")) {
        return thisX500Name.getRFC1779Name(paramMap);
      }
      if (paramString.equalsIgnoreCase("RFC2253")) {
        return thisX500Name.getRFC2253Name(paramMap);
      }
    }
    throw new IllegalArgumentException("invalid format specified");
  }
  
  public byte[] getEncoded()
  {
    try
    {
      return thisX500Name.getEncoded();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException("unable to get encoding", localIOException);
    }
  }
  
  public String toString()
  {
    return thisX500Name.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof X500Principal)) {
      return false;
    }
    X500Principal localX500Principal = (X500Principal)paramObject;
    return thisX500Name.equals(thisX500Name);
  }
  
  public int hashCode()
  {
    return thisX500Name.hashCode();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeObject(thisX500Name.getEncodedInternal());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, NotActiveException, ClassNotFoundException
  {
    thisX500Name = new X500Name((byte[])paramObjectInputStream.readObject());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\x500\X500Principal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */