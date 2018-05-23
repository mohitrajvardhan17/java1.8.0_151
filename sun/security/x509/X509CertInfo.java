package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class X509CertInfo
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info";
  public static final String NAME = "info";
  public static final String DN_NAME = "dname";
  public static final String VERSION = "version";
  public static final String SERIAL_NUMBER = "serialNumber";
  public static final String ALGORITHM_ID = "algorithmID";
  public static final String ISSUER = "issuer";
  public static final String SUBJECT = "subject";
  public static final String VALIDITY = "validity";
  public static final String KEY = "key";
  public static final String ISSUER_ID = "issuerID";
  public static final String SUBJECT_ID = "subjectID";
  public static final String EXTENSIONS = "extensions";
  protected CertificateVersion version = new CertificateVersion();
  protected CertificateSerialNumber serialNum = null;
  protected CertificateAlgorithmId algId = null;
  protected X500Name issuer = null;
  protected X500Name subject = null;
  protected CertificateValidity interval = null;
  protected CertificateX509Key pubKey = null;
  protected UniqueIdentity issuerUniqueId = null;
  protected UniqueIdentity subjectUniqueId = null;
  protected CertificateExtensions extensions = null;
  private static final int ATTR_VERSION = 1;
  private static final int ATTR_SERIAL = 2;
  private static final int ATTR_ALGORITHM = 3;
  private static final int ATTR_ISSUER = 4;
  private static final int ATTR_VALIDITY = 5;
  private static final int ATTR_SUBJECT = 6;
  private static final int ATTR_KEY = 7;
  private static final int ATTR_ISSUER_ID = 8;
  private static final int ATTR_SUBJECT_ID = 9;
  private static final int ATTR_EXTENSIONS = 10;
  private byte[] rawCertInfo = null;
  private static final Map<String, Integer> map = new HashMap();
  
  public X509CertInfo() {}
  
  public X509CertInfo(byte[] paramArrayOfByte)
    throws CertificateParsingException
  {
    try
    {
      DerValue localDerValue = new DerValue(paramArrayOfByte);
      parse(localDerValue);
    }
    catch (IOException localIOException)
    {
      throw new CertificateParsingException(localIOException);
    }
  }
  
  public X509CertInfo(DerValue paramDerValue)
    throws CertificateParsingException
  {
    try
    {
      parse(paramDerValue);
    }
    catch (IOException localIOException)
    {
      throw new CertificateParsingException(localIOException);
    }
  }
  
  public void encode(OutputStream paramOutputStream)
    throws CertificateException, IOException
  {
    if (rawCertInfo == null)
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      emit(localDerOutputStream);
      rawCertInfo = localDerOutputStream.toByteArray();
    }
    paramOutputStream.write((byte[])rawCertInfo.clone());
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("version");
    localAttributeNameEnumeration.addElement("serialNumber");
    localAttributeNameEnumeration.addElement("algorithmID");
    localAttributeNameEnumeration.addElement("issuer");
    localAttributeNameEnumeration.addElement("validity");
    localAttributeNameEnumeration.addElement("subject");
    localAttributeNameEnumeration.addElement("key");
    localAttributeNameEnumeration.addElement("issuerID");
    localAttributeNameEnumeration.addElement("subjectID");
    localAttributeNameEnumeration.addElement("extensions");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "info";
  }
  
  public byte[] getEncodedInfo()
    throws CertificateEncodingException
  {
    try
    {
      if (rawCertInfo == null)
      {
        DerOutputStream localDerOutputStream = new DerOutputStream();
        emit(localDerOutputStream);
        rawCertInfo = localDerOutputStream.toByteArray();
      }
      return (byte[])rawCertInfo.clone();
    }
    catch (IOException localIOException)
    {
      throw new CertificateEncodingException(localIOException.toString());
    }
    catch (CertificateException localCertificateException)
    {
      throw new CertificateEncodingException(localCertificateException.toString());
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof X509CertInfo)) {
      return equals((X509CertInfo)paramObject);
    }
    return false;
  }
  
  public boolean equals(X509CertInfo paramX509CertInfo)
  {
    if (this == paramX509CertInfo) {
      return true;
    }
    if ((rawCertInfo == null) || (rawCertInfo == null)) {
      return false;
    }
    if (rawCertInfo.length != rawCertInfo.length) {
      return false;
    }
    for (int i = 0; i < rawCertInfo.length; i++) {
      if (rawCertInfo[i] != rawCertInfo[i]) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 1; j < rawCertInfo.length; j++) {
      i += rawCertInfo[j] * j;
    }
    return i;
  }
  
  public String toString()
  {
    if ((subject == null) || (pubKey == null) || (interval == null) || (issuer == null) || (algId == null) || (serialNum == null)) {
      throw new NullPointerException("X.509 cert is incomplete");
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[\n");
    localStringBuilder.append("  " + version.toString() + "\n");
    localStringBuilder.append("  Subject: " + subject.toString() + "\n");
    localStringBuilder.append("  Signature Algorithm: " + algId.toString() + "\n");
    localStringBuilder.append("  Key:  " + pubKey.toString() + "\n");
    localStringBuilder.append("  " + interval.toString() + "\n");
    localStringBuilder.append("  Issuer: " + issuer.toString() + "\n");
    localStringBuilder.append("  " + serialNum.toString() + "\n");
    if (issuerUniqueId != null) {
      localStringBuilder.append("  Issuer Id:\n" + issuerUniqueId.toString() + "\n");
    }
    if (subjectUniqueId != null) {
      localStringBuilder.append("  Subject Id:\n" + subjectUniqueId.toString() + "\n");
    }
    if (extensions != null)
    {
      Collection localCollection = extensions.getAllExtensions();
      Extension[] arrayOfExtension = (Extension[])localCollection.toArray(new Extension[0]);
      localStringBuilder.append("\nCertificate Extensions: " + arrayOfExtension.length);
      Object localObject;
      for (int i = 0; i < arrayOfExtension.length; i++)
      {
        localStringBuilder.append("\n[" + (i + 1) + "]: ");
        Extension localExtension = arrayOfExtension[i];
        try
        {
          if (OIDMap.getClass(localExtension.getExtensionId()) == null)
          {
            localStringBuilder.append(localExtension.toString());
            byte[] arrayOfByte = localExtension.getExtensionValue();
            if (arrayOfByte != null)
            {
              localObject = new DerOutputStream();
              ((DerOutputStream)localObject).putOctetString(arrayOfByte);
              arrayOfByte = ((DerOutputStream)localObject).toByteArray();
              HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
              localStringBuilder.append("Extension unknown: DER encoded OCTET string =\n" + localHexDumpEncoder.encodeBuffer(arrayOfByte) + "\n");
            }
          }
          else
          {
            localStringBuilder.append(localExtension.toString());
          }
        }
        catch (Exception localException)
        {
          localStringBuilder.append(", Error parsing this extension");
        }
      }
      Map localMap = extensions.getUnparseableExtensions();
      if (!localMap.isEmpty())
      {
        localStringBuilder.append("\nUnparseable certificate extensions: " + localMap.size());
        int j = 1;
        Iterator localIterator = localMap.values().iterator();
        while (localIterator.hasNext())
        {
          localObject = (Extension)localIterator.next();
          localStringBuilder.append("\n[" + j++ + "]: ");
          localStringBuilder.append(localObject);
        }
      }
    }
    localStringBuilder.append("\n]");
    return localStringBuilder.toString();
  }
  
  public void set(String paramString, Object paramObject)
    throws CertificateException, IOException
  {
    X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
    int i = attributeMap(localX509AttributeName.getPrefix());
    if (i == 0) {
      throw new CertificateException("Attribute name not recognized: " + paramString);
    }
    rawCertInfo = null;
    String str = localX509AttributeName.getSuffix();
    switch (i)
    {
    case 1: 
      if (str == null) {
        setVersion(paramObject);
      } else {
        version.set(str, paramObject);
      }
      break;
    case 2: 
      if (str == null) {
        setSerialNumber(paramObject);
      } else {
        serialNum.set(str, paramObject);
      }
      break;
    case 3: 
      if (str == null) {
        setAlgorithmId(paramObject);
      } else {
        algId.set(str, paramObject);
      }
      break;
    case 4: 
      setIssuer(paramObject);
      break;
    case 5: 
      if (str == null) {
        setValidity(paramObject);
      } else {
        interval.set(str, paramObject);
      }
      break;
    case 6: 
      setSubject(paramObject);
      break;
    case 7: 
      if (str == null) {
        setKey(paramObject);
      } else {
        pubKey.set(str, paramObject);
      }
      break;
    case 8: 
      setIssuerUniqueId(paramObject);
      break;
    case 9: 
      setSubjectUniqueId(paramObject);
      break;
    case 10: 
      if (str == null)
      {
        setExtensions(paramObject);
      }
      else
      {
        if (extensions == null) {
          extensions = new CertificateExtensions();
        }
        extensions.set(str, paramObject);
      }
      break;
    }
  }
  
  public void delete(String paramString)
    throws CertificateException, IOException
  {
    X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
    int i = attributeMap(localX509AttributeName.getPrefix());
    if (i == 0) {
      throw new CertificateException("Attribute name not recognized: " + paramString);
    }
    rawCertInfo = null;
    String str = localX509AttributeName.getSuffix();
    switch (i)
    {
    case 1: 
      if (str == null) {
        version = null;
      } else {
        version.delete(str);
      }
      break;
    case 2: 
      if (str == null) {
        serialNum = null;
      } else {
        serialNum.delete(str);
      }
      break;
    case 3: 
      if (str == null) {
        algId = null;
      } else {
        algId.delete(str);
      }
      break;
    case 4: 
      issuer = null;
      break;
    case 5: 
      if (str == null) {
        interval = null;
      } else {
        interval.delete(str);
      }
      break;
    case 6: 
      subject = null;
      break;
    case 7: 
      if (str == null) {
        pubKey = null;
      } else {
        pubKey.delete(str);
      }
      break;
    case 8: 
      issuerUniqueId = null;
      break;
    case 9: 
      subjectUniqueId = null;
      break;
    case 10: 
      if (str == null) {
        extensions = null;
      } else if (extensions != null) {
        extensions.delete(str);
      }
      break;
    }
  }
  
  public Object get(String paramString)
    throws CertificateException, IOException
  {
    X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
    int i = attributeMap(localX509AttributeName.getPrefix());
    if (i == 0) {
      throw new CertificateParsingException("Attribute name not recognized: " + paramString);
    }
    String str = localX509AttributeName.getSuffix();
    switch (i)
    {
    case 10: 
      if (str == null) {
        return extensions;
      }
      if (extensions == null) {
        return null;
      }
      return extensions.get(str);
    case 6: 
      if (str == null) {
        return subject;
      }
      return getX500Name(str, false);
    case 4: 
      if (str == null) {
        return issuer;
      }
      return getX500Name(str, true);
    case 7: 
      if (str == null) {
        return pubKey;
      }
      return pubKey.get(str);
    case 3: 
      if (str == null) {
        return algId;
      }
      return algId.get(str);
    case 5: 
      if (str == null) {
        return interval;
      }
      return interval.get(str);
    case 1: 
      if (str == null) {
        return version;
      }
      return version.get(str);
    case 2: 
      if (str == null) {
        return serialNum;
      }
      return serialNum.get(str);
    case 8: 
      return issuerUniqueId;
    case 9: 
      return subjectUniqueId;
    }
    return null;
  }
  
  private Object getX500Name(String paramString, boolean paramBoolean)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("dname")) {
      return paramBoolean ? issuer : subject;
    }
    if (paramString.equalsIgnoreCase("x500principal")) {
      return paramBoolean ? issuer.asX500Principal() : subject.asX500Principal();
    }
    throw new IOException("Attribute name not recognized.");
  }
  
  private void parse(DerValue paramDerValue)
    throws CertificateParsingException, IOException
  {
    if (tag != 48) {
      throw new CertificateParsingException("signed fields invalid");
    }
    rawCertInfo = paramDerValue.toByteArray();
    DerInputStream localDerInputStream = data;
    DerValue localDerValue = localDerInputStream.getDerValue();
    if (localDerValue.isContextSpecific((byte)0))
    {
      version = new CertificateVersion(localDerValue);
      localDerValue = localDerInputStream.getDerValue();
    }
    serialNum = new CertificateSerialNumber(localDerValue);
    algId = new CertificateAlgorithmId(localDerInputStream);
    issuer = new X500Name(localDerInputStream);
    if (issuer.isEmpty()) {
      throw new CertificateParsingException("Empty issuer DN not allowed in X509Certificates");
    }
    interval = new CertificateValidity(localDerInputStream);
    subject = new X500Name(localDerInputStream);
    if ((version.compare(0) == 0) && (subject.isEmpty())) {
      throw new CertificateParsingException("Empty subject DN not allowed in v1 certificate");
    }
    pubKey = new CertificateX509Key(localDerInputStream);
    if (localDerInputStream.available() != 0)
    {
      if (version.compare(0) == 0) {
        throw new CertificateParsingException("no more data allowed for version 1 certificate");
      }
    }
    else {
      return;
    }
    localDerValue = localDerInputStream.getDerValue();
    if (localDerValue.isContextSpecific((byte)1))
    {
      issuerUniqueId = new UniqueIdentity(localDerValue);
      if (localDerInputStream.available() == 0) {
        return;
      }
      localDerValue = localDerInputStream.getDerValue();
    }
    if (localDerValue.isContextSpecific((byte)2))
    {
      subjectUniqueId = new UniqueIdentity(localDerValue);
      if (localDerInputStream.available() == 0) {
        return;
      }
      localDerValue = localDerInputStream.getDerValue();
    }
    if (version.compare(2) != 0) {
      throw new CertificateParsingException("Extensions not allowed in v2 certificate");
    }
    if ((localDerValue.isConstructed()) && (localDerValue.isContextSpecific((byte)3))) {
      extensions = new CertificateExtensions(data);
    }
    verifyCert(subject, extensions);
  }
  
  private void verifyCert(X500Name paramX500Name, CertificateExtensions paramCertificateExtensions)
    throws CertificateParsingException, IOException
  {
    if (paramX500Name.isEmpty())
    {
      if (paramCertificateExtensions == null) {
        throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and certificate has no extensions");
      }
      SubjectAlternativeNameExtension localSubjectAlternativeNameExtension = null;
      Object localObject = null;
      GeneralNames localGeneralNames = null;
      try
      {
        localSubjectAlternativeNameExtension = (SubjectAlternativeNameExtension)paramCertificateExtensions.get("SubjectAlternativeName");
        localGeneralNames = localSubjectAlternativeNameExtension.get("subject_name");
      }
      catch (IOException localIOException)
      {
        throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and SubjectAlternativeName extension is absent");
      }
      if ((localGeneralNames == null) || (localGeneralNames.isEmpty())) {
        throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and SubjectAlternativeName extension is empty");
      }
      if (!localSubjectAlternativeNameExtension.isCritical()) {
        throw new CertificateParsingException("X.509 Certificate is incomplete: SubjectAlternativeName extension MUST be marked critical when subject field is empty");
      }
    }
  }
  
  private void emit(DerOutputStream paramDerOutputStream)
    throws CertificateException, IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    version.encode(localDerOutputStream);
    serialNum.encode(localDerOutputStream);
    algId.encode(localDerOutputStream);
    if ((version.compare(0) == 0) && (issuer.toString() == null)) {
      throw new CertificateParsingException("Null issuer DN not allowed in v1 certificate");
    }
    issuer.encode(localDerOutputStream);
    interval.encode(localDerOutputStream);
    if ((version.compare(0) == 0) && (subject.toString() == null)) {
      throw new CertificateParsingException("Null subject DN not allowed in v1 certificate");
    }
    subject.encode(localDerOutputStream);
    pubKey.encode(localDerOutputStream);
    if (issuerUniqueId != null) {
      issuerUniqueId.encode(localDerOutputStream, DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1));
    }
    if (subjectUniqueId != null) {
      subjectUniqueId.encode(localDerOutputStream, DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)2));
    }
    if (extensions != null) {
      extensions.encode(localDerOutputStream);
    }
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
  
  private int attributeMap(String paramString)
  {
    Integer localInteger = (Integer)map.get(paramString);
    if (localInteger == null) {
      return 0;
    }
    return localInteger.intValue();
  }
  
  private void setVersion(Object paramObject)
    throws CertificateException
  {
    if (!(paramObject instanceof CertificateVersion)) {
      throw new CertificateException("Version class type invalid.");
    }
    version = ((CertificateVersion)paramObject);
  }
  
  private void setSerialNumber(Object paramObject)
    throws CertificateException
  {
    if (!(paramObject instanceof CertificateSerialNumber)) {
      throw new CertificateException("SerialNumber class type invalid.");
    }
    serialNum = ((CertificateSerialNumber)paramObject);
  }
  
  private void setAlgorithmId(Object paramObject)
    throws CertificateException
  {
    if (!(paramObject instanceof CertificateAlgorithmId)) {
      throw new CertificateException("AlgorithmId class type invalid.");
    }
    algId = ((CertificateAlgorithmId)paramObject);
  }
  
  private void setIssuer(Object paramObject)
    throws CertificateException
  {
    if (!(paramObject instanceof X500Name)) {
      throw new CertificateException("Issuer class type invalid.");
    }
    issuer = ((X500Name)paramObject);
  }
  
  private void setValidity(Object paramObject)
    throws CertificateException
  {
    if (!(paramObject instanceof CertificateValidity)) {
      throw new CertificateException("CertificateValidity class type invalid.");
    }
    interval = ((CertificateValidity)paramObject);
  }
  
  private void setSubject(Object paramObject)
    throws CertificateException
  {
    if (!(paramObject instanceof X500Name)) {
      throw new CertificateException("Subject class type invalid.");
    }
    subject = ((X500Name)paramObject);
  }
  
  private void setKey(Object paramObject)
    throws CertificateException
  {
    if (!(paramObject instanceof CertificateX509Key)) {
      throw new CertificateException("Key class type invalid.");
    }
    pubKey = ((CertificateX509Key)paramObject);
  }
  
  private void setIssuerUniqueId(Object paramObject)
    throws CertificateException
  {
    if (version.compare(1) < 0) {
      throw new CertificateException("Invalid version");
    }
    if (!(paramObject instanceof UniqueIdentity)) {
      throw new CertificateException("IssuerUniqueId class type invalid.");
    }
    issuerUniqueId = ((UniqueIdentity)paramObject);
  }
  
  private void setSubjectUniqueId(Object paramObject)
    throws CertificateException
  {
    if (version.compare(1) < 0) {
      throw new CertificateException("Invalid version");
    }
    if (!(paramObject instanceof UniqueIdentity)) {
      throw new CertificateException("SubjectUniqueId class type invalid.");
    }
    subjectUniqueId = ((UniqueIdentity)paramObject);
  }
  
  private void setExtensions(Object paramObject)
    throws CertificateException
  {
    if (version.compare(2) < 0) {
      throw new CertificateException("Invalid version");
    }
    if (!(paramObject instanceof CertificateExtensions)) {
      throw new CertificateException("Extensions class type invalid.");
    }
    extensions = ((CertificateExtensions)paramObject);
  }
  
  static
  {
    map.put("version", Integer.valueOf(1));
    map.put("serialNumber", Integer.valueOf(2));
    map.put("algorithmID", Integer.valueOf(3));
    map.put("issuer", Integer.valueOf(4));
    map.put("validity", Integer.valueOf(5));
    map.put("subject", Integer.valueOf(6));
    map.put("key", Integer.valueOf(7));
    map.put("issuerID", Integer.valueOf(8));
    map.put("subjectID", Integer.valueOf(9));
    map.put("extensions", Integer.valueOf(10));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\X509CertInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */