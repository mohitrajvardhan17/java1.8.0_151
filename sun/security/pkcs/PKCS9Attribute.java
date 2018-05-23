package sun.security.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.CertificateExtensions;

public class PKCS9Attribute
  implements DerEncoder
{
  private static final Debug debug = Debug.getInstance("jar");
  static final ObjectIdentifier[] PKCS9_OIDS = new ObjectIdentifier[18];
  private static final Class<?> BYTE_ARRAY_CLASS;
  public static final ObjectIdentifier EMAIL_ADDRESS_OID;
  public static final ObjectIdentifier UNSTRUCTURED_NAME_OID;
  public static final ObjectIdentifier CONTENT_TYPE_OID;
  public static final ObjectIdentifier MESSAGE_DIGEST_OID;
  public static final ObjectIdentifier SIGNING_TIME_OID;
  public static final ObjectIdentifier COUNTERSIGNATURE_OID;
  public static final ObjectIdentifier CHALLENGE_PASSWORD_OID;
  public static final ObjectIdentifier UNSTRUCTURED_ADDRESS_OID;
  public static final ObjectIdentifier EXTENDED_CERTIFICATE_ATTRIBUTES_OID;
  public static final ObjectIdentifier ISSUER_SERIALNUMBER_OID;
  public static final ObjectIdentifier EXTENSION_REQUEST_OID;
  public static final ObjectIdentifier SMIME_CAPABILITY_OID;
  public static final ObjectIdentifier SIGNING_CERTIFICATE_OID;
  public static final ObjectIdentifier SIGNATURE_TIMESTAMP_TOKEN_OID;
  public static final String EMAIL_ADDRESS_STR = "EmailAddress";
  public static final String UNSTRUCTURED_NAME_STR = "UnstructuredName";
  public static final String CONTENT_TYPE_STR = "ContentType";
  public static final String MESSAGE_DIGEST_STR = "MessageDigest";
  public static final String SIGNING_TIME_STR = "SigningTime";
  public static final String COUNTERSIGNATURE_STR = "Countersignature";
  public static final String CHALLENGE_PASSWORD_STR = "ChallengePassword";
  public static final String UNSTRUCTURED_ADDRESS_STR = "UnstructuredAddress";
  public static final String EXTENDED_CERTIFICATE_ATTRIBUTES_STR = "ExtendedCertificateAttributes";
  public static final String ISSUER_SERIALNUMBER_STR = "IssuerAndSerialNumber";
  private static final String RSA_PROPRIETARY_STR = "RSAProprietary";
  private static final String SMIME_SIGNING_DESC_STR = "SMIMESigningDesc";
  public static final String EXTENSION_REQUEST_STR = "ExtensionRequest";
  public static final String SMIME_CAPABILITY_STR = "SMIMECapability";
  public static final String SIGNING_CERTIFICATE_STR = "SigningCertificate";
  public static final String SIGNATURE_TIMESTAMP_TOKEN_STR = "SignatureTimestampToken";
  private static final Hashtable<String, ObjectIdentifier> NAME_OID_TABLE;
  private static final Hashtable<ObjectIdentifier, String> OID_NAME_TABLE;
  private static final Byte[][] PKCS9_VALUE_TAGS;
  private static final Class<?>[] VALUE_CLASSES;
  private static final boolean[] SINGLE_VALUED = { false, false, false, true, true, true, false, true, false, false, true, false, false, false, true, true, true, true };
  private ObjectIdentifier oid;
  private int index;
  private Object value;
  
  public PKCS9Attribute(ObjectIdentifier paramObjectIdentifier, Object paramObject)
    throws IllegalArgumentException
  {
    init(paramObjectIdentifier, paramObject);
  }
  
  public PKCS9Attribute(String paramString, Object paramObject)
    throws IllegalArgumentException
  {
    ObjectIdentifier localObjectIdentifier = getOID(paramString);
    if (localObjectIdentifier == null) {
      throw new IllegalArgumentException("Unrecognized attribute name " + paramString + " constructing PKCS9Attribute.");
    }
    init(localObjectIdentifier, paramObject);
  }
  
  private void init(ObjectIdentifier paramObjectIdentifier, Object paramObject)
    throws IllegalArgumentException
  {
    oid = paramObjectIdentifier;
    index = indexOf(paramObjectIdentifier, PKCS9_OIDS, 1);
    Class localClass = index == -1 ? BYTE_ARRAY_CLASS : VALUE_CLASSES[index];
    if (!localClass.isInstance(paramObject)) {
      throw new IllegalArgumentException("Wrong value class  for attribute " + paramObjectIdentifier + " constructing PKCS9Attribute; was " + paramObject.getClass().toString() + ", should be " + localClass.toString());
    }
    value = paramObject;
  }
  
  public PKCS9Attribute(DerValue paramDerValue)
    throws IOException
  {
    DerInputStream localDerInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue1 = localDerInputStream.getSequence(2);
    if (localDerInputStream.available() != 0) {
      throw new IOException("Excess data parsing PKCS9Attribute");
    }
    if (arrayOfDerValue1.length != 2) {
      throw new IOException("PKCS9Attribute doesn't have two components");
    }
    oid = arrayOfDerValue1[0].getOID();
    byte[] arrayOfByte = arrayOfDerValue1[1].toByteArray();
    DerValue[] arrayOfDerValue2 = new DerInputStream(arrayOfByte).getSet(1);
    index = indexOf(oid, PKCS9_OIDS, 1);
    if (index == -1)
    {
      if (debug != null) {
        debug.println("Unsupported signer attribute: " + oid);
      }
      value = arrayOfByte;
      return;
    }
    if ((SINGLE_VALUED[index] != 0) && (arrayOfDerValue2.length > 1)) {
      throwSingleValuedException();
    }
    for (int i = 0; i < arrayOfDerValue2.length; i++)
    {
      Byte localByte = new Byte(tag);
      if (indexOf(localByte, PKCS9_VALUE_TAGS[index], 0) == -1) {
        throwTagException(localByte);
      }
    }
    Object localObject;
    int j;
    switch (index)
    {
    case 1: 
    case 2: 
    case 8: 
      localObject = new String[arrayOfDerValue2.length];
      for (j = 0; j < arrayOfDerValue2.length; j++) {
        localObject[j] = arrayOfDerValue2[j].getAsString();
      }
      value = localObject;
      break;
    case 3: 
      value = arrayOfDerValue2[0].getOID();
      break;
    case 4: 
      value = arrayOfDerValue2[0].getOctetString();
      break;
    case 5: 
      value = new DerInputStream(arrayOfDerValue2[0].toByteArray()).getUTCTime();
      break;
    case 6: 
      localObject = new SignerInfo[arrayOfDerValue2.length];
      for (j = 0; j < arrayOfDerValue2.length; j++) {
        localObject[j] = new SignerInfo(arrayOfDerValue2[j].toDerInputStream());
      }
      value = localObject;
      break;
    case 7: 
      value = arrayOfDerValue2[0].getAsString();
      break;
    case 9: 
      throw new IOException("PKCS9 extended-certificate attribute not supported.");
    case 10: 
      throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
    case 11: 
    case 12: 
      throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
    case 13: 
      throw new IOException("PKCS9 attribute #13 not supported.");
    case 14: 
      value = new CertificateExtensions(new DerInputStream(arrayOfDerValue2[0].toByteArray()));
      break;
    case 15: 
      throw new IOException("PKCS9 SMIMECapability attribute not supported.");
    case 16: 
      value = new SigningCertificateInfo(arrayOfDerValue2[0].toByteArray());
      break;
    case 17: 
      value = arrayOfDerValue2[0].toByteArray();
      break;
    }
  }
  
  public void derEncode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putOID(oid);
    Object localObject2;
    int i;
    switch (index)
    {
    case -1: 
      localDerOutputStream.write((byte[])value);
      break;
    case 1: 
    case 2: 
      localObject1 = (String[])value;
      localObject2 = new DerOutputStream[localObject1.length];
      for (i = 0; i < localObject1.length; i++)
      {
        localObject2[i] = new DerOutputStream();
        localObject2[i].putIA5String(localObject1[i]);
      }
      localDerOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])localObject2);
      break;
    case 3: 
      localObject1 = new DerOutputStream();
      ((DerOutputStream)localObject1).putOID((ObjectIdentifier)value);
      localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
      break;
    case 4: 
      localObject1 = new DerOutputStream();
      ((DerOutputStream)localObject1).putOctetString((byte[])value);
      localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
      break;
    case 5: 
      localObject1 = new DerOutputStream();
      ((DerOutputStream)localObject1).putUTCTime((Date)value);
      localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
      break;
    case 6: 
      localDerOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])value);
      break;
    case 7: 
      localObject1 = new DerOutputStream();
      ((DerOutputStream)localObject1).putPrintableString((String)value);
      localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
      break;
    case 8: 
      localObject1 = (String[])value;
      localObject2 = new DerOutputStream[localObject1.length];
      for (i = 0; i < localObject1.length; i++)
      {
        localObject2[i] = new DerOutputStream();
        localObject2[i].putPrintableString(localObject1[i]);
      }
      localDerOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])localObject2);
      break;
    case 9: 
      throw new IOException("PKCS9 extended-certificate attribute not supported.");
    case 10: 
      throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
    case 11: 
    case 12: 
      throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
    case 13: 
      throw new IOException("PKCS9 attribute #13 not supported.");
    case 14: 
      localObject1 = new DerOutputStream();
      localObject2 = (CertificateExtensions)value;
      try
      {
        ((CertificateExtensions)localObject2).encode((OutputStream)localObject1, true);
      }
      catch (CertificateException localCertificateException)
      {
        throw new IOException(localCertificateException.toString());
      }
      localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
      break;
    case 15: 
      throw new IOException("PKCS9 attribute #15 not supported.");
    case 16: 
      throw new IOException("PKCS9 SigningCertificate attribute not supported.");
    case 17: 
      localDerOutputStream.write((byte)49, (byte[])value);
      break;
    }
    Object localObject1 = new DerOutputStream();
    ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream.toByteArray());
    paramOutputStream.write(((DerOutputStream)localObject1).toByteArray());
  }
  
  public boolean isKnown()
  {
    return index != -1;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public boolean isSingleValued()
  {
    return (index == -1) || (SINGLE_VALUED[index] != 0);
  }
  
  public ObjectIdentifier getOID()
  {
    return oid;
  }
  
  public String getName()
  {
    return index == -1 ? oid.toString() : (String)OID_NAME_TABLE.get(PKCS9_OIDS[index]);
  }
  
  public static ObjectIdentifier getOID(String paramString)
  {
    return (ObjectIdentifier)NAME_OID_TABLE.get(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public static String getName(ObjectIdentifier paramObjectIdentifier)
  {
    return (String)OID_NAME_TABLE.get(paramObjectIdentifier);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(100);
    localStringBuffer.append("[");
    if (index == -1) {
      localStringBuffer.append(oid.toString());
    } else {
      localStringBuffer.append((String)OID_NAME_TABLE.get(PKCS9_OIDS[index]));
    }
    localStringBuffer.append(": ");
    if ((index == -1) || (SINGLE_VALUED[index] != 0))
    {
      if ((value instanceof byte[]))
      {
        HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
        localStringBuffer.append(localHexDumpEncoder.encodeBuffer((byte[])value));
      }
      else
      {
        localStringBuffer.append(value.toString());
      }
      localStringBuffer.append("]");
      return localStringBuffer.toString();
    }
    int i = 1;
    Object[] arrayOfObject = (Object[])value;
    for (int j = 0; j < arrayOfObject.length; j++)
    {
      if (i != 0) {
        i = 0;
      } else {
        localStringBuffer.append(", ");
      }
      localStringBuffer.append(arrayOfObject[j].toString());
    }
    return localStringBuffer.toString();
  }
  
  static int indexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt)
  {
    for (int i = paramInt; i < paramArrayOfObject.length; i++) {
      if (paramObject.equals(paramArrayOfObject[i])) {
        return i;
      }
    }
    return -1;
  }
  
  private void throwSingleValuedException()
    throws IOException
  {
    throw new IOException("Single-value attribute " + oid + " (" + getName() + ") has multiple values.");
  }
  
  private void throwTagException(Byte paramByte)
    throws IOException
  {
    Byte[] arrayOfByte = PKCS9_VALUE_TAGS[index];
    StringBuffer localStringBuffer = new StringBuffer(100);
    localStringBuffer.append("Value of attribute ");
    localStringBuffer.append(oid.toString());
    localStringBuffer.append(" (");
    localStringBuffer.append(getName());
    localStringBuffer.append(") has wrong tag: ");
    localStringBuffer.append(paramByte.toString());
    localStringBuffer.append(".  Expected tags: ");
    localStringBuffer.append(arrayOfByte[0].toString());
    for (int i = 1; i < arrayOfByte.length; i++)
    {
      localStringBuffer.append(", ");
      localStringBuffer.append(arrayOfByte[i].toString());
    }
    localStringBuffer.append(".");
    throw new IOException(localStringBuffer.toString());
  }
  
  static
  {
    for (int i = 1; i < PKCS9_OIDS.length - 2; i++) {
      PKCS9_OIDS[i] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, i });
    }
    PKCS9_OIDS[(PKCS9_OIDS.length - 2)] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 12 });
    PKCS9_OIDS[(PKCS9_OIDS.length - 1)] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 14 });
    try
    {
      BYTE_ARRAY_CLASS = Class.forName("[B");
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      throw new ExceptionInInitializerError(localClassNotFoundException1.toString());
    }
    EMAIL_ADDRESS_OID = PKCS9_OIDS[1];
    UNSTRUCTURED_NAME_OID = PKCS9_OIDS[2];
    CONTENT_TYPE_OID = PKCS9_OIDS[3];
    MESSAGE_DIGEST_OID = PKCS9_OIDS[4];
    SIGNING_TIME_OID = PKCS9_OIDS[5];
    COUNTERSIGNATURE_OID = PKCS9_OIDS[6];
    CHALLENGE_PASSWORD_OID = PKCS9_OIDS[7];
    UNSTRUCTURED_ADDRESS_OID = PKCS9_OIDS[8];
    EXTENDED_CERTIFICATE_ATTRIBUTES_OID = PKCS9_OIDS[9];
    ISSUER_SERIALNUMBER_OID = PKCS9_OIDS[10];
    EXTENSION_REQUEST_OID = PKCS9_OIDS[14];
    SMIME_CAPABILITY_OID = PKCS9_OIDS[15];
    SIGNING_CERTIFICATE_OID = PKCS9_OIDS[16];
    SIGNATURE_TIMESTAMP_TOKEN_OID = PKCS9_OIDS[17];
    NAME_OID_TABLE = new Hashtable(18);
    NAME_OID_TABLE.put("emailaddress", PKCS9_OIDS[1]);
    NAME_OID_TABLE.put("unstructuredname", PKCS9_OIDS[2]);
    NAME_OID_TABLE.put("contenttype", PKCS9_OIDS[3]);
    NAME_OID_TABLE.put("messagedigest", PKCS9_OIDS[4]);
    NAME_OID_TABLE.put("signingtime", PKCS9_OIDS[5]);
    NAME_OID_TABLE.put("countersignature", PKCS9_OIDS[6]);
    NAME_OID_TABLE.put("challengepassword", PKCS9_OIDS[7]);
    NAME_OID_TABLE.put("unstructuredaddress", PKCS9_OIDS[8]);
    NAME_OID_TABLE.put("extendedcertificateattributes", PKCS9_OIDS[9]);
    NAME_OID_TABLE.put("issuerandserialnumber", PKCS9_OIDS[10]);
    NAME_OID_TABLE.put("rsaproprietary", PKCS9_OIDS[11]);
    NAME_OID_TABLE.put("rsaproprietary", PKCS9_OIDS[12]);
    NAME_OID_TABLE.put("signingdescription", PKCS9_OIDS[13]);
    NAME_OID_TABLE.put("extensionrequest", PKCS9_OIDS[14]);
    NAME_OID_TABLE.put("smimecapability", PKCS9_OIDS[15]);
    NAME_OID_TABLE.put("signingcertificate", PKCS9_OIDS[16]);
    NAME_OID_TABLE.put("signaturetimestamptoken", PKCS9_OIDS[17]);
    OID_NAME_TABLE = new Hashtable(16);
    OID_NAME_TABLE.put(PKCS9_OIDS[1], "EmailAddress");
    OID_NAME_TABLE.put(PKCS9_OIDS[2], "UnstructuredName");
    OID_NAME_TABLE.put(PKCS9_OIDS[3], "ContentType");
    OID_NAME_TABLE.put(PKCS9_OIDS[4], "MessageDigest");
    OID_NAME_TABLE.put(PKCS9_OIDS[5], "SigningTime");
    OID_NAME_TABLE.put(PKCS9_OIDS[6], "Countersignature");
    OID_NAME_TABLE.put(PKCS9_OIDS[7], "ChallengePassword");
    OID_NAME_TABLE.put(PKCS9_OIDS[8], "UnstructuredAddress");
    OID_NAME_TABLE.put(PKCS9_OIDS[9], "ExtendedCertificateAttributes");
    OID_NAME_TABLE.put(PKCS9_OIDS[10], "IssuerAndSerialNumber");
    OID_NAME_TABLE.put(PKCS9_OIDS[11], "RSAProprietary");
    OID_NAME_TABLE.put(PKCS9_OIDS[12], "RSAProprietary");
    OID_NAME_TABLE.put(PKCS9_OIDS[13], "SMIMESigningDesc");
    OID_NAME_TABLE.put(PKCS9_OIDS[14], "ExtensionRequest");
    OID_NAME_TABLE.put(PKCS9_OIDS[15], "SMIMECapability");
    OID_NAME_TABLE.put(PKCS9_OIDS[16], "SigningCertificate");
    OID_NAME_TABLE.put(PKCS9_OIDS[17], "SignatureTimestampToken");
    PKCS9_VALUE_TAGS = new Byte[][] { null, { new Byte(22) }, { new Byte(22), new Byte(19) }, { new Byte(6) }, { new Byte(4) }, { new Byte(23) }, { new Byte(48) }, { new Byte(19), new Byte(20) }, { new Byte(19), new Byte(20) }, { new Byte(49) }, { new Byte(48) }, null, null, null, { new Byte(48) }, { new Byte(48) }, { new Byte(48) }, { new Byte(48) } };
    VALUE_CLASSES = new Class[18];
    try
    {
      Class localClass = Class.forName("[Ljava.lang.String;");
      VALUE_CLASSES[0] = null;
      VALUE_CLASSES[1] = localClass;
      VALUE_CLASSES[2] = localClass;
      VALUE_CLASSES[3] = Class.forName("sun.security.util.ObjectIdentifier");
      VALUE_CLASSES[4] = BYTE_ARRAY_CLASS;
      VALUE_CLASSES[5] = Class.forName("java.util.Date");
      VALUE_CLASSES[6] = Class.forName("[Lsun.security.pkcs.SignerInfo;");
      VALUE_CLASSES[7] = Class.forName("java.lang.String");
      VALUE_CLASSES[8] = localClass;
      VALUE_CLASSES[9] = null;
      VALUE_CLASSES[10] = null;
      VALUE_CLASSES[11] = null;
      VALUE_CLASSES[12] = null;
      VALUE_CLASSES[13] = null;
      VALUE_CLASSES[14] = Class.forName("sun.security.x509.CertificateExtensions");
      VALUE_CLASSES[15] = null;
      VALUE_CLASSES[16] = null;
      VALUE_CLASSES[17] = BYTE_ARRAY_CLASS;
    }
    catch (ClassNotFoundException localClassNotFoundException2)
    {
      throw new ExceptionInInitializerError(localClassNotFoundException2.toString());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs\PKCS9Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */