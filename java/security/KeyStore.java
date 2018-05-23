package java.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import sun.security.util.Debug;

public class KeyStore
{
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  private static final boolean skipDebug = (Debug.isOn("engine=")) && (!Debug.isOn("keystore"));
  private static final String KEYSTORE_TYPE = "keystore.type";
  private String type;
  private Provider provider;
  private KeyStoreSpi keyStoreSpi;
  private boolean initialized = false;
  
  protected KeyStore(KeyStoreSpi paramKeyStoreSpi, Provider paramProvider, String paramString)
  {
    keyStoreSpi = paramKeyStoreSpi;
    provider = paramProvider;
    type = paramString;
    if ((!skipDebug) && (pdebug != null)) {
      pdebug.println("KeyStore." + paramString.toUpperCase() + " type from: " + provider.getName());
    }
  }
  
  public static KeyStore getInstance(String paramString)
    throws KeyStoreException
  {
    try
    {
      Object[] arrayOfObject = Security.getImpl(paramString, "KeyStore", (String)null);
      return new KeyStore((KeyStoreSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new KeyStoreException(paramString + " not found", localNoSuchAlgorithmException);
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new KeyStoreException(paramString + " not found", localNoSuchProviderException);
    }
  }
  
  public static KeyStore getInstance(String paramString1, String paramString2)
    throws KeyStoreException, NoSuchProviderException
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException("missing provider");
    }
    try
    {
      Object[] arrayOfObject = Security.getImpl(paramString1, "KeyStore", paramString2);
      return new KeyStore((KeyStoreSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new KeyStoreException(paramString1 + " not found", localNoSuchAlgorithmException);
    }
  }
  
  public static KeyStore getInstance(String paramString, Provider paramProvider)
    throws KeyStoreException
  {
    if (paramProvider == null) {
      throw new IllegalArgumentException("missing provider");
    }
    try
    {
      Object[] arrayOfObject = Security.getImpl(paramString, "KeyStore", paramProvider);
      return new KeyStore((KeyStoreSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new KeyStoreException(paramString + " not found", localNoSuchAlgorithmException);
    }
  }
  
  public static final String getDefaultType()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("keystore.type");
      }
    });
    if (str == null) {
      str = "jks";
    }
    return str;
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public final String getType()
  {
    return type;
  }
  
  public final Key getKey(String paramString, char[] paramArrayOfChar)
    throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineGetKey(paramString, paramArrayOfChar);
  }
  
  public final Certificate[] getCertificateChain(String paramString)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineGetCertificateChain(paramString);
  }
  
  public final Certificate getCertificate(String paramString)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineGetCertificate(paramString);
  }
  
  public final Date getCreationDate(String paramString)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineGetCreationDate(paramString);
  }
  
  public final void setKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    if (((paramKey instanceof PrivateKey)) && ((paramArrayOfCertificate == null) || (paramArrayOfCertificate.length == 0))) {
      throw new IllegalArgumentException("Private key must be accompanied by certificate chain");
    }
    keyStoreSpi.engineSetKeyEntry(paramString, paramKey, paramArrayOfChar, paramArrayOfCertificate);
  }
  
  public final void setKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    keyStoreSpi.engineSetKeyEntry(paramString, paramArrayOfByte, paramArrayOfCertificate);
  }
  
  public final void setCertificateEntry(String paramString, Certificate paramCertificate)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    keyStoreSpi.engineSetCertificateEntry(paramString, paramCertificate);
  }
  
  public final void deleteEntry(String paramString)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    keyStoreSpi.engineDeleteEntry(paramString);
  }
  
  public final Enumeration<String> aliases()
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineAliases();
  }
  
  public final boolean containsAlias(String paramString)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineContainsAlias(paramString);
  }
  
  public final int size()
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineSize();
  }
  
  public final boolean isKeyEntry(String paramString)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineIsKeyEntry(paramString);
  }
  
  public final boolean isCertificateEntry(String paramString)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineIsCertificateEntry(paramString);
  }
  
  public final String getCertificateAlias(Certificate paramCertificate)
    throws KeyStoreException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineGetCertificateAlias(paramCertificate);
  }
  
  public final void store(OutputStream paramOutputStream, char[] paramArrayOfChar)
    throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    keyStoreSpi.engineStore(paramOutputStream, paramArrayOfChar);
  }
  
  public final void store(LoadStoreParameter paramLoadStoreParameter)
    throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
  {
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    keyStoreSpi.engineStore(paramLoadStoreParameter);
  }
  
  public final void load(InputStream paramInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    keyStoreSpi.engineLoad(paramInputStream, paramArrayOfChar);
    initialized = true;
  }
  
  public final void load(LoadStoreParameter paramLoadStoreParameter)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    keyStoreSpi.engineLoad(paramLoadStoreParameter);
    initialized = true;
  }
  
  public final Entry getEntry(String paramString, ProtectionParameter paramProtectionParameter)
    throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException
  {
    if (paramString == null) {
      throw new NullPointerException("invalid null input");
    }
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineGetEntry(paramString, paramProtectionParameter);
  }
  
  public final void setEntry(String paramString, Entry paramEntry, ProtectionParameter paramProtectionParameter)
    throws KeyStoreException
  {
    if ((paramString == null) || (paramEntry == null)) {
      throw new NullPointerException("invalid null input");
    }
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    keyStoreSpi.engineSetEntry(paramString, paramEntry, paramProtectionParameter);
  }
  
  public final boolean entryInstanceOf(String paramString, Class<? extends Entry> paramClass)
    throws KeyStoreException
  {
    if ((paramString == null) || (paramClass == null)) {
      throw new NullPointerException("invalid null input");
    }
    if (!initialized) {
      throw new KeyStoreException("Uninitialized keystore");
    }
    return keyStoreSpi.engineEntryInstanceOf(paramString, paramClass);
  }
  
  public static abstract class Builder
  {
    static final int MAX_CALLBACK_TRIES = 3;
    
    protected Builder() {}
    
    public abstract KeyStore getKeyStore()
      throws KeyStoreException;
    
    public abstract KeyStore.ProtectionParameter getProtectionParameter(String paramString)
      throws KeyStoreException;
    
    public static Builder newInstance(KeyStore paramKeyStore, final KeyStore.ProtectionParameter paramProtectionParameter)
    {
      if ((paramKeyStore == null) || (paramProtectionParameter == null)) {
        throw new NullPointerException();
      }
      if (!initialized) {
        throw new IllegalArgumentException("KeyStore not initialized");
      }
      new Builder()
      {
        private volatile boolean getCalled;
        
        public KeyStore getKeyStore()
        {
          getCalled = true;
          return val$keyStore;
        }
        
        public KeyStore.ProtectionParameter getProtectionParameter(String paramAnonymousString)
        {
          if (paramAnonymousString == null) {
            throw new NullPointerException();
          }
          if (!getCalled) {
            throw new IllegalStateException("getKeyStore() must be called first");
          }
          return paramProtectionParameter;
        }
      };
    }
    
    public static Builder newInstance(String paramString, Provider paramProvider, File paramFile, KeyStore.ProtectionParameter paramProtectionParameter)
    {
      if ((paramString == null) || (paramFile == null) || (paramProtectionParameter == null)) {
        throw new NullPointerException();
      }
      if ((!(paramProtectionParameter instanceof KeyStore.PasswordProtection)) && (!(paramProtectionParameter instanceof KeyStore.CallbackHandlerProtection))) {
        throw new IllegalArgumentException("Protection must be PasswordProtection or CallbackHandlerProtection");
      }
      if (!paramFile.isFile()) {
        throw new IllegalArgumentException("File does not exist or it does not refer to a normal file: " + paramFile);
      }
      return new FileBuilder(paramString, paramProvider, paramFile, paramProtectionParameter, AccessController.getContext());
    }
    
    public static Builder newInstance(final String paramString, Provider paramProvider, final KeyStore.ProtectionParameter paramProtectionParameter)
    {
      if ((paramString == null) || (paramProtectionParameter == null)) {
        throw new NullPointerException();
      }
      final AccessControlContext localAccessControlContext = AccessController.getContext();
      new Builder()
      {
        private volatile boolean getCalled;
        private IOException oldException;
        private final PrivilegedExceptionAction<KeyStore> action = new PrivilegedExceptionAction()
        {
          public KeyStore run()
            throws Exception
          {
            KeyStore localKeyStore;
            if (val$provider == null) {
              localKeyStore = KeyStore.getInstance(val$type);
            } else {
              localKeyStore = KeyStore.getInstance(val$type, val$provider);
            }
            KeyStore.SimpleLoadStoreParameter localSimpleLoadStoreParameter = new KeyStore.SimpleLoadStoreParameter(val$protection);
            if (!(val$protection instanceof KeyStore.CallbackHandlerProtection))
            {
              localKeyStore.load(localSimpleLoadStoreParameter);
            }
            else
            {
              int i = 0;
              for (;;)
              {
                i++;
                try
                {
                  localKeyStore.load(localSimpleLoadStoreParameter);
                }
                catch (IOException localIOException)
                {
                  if ((localIOException.getCause() instanceof UnrecoverableKeyException))
                  {
                    if (i >= 3) {
                      oldException = localIOException;
                    }
                  }
                  else {
                    throw localIOException;
                  }
                }
              }
            }
            getCalled = true;
            return localKeyStore;
          }
        };
        
        public synchronized KeyStore getKeyStore()
          throws KeyStoreException
        {
          if (oldException != null) {
            throw new KeyStoreException("Previous KeyStore instantiation failed", oldException);
          }
          try
          {
            return (KeyStore)AccessController.doPrivileged(action, localAccessControlContext);
          }
          catch (PrivilegedActionException localPrivilegedActionException)
          {
            Throwable localThrowable = localPrivilegedActionException.getCause();
            throw new KeyStoreException("KeyStore instantiation failed", localThrowable);
          }
        }
        
        public KeyStore.ProtectionParameter getProtectionParameter(String paramAnonymousString)
        {
          if (paramAnonymousString == null) {
            throw new NullPointerException();
          }
          if (!getCalled) {
            throw new IllegalStateException("getKeyStore() must be called first");
          }
          return paramProtectionParameter;
        }
      };
    }
    
    private static final class FileBuilder
      extends KeyStore.Builder
    {
      private final String type;
      private final Provider provider;
      private final File file;
      private KeyStore.ProtectionParameter protection;
      private KeyStore.ProtectionParameter keyProtection;
      private final AccessControlContext context;
      private KeyStore keyStore;
      private Throwable oldException;
      
      FileBuilder(String paramString, Provider paramProvider, File paramFile, KeyStore.ProtectionParameter paramProtectionParameter, AccessControlContext paramAccessControlContext)
      {
        type = paramString;
        provider = paramProvider;
        file = paramFile;
        protection = paramProtectionParameter;
        context = paramAccessControlContext;
      }
      
      public synchronized KeyStore getKeyStore()
        throws KeyStoreException
      {
        if (keyStore != null) {
          return keyStore;
        }
        if (oldException != null) {
          throw new KeyStoreException("Previous KeyStore instantiation failed", oldException);
        }
        PrivilegedExceptionAction local1 = new PrivilegedExceptionAction()
        {
          public KeyStore run()
            throws Exception
          {
            if (!(protection instanceof KeyStore.CallbackHandlerProtection)) {
              return run0();
            }
            int i = 0;
            do
            {
              i++;
              try
              {
                return run0();
              }
              catch (IOException localIOException) {}
            } while ((i < 3) && ((localIOException.getCause() instanceof UnrecoverableKeyException)));
            throw localIOException;
          }
          
          public KeyStore run0()
            throws Exception
          {
            KeyStore localKeyStore;
            if (provider == null) {
              localKeyStore = KeyStore.getInstance(type);
            } else {
              localKeyStore = KeyStore.getInstance(type, provider);
            }
            FileInputStream localFileInputStream = null;
            char[] arrayOfChar = null;
            try
            {
              localFileInputStream = new FileInputStream(file);
              if ((protection instanceof KeyStore.PasswordProtection))
              {
                arrayOfChar = ((KeyStore.PasswordProtection)protection).getPassword();
                keyProtection = protection;
              }
              else
              {
                localObject1 = ((KeyStore.CallbackHandlerProtection)protection).getCallbackHandler();
                PasswordCallback localPasswordCallback = new PasswordCallback("Password for keystore " + file.getName(), false);
                ((CallbackHandler)localObject1).handle(new Callback[] { localPasswordCallback });
                arrayOfChar = localPasswordCallback.getPassword();
                if (arrayOfChar == null) {
                  throw new KeyStoreException("No password provided");
                }
                localPasswordCallback.clearPassword();
                keyProtection = new KeyStore.PasswordProtection(arrayOfChar);
              }
              localKeyStore.load(localFileInputStream, arrayOfChar);
              Object localObject1 = localKeyStore;
              return (KeyStore)localObject1;
            }
            finally
            {
              if (localFileInputStream != null) {
                localFileInputStream.close();
              }
            }
          }
        };
        try
        {
          keyStore = ((KeyStore)AccessController.doPrivileged(local1, context));
          return keyStore;
        }
        catch (PrivilegedActionException localPrivilegedActionException)
        {
          oldException = localPrivilegedActionException.getCause();
          throw new KeyStoreException("KeyStore instantiation failed", oldException);
        }
      }
      
      public synchronized KeyStore.ProtectionParameter getProtectionParameter(String paramString)
      {
        if (paramString == null) {
          throw new NullPointerException();
        }
        if (keyStore == null) {
          throw new IllegalStateException("getKeyStore() must be called first");
        }
        return keyProtection;
      }
    }
  }
  
  public static class CallbackHandlerProtection
    implements KeyStore.ProtectionParameter
  {
    private final CallbackHandler handler;
    
    public CallbackHandlerProtection(CallbackHandler paramCallbackHandler)
    {
      if (paramCallbackHandler == null) {
        throw new NullPointerException("handler must not be null");
      }
      handler = paramCallbackHandler;
    }
    
    public CallbackHandler getCallbackHandler()
    {
      return handler;
    }
  }
  
  public static abstract interface Entry
  {
    public Set<Attribute> getAttributes()
    {
      return Collections.emptySet();
    }
    
    public static abstract interface Attribute
    {
      public abstract String getName();
      
      public abstract String getValue();
    }
  }
  
  public static abstract interface LoadStoreParameter
  {
    public abstract KeyStore.ProtectionParameter getProtectionParameter();
  }
  
  public static class PasswordProtection
    implements KeyStore.ProtectionParameter, Destroyable
  {
    private final char[] password;
    private final String protectionAlgorithm;
    private final AlgorithmParameterSpec protectionParameters;
    private volatile boolean destroyed = false;
    
    public PasswordProtection(char[] paramArrayOfChar)
    {
      password = (paramArrayOfChar == null ? null : (char[])paramArrayOfChar.clone());
      protectionAlgorithm = null;
      protectionParameters = null;
    }
    
    public PasswordProtection(char[] paramArrayOfChar, String paramString, AlgorithmParameterSpec paramAlgorithmParameterSpec)
    {
      if (paramString == null) {
        throw new NullPointerException("invalid null input");
      }
      password = (paramArrayOfChar == null ? null : (char[])paramArrayOfChar.clone());
      protectionAlgorithm = paramString;
      protectionParameters = paramAlgorithmParameterSpec;
    }
    
    public String getProtectionAlgorithm()
    {
      return protectionAlgorithm;
    }
    
    public AlgorithmParameterSpec getProtectionParameters()
    {
      return protectionParameters;
    }
    
    public synchronized char[] getPassword()
    {
      if (destroyed) {
        throw new IllegalStateException("password has been cleared");
      }
      return password;
    }
    
    public synchronized void destroy()
      throws DestroyFailedException
    {
      destroyed = true;
      if (password != null) {
        Arrays.fill(password, ' ');
      }
    }
    
    public synchronized boolean isDestroyed()
    {
      return destroyed;
    }
  }
  
  public static final class PrivateKeyEntry
    implements KeyStore.Entry
  {
    private final PrivateKey privKey;
    private final Certificate[] chain;
    private final Set<KeyStore.Entry.Attribute> attributes;
    
    public PrivateKeyEntry(PrivateKey paramPrivateKey, Certificate[] paramArrayOfCertificate)
    {
      this(paramPrivateKey, paramArrayOfCertificate, Collections.emptySet());
    }
    
    public PrivateKeyEntry(PrivateKey paramPrivateKey, Certificate[] paramArrayOfCertificate, Set<KeyStore.Entry.Attribute> paramSet)
    {
      if ((paramPrivateKey == null) || (paramArrayOfCertificate == null) || (paramSet == null)) {
        throw new NullPointerException("invalid null input");
      }
      if (paramArrayOfCertificate.length == 0) {
        throw new IllegalArgumentException("invalid zero-length input chain");
      }
      Certificate[] arrayOfCertificate = (Certificate[])paramArrayOfCertificate.clone();
      String str = arrayOfCertificate[0].getType();
      for (int i = 1; i < arrayOfCertificate.length; i++) {
        if (!str.equals(arrayOfCertificate[i].getType())) {
          throw new IllegalArgumentException("chain does not contain certificates of the same type");
        }
      }
      if (!paramPrivateKey.getAlgorithm().equals(arrayOfCertificate[0].getPublicKey().getAlgorithm())) {
        throw new IllegalArgumentException("private key algorithm does not match algorithm of public key in end entity certificate (at index 0)");
      }
      privKey = paramPrivateKey;
      if (((arrayOfCertificate[0] instanceof X509Certificate)) && (!(arrayOfCertificate instanceof X509Certificate[])))
      {
        chain = new X509Certificate[arrayOfCertificate.length];
        System.arraycopy(arrayOfCertificate, 0, chain, 0, arrayOfCertificate.length);
      }
      else
      {
        chain = arrayOfCertificate;
      }
      attributes = Collections.unmodifiableSet(new HashSet(paramSet));
    }
    
    public PrivateKey getPrivateKey()
    {
      return privKey;
    }
    
    public Certificate[] getCertificateChain()
    {
      return (Certificate[])chain.clone();
    }
    
    public Certificate getCertificate()
    {
      return chain[0];
    }
    
    public Set<KeyStore.Entry.Attribute> getAttributes()
    {
      return attributes;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Private key entry and certificate chain with " + chain.length + " elements:\r\n");
      for (Certificate localCertificate : chain)
      {
        localStringBuilder.append(localCertificate);
        localStringBuilder.append("\r\n");
      }
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface ProtectionParameter {}
  
  public static final class SecretKeyEntry
    implements KeyStore.Entry
  {
    private final SecretKey sKey;
    private final Set<KeyStore.Entry.Attribute> attributes;
    
    public SecretKeyEntry(SecretKey paramSecretKey)
    {
      if (paramSecretKey == null) {
        throw new NullPointerException("invalid null input");
      }
      sKey = paramSecretKey;
      attributes = Collections.emptySet();
    }
    
    public SecretKeyEntry(SecretKey paramSecretKey, Set<KeyStore.Entry.Attribute> paramSet)
    {
      if ((paramSecretKey == null) || (paramSet == null)) {
        throw new NullPointerException("invalid null input");
      }
      sKey = paramSecretKey;
      attributes = Collections.unmodifiableSet(new HashSet(paramSet));
    }
    
    public SecretKey getSecretKey()
    {
      return sKey;
    }
    
    public Set<KeyStore.Entry.Attribute> getAttributes()
    {
      return attributes;
    }
    
    public String toString()
    {
      return "Secret key entry with algorithm " + sKey.getAlgorithm();
    }
  }
  
  static class SimpleLoadStoreParameter
    implements KeyStore.LoadStoreParameter
  {
    private final KeyStore.ProtectionParameter protection;
    
    SimpleLoadStoreParameter(KeyStore.ProtectionParameter paramProtectionParameter)
    {
      protection = paramProtectionParameter;
    }
    
    public KeyStore.ProtectionParameter getProtectionParameter()
    {
      return protection;
    }
  }
  
  public static final class TrustedCertificateEntry
    implements KeyStore.Entry
  {
    private final Certificate cert;
    private final Set<KeyStore.Entry.Attribute> attributes;
    
    public TrustedCertificateEntry(Certificate paramCertificate)
    {
      if (paramCertificate == null) {
        throw new NullPointerException("invalid null input");
      }
      cert = paramCertificate;
      attributes = Collections.emptySet();
    }
    
    public TrustedCertificateEntry(Certificate paramCertificate, Set<KeyStore.Entry.Attribute> paramSet)
    {
      if ((paramCertificate == null) || (paramSet == null)) {
        throw new NullPointerException("invalid null input");
      }
      cert = paramCertificate;
      attributes = Collections.unmodifiableSet(new HashSet(paramSet));
    }
    
    public Certificate getTrustedCertificate()
    {
      return cert;
    }
    
    public Set<KeyStore.Entry.Attribute> getAttributes()
    {
      return attributes;
    }
    
    public String toString()
    {
      return "Trusted certificate entry:\r\n" + cert.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\KeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */