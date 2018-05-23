package org.ietf.jgss;

import java.io.IOException;
import java.io.InputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class Oid
{
  private ObjectIdentifier oid;
  private byte[] derEncoding;
  
  public Oid(String paramString)
    throws GSSException
  {
    try
    {
      oid = new ObjectIdentifier(paramString);
      derEncoding = null;
    }
    catch (Exception localException)
    {
      throw new GSSException(11, "Improperly formatted Object Identifier String - " + paramString);
    }
  }
  
  public Oid(InputStream paramInputStream)
    throws GSSException
  {
    try
    {
      DerValue localDerValue = new DerValue(paramInputStream);
      derEncoding = localDerValue.toByteArray();
      oid = localDerValue.getOID();
    }
    catch (IOException localIOException)
    {
      throw new GSSException(11, "Improperly formatted ASN.1 DER encoding for Oid");
    }
  }
  
  public Oid(byte[] paramArrayOfByte)
    throws GSSException
  {
    try
    {
      DerValue localDerValue = new DerValue(paramArrayOfByte);
      derEncoding = localDerValue.toByteArray();
      oid = localDerValue.getOID();
    }
    catch (IOException localIOException)
    {
      throw new GSSException(11, "Improperly formatted ASN.1 DER encoding for Oid");
    }
  }
  
  static Oid getInstance(String paramString)
  {
    Oid localOid = null;
    try
    {
      localOid = new Oid(paramString);
    }
    catch (GSSException localGSSException) {}
    return localOid;
  }
  
  public String toString()
  {
    return oid.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Oid)) {
      return oid.equals(oid);
    }
    if ((paramObject instanceof ObjectIdentifier)) {
      return oid.equals(paramObject);
    }
    return false;
  }
  
  public byte[] getDER()
    throws GSSException
  {
    if (derEncoding == null)
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      try
      {
        localDerOutputStream.putOID(oid);
      }
      catch (IOException localIOException)
      {
        throw new GSSException(11, localIOException.getMessage());
      }
      derEncoding = localDerOutputStream.toByteArray();
    }
    return (byte[])derEncoding.clone();
  }
  
  public boolean containedIn(Oid[] paramArrayOfOid)
  {
    for (int i = 0; i < paramArrayOfOid.length; i++) {
      if (paramArrayOfOid[i].equals(this)) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return oid.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\ietf\jgss\Oid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */