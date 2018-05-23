package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class AlgorithmId
  implements Serializable, DerEncoder
{
  private static final long serialVersionUID = 7205873507486557157L;
  private ObjectIdentifier algid;
  private AlgorithmParameters algParams;
  private boolean constructedFromDer = true;
  protected DerValue params;
  private static boolean initOidTable = false;
  private static Map<String, ObjectIdentifier> oidTable;
  private static final Map<ObjectIdentifier, String> nameTable;
  public static final ObjectIdentifier MD2_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 2, 2 });
  public static final ObjectIdentifier MD5_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 2, 5 });
  public static final ObjectIdentifier SHA_oid = ObjectIdentifier.newInternal(new int[] { 1, 3, 14, 3, 2, 26 });
  public static final ObjectIdentifier SHA224_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 4 });
  public static final ObjectIdentifier SHA256_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 1 });
  public static final ObjectIdentifier SHA384_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 2 });
  public static final ObjectIdentifier SHA512_oid = ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 101, 3, 4, 2, 3 });
  private static final int[] DH_data = { 1, 2, 840, 113549, 1, 3, 1 };
  private static final int[] DH_PKIX_data = { 1, 2, 840, 10046, 2, 1 };
  private static final int[] DSA_OIW_data = { 1, 3, 14, 3, 2, 12 };
  private static final int[] DSA_PKIX_data = { 1, 2, 840, 10040, 4, 1 };
  private static final int[] RSA_data = { 2, 5, 8, 1, 1 };
  private static final int[] RSAEncryption_data = { 1, 2, 840, 113549, 1, 1, 1 };
  public static final ObjectIdentifier DH_oid;
  public static final ObjectIdentifier DH_PKIX_oid;
  public static final ObjectIdentifier DSA_oid;
  public static final ObjectIdentifier DSA_OIW_oid;
  public static final ObjectIdentifier EC_oid = oid(new int[] { 1, 2, 840, 10045, 2, 1 });
  public static final ObjectIdentifier ECDH_oid = oid(new int[] { 1, 3, 132, 1, 12 });
  public static final ObjectIdentifier RSA_oid;
  public static final ObjectIdentifier RSAEncryption_oid;
  public static final ObjectIdentifier AES_oid = oid(new int[] { 2, 16, 840, 1, 101, 3, 4, 1 });
  private static final int[] md2WithRSAEncryption_data = { 1, 2, 840, 113549, 1, 1, 2 };
  private static final int[] md5WithRSAEncryption_data = { 1, 2, 840, 113549, 1, 1, 4 };
  private static final int[] sha1WithRSAEncryption_data = { 1, 2, 840, 113549, 1, 1, 5 };
  private static final int[] sha1WithRSAEncryption_OIW_data = { 1, 3, 14, 3, 2, 29 };
  private static final int[] sha224WithRSAEncryption_data = { 1, 2, 840, 113549, 1, 1, 14 };
  private static final int[] sha256WithRSAEncryption_data = { 1, 2, 840, 113549, 1, 1, 11 };
  private static final int[] sha384WithRSAEncryption_data = { 1, 2, 840, 113549, 1, 1, 12 };
  private static final int[] sha512WithRSAEncryption_data = { 1, 2, 840, 113549, 1, 1, 13 };
  private static final int[] shaWithDSA_OIW_data = { 1, 3, 14, 3, 2, 13 };
  private static final int[] sha1WithDSA_OIW_data = { 1, 3, 14, 3, 2, 27 };
  private static final int[] dsaWithSHA1_PKIX_data = { 1, 2, 840, 10040, 4, 3 };
  public static final ObjectIdentifier md2WithRSAEncryption_oid;
  public static final ObjectIdentifier md5WithRSAEncryption_oid;
  public static final ObjectIdentifier sha1WithRSAEncryption_oid;
  public static final ObjectIdentifier sha1WithRSAEncryption_OIW_oid;
  public static final ObjectIdentifier sha224WithRSAEncryption_oid;
  public static final ObjectIdentifier sha256WithRSAEncryption_oid;
  public static final ObjectIdentifier sha384WithRSAEncryption_oid;
  public static final ObjectIdentifier sha512WithRSAEncryption_oid;
  public static final ObjectIdentifier shaWithDSA_OIW_oid;
  public static final ObjectIdentifier sha1WithDSA_OIW_oid;
  public static final ObjectIdentifier sha1WithDSA_oid;
  public static final ObjectIdentifier sha224WithDSA_oid = oid(new int[] { 2, 16, 840, 1, 101, 3, 4, 3, 1 });
  public static final ObjectIdentifier sha256WithDSA_oid = oid(new int[] { 2, 16, 840, 1, 101, 3, 4, 3, 2 });
  public static final ObjectIdentifier sha1WithECDSA_oid = oid(new int[] { 1, 2, 840, 10045, 4, 1 });
  public static final ObjectIdentifier sha224WithECDSA_oid = oid(new int[] { 1, 2, 840, 10045, 4, 3, 1 });
  public static final ObjectIdentifier sha256WithECDSA_oid = oid(new int[] { 1, 2, 840, 10045, 4, 3, 2 });
  public static final ObjectIdentifier sha384WithECDSA_oid = oid(new int[] { 1, 2, 840, 10045, 4, 3, 3 });
  public static final ObjectIdentifier sha512WithECDSA_oid = oid(new int[] { 1, 2, 840, 10045, 4, 3, 4 });
  public static final ObjectIdentifier specifiedWithECDSA_oid = oid(new int[] { 1, 2, 840, 10045, 4, 3 });
  public static final ObjectIdentifier pbeWithMD5AndDES_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 5, 3 });
  public static final ObjectIdentifier pbeWithMD5AndRC2_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 5, 6 });
  public static final ObjectIdentifier pbeWithSHA1AndDES_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 5, 10 });
  public static final ObjectIdentifier pbeWithSHA1AndRC2_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 5, 11 });
  public static ObjectIdentifier pbeWithSHA1AndDESede_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 12, 1, 3 });
  public static ObjectIdentifier pbeWithSHA1AndRC2_40_oid = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 12, 1, 6 });
  
  @Deprecated
  public AlgorithmId() {}
  
  public AlgorithmId(ObjectIdentifier paramObjectIdentifier)
  {
    algid = paramObjectIdentifier;
  }
  
  public AlgorithmId(ObjectIdentifier paramObjectIdentifier, AlgorithmParameters paramAlgorithmParameters)
  {
    algid = paramObjectIdentifier;
    algParams = paramAlgorithmParameters;
    constructedFromDer = false;
  }
  
  private AlgorithmId(ObjectIdentifier paramObjectIdentifier, DerValue paramDerValue)
    throws IOException
  {
    algid = paramObjectIdentifier;
    params = paramDerValue;
    if (params != null) {
      decodeParams();
    }
  }
  
  protected void decodeParams()
    throws IOException
  {
    String str = algid.toString();
    try
    {
      algParams = AlgorithmParameters.getInstance(str);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      algParams = null;
      return;
    }
    algParams.init(params.toByteArray());
  }
  
  public final void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    derEncode(paramDerOutputStream);
  }
  
  public void derEncode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream1.putOID(algid);
    if (!constructedFromDer) {
      if (algParams != null) {
        params = new DerValue(algParams.getEncoded());
      } else {
        params = null;
      }
    }
    if (params == null) {
      localDerOutputStream1.putNull();
    } else {
      localDerOutputStream1.putDerValue(params);
    }
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    paramOutputStream.write(localDerOutputStream2.toByteArray());
  }
  
  public final byte[] encode()
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    derEncode(localDerOutputStream);
    return localDerOutputStream.toByteArray();
  }
  
  public final ObjectIdentifier getOID()
  {
    return algid;
  }
  
  public String getName()
  {
    String str1 = (String)nameTable.get(algid);
    if (str1 != null) {
      return str1;
    }
    if ((params != null) && (algid.equals(specifiedWithECDSA_oid))) {
      try
      {
        AlgorithmId localAlgorithmId = parse(new DerValue(getEncodedParams()));
        String str2 = localAlgorithmId.getName();
        str1 = makeSigAlg(str2, "EC");
      }
      catch (IOException localIOException) {}
    }
    return str1 == null ? algid.toString() : str1;
  }
  
  public AlgorithmParameters getParameters()
  {
    return algParams;
  }
  
  public byte[] getEncodedParams()
    throws IOException
  {
    return params == null ? null : params.toByteArray();
  }
  
  public boolean equals(AlgorithmId paramAlgorithmId)
  {
    boolean bool = params == null ? false : params == null ? true : params.equals(params);
    return (algid.equals(algid)) && (bool);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof AlgorithmId)) {
      return equals((AlgorithmId)paramObject);
    }
    if ((paramObject instanceof ObjectIdentifier)) {
      return equals((ObjectIdentifier)paramObject);
    }
    return false;
  }
  
  public final boolean equals(ObjectIdentifier paramObjectIdentifier)
  {
    return algid.equals(paramObjectIdentifier);
  }
  
  public int hashCode()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(algid.toString());
    localStringBuilder.append(paramsToString());
    return localStringBuilder.toString().hashCode();
  }
  
  protected String paramsToString()
  {
    if (params == null) {
      return "";
    }
    if (algParams != null) {
      return algParams.toString();
    }
    return ", params unparsed";
  }
  
  public String toString()
  {
    return getName() + paramsToString();
  }
  
  public static AlgorithmId parse(DerValue paramDerValue)
    throws IOException
  {
    if (tag != 48) {
      throw new IOException("algid parse error, not a sequence");
    }
    DerInputStream localDerInputStream = paramDerValue.toDerInputStream();
    ObjectIdentifier localObjectIdentifier = localDerInputStream.getOID();
    DerValue localDerValue;
    if (localDerInputStream.available() == 0)
    {
      localDerValue = null;
    }
    else
    {
      localDerValue = localDerInputStream.getDerValue();
      if (tag == 5)
      {
        if (localDerValue.length() != 0) {
          throw new IOException("invalid NULL");
        }
        localDerValue = null;
      }
      if (localDerInputStream.available() != 0) {
        throw new IOException("Invalid AlgorithmIdentifier: extra data");
      }
    }
    return new AlgorithmId(localObjectIdentifier, localDerValue);
  }
  
  @Deprecated
  public static AlgorithmId getAlgorithmId(String paramString)
    throws NoSuchAlgorithmException
  {
    return get(paramString);
  }
  
  public static AlgorithmId get(String paramString)
    throws NoSuchAlgorithmException
  {
    ObjectIdentifier localObjectIdentifier;
    try
    {
      localObjectIdentifier = algOID(paramString);
    }
    catch (IOException localIOException)
    {
      throw new NoSuchAlgorithmException("Invalid ObjectIdentifier " + paramString);
    }
    if (localObjectIdentifier == null) {
      throw new NoSuchAlgorithmException("unrecognized algorithm name: " + paramString);
    }
    return new AlgorithmId(localObjectIdentifier);
  }
  
  public static AlgorithmId get(AlgorithmParameters paramAlgorithmParameters)
    throws NoSuchAlgorithmException
  {
    String str = paramAlgorithmParameters.getAlgorithm();
    ObjectIdentifier localObjectIdentifier;
    try
    {
      localObjectIdentifier = algOID(str);
    }
    catch (IOException localIOException)
    {
      throw new NoSuchAlgorithmException("Invalid ObjectIdentifier " + str);
    }
    if (localObjectIdentifier == null) {
      throw new NoSuchAlgorithmException("unrecognized algorithm name: " + str);
    }
    return new AlgorithmId(localObjectIdentifier, paramAlgorithmParameters);
  }
  
  private static ObjectIdentifier algOID(String paramString)
    throws IOException
  {
    if (paramString.indexOf('.') != -1)
    {
      if (paramString.startsWith("OID.")) {
        return new ObjectIdentifier(paramString.substring("OID.".length()));
      }
      return new ObjectIdentifier(paramString);
    }
    if (paramString.equalsIgnoreCase("MD5")) {
      return MD5_oid;
    }
    if (paramString.equalsIgnoreCase("MD2")) {
      return MD2_oid;
    }
    if ((paramString.equalsIgnoreCase("SHA")) || (paramString.equalsIgnoreCase("SHA1")) || (paramString.equalsIgnoreCase("SHA-1"))) {
      return SHA_oid;
    }
    if ((paramString.equalsIgnoreCase("SHA-256")) || (paramString.equalsIgnoreCase("SHA256"))) {
      return SHA256_oid;
    }
    if ((paramString.equalsIgnoreCase("SHA-384")) || (paramString.equalsIgnoreCase("SHA384"))) {
      return SHA384_oid;
    }
    if ((paramString.equalsIgnoreCase("SHA-512")) || (paramString.equalsIgnoreCase("SHA512"))) {
      return SHA512_oid;
    }
    if ((paramString.equalsIgnoreCase("SHA-224")) || (paramString.equalsIgnoreCase("SHA224"))) {
      return SHA224_oid;
    }
    if (paramString.equalsIgnoreCase("RSA")) {
      return RSAEncryption_oid;
    }
    if ((paramString.equalsIgnoreCase("Diffie-Hellman")) || (paramString.equalsIgnoreCase("DH"))) {
      return DH_oid;
    }
    if (paramString.equalsIgnoreCase("DSA")) {
      return DSA_oid;
    }
    if (paramString.equalsIgnoreCase("EC")) {
      return EC_oid;
    }
    if (paramString.equalsIgnoreCase("ECDH")) {
      return ECDH_oid;
    }
    if (paramString.equalsIgnoreCase("AES")) {
      return AES_oid;
    }
    if ((paramString.equalsIgnoreCase("MD5withRSA")) || (paramString.equalsIgnoreCase("MD5/RSA"))) {
      return md5WithRSAEncryption_oid;
    }
    if ((paramString.equalsIgnoreCase("MD2withRSA")) || (paramString.equalsIgnoreCase("MD2/RSA"))) {
      return md2WithRSAEncryption_oid;
    }
    if ((paramString.equalsIgnoreCase("SHAwithDSA")) || (paramString.equalsIgnoreCase("SHA1withDSA")) || (paramString.equalsIgnoreCase("SHA/DSA")) || (paramString.equalsIgnoreCase("SHA1/DSA")) || (paramString.equalsIgnoreCase("DSAWithSHA1")) || (paramString.equalsIgnoreCase("DSS")) || (paramString.equalsIgnoreCase("SHA-1/DSA"))) {
      return sha1WithDSA_oid;
    }
    if (paramString.equalsIgnoreCase("SHA224WithDSA")) {
      return sha224WithDSA_oid;
    }
    if (paramString.equalsIgnoreCase("SHA256WithDSA")) {
      return sha256WithDSA_oid;
    }
    if ((paramString.equalsIgnoreCase("SHA1WithRSA")) || (paramString.equalsIgnoreCase("SHA1/RSA"))) {
      return sha1WithRSAEncryption_oid;
    }
    if ((paramString.equalsIgnoreCase("SHA1withECDSA")) || (paramString.equalsIgnoreCase("ECDSA"))) {
      return sha1WithECDSA_oid;
    }
    if (paramString.equalsIgnoreCase("SHA224withECDSA")) {
      return sha224WithECDSA_oid;
    }
    if (paramString.equalsIgnoreCase("SHA256withECDSA")) {
      return sha256WithECDSA_oid;
    }
    if (paramString.equalsIgnoreCase("SHA384withECDSA")) {
      return sha384WithECDSA_oid;
    }
    if (paramString.equalsIgnoreCase("SHA512withECDSA")) {
      return sha512WithECDSA_oid;
    }
    if (!initOidTable)
    {
      Provider[] arrayOfProvider = Security.getProviders();
      for (int i = 0; i < arrayOfProvider.length; i++)
      {
        Enumeration localEnumeration = arrayOfProvider[i].keys();
        while (localEnumeration.hasMoreElements())
        {
          String str2 = (String)localEnumeration.nextElement();
          String str3 = str2.toUpperCase(Locale.ENGLISH);
          int j;
          if ((str3.startsWith("ALG.ALIAS")) && ((j = str3.indexOf("OID.", 0)) != -1))
          {
            j += "OID.".length();
            if (j == str2.length()) {
              break;
            }
            if (oidTable == null) {
              oidTable = new HashMap();
            }
            String str1 = str2.substring(j);
            String str4 = arrayOfProvider[i].getProperty(str2);
            if (str4 != null) {
              str4 = str4.toUpperCase(Locale.ENGLISH);
            }
            if ((str4 != null) && (oidTable.get(str4) == null)) {
              oidTable.put(str4, new ObjectIdentifier(str1));
            }
          }
        }
      }
      if (oidTable == null) {
        oidTable = Collections.emptyMap();
      }
      initOidTable = true;
    }
    return (ObjectIdentifier)oidTable.get(paramString.toUpperCase(Locale.ENGLISH));
  }
  
  private static ObjectIdentifier oid(int... paramVarArgs)
  {
    return ObjectIdentifier.newInternal(paramVarArgs);
  }
  
  public static String makeSigAlg(String paramString1, String paramString2)
  {
    paramString1 = paramString1.replace("-", "");
    if (paramString2.equalsIgnoreCase("EC")) {
      paramString2 = "ECDSA";
    }
    return paramString1 + "with" + paramString2;
  }
  
  public static String getEncAlgFromSigAlg(String paramString)
  {
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    int i = paramString.indexOf("WITH");
    String str = null;
    if (i > 0)
    {
      int j = paramString.indexOf("AND", i + 4);
      if (j > 0) {
        str = paramString.substring(i + 4, j);
      } else {
        str = paramString.substring(i + 4);
      }
      if (str.equalsIgnoreCase("ECDSA")) {
        str = "EC";
      }
    }
    return str;
  }
  
  public static String getDigAlgFromSigAlg(String paramString)
  {
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    int i = paramString.indexOf("WITH");
    if (i > 0) {
      return paramString.substring(0, i);
    }
    return null;
  }
  
  static
  {
    DH_oid = ObjectIdentifier.newInternal(DH_data);
    DH_PKIX_oid = ObjectIdentifier.newInternal(DH_PKIX_data);
    DSA_OIW_oid = ObjectIdentifier.newInternal(DSA_OIW_data);
    DSA_oid = ObjectIdentifier.newInternal(DSA_PKIX_data);
    RSA_oid = ObjectIdentifier.newInternal(RSA_data);
    RSAEncryption_oid = ObjectIdentifier.newInternal(RSAEncryption_data);
    md2WithRSAEncryption_oid = ObjectIdentifier.newInternal(md2WithRSAEncryption_data);
    md5WithRSAEncryption_oid = ObjectIdentifier.newInternal(md5WithRSAEncryption_data);
    sha1WithRSAEncryption_oid = ObjectIdentifier.newInternal(sha1WithRSAEncryption_data);
    sha1WithRSAEncryption_OIW_oid = ObjectIdentifier.newInternal(sha1WithRSAEncryption_OIW_data);
    sha224WithRSAEncryption_oid = ObjectIdentifier.newInternal(sha224WithRSAEncryption_data);
    sha256WithRSAEncryption_oid = ObjectIdentifier.newInternal(sha256WithRSAEncryption_data);
    sha384WithRSAEncryption_oid = ObjectIdentifier.newInternal(sha384WithRSAEncryption_data);
    sha512WithRSAEncryption_oid = ObjectIdentifier.newInternal(sha512WithRSAEncryption_data);
    shaWithDSA_OIW_oid = ObjectIdentifier.newInternal(shaWithDSA_OIW_data);
    sha1WithDSA_OIW_oid = ObjectIdentifier.newInternal(sha1WithDSA_OIW_data);
    sha1WithDSA_oid = ObjectIdentifier.newInternal(dsaWithSHA1_PKIX_data);
    nameTable = new HashMap();
    nameTable.put(MD5_oid, "MD5");
    nameTable.put(MD2_oid, "MD2");
    nameTable.put(SHA_oid, "SHA-1");
    nameTable.put(SHA224_oid, "SHA-224");
    nameTable.put(SHA256_oid, "SHA-256");
    nameTable.put(SHA384_oid, "SHA-384");
    nameTable.put(SHA512_oid, "SHA-512");
    nameTable.put(RSAEncryption_oid, "RSA");
    nameTable.put(RSA_oid, "RSA");
    nameTable.put(DH_oid, "Diffie-Hellman");
    nameTable.put(DH_PKIX_oid, "Diffie-Hellman");
    nameTable.put(DSA_oid, "DSA");
    nameTable.put(DSA_OIW_oid, "DSA");
    nameTable.put(EC_oid, "EC");
    nameTable.put(ECDH_oid, "ECDH");
    nameTable.put(AES_oid, "AES");
    nameTable.put(sha1WithECDSA_oid, "SHA1withECDSA");
    nameTable.put(sha224WithECDSA_oid, "SHA224withECDSA");
    nameTable.put(sha256WithECDSA_oid, "SHA256withECDSA");
    nameTable.put(sha384WithECDSA_oid, "SHA384withECDSA");
    nameTable.put(sha512WithECDSA_oid, "SHA512withECDSA");
    nameTable.put(md5WithRSAEncryption_oid, "MD5withRSA");
    nameTable.put(md2WithRSAEncryption_oid, "MD2withRSA");
    nameTable.put(sha1WithDSA_oid, "SHA1withDSA");
    nameTable.put(sha1WithDSA_OIW_oid, "SHA1withDSA");
    nameTable.put(shaWithDSA_OIW_oid, "SHA1withDSA");
    nameTable.put(sha224WithDSA_oid, "SHA224withDSA");
    nameTable.put(sha256WithDSA_oid, "SHA256withDSA");
    nameTable.put(sha1WithRSAEncryption_oid, "SHA1withRSA");
    nameTable.put(sha1WithRSAEncryption_OIW_oid, "SHA1withRSA");
    nameTable.put(sha224WithRSAEncryption_oid, "SHA224withRSA");
    nameTable.put(sha256WithRSAEncryption_oid, "SHA256withRSA");
    nameTable.put(sha384WithRSAEncryption_oid, "SHA384withRSA");
    nameTable.put(sha512WithRSAEncryption_oid, "SHA512withRSA");
    nameTable.put(pbeWithMD5AndDES_oid, "PBEWithMD5AndDES");
    nameTable.put(pbeWithMD5AndRC2_oid, "PBEWithMD5AndRC2");
    nameTable.put(pbeWithSHA1AndDES_oid, "PBEWithSHA1AndDES");
    nameTable.put(pbeWithSHA1AndRC2_oid, "PBEWithSHA1AndRC2");
    nameTable.put(pbeWithSHA1AndDESede_oid, "PBEWithSHA1AndDESede");
    nameTable.put(pbeWithSHA1AndRC2_40_oid, "PBEWithSHA1AndRC2_40");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\AlgorithmId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */