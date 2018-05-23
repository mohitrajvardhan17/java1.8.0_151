package sun.security.pkcs12;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore.Entry;
import java.security.KeyStore.Entry.Attribute;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PKCS12Attribute;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.x500.X500Principal;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public final class PKCS12KeyStore
  extends KeyStoreSpi
{
  public static final int VERSION_3 = 3;
  private static final String[] KEY_PROTECTION_ALGORITHM = { "keystore.pkcs12.keyProtectionAlgorithm", "keystore.PKCS12.keyProtectionAlgorithm" };
  private static final int MAX_ITERATION_COUNT = 5000000;
  private static final int PBE_ITERATION_COUNT = 50000;
  private static final int MAC_ITERATION_COUNT = 100000;
  private static final int SALT_LEN = 20;
  private static final String[] CORE_ATTRIBUTES = { "1.2.840.113549.1.9.20", "1.2.840.113549.1.9.21", "2.16.840.1.113894.746875.1.1" };
  private static final Debug debug = Debug.getInstance("pkcs12");
  private static final int[] keyBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 2 };
  private static final int[] certBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 3 };
  private static final int[] secretBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 5 };
  private static final int[] pkcs9Name = { 1, 2, 840, 113549, 1, 9, 20 };
  private static final int[] pkcs9KeyId = { 1, 2, 840, 113549, 1, 9, 21 };
  private static final int[] pkcs9certType = { 1, 2, 840, 113549, 1, 9, 22, 1 };
  private static final int[] pbeWithSHAAnd40BitRC2CBC = { 1, 2, 840, 113549, 1, 12, 1, 6 };
  private static final int[] pbeWithSHAAnd3KeyTripleDESCBC = { 1, 2, 840, 113549, 1, 12, 1, 3 };
  private static final int[] pbes2 = { 1, 2, 840, 113549, 1, 5, 13 };
  private static final int[] TrustedKeyUsage = { 2, 16, 840, 1, 113894, 746875, 1, 1 };
  private static final int[] AnyExtendedKeyUsage = { 2, 5, 29, 37, 0 };
  private static ObjectIdentifier PKCS8ShroudedKeyBag_OID;
  private static ObjectIdentifier CertBag_OID;
  private static ObjectIdentifier SecretBag_OID;
  private static ObjectIdentifier PKCS9FriendlyName_OID;
  private static ObjectIdentifier PKCS9LocalKeyId_OID;
  private static ObjectIdentifier PKCS9CertType_OID;
  private static ObjectIdentifier pbeWithSHAAnd40BitRC2CBC_OID;
  private static ObjectIdentifier pbeWithSHAAnd3KeyTripleDESCBC_OID;
  private static ObjectIdentifier pbes2_OID;
  private static ObjectIdentifier TrustedKeyUsage_OID;
  private static ObjectIdentifier[] AnyUsage;
  private int counter = 0;
  private int privateKeyCount = 0;
  private int secretKeyCount = 0;
  private int certificateCount = 0;
  private SecureRandom random;
  private Map<String, Entry> entries = Collections.synchronizedMap(new LinkedHashMap());
  private ArrayList<KeyEntry> keyList = new ArrayList();
  private LinkedHashMap<X500Principal, X509Certificate> certsMap = new LinkedHashMap();
  private ArrayList<CertEntry> certEntries = new ArrayList();
  
  public PKCS12KeyStore() {}
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar)
    throws NoSuchAlgorithmException, UnrecoverableKeyException
  {
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    Object localObject1 = null;
    if ((localEntry == null) || (!(localEntry instanceof KeyEntry))) {
      return null;
    }
    byte[] arrayOfByte1 = null;
    if ((localEntry instanceof PrivateKeyEntry)) {
      arrayOfByte1 = protectedPrivKey;
    } else if ((localEntry instanceof SecretKeyEntry)) {
      arrayOfByte1 = protectedSecretKey;
    } else {
      throw new UnrecoverableKeyException("Error locating key");
    }
    byte[] arrayOfByte2;
    ObjectIdentifier localObjectIdentifier;
    AlgorithmParameters localAlgorithmParameters;
    try
    {
      EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte1);
      arrayOfByte2 = localEncryptedPrivateKeyInfo.getEncryptedData();
      localObject2 = new DerValue(localEncryptedPrivateKeyInfo.getAlgorithm().encode());
      DerInputStream localDerInputStream = ((DerValue)localObject2).toDerInputStream();
      localObjectIdentifier = localDerInputStream.getOID();
      localAlgorithmParameters = parseAlgParameters(localObjectIdentifier, localDerInputStream);
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new UnrecoverableKeyException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo: " + localIOException);
      ((UnrecoverableKeyException)localObject2).initCause(localIOException);
      throw ((Throwable)localObject2);
    }
    try
    {
      int i = 0;
      if (localAlgorithmParameters != null)
      {
        PBEParameterSpec localPBEParameterSpec;
        try
        {
          localPBEParameterSpec = (PBEParameterSpec)localAlgorithmParameters.getParameterSpec(PBEParameterSpec.class);
        }
        catch (InvalidParameterSpecException localInvalidParameterSpecException)
        {
          throw new IOException("Invalid PBE algorithm parameters");
        }
        i = localPBEParameterSpec.getIterationCount();
        if (i > 5000000) {
          throw new IOException("PBE iteration count too large");
        }
      }
      byte[] arrayOfByte3;
      try
      {
        SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
        localObject3 = Cipher.getInstance(mapPBEParamsToAlgorithm(localObjectIdentifier, localAlgorithmParameters));
        ((Cipher)localObject3).init(2, localSecretKey, localAlgorithmParameters);
        arrayOfByte3 = ((Cipher)localObject3).doFinal(arrayOfByte2);
      }
      catch (Exception localException2)
      {
        while (paramArrayOfChar.length == 0) {
          paramArrayOfChar = new char[1];
        }
        throw localException2;
      }
      DerValue localDerValue = new DerValue(arrayOfByte3);
      Object localObject3 = localDerValue.toDerInputStream();
      int j = ((DerInputStream)localObject3).getInteger();
      DerValue[] arrayOfDerValue = ((DerInputStream)localObject3).getSequence(2);
      AlgorithmId localAlgorithmId = new AlgorithmId(arrayOfDerValue[0].getOID());
      String str = localAlgorithmId.getName();
      Object localObject4;
      Object localObject5;
      if ((localEntry instanceof PrivateKeyEntry))
      {
        localObject4 = KeyFactory.getInstance(str);
        localObject5 = new PKCS8EncodedKeySpec(arrayOfByte3);
        localObject1 = ((KeyFactory)localObject4).generatePrivate((KeySpec)localObject5);
        if (debug != null) {
          debug.println("Retrieved a protected private key at alias '" + paramString + "' (" + new AlgorithmId(localObjectIdentifier).getName() + " iterations: " + i + ")");
        }
      }
      else
      {
        localObject4 = ((DerInputStream)localObject3).getOctetString();
        localObject5 = new SecretKeySpec((byte[])localObject4, str);
        if (str.startsWith("PBE"))
        {
          SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance(str);
          KeySpec localKeySpec = localSecretKeyFactory.getKeySpec((SecretKey)localObject5, PBEKeySpec.class);
          localObject1 = localSecretKeyFactory.generateSecret(localKeySpec);
        }
        else
        {
          localObject1 = localObject5;
        }
        if (debug != null) {
          debug.println("Retrieved a protected secret key at alias '" + paramString + "' (" + new AlgorithmId(localObjectIdentifier).getName() + " iterations: " + i + ")");
        }
      }
    }
    catch (Exception localException1)
    {
      UnrecoverableKeyException localUnrecoverableKeyException = new UnrecoverableKeyException("Get Key failed: " + localException1.getMessage());
      localUnrecoverableKeyException.initCause(localException1);
      throw localUnrecoverableKeyException;
    }
    return (Key)localObject1;
  }
  
  public Certificate[] engineGetCertificateChain(String paramString)
  {
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if ((localEntry != null) && ((localEntry instanceof PrivateKeyEntry)))
    {
      if (chain == null) {
        return null;
      }
      if (debug != null) {
        debug.println("Retrieved a " + chain.length + "-certificate chain at alias '" + paramString + "'");
      }
      return (Certificate[])chain.clone();
    }
    return null;
  }
  
  public Certificate engineGetCertificate(String paramString)
  {
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (localEntry == null) {
      return null;
    }
    if (((localEntry instanceof CertEntry)) && (trustedKeyUsage != null))
    {
      if (debug != null) {
        if (Arrays.equals(AnyUsage, trustedKeyUsage)) {
          debug.println("Retrieved a certificate at alias '" + paramString + "' (trusted for any purpose)");
        } else {
          debug.println("Retrieved a certificate at alias '" + paramString + "' (trusted for limited purposes)");
        }
      }
      return cert;
    }
    if ((localEntry instanceof PrivateKeyEntry))
    {
      if (chain == null) {
        return null;
      }
      if (debug != null) {
        debug.println("Retrieved a certificate at alias '" + paramString + "'");
      }
      return chain[0];
    }
    return null;
  }
  
  public Date engineGetCreationDate(String paramString)
  {
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (localEntry != null) {
      return new Date(date.getTime());
    }
    return null;
  }
  
  public synchronized void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    KeyStore.PasswordProtection localPasswordProtection = new KeyStore.PasswordProtection(paramArrayOfChar);
    try
    {
      setKeyEntry(paramString, paramKey, localPasswordProtection, paramArrayOfCertificate, null);
      return;
    }
    finally
    {
      try
      {
        localPasswordProtection.destroy();
      }
      catch (DestroyFailedException localDestroyFailedException2) {}
    }
  }
  
  private void setKeyEntry(String paramString, Key paramKey, KeyStore.PasswordProtection paramPasswordProtection, Certificate[] paramArrayOfCertificate, Set<KeyStore.Entry.Attribute> paramSet)
    throws KeyStoreException
  {
    try
    {
      Object localObject2;
      Object localObject1;
      if ((paramKey instanceof PrivateKey))
      {
        localObject2 = new PrivateKeyEntry(null);
        date = new Date();
        if ((paramKey.getFormat().equals("PKCS#8")) || (paramKey.getFormat().equals("PKCS8")))
        {
          if (debug != null) {
            debug.println("Setting a protected private key at alias '" + paramString + "'");
          }
          protectedPrivKey = encryptPrivateKey(paramKey.getEncoded(), paramPasswordProtection);
        }
        else
        {
          throw new KeyStoreException("Private key is not encodedas PKCS#8");
        }
        if (paramArrayOfCertificate != null)
        {
          if ((paramArrayOfCertificate.length > 1) && (!validateChain(paramArrayOfCertificate))) {
            throw new KeyStoreException("Certificate chain is not valid");
          }
          chain = ((Certificate[])paramArrayOfCertificate.clone());
          certificateCount += paramArrayOfCertificate.length;
          if (debug != null) {
            debug.println("Setting a " + paramArrayOfCertificate.length + "-certificate chain at alias '" + paramString + "'");
          }
        }
        privateKeyCount += 1;
        localObject1 = localObject2;
      }
      else if ((paramKey instanceof SecretKey))
      {
        localObject2 = new SecretKeyEntry(null);
        date = new Date();
        DerOutputStream localDerOutputStream1 = new DerOutputStream();
        DerOutputStream localDerOutputStream2 = new DerOutputStream();
        localDerOutputStream2.putInteger(0);
        AlgorithmId localAlgorithmId = AlgorithmId.get(paramKey.getAlgorithm());
        localAlgorithmId.encode(localDerOutputStream2);
        localDerOutputStream2.putOctetString(paramKey.getEncoded());
        localDerOutputStream1.write((byte)48, localDerOutputStream2);
        protectedSecretKey = encryptPrivateKey(localDerOutputStream1.toByteArray(), paramPasswordProtection);
        if (debug != null) {
          debug.println("Setting a protected secret key at alias '" + paramString + "'");
        }
        secretKeyCount += 1;
        localObject1 = localObject2;
      }
      else
      {
        throw new KeyStoreException("Unsupported Key type");
      }
      attributes = new HashSet();
      if (paramSet != null) {
        attributes.addAll(paramSet);
      }
      keyId = ("Time " + date.getTime()).getBytes("UTF8");
      alias = paramString.toLowerCase(Locale.ENGLISH);
      entries.put(paramString.toLowerCase(Locale.ENGLISH), localObject1);
    }
    catch (Exception localException)
    {
      throw new KeyStoreException("Key protection  algorithm not found: " + localException, localException);
    }
  }
  
  public synchronized void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    try
    {
      new EncryptedPrivateKeyInfo(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new KeyStoreException("Private key is not stored as PKCS#8 EncryptedPrivateKeyInfo: " + localIOException, localIOException);
    }
    PrivateKeyEntry localPrivateKeyEntry = new PrivateKeyEntry(null);
    date = new Date();
    if (debug != null) {
      debug.println("Setting a protected private key at alias '" + paramString + "'");
    }
    try
    {
      keyId = ("Time " + date.getTime()).getBytes("UTF8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    alias = paramString.toLowerCase(Locale.ENGLISH);
    protectedPrivKey = ((byte[])paramArrayOfByte.clone());
    if (paramArrayOfCertificate != null)
    {
      if ((paramArrayOfCertificate.length > 1) && (!validateChain(paramArrayOfCertificate))) {
        throw new KeyStoreException("Certificate chain is not valid");
      }
      chain = ((Certificate[])paramArrayOfCertificate.clone());
      certificateCount += paramArrayOfCertificate.length;
      if (debug != null) {
        debug.println("Setting a " + chain.length + "-certificate chain at alias '" + paramString + "'");
      }
    }
    privateKeyCount += 1;
    entries.put(paramString.toLowerCase(Locale.ENGLISH), localPrivateKeyEntry);
  }
  
  private byte[] getSalt()
  {
    byte[] arrayOfByte = new byte[20];
    if (random == null) {
      random = new SecureRandom();
    }
    random.nextBytes(arrayOfByte);
    return arrayOfByte;
  }
  
  private AlgorithmParameters getPBEAlgorithmParameters(String paramString)
    throws IOException
  {
    AlgorithmParameters localAlgorithmParameters = null;
    PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(getSalt(), 50000);
    try
    {
      localAlgorithmParameters = AlgorithmParameters.getInstance(paramString);
      localAlgorithmParameters.init(localPBEParameterSpec);
    }
    catch (Exception localException)
    {
      throw new IOException("getPBEAlgorithmParameters failed: " + localException.getMessage(), localException);
    }
    return localAlgorithmParameters;
  }
  
  private AlgorithmParameters parseAlgParameters(ObjectIdentifier paramObjectIdentifier, DerInputStream paramDerInputStream)
    throws IOException
  {
    AlgorithmParameters localAlgorithmParameters = null;
    try
    {
      DerValue localDerValue;
      if (paramDerInputStream.available() == 0)
      {
        localDerValue = null;
      }
      else
      {
        localDerValue = paramDerInputStream.getDerValue();
        if (tag == 5) {
          localDerValue = null;
        }
      }
      if (localDerValue != null)
      {
        if (paramObjectIdentifier.equals(pbes2_OID)) {
          localAlgorithmParameters = AlgorithmParameters.getInstance("PBES2");
        } else {
          localAlgorithmParameters = AlgorithmParameters.getInstance("PBE");
        }
        localAlgorithmParameters.init(localDerValue.toByteArray());
      }
    }
    catch (Exception localException)
    {
      throw new IOException("parseAlgParameters failed: " + localException.getMessage(), localException);
    }
    return localAlgorithmParameters;
  }
  
  private SecretKey getPBEKey(char[] paramArrayOfChar)
    throws IOException
  {
    SecretKey localSecretKey = null;
    try
    {
      PBEKeySpec localPBEKeySpec = new PBEKeySpec(paramArrayOfChar);
      SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance("PBE");
      localSecretKey = localSecretKeyFactory.generateSecret(localPBEKeySpec);
      localPBEKeySpec.clearPassword();
    }
    catch (Exception localException)
    {
      throw new IOException("getSecretKey failed: " + localException.getMessage(), localException);
    }
    return localSecretKey;
  }
  
  private byte[] encryptPrivateKey(byte[] paramArrayOfByte, KeyStore.PasswordProtection paramPasswordProtection)
    throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException
  {
    byte[] arrayOfByte1 = null;
    try
    {
      String str = paramPasswordProtection.getProtectionAlgorithm();
      if (str != null)
      {
        localObject2 = paramPasswordProtection.getProtectionParameters();
        if (localObject2 != null)
        {
          localObject1 = AlgorithmParameters.getInstance(str);
          ((AlgorithmParameters)localObject1).init((AlgorithmParameterSpec)localObject2);
        }
        else
        {
          localObject1 = getPBEAlgorithmParameters(str);
        }
      }
      else
      {
        str = (String)AccessController.doPrivileged(new PrivilegedAction()
        {
          public String run()
          {
            String str = Security.getProperty(PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[0]);
            if (str == null) {
              str = Security.getProperty(PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[1]);
            }
            return str;
          }
        });
        if ((str == null) || (str.isEmpty())) {
          str = "PBEWithSHA1AndDESede";
        }
        localObject1 = getPBEAlgorithmParameters(str);
      }
      Object localObject2 = mapPBEAlgorithmToOID(str);
      if (localObject2 == null) {
        throw new IOException("PBE algorithm '" + str + " 'is not supported for key entry protection");
      }
      SecretKey localSecretKey = getPBEKey(paramPasswordProtection.getPassword());
      Cipher localCipher = Cipher.getInstance(str);
      localCipher.init(1, localSecretKey, (AlgorithmParameters)localObject1);
      byte[] arrayOfByte2 = localCipher.doFinal(paramArrayOfByte);
      AlgorithmId localAlgorithmId = new AlgorithmId((ObjectIdentifier)localObject2, localCipher.getParameters());
      if (debug != null) {
        debug.println("  (Cipher algorithm: " + localCipher.getAlgorithm() + ")");
      }
      EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(localAlgorithmId, arrayOfByte2);
      arrayOfByte1 = localEncryptedPrivateKeyInfo.getEncoded();
    }
    catch (Exception localException)
    {
      Object localObject1 = new UnrecoverableKeyException("Encrypt Private Key failed: " + localException.getMessage());
      ((UnrecoverableKeyException)localObject1).initCause(localException);
      throw ((Throwable)localObject1);
    }
    return arrayOfByte1;
  }
  
  private static ObjectIdentifier mapPBEAlgorithmToOID(String paramString)
    throws NoSuchAlgorithmException
  {
    if (paramString.toLowerCase(Locale.ENGLISH).startsWith("pbewithhmacsha")) {
      return pbes2_OID;
    }
    return AlgorithmId.get(paramString).getOID();
  }
  
  private static String mapPBEParamsToAlgorithm(ObjectIdentifier paramObjectIdentifier, AlgorithmParameters paramAlgorithmParameters)
    throws NoSuchAlgorithmException
  {
    if ((paramObjectIdentifier.equals(pbes2_OID)) && (paramAlgorithmParameters != null)) {
      return paramAlgorithmParameters.toString();
    }
    return paramObjectIdentifier.toString();
  }
  
  public synchronized void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
    throws KeyStoreException
  {
    setCertEntry(paramString, paramCertificate, null);
  }
  
  private void setCertEntry(String paramString, Certificate paramCertificate, Set<KeyStore.Entry.Attribute> paramSet)
    throws KeyStoreException
  {
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if ((localEntry != null) && ((localEntry instanceof KeyEntry))) {
      throw new KeyStoreException("Cannot overwrite own certificate");
    }
    CertEntry localCertEntry = new CertEntry((X509Certificate)paramCertificate, null, paramString, AnyUsage, paramSet);
    certificateCount += 1;
    entries.put(paramString, localCertEntry);
    if (debug != null) {
      debug.println("Setting a trusted certificate at alias '" + paramString + "'");
    }
  }
  
  public synchronized void engineDeleteEntry(String paramString)
    throws KeyStoreException
  {
    if (debug != null) {
      debug.println("Removing entry at alias '" + paramString + "'");
    }
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if ((localEntry instanceof PrivateKeyEntry))
    {
      PrivateKeyEntry localPrivateKeyEntry = (PrivateKeyEntry)localEntry;
      if (chain != null) {
        certificateCount -= chain.length;
      }
      privateKeyCount -= 1;
    }
    else if ((localEntry instanceof CertEntry))
    {
      certificateCount -= 1;
    }
    else if ((localEntry instanceof SecretKeyEntry))
    {
      secretKeyCount -= 1;
    }
    entries.remove(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public Enumeration<String> engineAliases()
  {
    return Collections.enumeration(entries.keySet());
  }
  
  public boolean engineContainsAlias(String paramString)
  {
    return entries.containsKey(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public int engineSize()
  {
    return entries.size();
  }
  
  public boolean engineIsKeyEntry(String paramString)
  {
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    return (localEntry != null) && ((localEntry instanceof KeyEntry));
  }
  
  public boolean engineIsCertificateEntry(String paramString)
  {
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    return (localEntry != null) && ((localEntry instanceof CertEntry)) && (trustedKeyUsage != null);
  }
  
  public boolean engineEntryInstanceOf(String paramString, Class<? extends KeyStore.Entry> paramClass)
  {
    if (paramClass == KeyStore.TrustedCertificateEntry.class) {
      return engineIsCertificateEntry(paramString);
    }
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (paramClass == KeyStore.PrivateKeyEntry.class) {
      return (localEntry != null) && ((localEntry instanceof PrivateKeyEntry));
    }
    if (paramClass == KeyStore.SecretKeyEntry.class) {
      return (localEntry != null) && ((localEntry instanceof SecretKeyEntry));
    }
    return false;
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate)
  {
    Object localObject = null;
    Enumeration localEnumeration = engineAliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      Entry localEntry = (Entry)entries.get(str);
      if ((localEntry instanceof PrivateKeyEntry))
      {
        if (chain != null) {
          localObject = chain[0];
        }
      }
      else
      {
        if ((!(localEntry instanceof CertEntry)) || (trustedKeyUsage == null)) {
          continue;
        }
        localObject = cert;
      }
      if ((localObject != null) && (((Certificate)localObject).equals(paramCertificate))) {
        return str;
      }
    }
    return null;
  }
  
  public synchronized void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    if (paramArrayOfChar == null) {
      throw new IllegalArgumentException("password can't be null");
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(3);
    byte[] arrayOfByte1 = localDerOutputStream2.toByteArray();
    localDerOutputStream1.write(arrayOfByte1);
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    DerOutputStream localDerOutputStream4 = new DerOutputStream();
    if ((privateKeyCount > 0) || (secretKeyCount > 0))
    {
      if (debug != null) {
        debug.println("Storing " + (privateKeyCount + secretKeyCount) + " protected key(s) in a PKCS#7 data");
      }
      localObject1 = createSafeContent();
      localObject2 = new ContentInfo((byte[])localObject1);
      ((ContentInfo)localObject2).encode(localDerOutputStream4);
    }
    if (certificateCount > 0)
    {
      if (debug != null) {
        debug.println("Storing " + certificateCount + " certificate(s) in a PKCS#7 encryptedData");
      }
      localObject1 = createEncryptedData(paramArrayOfChar);
      localObject2 = new ContentInfo(ContentInfo.ENCRYPTED_DATA_OID, new DerValue((byte[])localObject1));
      ((ContentInfo)localObject2).encode(localDerOutputStream4);
    }
    Object localObject1 = new DerOutputStream();
    ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream4);
    Object localObject2 = ((DerOutputStream)localObject1).toByteArray();
    ContentInfo localContentInfo = new ContentInfo((byte[])localObject2);
    localContentInfo.encode(localDerOutputStream3);
    byte[] arrayOfByte2 = localDerOutputStream3.toByteArray();
    localDerOutputStream1.write(arrayOfByte2);
    byte[] arrayOfByte3 = calculateMac(paramArrayOfChar, (byte[])localObject2);
    localDerOutputStream1.write(arrayOfByte3);
    DerOutputStream localDerOutputStream5 = new DerOutputStream();
    localDerOutputStream5.write((byte)48, localDerOutputStream1);
    byte[] arrayOfByte4 = localDerOutputStream5.toByteArray();
    paramOutputStream.write(arrayOfByte4);
    paramOutputStream.flush();
  }
  
  public KeyStore.Entry engineGetEntry(String paramString, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException
  {
    if (!engineContainsAlias(paramString)) {
      return null;
    }
    Entry localEntry = (Entry)entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (paramProtectionParameter == null) {
      if (engineIsCertificateEntry(paramString))
      {
        if (((localEntry instanceof CertEntry)) && (trustedKeyUsage != null))
        {
          if (debug != null) {
            debug.println("Retrieved a trusted certificate at alias '" + paramString + "'");
          }
          return new KeyStore.TrustedCertificateEntry(cert, getAttributes(localEntry));
        }
      }
      else {
        throw new UnrecoverableKeyException("requested entry requires a password");
      }
    }
    if ((paramProtectionParameter instanceof KeyStore.PasswordProtection))
    {
      if (engineIsCertificateEntry(paramString)) {
        throw new UnsupportedOperationException("trusted certificate entries are not password-protected");
      }
      if (engineIsKeyEntry(paramString))
      {
        KeyStore.PasswordProtection localPasswordProtection = (KeyStore.PasswordProtection)paramProtectionParameter;
        char[] arrayOfChar = localPasswordProtection.getPassword();
        Key localKey = engineGetKey(paramString, arrayOfChar);
        if ((localKey instanceof PrivateKey))
        {
          Certificate[] arrayOfCertificate = engineGetCertificateChain(paramString);
          return new KeyStore.PrivateKeyEntry((PrivateKey)localKey, arrayOfCertificate, getAttributes(localEntry));
        }
        if ((localKey instanceof SecretKey)) {
          return new KeyStore.SecretKeyEntry((SecretKey)localKey, getAttributes(localEntry));
        }
      }
      else if (!engineIsKeyEntry(paramString))
      {
        throw new UnsupportedOperationException("untrusted certificate entries are not password-protected");
      }
    }
    throw new UnsupportedOperationException();
  }
  
  public synchronized void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException
  {
    if ((paramProtectionParameter != null) && (!(paramProtectionParameter instanceof KeyStore.PasswordProtection))) {
      throw new KeyStoreException("unsupported protection parameter");
    }
    KeyStore.PasswordProtection localPasswordProtection = null;
    if (paramProtectionParameter != null) {
      localPasswordProtection = (KeyStore.PasswordProtection)paramProtectionParameter;
    }
    Object localObject;
    if ((paramEntry instanceof KeyStore.TrustedCertificateEntry))
    {
      if ((paramProtectionParameter != null) && (localPasswordProtection.getPassword() != null)) {
        throw new KeyStoreException("trusted certificate entries are not password-protected");
      }
      localObject = (KeyStore.TrustedCertificateEntry)paramEntry;
      setCertEntry(paramString, ((KeyStore.TrustedCertificateEntry)localObject).getTrustedCertificate(), ((KeyStore.TrustedCertificateEntry)localObject).getAttributes());
      return;
    }
    if ((paramEntry instanceof KeyStore.PrivateKeyEntry))
    {
      if ((localPasswordProtection == null) || (localPasswordProtection.getPassword() == null)) {
        throw new KeyStoreException("non-null password required to create PrivateKeyEntry");
      }
      localObject = (KeyStore.PrivateKeyEntry)paramEntry;
      setKeyEntry(paramString, ((KeyStore.PrivateKeyEntry)localObject).getPrivateKey(), localPasswordProtection, ((KeyStore.PrivateKeyEntry)localObject).getCertificateChain(), ((KeyStore.PrivateKeyEntry)localObject).getAttributes());
      return;
    }
    if ((paramEntry instanceof KeyStore.SecretKeyEntry))
    {
      if ((localPasswordProtection == null) || (localPasswordProtection.getPassword() == null)) {
        throw new KeyStoreException("non-null password required to create SecretKeyEntry");
      }
      localObject = (KeyStore.SecretKeyEntry)paramEntry;
      setKeyEntry(paramString, ((KeyStore.SecretKeyEntry)localObject).getSecretKey(), localPasswordProtection, (Certificate[])null, ((KeyStore.SecretKeyEntry)localObject).getAttributes());
      return;
    }
    throw new KeyStoreException("unsupported entry type: " + paramEntry.getClass().getName());
  }
  
  private Set<KeyStore.Entry.Attribute> getAttributes(Entry paramEntry)
  {
    if (attributes == null) {
      attributes = new HashSet();
    }
    attributes.add(new PKCS12Attribute(PKCS9FriendlyName_OID.toString(), alias));
    byte[] arrayOfByte = keyId;
    if (arrayOfByte != null) {
      attributes.add(new PKCS12Attribute(PKCS9LocalKeyId_OID.toString(), Debug.toString(arrayOfByte)));
    }
    if ((paramEntry instanceof CertEntry))
    {
      ObjectIdentifier[] arrayOfObjectIdentifier = trustedKeyUsage;
      if (arrayOfObjectIdentifier != null) {
        if (arrayOfObjectIdentifier.length == 1) {
          attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID.toString(), arrayOfObjectIdentifier[0].toString()));
        } else {
          attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID.toString(), Arrays.toString(arrayOfObjectIdentifier)));
        }
      }
    }
    return attributes;
  }
  
  private byte[] generateHash(byte[] paramArrayOfByte)
    throws IOException
  {
    byte[] arrayOfByte = null;
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA1");
      localMessageDigest.update(paramArrayOfByte);
      arrayOfByte = localMessageDigest.digest();
    }
    catch (Exception localException)
    {
      throw new IOException("generateHash failed: " + localException, localException);
    }
    return arrayOfByte;
  }
  
  private byte[] calculateMac(char[] paramArrayOfChar, byte[] paramArrayOfByte)
    throws IOException
  {
    byte[] arrayOfByte1 = null;
    String str = "SHA1";
    try
    {
      byte[] arrayOfByte2 = getSalt();
      Mac localMac = Mac.getInstance("HmacPBESHA1");
      PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(arrayOfByte2, 100000);
      SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
      localMac.init(localSecretKey, localPBEParameterSpec);
      localMac.update(paramArrayOfByte);
      byte[] arrayOfByte3 = localMac.doFinal();
      MacData localMacData = new MacData(str, arrayOfByte3, arrayOfByte2, 100000);
      DerOutputStream localDerOutputStream = new DerOutputStream();
      localDerOutputStream.write(localMacData.getEncoded());
      arrayOfByte1 = localDerOutputStream.toByteArray();
    }
    catch (Exception localException)
    {
      throw new IOException("calculateMac failed: " + localException, localException);
    }
    return arrayOfByte1;
  }
  
  private boolean validateChain(Certificate[] paramArrayOfCertificate)
  {
    for (int i = 0; i < paramArrayOfCertificate.length - 1; i++)
    {
      X500Principal localX500Principal1 = ((X509Certificate)paramArrayOfCertificate[i]).getIssuerX500Principal();
      X500Principal localX500Principal2 = ((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectX500Principal();
      if (!localX500Principal1.equals(localX500Principal2)) {
        return false;
      }
    }
    HashSet localHashSet = new HashSet(Arrays.asList(paramArrayOfCertificate));
    return localHashSet.size() == paramArrayOfCertificate.length;
  }
  
  private byte[] getBagAttributes(String paramString, byte[] paramArrayOfByte, Set<KeyStore.Entry.Attribute> paramSet)
    throws IOException
  {
    return getBagAttributes(paramString, paramArrayOfByte, null, paramSet);
  }
  
  private byte[] getBagAttributes(String paramString, byte[] paramArrayOfByte, ObjectIdentifier[] paramArrayOfObjectIdentifier, Set<KeyStore.Entry.Attribute> paramSet)
    throws IOException
  {
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    byte[] arrayOfByte3 = null;
    if ((paramString == null) && (paramArrayOfByte == null) && (arrayOfByte3 == null)) {
      return null;
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    Object localObject1;
    Object localObject2;
    if (paramString != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putOID(PKCS9FriendlyName_OID);
      localObject1 = new DerOutputStream();
      localObject2 = new DerOutputStream();
      ((DerOutputStream)localObject1).putBMPString(paramString);
      localDerOutputStream2.write((byte)49, (DerOutputStream)localObject1);
      ((DerOutputStream)localObject2).write((byte)48, localDerOutputStream2);
      arrayOfByte2 = ((DerOutputStream)localObject2).toByteArray();
    }
    if (paramArrayOfByte != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putOID(PKCS9LocalKeyId_OID);
      localObject1 = new DerOutputStream();
      localObject2 = new DerOutputStream();
      ((DerOutputStream)localObject1).putOctetString(paramArrayOfByte);
      localDerOutputStream2.write((byte)49, (DerOutputStream)localObject1);
      ((DerOutputStream)localObject2).write((byte)48, localDerOutputStream2);
      arrayOfByte1 = ((DerOutputStream)localObject2).toByteArray();
    }
    if (paramArrayOfObjectIdentifier != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putOID(TrustedKeyUsage_OID);
      localObject1 = new DerOutputStream();
      localObject2 = new DerOutputStream();
      for (ObjectIdentifier localObjectIdentifier : paramArrayOfObjectIdentifier) {
        ((DerOutputStream)localObject1).putOID(localObjectIdentifier);
      }
      localDerOutputStream2.write((byte)49, (DerOutputStream)localObject1);
      ((DerOutputStream)localObject2).write((byte)48, localDerOutputStream2);
      arrayOfByte3 = ((DerOutputStream)localObject2).toByteArray();
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    if (arrayOfByte2 != null) {
      localDerOutputStream2.write(arrayOfByte2);
    }
    if (arrayOfByte1 != null) {
      localDerOutputStream2.write(arrayOfByte1);
    }
    if (arrayOfByte3 != null) {
      localDerOutputStream2.write(arrayOfByte3);
    }
    if (paramSet != null)
    {
      localObject1 = paramSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (KeyStore.Entry.Attribute)((Iterator)localObject1).next();
        ??? = ((KeyStore.Entry.Attribute)localObject2).getName();
        if ((!CORE_ATTRIBUTES[0].equals(???)) && (!CORE_ATTRIBUTES[1].equals(???)) && (!CORE_ATTRIBUTES[2].equals(???))) {
          localDerOutputStream2.write(((PKCS12Attribute)localObject2).getEncoded());
        }
      }
    }
    localDerOutputStream1.write((byte)49, localDerOutputStream2);
    return localDerOutputStream1.toByteArray();
  }
  
  private byte[] createEncryptedData(char[] paramArrayOfChar)
    throws CertificateException, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    Object localObject1 = engineAliases();
    while (((Enumeration)localObject1).hasMoreElements())
    {
      localObject2 = (String)((Enumeration)localObject1).nextElement();
      localObject3 = (Entry)entries.get(localObject2);
      if ((localObject3 instanceof PrivateKeyEntry))
      {
        PrivateKeyEntry localPrivateKeyEntry = (PrivateKeyEntry)localObject3;
        if (chain != null) {
          localObject4 = chain;
        } else {
          localObject4 = new Certificate[0];
        }
      }
      else if ((localObject3 instanceof CertEntry))
      {
        localObject4 = new Certificate[] { cert };
      }
      else
      {
        localObject4 = new Certificate[0];
      }
      for (int i = 0; i < localObject4.length; i++)
      {
        DerOutputStream localDerOutputStream3 = new DerOutputStream();
        localDerOutputStream3.putOID(CertBag_OID);
        DerOutputStream localDerOutputStream4 = new DerOutputStream();
        localDerOutputStream4.putOID(PKCS9CertType_OID);
        DerOutputStream localDerOutputStream5 = new DerOutputStream();
        X509Certificate localX509Certificate = (X509Certificate)localObject4[i];
        localDerOutputStream5.putOctetString(localX509Certificate.getEncoded());
        localDerOutputStream4.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream5);
        DerOutputStream localDerOutputStream6 = new DerOutputStream();
        localDerOutputStream6.write((byte)48, localDerOutputStream4);
        byte[] arrayOfByte1 = localDerOutputStream6.toByteArray();
        DerOutputStream localDerOutputStream7 = new DerOutputStream();
        localDerOutputStream7.write(arrayOfByte1);
        localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream7);
        byte[] arrayOfByte2 = null;
        if (i == 0)
        {
          Object localObject5;
          if ((localObject3 instanceof KeyEntry))
          {
            localObject5 = (KeyEntry)localObject3;
            arrayOfByte2 = getBagAttributes(alias, keyId, attributes);
          }
          else
          {
            localObject5 = (CertEntry)localObject3;
            arrayOfByte2 = getBagAttributes(alias, keyId, trustedKeyUsage, attributes);
          }
        }
        else
        {
          arrayOfByte2 = getBagAttributes(localX509Certificate.getSubjectX500Principal().getName(), null, attributes);
        }
        if (arrayOfByte2 != null) {
          localDerOutputStream3.write(arrayOfByte2);
        }
        localDerOutputStream1.write((byte)48, localDerOutputStream3);
      }
    }
    localObject1 = new DerOutputStream();
    ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream1);
    Object localObject2 = ((DerOutputStream)localObject1).toByteArray();
    Object localObject3 = encryptContent((byte[])localObject2, paramArrayOfChar);
    Object localObject4 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    ((DerOutputStream)localObject4).putInteger(0);
    ((DerOutputStream)localObject4).write((byte[])localObject3);
    localDerOutputStream2.write((byte)48, (DerOutputStream)localObject4);
    return localDerOutputStream2.toByteArray();
  }
  
  private byte[] createSafeContent()
    throws CertificateException, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    Object localObject1 = engineAliases();
    while (((Enumeration)localObject1).hasMoreElements())
    {
      String str = (String)((Enumeration)localObject1).nextElement();
      Entry localEntry = (Entry)entries.get(str);
      if ((localEntry != null) && ((localEntry instanceof KeyEntry)))
      {
        DerOutputStream localDerOutputStream2 = new DerOutputStream();
        KeyEntry localKeyEntry = (KeyEntry)localEntry;
        Object localObject3;
        DerOutputStream localDerOutputStream3;
        if ((localKeyEntry instanceof PrivateKeyEntry))
        {
          localDerOutputStream2.putOID(PKCS8ShroudedKeyBag_OID);
          localObject2 = protectedPrivKey;
          localObject3 = null;
          try
          {
            localObject3 = new EncryptedPrivateKeyInfo((byte[])localObject2);
          }
          catch (IOException localIOException)
          {
            throw new IOException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo" + localIOException.getMessage());
          }
          localDerOutputStream3 = new DerOutputStream();
          localDerOutputStream3.write(((EncryptedPrivateKeyInfo)localObject3).getEncoded());
          localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream3);
        }
        else
        {
          if (!(localKeyEntry instanceof SecretKeyEntry)) {
            continue;
          }
          localDerOutputStream2.putOID(SecretBag_OID);
          localObject2 = new DerOutputStream();
          ((DerOutputStream)localObject2).putOID(PKCS8ShroudedKeyBag_OID);
          localObject3 = new DerOutputStream();
          ((DerOutputStream)localObject3).putOctetString(protectedSecretKey);
          ((DerOutputStream)localObject2).write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), (DerOutputStream)localObject3);
          localDerOutputStream3 = new DerOutputStream();
          localDerOutputStream3.write((byte)48, (DerOutputStream)localObject2);
          byte[] arrayOfByte = localDerOutputStream3.toByteArray();
          DerOutputStream localDerOutputStream4 = new DerOutputStream();
          localDerOutputStream4.write(arrayOfByte);
          localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream4);
        }
        Object localObject2 = getBagAttributes(str, keyId, attributes);
        localDerOutputStream2.write((byte[])localObject2);
        localDerOutputStream1.write((byte)48, localDerOutputStream2);
      }
    }
    localObject1 = new DerOutputStream();
    ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream1);
    return ((DerOutputStream)localObject1).toByteArray();
  }
  
  private byte[] encryptContent(byte[] paramArrayOfByte, char[] paramArrayOfChar)
    throws IOException
  {
    byte[] arrayOfByte1 = null;
    AlgorithmParameters localAlgorithmParameters = getPBEAlgorithmParameters("PBEWithSHA1AndRC2_40");
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    AlgorithmId localAlgorithmId = new AlgorithmId(pbeWithSHAAnd40BitRC2CBC_OID, localAlgorithmParameters);
    localAlgorithmId.encode(localDerOutputStream1);
    byte[] arrayOfByte2 = localDerOutputStream1.toByteArray();
    try
    {
      SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
      localObject = Cipher.getInstance("PBEWithSHA1AndRC2_40");
      ((Cipher)localObject).init(1, localSecretKey, localAlgorithmParameters);
      arrayOfByte1 = ((Cipher)localObject).doFinal(paramArrayOfByte);
      if (debug != null) {
        debug.println("  (Cipher algorithm: " + ((Cipher)localObject).getAlgorithm() + ")");
      }
    }
    catch (Exception localException)
    {
      throw new IOException("Failed to encrypt safe contents entry: " + localException, localException);
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putOID(ContentInfo.DATA_OID);
    localDerOutputStream2.write(arrayOfByte2);
    Object localObject = new DerOutputStream();
    ((DerOutputStream)localObject).putOctetString(arrayOfByte1);
    localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)0), (DerOutputStream)localObject);
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write((byte)48, localDerOutputStream2);
    return localDerOutputStream3.toByteArray();
  }
  
  public synchronized void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    if (paramInputStream == null) {
      return;
    }
    counter = 0;
    DerValue localDerValue = new DerValue(paramInputStream);
    DerInputStream localDerInputStream1 = localDerValue.toDerInputStream();
    int i = localDerInputStream1.getInteger();
    if (i != 3) {
      throw new IOException("PKCS12 keystore not in version 3 format");
    }
    entries.clear();
    ContentInfo localContentInfo = new ContentInfo(localDerInputStream1);
    ObjectIdentifier localObjectIdentifier1 = localContentInfo.getContentType();
    byte[] arrayOfByte1;
    if (localObjectIdentifier1.equals(ContentInfo.DATA_OID)) {
      arrayOfByte1 = localContentInfo.getData();
    } else {
      throw new IOException("public key protected PKCS12 not supported");
    }
    DerInputStream localDerInputStream2 = new DerInputStream(arrayOfByte1);
    DerValue[] arrayOfDerValue1 = localDerInputStream2.getSequence(2);
    int j = arrayOfDerValue1.length;
    privateKeyCount = 0;
    secretKeyCount = 0;
    certificateCount = 0;
    Object localObject7;
    Object localObject6;
    Object localObject5;
    Object localObject8;
    for (int k = 0; k < j; k++)
    {
      localObject7 = null;
      localObject6 = new DerInputStream(arrayOfDerValue1[k].toByteArray());
      localObject5 = new ContentInfo((DerInputStream)localObject6);
      localObjectIdentifier1 = ((ContentInfo)localObject5).getContentType();
      byte[] arrayOfByte2 = null;
      if (localObjectIdentifier1.equals(ContentInfo.DATA_OID))
      {
        if (debug != null) {
          debug.println("Loading PKCS#7 data");
        }
        arrayOfByte2 = ((ContentInfo)localObject5).getData();
      }
      else if (localObjectIdentifier1.equals(ContentInfo.ENCRYPTED_DATA_OID))
      {
        if (paramArrayOfChar == null)
        {
          if (debug == null) {
            continue;
          }
          debug.println("Warning: skipping PKCS#7 encryptedData - no password was supplied");
          continue;
        }
        localObject8 = ((ContentInfo)localObject5).getContent().toDerInputStream();
        int n = ((DerInputStream)localObject8).getInteger();
        DerValue[] arrayOfDerValue2 = ((DerInputStream)localObject8).getSequence(2);
        ObjectIdentifier localObjectIdentifier2 = arrayOfDerValue2[0].getOID();
        localObject7 = arrayOfDerValue2[1].toByteArray();
        if (!arrayOfDerValue2[2].isContextSpecific((byte)0)) {
          throw new IOException("encrypted content not present!");
        }
        byte b = 4;
        if (arrayOfDerValue2[2].isConstructed()) {
          b = (byte)(b | 0x20);
        }
        arrayOfDerValue2[2].resetTag(b);
        arrayOfByte2 = arrayOfDerValue2[2].getOctetString();
        DerInputStream localDerInputStream3 = arrayOfDerValue2[1].toDerInputStream();
        ObjectIdentifier localObjectIdentifier3 = localDerInputStream3.getOID();
        AlgorithmParameters localAlgorithmParameters = parseAlgParameters(localObjectIdentifier3, localDerInputStream3);
        int i1 = 0;
        if (localAlgorithmParameters != null)
        {
          PBEParameterSpec localPBEParameterSpec;
          try
          {
            localPBEParameterSpec = (PBEParameterSpec)localAlgorithmParameters.getParameterSpec(PBEParameterSpec.class);
          }
          catch (InvalidParameterSpecException localInvalidParameterSpecException)
          {
            throw new IOException("Invalid PBE algorithm parameters");
          }
          i1 = localPBEParameterSpec.getIterationCount();
          if (i1 > 5000000) {
            throw new IOException("PBE iteration count too large");
          }
        }
        if (debug != null) {
          debug.println("Loading PKCS#7 encryptedData (" + new AlgorithmId(localObjectIdentifier3).getName() + " iterations: " + i1 + ")");
        }
        try
        {
          SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
          Cipher localCipher = Cipher.getInstance(localObjectIdentifier3.toString());
          localCipher.init(2, localSecretKey, localAlgorithmParameters);
          arrayOfByte2 = localCipher.doFinal(arrayOfByte2);
        }
        catch (Exception localException2)
        {
          while (paramArrayOfChar.length == 0) {
            paramArrayOfChar = new char[1];
          }
          throw new IOException("keystore password was incorrect", new UnrecoverableKeyException("failed to decrypt safe contents entry: " + localException2));
        }
      }
      else
      {
        throw new IOException("public key protected PKCS12 not supported");
      }
      localObject8 = new DerInputStream(arrayOfByte2);
      loadSafeContents((DerInputStream)localObject8, paramArrayOfChar);
    }
    Object localObject9;
    if ((paramArrayOfChar != null) && (localDerInputStream1.available() > 0))
    {
      localObject4 = new MacData(localDerInputStream1);
      m = ((MacData)localObject4).getIterations();
      try
      {
        if (m > 5000000) {
          throw new InvalidAlgorithmParameterException("MAC iteration count too large: " + m);
        }
        localObject5 = ((MacData)localObject4).getDigestAlgName().toUpperCase(Locale.ENGLISH);
        localObject5 = ((String)localObject5).replace("-", "");
        localObject6 = Mac.getInstance("HmacPBE" + (String)localObject5);
        localObject7 = new PBEParameterSpec(((MacData)localObject4).getSalt(), m);
        localObject8 = getPBEKey(paramArrayOfChar);
        ((Mac)localObject6).init((Key)localObject8, (AlgorithmParameterSpec)localObject7);
        ((Mac)localObject6).update(arrayOfByte1);
        localObject9 = ((Mac)localObject6).doFinal();
        if (debug != null) {
          debug.println("Checking keystore integrity (" + ((Mac)localObject6).getAlgorithm() + " iterations: " + m + ")");
        }
        if (!MessageDigest.isEqual(((MacData)localObject4).getDigest(), (byte[])localObject9)) {
          throw new UnrecoverableKeyException("Failed PKCS12 integrity checking");
        }
      }
      catch (Exception localException1)
      {
        throw new IOException("Integrity check failed: " + localException1, localException1);
      }
    }
    Object localObject4 = (PrivateKeyEntry[])keyList.toArray(new PrivateKeyEntry[keyList.size()]);
    for (int m = 0; m < localObject4.length; m++)
    {
      PrivateKeyEntry localPrivateKeyEntry = localObject4[m];
      if (keyId != null)
      {
        localObject6 = new ArrayList();
        for (localObject7 = findMatchedCertificate(localPrivateKeyEntry); localObject7 != null; localObject7 = (X509Certificate)certsMap.get(localObject8))
        {
          if (!((ArrayList)localObject6).isEmpty())
          {
            localObject8 = ((ArrayList)localObject6).iterator();
            while (((Iterator)localObject8).hasNext())
            {
              localObject9 = (X509Certificate)((Iterator)localObject8).next();
              if (((X509Certificate)localObject7).equals(localObject9))
              {
                if (debug == null) {
                  break label1140;
                }
                debug.println("Loop detected in certificate chain. Skip adding repeated cert to chain. Subject: " + ((X509Certificate)localObject7).getSubjectX500Principal().toString());
                break label1140;
              }
            }
          }
          ((ArrayList)localObject6).add(localObject7);
          localObject8 = ((X509Certificate)localObject7).getIssuerX500Principal();
          if (((X500Principal)localObject8).equals(((X509Certificate)localObject7).getSubjectX500Principal())) {
            break;
          }
        }
        label1140:
        if (((ArrayList)localObject6).size() > 0) {
          chain = ((Certificate[])((ArrayList)localObject6).toArray(new Certificate[((ArrayList)localObject6).size()]));
        }
      }
    }
    if (debug != null)
    {
      if (privateKeyCount > 0) {
        debug.println("Loaded " + privateKeyCount + " protected private key(s)");
      }
      if (secretKeyCount > 0) {
        debug.println("Loaded " + secretKeyCount + " protected secret key(s)");
      }
      if (certificateCount > 0) {
        debug.println("Loaded " + certificateCount + " certificate(s)");
      }
    }
    certEntries.clear();
    certsMap.clear();
    keyList.clear();
  }
  
  private X509Certificate findMatchedCertificate(PrivateKeyEntry paramPrivateKeyEntry)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Iterator localIterator = certEntries.iterator();
    while (localIterator.hasNext())
    {
      CertEntry localCertEntry = (CertEntry)localIterator.next();
      if (Arrays.equals(keyId, keyId))
      {
        localObject1 = localCertEntry;
        if (alias.equalsIgnoreCase(alias)) {
          return cert;
        }
      }
      else if (alias.equalsIgnoreCase(alias))
      {
        localObject2 = localCertEntry;
      }
    }
    if (localObject1 != null) {
      return cert;
    }
    if (localObject2 != null) {
      return cert;
    }
    return null;
  }
  
  private void loadSafeContents(DerInputStream paramDerInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    DerValue[] arrayOfDerValue1 = paramDerInputStream.getSequence(2);
    int i = arrayOfDerValue1.length;
    for (int j = 0; j < i; j++)
    {
      Object localObject1 = null;
      DerInputStream localDerInputStream1 = arrayOfDerValue1[j].toDerInputStream();
      ObjectIdentifier localObjectIdentifier1 = localDerInputStream1.getOID();
      DerValue localDerValue = localDerInputStream1.getDerValue();
      if (!localDerValue.isContextSpecific((byte)0)) {
        throw new IOException("unsupported PKCS12 bag value type " + tag);
      }
      localDerValue = data.getDerValue();
      Object localObject2;
      if (localObjectIdentifier1.equals(PKCS8ShroudedKeyBag_OID))
      {
        localObject2 = new PrivateKeyEntry(null);
        protectedPrivKey = localDerValue.toByteArray();
        localObject1 = localObject2;
        privateKeyCount += 1;
      }
      else
      {
        DerValue[] arrayOfDerValue2;
        if (localObjectIdentifier1.equals(CertBag_OID))
        {
          localObject2 = new DerInputStream(localDerValue.toByteArray());
          arrayOfDerValue2 = ((DerInputStream)localObject2).getSequence(2);
          localObject3 = arrayOfDerValue2[0].getOID();
          if (!arrayOfDerValue2[1].isContextSpecific((byte)0)) {
            throw new IOException("unsupported PKCS12 cert value type " + 1tag);
          }
          localObject4 = 1data.getDerValue();
          localObject5 = CertificateFactory.getInstance("X509");
          X509Certificate localX509Certificate = (X509Certificate)((CertificateFactory)localObject5).generateCertificate(new ByteArrayInputStream(((DerValue)localObject4).getOctetString()));
          localObject1 = localX509Certificate;
          certificateCount += 1;
        }
        else if (localObjectIdentifier1.equals(SecretBag_OID))
        {
          localObject2 = new DerInputStream(localDerValue.toByteArray());
          arrayOfDerValue2 = ((DerInputStream)localObject2).getSequence(2);
          localObject3 = arrayOfDerValue2[0].getOID();
          if (!arrayOfDerValue2[1].isContextSpecific((byte)0)) {
            throw new IOException("unsupported PKCS12 secret value type " + 1tag);
          }
          localObject4 = 1data.getDerValue();
          localObject5 = new SecretKeyEntry(null);
          protectedSecretKey = ((DerValue)localObject4).getOctetString();
          localObject1 = localObject5;
          secretKeyCount += 1;
        }
        else if (debug != null)
        {
          debug.println("Unsupported PKCS12 bag type: " + localObjectIdentifier1);
        }
      }
      try
      {
        localObject2 = localDerInputStream1.getSet(3);
      }
      catch (IOException localIOException1)
      {
        localObject2 = null;
      }
      String str = null;
      Object localObject3 = null;
      Object localObject4 = null;
      Object localObject5 = new HashSet();
      Object localObject7;
      Object localObject8;
      if (localObject2 != null) {
        for (int k = 0; k < localObject2.length; k++)
        {
          localObject7 = localObject2[k].toByteArray();
          localObject8 = new DerInputStream((byte[])localObject7);
          DerValue[] arrayOfDerValue3 = ((DerInputStream)localObject8).getSequence(2);
          ObjectIdentifier localObjectIdentifier2 = arrayOfDerValue3[0].getOID();
          DerInputStream localDerInputStream2 = new DerInputStream(arrayOfDerValue3[1].toByteArray());
          DerValue[] arrayOfDerValue4;
          try
          {
            arrayOfDerValue4 = localDerInputStream2.getSet(1);
          }
          catch (IOException localIOException2)
          {
            throw new IOException("Attribute " + localObjectIdentifier2 + " should have a value " + localIOException2.getMessage());
          }
          if (localObjectIdentifier2.equals(PKCS9FriendlyName_OID))
          {
            str = arrayOfDerValue4[0].getBMPString();
          }
          else if (localObjectIdentifier2.equals(PKCS9LocalKeyId_OID))
          {
            localObject3 = arrayOfDerValue4[0].getOctetString();
          }
          else if (localObjectIdentifier2.equals(TrustedKeyUsage_OID))
          {
            localObject4 = new ObjectIdentifier[arrayOfDerValue4.length];
            for (int m = 0; m < arrayOfDerValue4.length; m++) {
              localObject4[m] = arrayOfDerValue4[m].getOID();
            }
          }
          else
          {
            ((Set)localObject5).add(new PKCS12Attribute((byte[])localObject7));
          }
        }
      }
      Object localObject6;
      if ((localObject1 instanceof KeyEntry))
      {
        localObject6 = (KeyEntry)localObject1;
        if (((localObject1 instanceof PrivateKeyEntry)) && (localObject3 == null))
        {
          if (privateKeyCount == 1) {
            localObject3 = "01".getBytes("UTF8");
          }
        }
        else
        {
          keyId = ((byte[])localObject3);
          localObject7 = new String((byte[])localObject3, "UTF8");
          localObject8 = null;
          if (((String)localObject7).startsWith("Time ")) {
            try
            {
              localObject8 = new Date(Long.parseLong(((String)localObject7).substring(5)));
            }
            catch (Exception localException)
            {
              localObject8 = null;
            }
          }
          if (localObject8 == null) {
            localObject8 = new Date();
          }
          date = ((Date)localObject8);
          if ((localObject1 instanceof PrivateKeyEntry)) {
            keyList.add((PrivateKeyEntry)localObject6);
          }
          if (attributes == null) {
            attributes = new HashSet();
          }
          attributes.addAll((Collection)localObject5);
          if (str == null) {
            str = getUnfriendlyName();
          }
          alias = str;
          entries.put(str.toLowerCase(Locale.ENGLISH), localObject6);
        }
      }
      else if ((localObject1 instanceof X509Certificate))
      {
        localObject6 = (X509Certificate)localObject1;
        if ((localObject3 == null) && (privateKeyCount == 1) && (j == 0)) {
          localObject3 = "01".getBytes("UTF8");
        }
        if (localObject4 != null)
        {
          if (str == null) {
            str = getUnfriendlyName();
          }
          localObject7 = new CertEntry((X509Certificate)localObject6, (byte[])localObject3, str, (ObjectIdentifier[])localObject4, (Set)localObject5);
          entries.put(str.toLowerCase(Locale.ENGLISH), localObject7);
        }
        else
        {
          certEntries.add(new CertEntry((X509Certificate)localObject6, (byte[])localObject3, str));
        }
        localObject7 = ((X509Certificate)localObject6).getSubjectX500Principal();
        if ((localObject7 != null) && (!certsMap.containsKey(localObject7))) {
          certsMap.put(localObject7, localObject6);
        }
      }
    }
  }
  
  private String getUnfriendlyName()
  {
    counter += 1;
    return String.valueOf(counter);
  }
  
  static
  {
    try
    {
      PKCS8ShroudedKeyBag_OID = new ObjectIdentifier(keyBag);
      CertBag_OID = new ObjectIdentifier(certBag);
      SecretBag_OID = new ObjectIdentifier(secretBag);
      PKCS9FriendlyName_OID = new ObjectIdentifier(pkcs9Name);
      PKCS9LocalKeyId_OID = new ObjectIdentifier(pkcs9KeyId);
      PKCS9CertType_OID = new ObjectIdentifier(pkcs9certType);
      pbeWithSHAAnd40BitRC2CBC_OID = new ObjectIdentifier(pbeWithSHAAnd40BitRC2CBC);
      pbeWithSHAAnd3KeyTripleDESCBC_OID = new ObjectIdentifier(pbeWithSHAAnd3KeyTripleDESCBC);
      pbes2_OID = new ObjectIdentifier(pbes2);
      TrustedKeyUsage_OID = new ObjectIdentifier(TrustedKeyUsage);
      AnyUsage = new ObjectIdentifier[] { new ObjectIdentifier(AnyExtendedKeyUsage) };
    }
    catch (IOException localIOException) {}
  }
  
  private static class CertEntry
    extends PKCS12KeyStore.Entry
  {
    final X509Certificate cert;
    ObjectIdentifier[] trustedKeyUsage;
    
    CertEntry(X509Certificate paramX509Certificate, byte[] paramArrayOfByte, String paramString)
    {
      this(paramX509Certificate, paramArrayOfByte, paramString, null, null);
    }
    
    CertEntry(X509Certificate paramX509Certificate, byte[] paramArrayOfByte, String paramString, ObjectIdentifier[] paramArrayOfObjectIdentifier, Set<? extends KeyStore.Entry.Attribute> paramSet)
    {
      super();
      date = new Date();
      cert = paramX509Certificate;
      keyId = paramArrayOfByte;
      alias = paramString;
      trustedKeyUsage = paramArrayOfObjectIdentifier;
      attributes = new HashSet();
      if (paramSet != null) {
        attributes.addAll(paramSet);
      }
    }
  }
  
  private static class Entry
  {
    Date date;
    String alias;
    byte[] keyId;
    Set<KeyStore.Entry.Attribute> attributes;
    
    private Entry() {}
  }
  
  private static class KeyEntry
    extends PKCS12KeyStore.Entry
  {
    private KeyEntry()
    {
      super();
    }
  }
  
  private static class PrivateKeyEntry
    extends PKCS12KeyStore.KeyEntry
  {
    byte[] protectedPrivKey;
    Certificate[] chain;
    
    private PrivateKeyEntry()
    {
      super();
    }
  }
  
  private static class SecretKeyEntry
    extends PKCS12KeyStore.KeyEntry
  {
    byte[] protectedSecretKey;
    
    private SecretKeyEntry()
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs12\PKCS12KeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */