package sun.security.jgss.wrapper;

import java.io.IOException;
import java.security.Provider;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSExceptionImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Realm;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;

public class GSSNameElement
  implements GSSNameSpi
{
  long pName = 0L;
  private String printableName;
  private Oid printableType;
  private GSSLibStub cStub;
  static final GSSNameElement DEF_ACCEPTOR = new GSSNameElement();
  
  private static Oid getNativeNameType(Oid paramOid, GSSLibStub paramGSSLibStub)
  {
    if (GSSUtil.NT_GSS_KRB5_PRINCIPAL.equals(paramOid))
    {
      Oid[] arrayOfOid = null;
      try
      {
        arrayOfOid = paramGSSLibStub.inquireNamesForMech();
      }
      catch (GSSException localGSSException1)
      {
        if ((localGSSException1.getMajor() == 2) && (GSSUtil.isSpNegoMech(paramGSSLibStub.getMech()))) {
          try
          {
            paramGSSLibStub = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID);
            arrayOfOid = paramGSSLibStub.inquireNamesForMech();
          }
          catch (GSSException localGSSException2)
          {
            SunNativeProvider.debug("Name type list unavailable: " + localGSSException2.getMajorString());
          }
        } else {
          SunNativeProvider.debug("Name type list unavailable: " + localGSSException1.getMajorString());
        }
      }
      if (arrayOfOid != null)
      {
        for (int i = 0; i < arrayOfOid.length; i++) {
          if (arrayOfOid[i].equals(paramOid)) {
            return paramOid;
          }
        }
        SunNativeProvider.debug("Override " + paramOid + " with mechanism default(null)");
        return null;
      }
    }
    return paramOid;
  }
  
  private GSSNameElement()
  {
    printableName = "<DEFAULT ACCEPTOR>";
  }
  
  GSSNameElement(long paramLong, GSSLibStub paramGSSLibStub)
    throws GSSException
  {
    assert (paramGSSLibStub != null);
    if (paramLong == 0L) {
      throw new GSSException(3);
    }
    pName = paramLong;
    cStub = paramGSSLibStub;
    setPrintables();
  }
  
  GSSNameElement(byte[] paramArrayOfByte, Oid paramOid, GSSLibStub paramGSSLibStub)
    throws GSSException
  {
    assert (paramGSSLibStub != null);
    if (paramArrayOfByte == null) {
      throw new GSSException(3);
    }
    cStub = paramGSSLibStub;
    byte[] arrayOfByte = paramArrayOfByte;
    Object localObject2;
    if (paramOid != null)
    {
      paramOid = getNativeNameType(paramOid, paramGSSLibStub);
      if (GSSName.NT_EXPORT_NAME.equals(paramOid))
      {
        localObject1 = null;
        localObject2 = new DerOutputStream();
        Oid localOid = cStub.getMech();
        try
        {
          ((DerOutputStream)localObject2).putOID(new ObjectIdentifier(localOid.toString()));
        }
        catch (IOException localIOException)
        {
          throw new GSSExceptionImpl(11, localIOException);
        }
        localObject1 = ((DerOutputStream)localObject2).toByteArray();
        arrayOfByte = new byte[4 + localObject1.length + 4 + paramArrayOfByte.length];
        int j = 0;
        arrayOfByte[(j++)] = 4;
        arrayOfByte[(j++)] = 1;
        arrayOfByte[(j++)] = ((byte)(localObject1.length >>> 8));
        arrayOfByte[(j++)] = ((byte)localObject1.length);
        System.arraycopy(localObject1, 0, arrayOfByte, j, localObject1.length);
        j += localObject1.length;
        arrayOfByte[(j++)] = ((byte)(paramArrayOfByte.length >>> 24));
        arrayOfByte[(j++)] = ((byte)(paramArrayOfByte.length >>> 16));
        arrayOfByte[(j++)] = ((byte)(paramArrayOfByte.length >>> 8));
        arrayOfByte[(j++)] = ((byte)paramArrayOfByte.length);
        System.arraycopy(paramArrayOfByte, 0, arrayOfByte, j, paramArrayOfByte.length);
      }
    }
    pName = cStub.importName(arrayOfByte, paramOid);
    setPrintables();
    Object localObject1 = System.getSecurityManager();
    if ((localObject1 != null) && (!Realm.AUTODEDUCEREALM))
    {
      localObject2 = getKrbName();
      int i = ((String)localObject2).lastIndexOf('@');
      if (i != -1)
      {
        String str = ((String)localObject2).substring(i);
        if (((paramOid != null) && (!paramOid.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL))) || (!new String(paramArrayOfByte).endsWith(str))) {
          try
          {
            ((SecurityManager)localObject1).checkPermission(new ServicePermission(str, "-"));
          }
          catch (SecurityException localSecurityException)
          {
            throw new GSSException(11);
          }
        }
      }
    }
    SunNativeProvider.debug("Imported " + printableName + " w/ type " + printableType);
  }
  
  private void setPrintables()
    throws GSSException
  {
    Object[] arrayOfObject = null;
    arrayOfObject = cStub.displayName(pName);
    assert ((arrayOfObject != null) && (arrayOfObject.length == 2));
    printableName = ((String)arrayOfObject[0]);
    assert (printableName != null);
    printableType = ((Oid)arrayOfObject[1]);
    if (printableType == null) {
      printableType = GSSName.NT_USER_NAME;
    }
  }
  
  public String getKrbName()
    throws GSSException
  {
    long l = 0L;
    GSSLibStub localGSSLibStub = cStub;
    if (!GSSUtil.isKerberosMech(cStub.getMech())) {
      localGSSLibStub = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID);
    }
    l = localGSSLibStub.canonicalizeName(pName);
    Object[] arrayOfObject = localGSSLibStub.displayName(l);
    localGSSLibStub.releaseName(l);
    SunNativeProvider.debug("Got kerberized name: " + arrayOfObject[0]);
    return (String)arrayOfObject[0];
  }
  
  public Provider getProvider()
  {
    return SunNativeProvider.INSTANCE;
  }
  
  public boolean equals(GSSNameSpi paramGSSNameSpi)
    throws GSSException
  {
    if (!(paramGSSNameSpi instanceof GSSNameElement)) {
      return false;
    }
    return cStub.compareName(pName, pName);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof GSSNameElement)) {
      return false;
    }
    try
    {
      return equals((GSSNameElement)paramObject);
    }
    catch (GSSException localGSSException) {}
    return false;
  }
  
  public int hashCode()
  {
    return new Long(pName).hashCode();
  }
  
  public byte[] export()
    throws GSSException
  {
    byte[] arrayOfByte1 = cStub.exportName(pName);
    int i = 0;
    if ((arrayOfByte1[(i++)] != 4) || (arrayOfByte1[(i++)] != 1)) {
      throw new GSSException(3);
    }
    int j = (0xFF & arrayOfByte1[(i++)]) << 8 | 0xFF & arrayOfByte1[(i++)];
    ObjectIdentifier localObjectIdentifier = null;
    try
    {
      DerInputStream localDerInputStream = new DerInputStream(arrayOfByte1, i, j);
      localObjectIdentifier = new ObjectIdentifier(localDerInputStream);
    }
    catch (IOException localIOException)
    {
      throw new GSSExceptionImpl(3, localIOException);
    }
    Oid localOid = new Oid(localObjectIdentifier.toString());
    assert (localOid.equals(getMechanism()));
    i += j;
    int k = (0xFF & arrayOfByte1[(i++)]) << 24 | (0xFF & arrayOfByte1[(i++)]) << 16 | (0xFF & arrayOfByte1[(i++)]) << 8 | 0xFF & arrayOfByte1[(i++)];
    if (k < 0) {
      throw new GSSException(3);
    }
    byte[] arrayOfByte2 = new byte[k];
    System.arraycopy(arrayOfByte1, i, arrayOfByte2, 0, k);
    return arrayOfByte2;
  }
  
  public Oid getMechanism()
  {
    return cStub.getMech();
  }
  
  public String toString()
  {
    return printableName;
  }
  
  public Oid getStringNameType()
  {
    return printableType;
  }
  
  public boolean isAnonymousName()
  {
    return GSSName.NT_ANONYMOUS.equals(printableType);
  }
  
  public void dispose()
  {
    if (pName != 0L)
    {
      cStub.releaseName(pName);
      pName = 0L;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\wrapper\GSSNameElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */