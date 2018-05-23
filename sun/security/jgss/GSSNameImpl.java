package sun.security.jgss;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;

public class GSSNameImpl
  implements GSSName
{
  static final Oid oldHostbasedServiceName;
  private GSSManagerImpl gssManager = null;
  private String appNameStr = null;
  private byte[] appNameBytes = null;
  private Oid appNameType = null;
  private String printableName = null;
  private Oid printableNameType = null;
  private HashMap<Oid, GSSNameSpi> elements = null;
  private GSSNameSpi mechElement = null;
  
  static GSSNameImpl wrapElement(GSSManagerImpl paramGSSManagerImpl, GSSNameSpi paramGSSNameSpi)
    throws GSSException
  {
    return paramGSSNameSpi == null ? null : new GSSNameImpl(paramGSSManagerImpl, paramGSSNameSpi);
  }
  
  GSSNameImpl(GSSManagerImpl paramGSSManagerImpl, GSSNameSpi paramGSSNameSpi)
  {
    gssManager = paramGSSManagerImpl;
    appNameStr = (printableName = paramGSSNameSpi.toString());
    appNameType = (printableNameType = paramGSSNameSpi.getStringNameType());
    mechElement = paramGSSNameSpi;
    elements = new HashMap(1);
    elements.put(paramGSSNameSpi.getMechanism(), mechElement);
  }
  
  GSSNameImpl(GSSManagerImpl paramGSSManagerImpl, Object paramObject, Oid paramOid)
    throws GSSException
  {
    this(paramGSSManagerImpl, paramObject, paramOid, null);
  }
  
  GSSNameImpl(GSSManagerImpl paramGSSManagerImpl, Object paramObject, Oid paramOid1, Oid paramOid2)
    throws GSSException
  {
    if (oldHostbasedServiceName.equals(paramOid1)) {
      paramOid1 = GSSName.NT_HOSTBASED_SERVICE;
    }
    if (paramObject == null) {
      throw new GSSExceptionImpl(3, "Cannot import null name");
    }
    if (paramOid2 == null) {
      paramOid2 = ProviderList.DEFAULT_MECH_OID;
    }
    if (NT_EXPORT_NAME.equals(paramOid1)) {
      importName(paramGSSManagerImpl, paramObject);
    } else {
      init(paramGSSManagerImpl, paramObject, paramOid1, paramOid2);
    }
  }
  
  private void init(GSSManagerImpl paramGSSManagerImpl, Object paramObject, Oid paramOid1, Oid paramOid2)
    throws GSSException
  {
    gssManager = paramGSSManagerImpl;
    elements = new HashMap(paramGSSManagerImpl.getMechs().length);
    if ((paramObject instanceof String))
    {
      appNameStr = ((String)paramObject);
      if (paramOid1 != null)
      {
        printableName = appNameStr;
        printableNameType = paramOid1;
      }
    }
    else
    {
      appNameBytes = ((byte[])paramObject);
    }
    appNameType = paramOid1;
    mechElement = getElement(paramOid2);
    if (printableName == null)
    {
      printableName = mechElement.toString();
      printableNameType = mechElement.getStringNameType();
    }
  }
  
  private void importName(GSSManagerImpl paramGSSManagerImpl, Object paramObject)
    throws GSSException
  {
    int i = 0;
    byte[] arrayOfByte1 = null;
    if ((paramObject instanceof String)) {
      try
      {
        arrayOfByte1 = ((String)paramObject).getBytes("UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    } else {
      arrayOfByte1 = (byte[])paramObject;
    }
    if ((arrayOfByte1[(i++)] != 4) || (arrayOfByte1[(i++)] != 1)) {
      throw new GSSExceptionImpl(3, "Exported name token id is corrupted!");
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
      throw new GSSExceptionImpl(3, "Exported name Object identifier is corrupted!");
    }
    Oid localOid = new Oid(localObjectIdentifier.toString());
    i += j;
    int k = (0xFF & arrayOfByte1[(i++)]) << 24 | (0xFF & arrayOfByte1[(i++)]) << 16 | (0xFF & arrayOfByte1[(i++)]) << 8 | 0xFF & arrayOfByte1[(i++)];
    if ((k < 0) || (i > arrayOfByte1.length - k)) {
      throw new GSSExceptionImpl(3, "Exported name mech name is corrupted!");
    }
    byte[] arrayOfByte2 = new byte[k];
    System.arraycopy(arrayOfByte1, i, arrayOfByte2, 0, k);
    init(paramGSSManagerImpl, arrayOfByte2, NT_EXPORT_NAME, localOid);
  }
  
  public GSSName canonicalize(Oid paramOid)
    throws GSSException
  {
    if (paramOid == null) {
      paramOid = ProviderList.DEFAULT_MECH_OID;
    }
    return wrapElement(gssManager, getElement(paramOid));
  }
  
  public boolean equals(GSSName paramGSSName)
    throws GSSException
  {
    if ((isAnonymous()) || (paramGSSName.isAnonymous())) {
      return false;
    }
    if (paramGSSName == this) {
      return true;
    }
    if (!(paramGSSName instanceof GSSNameImpl)) {
      return equals(gssManager.createName(paramGSSName.toString(), paramGSSName.getStringNameType()));
    }
    GSSNameImpl localGSSNameImpl = (GSSNameImpl)paramGSSName;
    GSSNameSpi localGSSNameSpi1 = mechElement;
    GSSNameSpi localGSSNameSpi2 = mechElement;
    if ((localGSSNameSpi1 == null) && (localGSSNameSpi2 != null)) {
      localGSSNameSpi1 = getElement(localGSSNameSpi2.getMechanism());
    } else if ((localGSSNameSpi1 != null) && (localGSSNameSpi2 == null)) {
      localGSSNameSpi2 = localGSSNameImpl.getElement(localGSSNameSpi1.getMechanism());
    }
    if ((localGSSNameSpi1 != null) && (localGSSNameSpi2 != null)) {
      return localGSSNameSpi1.equals(localGSSNameSpi2);
    }
    if ((appNameType != null) && (appNameType != null))
    {
      if (!appNameType.equals(appNameType)) {
        return false;
      }
      byte[] arrayOfByte1 = null;
      byte[] arrayOfByte2 = null;
      try
      {
        arrayOfByte1 = appNameStr != null ? appNameStr.getBytes("UTF-8") : appNameBytes;
        arrayOfByte2 = appNameStr != null ? appNameStr.getBytes("UTF-8") : appNameBytes;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
      return Arrays.equals(arrayOfByte1, arrayOfByte2);
    }
    return false;
  }
  
  public int hashCode()
  {
    return 1;
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      if ((paramObject instanceof GSSName)) {
        return equals((GSSName)paramObject);
      }
    }
    catch (GSSException localGSSException) {}
    return false;
  }
  
  public byte[] export()
    throws GSSException
  {
    if (mechElement == null) {
      mechElement = getElement(ProviderList.DEFAULT_MECH_OID);
    }
    byte[] arrayOfByte1 = mechElement.export();
    byte[] arrayOfByte2 = null;
    ObjectIdentifier localObjectIdentifier = null;
    try
    {
      localObjectIdentifier = new ObjectIdentifier(mechElement.getMechanism().toString());
    }
    catch (IOException localIOException1)
    {
      throw new GSSExceptionImpl(11, "Invalid OID String ");
    }
    DerOutputStream localDerOutputStream = new DerOutputStream();
    try
    {
      localDerOutputStream.putOID(localObjectIdentifier);
    }
    catch (IOException localIOException2)
    {
      throw new GSSExceptionImpl(11, "Could not ASN.1 Encode " + localObjectIdentifier.toString());
    }
    arrayOfByte2 = localDerOutputStream.toByteArray();
    byte[] arrayOfByte3 = new byte[4 + arrayOfByte2.length + 4 + arrayOfByte1.length];
    int i = 0;
    arrayOfByte3[(i++)] = 4;
    arrayOfByte3[(i++)] = 1;
    arrayOfByte3[(i++)] = ((byte)(arrayOfByte2.length >>> 8));
    arrayOfByte3[(i++)] = ((byte)arrayOfByte2.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, i, arrayOfByte2.length);
    i += arrayOfByte2.length;
    arrayOfByte3[(i++)] = ((byte)(arrayOfByte1.length >>> 24));
    arrayOfByte3[(i++)] = ((byte)(arrayOfByte1.length >>> 16));
    arrayOfByte3[(i++)] = ((byte)(arrayOfByte1.length >>> 8));
    arrayOfByte3[(i++)] = ((byte)arrayOfByte1.length);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i, arrayOfByte1.length);
    return arrayOfByte3;
  }
  
  public String toString()
  {
    return printableName;
  }
  
  public Oid getStringNameType()
    throws GSSException
  {
    return printableNameType;
  }
  
  public boolean isAnonymous()
  {
    if (printableNameType == null) {
      return false;
    }
    return GSSName.NT_ANONYMOUS.equals(printableNameType);
  }
  
  public boolean isMN()
  {
    return true;
  }
  
  public synchronized GSSNameSpi getElement(Oid paramOid)
    throws GSSException
  {
    GSSNameSpi localGSSNameSpi = (GSSNameSpi)elements.get(paramOid);
    if (localGSSNameSpi == null)
    {
      if (appNameStr != null) {
        localGSSNameSpi = gssManager.getNameElement(appNameStr, appNameType, paramOid);
      } else {
        localGSSNameSpi = gssManager.getNameElement(appNameBytes, appNameType, paramOid);
      }
      elements.put(paramOid, localGSSNameSpi);
    }
    return localGSSNameSpi;
  }
  
  Set<GSSNameSpi> getElements()
  {
    return new HashSet(elements.values());
  }
  
  private static String getNameTypeStr(Oid paramOid)
  {
    if (paramOid == null) {
      return "(NT is null)";
    }
    if (paramOid.equals(NT_USER_NAME)) {
      return "NT_USER_NAME";
    }
    if (paramOid.equals(NT_HOSTBASED_SERVICE)) {
      return "NT_HOSTBASED_SERVICE";
    }
    if (paramOid.equals(NT_EXPORT_NAME)) {
      return "NT_EXPORT_NAME";
    }
    if (paramOid.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL)) {
      return "NT_GSS_KRB5_PRINCIPAL";
    }
    return "Unknown";
  }
  
  static
  {
    Oid localOid = null;
    try
    {
      localOid = new Oid("1.3.6.1.5.6.2");
    }
    catch (Exception localException) {}
    oldHostbasedServiceName = localOid;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\GSSNameImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */