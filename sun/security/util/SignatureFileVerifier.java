package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.CodeSigner;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarException;
import java.util.jar.Manifest;
import sun.security.jca.Providers;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;

public class SignatureFileVerifier
{
  private static final Debug debug = Debug.getInstance("jar");
  private static final DisabledAlgorithmConstraints JAR_DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
  private ArrayList<CodeSigner[]> signerCache;
  private static final String ATTR_DIGEST = "-DIGEST-Manifest-Main-Attributes".toUpperCase(Locale.ENGLISH);
  private PKCS7 block;
  private byte[] sfBytes;
  private String name;
  private ManifestDigester md;
  private HashMap<String, MessageDigest> createdDigests;
  private boolean workaround = false;
  private CertificateFactory certificateFactory = null;
  private Map<String, Boolean> permittedAlgs = new HashMap();
  private Timestamp timestamp = null;
  private static final char[] hexc = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  
  public SignatureFileVerifier(ArrayList<CodeSigner[]> paramArrayList, ManifestDigester paramManifestDigester, String paramString, byte[] paramArrayOfByte)
    throws IOException, CertificateException
  {
    Object localObject1 = null;
    try
    {
      localObject1 = Providers.startJarVerification();
      block = new PKCS7(paramArrayOfByte);
      sfBytes = block.getContentInfo().getData();
      certificateFactory = CertificateFactory.getInstance("X509");
    }
    finally
    {
      Providers.stopJarVerification(localObject1);
    }
    name = paramString.substring(0, paramString.lastIndexOf('.')).toUpperCase(Locale.ENGLISH);
    md = paramManifestDigester;
    signerCache = paramArrayList;
  }
  
  public boolean needSignatureFileBytes()
  {
    return sfBytes == null;
  }
  
  public boolean needSignatureFile(String paramString)
  {
    return name.equalsIgnoreCase(paramString);
  }
  
  public void setSignatureFile(byte[] paramArrayOfByte)
  {
    sfBytes = paramArrayOfByte;
  }
  
  public static boolean isBlockOrSF(String paramString)
  {
    return (paramString.endsWith(".SF")) || (paramString.endsWith(".DSA")) || (paramString.endsWith(".RSA")) || (paramString.endsWith(".EC"));
  }
  
  public static boolean isSigningRelated(String paramString)
  {
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    if (!paramString.startsWith("META-INF/")) {
      return false;
    }
    paramString = paramString.substring(9);
    if (paramString.indexOf('/') != -1) {
      return false;
    }
    if ((isBlockOrSF(paramString)) || (paramString.equals("MANIFEST.MF"))) {
      return true;
    }
    if (paramString.startsWith("SIG-"))
    {
      int i = paramString.lastIndexOf('.');
      if (i != -1)
      {
        String str = paramString.substring(i + 1);
        if ((str.length() > 3) || (str.length() < 1)) {
          return false;
        }
        for (int j = 0; j < str.length(); j++)
        {
          int k = str.charAt(j);
          if (((k < 65) || (k > 90)) && ((k < 48) || (k > 57))) {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }
  
  private MessageDigest getDigest(String paramString)
    throws SignatureException
  {
    if (createdDigests == null) {
      createdDigests = new HashMap();
    }
    MessageDigest localMessageDigest = (MessageDigest)createdDigests.get(paramString);
    if (localMessageDigest == null) {
      try
      {
        localMessageDigest = MessageDigest.getInstance(paramString);
        createdDigests.put(paramString, localMessageDigest);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
    }
    return localMessageDigest;
  }
  
  public void process(Hashtable<String, CodeSigner[]> paramHashtable, List<Object> paramList)
    throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException
  {
    Object localObject1 = null;
    try
    {
      localObject1 = Providers.startJarVerification();
      processImpl(paramHashtable, paramList);
    }
    finally
    {
      Providers.stopJarVerification(localObject1);
    }
  }
  
  private void processImpl(Hashtable<String, CodeSigner[]> paramHashtable, List<Object> paramList)
    throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException
  {
    Manifest localManifest = new Manifest();
    localManifest.read(new ByteArrayInputStream(sfBytes));
    String str1 = localManifest.getMainAttributes().getValue(Attributes.Name.SIGNATURE_VERSION);
    if ((str1 == null) || (!str1.equalsIgnoreCase("1.0"))) {
      return;
    }
    SignerInfo[] arrayOfSignerInfo = block.verify(sfBytes);
    if (arrayOfSignerInfo == null) {
      throw new SecurityException("cannot verify signature block file " + name);
    }
    CodeSigner[] arrayOfCodeSigner = getSigners(arrayOfSignerInfo, block);
    if (arrayOfCodeSigner == null) {
      return;
    }
    String str2;
    for (str2 : arrayOfCodeSigner)
    {
      if (debug != null) {
        debug.println("Gathering timestamp for:  " + str2.toString());
      }
      if (str2.getTimestamp() == null)
      {
        timestamp = null;
        break;
      }
      if (timestamp == null) {
        timestamp = str2.getTimestamp();
      } else if (timestamp.getTimestamp().before(str2.getTimestamp().getTimestamp())) {
        timestamp = str2.getTimestamp();
      }
    }
    ??? = localManifest.getEntries().entrySet().iterator();
    boolean bool = verifyManifestHash(localManifest, md, paramList);
    if ((!bool) && (!verifyManifestMainAttrs(localManifest, md))) {
      throw new SecurityException("Invalid signature file digest for Manifest main attributes");
    }
    while (((Iterator)???).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)???).next();
      str2 = (String)localEntry.getKey();
      if ((bool) || (verifySection((Attributes)localEntry.getValue(), str2, md)))
      {
        if (str2.startsWith("./")) {
          str2 = str2.substring(2);
        }
        if (str2.startsWith("/")) {
          str2 = str2.substring(1);
        }
        updateSigners(arrayOfCodeSigner, paramHashtable, str2);
        if (debug != null) {
          debug.println("processSignature signed name = " + str2);
        }
      }
      else if (debug != null)
      {
        debug.println("processSignature unsigned name = " + str2);
      }
    }
    updateSigners(arrayOfCodeSigner, paramHashtable, "META-INF/MANIFEST.MF");
  }
  
  boolean permittedCheck(String paramString1, String paramString2)
  {
    Boolean localBoolean = (Boolean)permittedAlgs.get(paramString2);
    if (localBoolean == null)
    {
      try
      {
        JAR_DISABLED_CHECK.permits(paramString2, new ConstraintsParameters(timestamp));
      }
      catch (GeneralSecurityException localGeneralSecurityException)
      {
        permittedAlgs.put(paramString2, Boolean.FALSE);
        permittedAlgs.put(paramString1.toUpperCase(), Boolean.FALSE);
        if (debug != null) {
          if (localGeneralSecurityException.getMessage() != null)
          {
            debug.println(paramString1 + ":  " + localGeneralSecurityException.getMessage());
          }
          else
          {
            debug.println(paramString1 + ":  " + paramString2 + " was disabled, no exception msg given.");
            localGeneralSecurityException.printStackTrace();
          }
        }
        return false;
      }
      permittedAlgs.put(paramString2, Boolean.TRUE);
      return true;
    }
    return localBoolean.booleanValue();
  }
  
  String getWeakAlgorithms(String paramString)
  {
    String str1 = "";
    try
    {
      Iterator localIterator = permittedAlgs.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        if (str2.endsWith(paramString)) {
          str1 = str1 + str2.substring(0, str2.length() - paramString.length()) + " ";
        }
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      str1 = "Unknown Algorithm(s).  Error processing " + paramString + ".  " + localRuntimeException.getMessage();
    }
    if (str1.length() == 0) {
      return "Unknown Algorithm(s)";
    }
    return str1;
  }
  
  private boolean verifyManifestHash(Manifest paramManifest, ManifestDigester paramManifestDigester, List<Object> paramList)
    throws IOException, SignatureException
  {
    Attributes localAttributes = paramManifest.getMainAttributes();
    boolean bool = false;
    int i = 1;
    int j = 0;
    Iterator localIterator = localAttributes.entrySet().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Map.Entry)localIterator.next();
      String str1 = ((Map.Entry)localObject).getKey().toString();
      if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST-MANIFEST"))
      {
        String str2 = str1.substring(0, str1.length() - 16);
        j = 1;
        if (permittedCheck(str1, str2))
        {
          i = 0;
          paramList.add(str1);
          paramList.add(((Map.Entry)localObject).getValue());
          MessageDigest localMessageDigest = getDigest(str2);
          if (localMessageDigest != null)
          {
            byte[] arrayOfByte1 = paramManifestDigester.manifestDigest(localMessageDigest);
            byte[] arrayOfByte2 = Base64.getMimeDecoder().decode((String)((Map.Entry)localObject).getValue());
            if (debug != null)
            {
              debug.println("Signature File: Manifest digest " + str2);
              debug.println("  sigfile  " + toHex(arrayOfByte2));
              debug.println("  computed " + toHex(arrayOfByte1));
              debug.println();
            }
            if (MessageDigest.isEqual(arrayOfByte1, arrayOfByte2)) {
              bool = true;
            }
          }
        }
      }
    }
    if (debug != null)
    {
      debug.println("PermittedAlgs mapping: ");
      localIterator = permittedAlgs.keySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (String)localIterator.next();
        debug.println((String)localObject + " : " + ((Boolean)permittedAlgs.get(localObject)).toString());
      }
    }
    if ((j != 0) && (i != 0)) {
      throw new SignatureException("Manifest hash check failed (DIGEST-MANIFEST). Disabled algorithm(s) used: " + getWeakAlgorithms("-DIGEST-MANIFEST"));
    }
    return bool;
  }
  
  private boolean verifyManifestMainAttrs(Manifest paramManifest, ManifestDigester paramManifestDigester)
    throws IOException, SignatureException
  {
    Attributes localAttributes = paramManifest.getMainAttributes();
    boolean bool = true;
    int i = 1;
    int j = 0;
    Iterator localIterator = localAttributes.entrySet().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Map.Entry)localIterator.next();
      String str1 = ((Map.Entry)localObject).getKey().toString();
      if (str1.toUpperCase(Locale.ENGLISH).endsWith(ATTR_DIGEST))
      {
        String str2 = str1.substring(0, str1.length() - ATTR_DIGEST.length());
        j = 1;
        if (permittedCheck(str1, str2))
        {
          i = 0;
          MessageDigest localMessageDigest = getDigest(str2);
          if (localMessageDigest != null)
          {
            ManifestDigester.Entry localEntry = paramManifestDigester.get("Manifest-Main-Attributes", false);
            byte[] arrayOfByte1 = localEntry.digest(localMessageDigest);
            byte[] arrayOfByte2 = Base64.getMimeDecoder().decode((String)((Map.Entry)localObject).getValue());
            if (debug != null)
            {
              debug.println("Signature File: Manifest Main Attributes digest " + localMessageDigest.getAlgorithm());
              debug.println("  sigfile  " + toHex(arrayOfByte2));
              debug.println("  computed " + toHex(arrayOfByte1));
              debug.println();
            }
            if (!MessageDigest.isEqual(arrayOfByte1, arrayOfByte2))
            {
              bool = false;
              if (debug == null) {
                break;
              }
              debug.println("Verification of Manifest main attributes failed");
              debug.println();
              break;
            }
          }
        }
      }
    }
    if (debug != null)
    {
      debug.println("PermittedAlgs mapping: ");
      localIterator = permittedAlgs.keySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (String)localIterator.next();
        debug.println((String)localObject + " : " + ((Boolean)permittedAlgs.get(localObject)).toString());
      }
    }
    if ((j != 0) && (i != 0)) {
      throw new SignatureException("Manifest Main Attribute check failed (" + ATTR_DIGEST + ").  Disabled algorithm(s) used: " + getWeakAlgorithms(ATTR_DIGEST));
    }
    return bool;
  }
  
  private boolean verifySection(Attributes paramAttributes, String paramString, ManifestDigester paramManifestDigester)
    throws IOException, SignatureException
  {
    boolean bool = false;
    ManifestDigester.Entry localEntry = paramManifestDigester.get(paramString, block.isOldStyle());
    int i = 1;
    int j = 0;
    if (localEntry == null) {
      throw new SecurityException("no manifest section for signature file entry " + paramString);
    }
    Iterator localIterator;
    Object localObject;
    if (paramAttributes != null)
    {
      localIterator = paramAttributes.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (Map.Entry)localIterator.next();
        String str1 = ((Map.Entry)localObject).getKey().toString();
        if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST"))
        {
          String str2 = str1.substring(0, str1.length() - 7);
          j = 1;
          if (permittedCheck(str1, str2))
          {
            i = 0;
            MessageDigest localMessageDigest = getDigest(str2);
            if (localMessageDigest != null)
            {
              int k = 0;
              byte[] arrayOfByte1 = Base64.getMimeDecoder().decode((String)((Map.Entry)localObject).getValue());
              byte[] arrayOfByte2;
              if (workaround) {
                arrayOfByte2 = localEntry.digestWorkaround(localMessageDigest);
              } else {
                arrayOfByte2 = localEntry.digest(localMessageDigest);
              }
              if (debug != null)
              {
                debug.println("Signature Block File: " + paramString + " digest=" + localMessageDigest.getAlgorithm());
                debug.println("  expected " + toHex(arrayOfByte1));
                debug.println("  computed " + toHex(arrayOfByte2));
                debug.println();
              }
              if (MessageDigest.isEqual(arrayOfByte2, arrayOfByte1))
              {
                bool = true;
                k = 1;
              }
              else if (!workaround)
              {
                arrayOfByte2 = localEntry.digestWorkaround(localMessageDigest);
                if (MessageDigest.isEqual(arrayOfByte2, arrayOfByte1))
                {
                  if (debug != null)
                  {
                    debug.println("  re-computed " + toHex(arrayOfByte2));
                    debug.println();
                  }
                  workaround = true;
                  bool = true;
                  k = 1;
                }
              }
              if (k == 0) {
                throw new SecurityException("invalid " + localMessageDigest.getAlgorithm() + " signature file digest for " + paramString);
              }
            }
          }
        }
      }
    }
    if (debug != null)
    {
      debug.println("PermittedAlgs mapping: ");
      localIterator = permittedAlgs.keySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (String)localIterator.next();
        debug.println((String)localObject + " : " + ((Boolean)permittedAlgs.get(localObject)).toString());
      }
    }
    if ((j != 0) && (i != 0)) {
      throw new SignatureException("Manifest Main Attribute check failed (DIGEST).  Disabled algorithm(s) used: " + getWeakAlgorithms("DIGEST"));
    }
    return bool;
  }
  
  private CodeSigner[] getSigners(SignerInfo[] paramArrayOfSignerInfo, PKCS7 paramPKCS7)
    throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException
  {
    ArrayList localArrayList1 = null;
    for (int i = 0; i < paramArrayOfSignerInfo.length; i++)
    {
      SignerInfo localSignerInfo = paramArrayOfSignerInfo[i];
      ArrayList localArrayList2 = localSignerInfo.getCertificateChain(paramPKCS7);
      CertPath localCertPath = certificateFactory.generateCertPath(localArrayList2);
      if (localArrayList1 == null) {
        localArrayList1 = new ArrayList();
      }
      localArrayList1.add(new CodeSigner(localCertPath, localSignerInfo.getTimestamp()));
      if (debug != null) {
        debug.println("Signature Block Certificate: " + localArrayList2.get(0));
      }
    }
    if (localArrayList1 != null) {
      return (CodeSigner[])localArrayList1.toArray(new CodeSigner[localArrayList1.size()]);
    }
    return null;
  }
  
  static String toHex(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length * 2);
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      localStringBuilder.append(hexc[(paramArrayOfByte[i] >> 4 & 0xF)]);
      localStringBuilder.append(hexc[(paramArrayOfByte[i] & 0xF)]);
    }
    return localStringBuilder.toString();
  }
  
  static boolean contains(CodeSigner[] paramArrayOfCodeSigner, CodeSigner paramCodeSigner)
  {
    for (int i = 0; i < paramArrayOfCodeSigner.length; i++) {
      if (paramArrayOfCodeSigner[i].equals(paramCodeSigner)) {
        return true;
      }
    }
    return false;
  }
  
  static boolean isSubSet(CodeSigner[] paramArrayOfCodeSigner1, CodeSigner[] paramArrayOfCodeSigner2)
  {
    if (paramArrayOfCodeSigner2 == paramArrayOfCodeSigner1) {
      return true;
    }
    for (int i = 0; i < paramArrayOfCodeSigner1.length; i++) {
      if (!contains(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1[i])) {
        return false;
      }
    }
    return true;
  }
  
  static boolean matches(CodeSigner[] paramArrayOfCodeSigner1, CodeSigner[] paramArrayOfCodeSigner2, CodeSigner[] paramArrayOfCodeSigner3)
  {
    if ((paramArrayOfCodeSigner2 == null) && (paramArrayOfCodeSigner1 == paramArrayOfCodeSigner3)) {
      return true;
    }
    if ((paramArrayOfCodeSigner2 != null) && (!isSubSet(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1))) {
      return false;
    }
    if (!isSubSet(paramArrayOfCodeSigner3, paramArrayOfCodeSigner1)) {
      return false;
    }
    for (int i = 0; i < paramArrayOfCodeSigner1.length; i++)
    {
      int j = ((paramArrayOfCodeSigner2 != null) && (contains(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1[i]))) || (contains(paramArrayOfCodeSigner3, paramArrayOfCodeSigner1[i])) ? 1 : 0;
      if (j == 0) {
        return false;
      }
    }
    return true;
  }
  
  void updateSigners(CodeSigner[] paramArrayOfCodeSigner, Hashtable<String, CodeSigner[]> paramHashtable, String paramString)
  {
    CodeSigner[] arrayOfCodeSigner1 = (CodeSigner[])paramHashtable.get(paramString);
    CodeSigner[] arrayOfCodeSigner2;
    for (int i = signerCache.size() - 1; i != -1; i--)
    {
      arrayOfCodeSigner2 = (CodeSigner[])signerCache.get(i);
      if (matches(arrayOfCodeSigner2, arrayOfCodeSigner1, paramArrayOfCodeSigner))
      {
        paramHashtable.put(paramString, arrayOfCodeSigner2);
        return;
      }
    }
    if (arrayOfCodeSigner1 == null)
    {
      arrayOfCodeSigner2 = paramArrayOfCodeSigner;
    }
    else
    {
      arrayOfCodeSigner2 = new CodeSigner[arrayOfCodeSigner1.length + paramArrayOfCodeSigner.length];
      System.arraycopy(arrayOfCodeSigner1, 0, arrayOfCodeSigner2, 0, arrayOfCodeSigner1.length);
      System.arraycopy(paramArrayOfCodeSigner, 0, arrayOfCodeSigner2, arrayOfCodeSigner1.length, paramArrayOfCodeSigner.length);
    }
    signerCache.add(arrayOfCodeSigner2);
    paramHashtable.put(paramString, arrayOfCodeSigner2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\SignatureFileVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */