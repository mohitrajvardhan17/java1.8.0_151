package sun.security.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore.Entry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import sun.security.util.Debug;

class KeyStoreDelegator
  extends KeyStoreSpi
{
  private static final String KEYSTORE_TYPE_COMPAT = "keystore.type.compat";
  private static final Debug debug = Debug.getInstance("keystore");
  private final String primaryType;
  private final String secondaryType;
  private final Class<? extends KeyStoreSpi> primaryKeyStore;
  private final Class<? extends KeyStoreSpi> secondaryKeyStore;
  private String type;
  private KeyStoreSpi keystore;
  private boolean compatModeEnabled = true;
  
  public KeyStoreDelegator(String paramString1, Class<? extends KeyStoreSpi> paramClass1, String paramString2, Class<? extends KeyStoreSpi> paramClass2)
  {
    if (compatModeEnabled)
    {
      primaryType = paramString1;
      secondaryType = paramString2;
      primaryKeyStore = paramClass1;
      secondaryKeyStore = paramClass2;
    }
    else
    {
      primaryType = paramString1;
      secondaryType = null;
      primaryKeyStore = paramClass1;
      secondaryKeyStore = null;
      if (debug != null) {
        debug.println("WARNING: compatibility mode disabled for " + paramString1 + " and " + paramString2 + " keystore types");
      }
    }
  }
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar)
    throws NoSuchAlgorithmException, UnrecoverableKeyException
  {
    return keystore.engineGetKey(paramString, paramArrayOfChar);
  }
  
  public Certificate[] engineGetCertificateChain(String paramString)
  {
    return keystore.engineGetCertificateChain(paramString);
  }
  
  public Certificate engineGetCertificate(String paramString)
  {
    return keystore.engineGetCertificate(paramString);
  }
  
  public Date engineGetCreationDate(String paramString)
  {
    return keystore.engineGetCreationDate(paramString);
  }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    keystore.engineSetKeyEntry(paramString, paramKey, paramArrayOfChar, paramArrayOfCertificate);
  }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    keystore.engineSetKeyEntry(paramString, paramArrayOfByte, paramArrayOfCertificate);
  }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
    throws KeyStoreException
  {
    keystore.engineSetCertificateEntry(paramString, paramCertificate);
  }
  
  public void engineDeleteEntry(String paramString)
    throws KeyStoreException
  {
    keystore.engineDeleteEntry(paramString);
  }
  
  public Enumeration<String> engineAliases()
  {
    return keystore.engineAliases();
  }
  
  public boolean engineContainsAlias(String paramString)
  {
    return keystore.engineContainsAlias(paramString);
  }
  
  public int engineSize()
  {
    return keystore.engineSize();
  }
  
  public boolean engineIsKeyEntry(String paramString)
  {
    return keystore.engineIsKeyEntry(paramString);
  }
  
  public boolean engineIsCertificateEntry(String paramString)
  {
    return keystore.engineIsCertificateEntry(paramString);
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate)
  {
    return keystore.engineGetCertificateAlias(paramCertificate);
  }
  
  public KeyStore.Entry engineGetEntry(String paramString, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException
  {
    return keystore.engineGetEntry(paramString, paramProtectionParameter);
  }
  
  public void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException
  {
    keystore.engineSetEntry(paramString, paramEntry, paramProtectionParameter);
  }
  
  public boolean engineEntryInstanceOf(String paramString, Class<? extends KeyStore.Entry> paramClass)
  {
    return keystore.engineEntryInstanceOf(paramString, paramClass);
  }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    if (debug != null) {
      debug.println("Storing keystore in " + type + " format");
    }
    keystore.engineStore(paramOutputStream, paramArrayOfChar);
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    if ((paramInputStream == null) || (!compatModeEnabled))
    {
      try
      {
        keystore = ((KeyStoreSpi)primaryKeyStore.newInstance());
      }
      catch (InstantiationException|IllegalAccessException localInstantiationException1) {}
      type = primaryType;
      if ((debug != null) && (paramInputStream == null)) {
        debug.println("Creating a new keystore in " + type + " format");
      }
      keystore.engineLoad(paramInputStream, paramArrayOfChar);
    }
    else
    {
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream);
      localBufferedInputStream.mark(Integer.MAX_VALUE);
      try
      {
        keystore = ((KeyStoreSpi)primaryKeyStore.newInstance());
        type = primaryType;
        keystore.engineLoad(localBufferedInputStream, paramArrayOfChar);
      }
      catch (Exception localException)
      {
        if (((localException instanceof IOException)) && ((localException.getCause() instanceof UnrecoverableKeyException))) {
          throw ((IOException)localException);
        }
        try
        {
          keystore = ((KeyStoreSpi)secondaryKeyStore.newInstance());
          type = secondaryType;
          localBufferedInputStream.reset();
          keystore.engineLoad(localBufferedInputStream, paramArrayOfChar);
          if (debug != null) {
            debug.println("WARNING: switching from " + primaryType + " to " + secondaryType + " keystore file format has altered the keystore security level");
          }
        }
        catch (InstantiationException|IllegalAccessException localInstantiationException2) {}catch (IOException|NoSuchAlgorithmException|CertificateException localIOException)
        {
          if (((localIOException instanceof IOException)) && ((localIOException.getCause() instanceof UnrecoverableKeyException))) {
            throw ((IOException)localIOException);
          }
          if ((localException instanceof IOException)) {
            throw ((IOException)localException);
          }
          if ((localException instanceof CertificateException)) {
            throw ((CertificateException)localException);
          }
          if ((localException instanceof NoSuchAlgorithmException)) {
            throw ((NoSuchAlgorithmException)localException);
          }
        }
      }
      if (debug != null) {
        debug.println("Loaded a keystore in " + type + " format");
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\KeyStoreDelegator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */