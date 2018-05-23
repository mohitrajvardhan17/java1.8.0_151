package com.sun.security.auth.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.AuthProvider;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;
import jdk.Exported;
import sun.security.util.Password;

@Exported
public class KeyStoreLoginModule
  implements LoginModule
{
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  private static final int UNINITIALIZED = 0;
  private static final int INITIALIZED = 1;
  private static final int AUTHENTICATED = 2;
  private static final int LOGGED_IN = 3;
  private static final int PROTECTED_PATH = 0;
  private static final int TOKEN = 1;
  private static final int NORMAL = 2;
  private static final String NONE = "NONE";
  private static final String P11KEYSTORE = "PKCS11";
  private static final TextOutputCallback bannerCallback = new TextOutputCallback(0, rb.getString("Please.enter.keystore.information"));
  private final ConfirmationCallback confirmationCallback = new ConfirmationCallback(0, 2, 3);
  private Subject subject;
  private CallbackHandler callbackHandler;
  private Map<String, Object> sharedState;
  private Map<String, ?> options;
  private char[] keyStorePassword;
  private char[] privateKeyPassword;
  private KeyStore keyStore;
  private String keyStoreURL;
  private String keyStoreType;
  private String keyStoreProvider;
  private String keyStoreAlias;
  private String keyStorePasswordURL;
  private String privateKeyPasswordURL;
  private boolean debug;
  private X500Principal principal;
  private Certificate[] fromKeyStore;
  private CertPath certP = null;
  private X500PrivateCredential privateCredential;
  private int status = 0;
  private boolean nullStream = false;
  private boolean token = false;
  private boolean protectedPath = false;
  
  public KeyStoreLoginModule() {}
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2)
  {
    subject = paramSubject;
    callbackHandler = paramCallbackHandler;
    sharedState = paramMap1;
    options = paramMap2;
    processOptions();
    status = 1;
  }
  
  private void processOptions()
  {
    keyStoreURL = ((String)options.get("keyStoreURL"));
    if (keyStoreURL == null) {
      keyStoreURL = ("file:" + System.getProperty("user.home").replace(File.separatorChar, '/') + '/' + ".keystore");
    } else if ("NONE".equals(keyStoreURL)) {
      nullStream = true;
    }
    keyStoreType = ((String)options.get("keyStoreType"));
    if (keyStoreType == null) {
      keyStoreType = KeyStore.getDefaultType();
    }
    if ("PKCS11".equalsIgnoreCase(keyStoreType)) {
      token = true;
    }
    keyStoreProvider = ((String)options.get("keyStoreProvider"));
    keyStoreAlias = ((String)options.get("keyStoreAlias"));
    keyStorePasswordURL = ((String)options.get("keyStorePasswordURL"));
    privateKeyPasswordURL = ((String)options.get("privateKeyPasswordURL"));
    protectedPath = "true".equalsIgnoreCase((String)options.get("protected"));
    debug = "true".equalsIgnoreCase((String)options.get("debug"));
    if (debug)
    {
      debugPrint(null);
      debugPrint("keyStoreURL=" + keyStoreURL);
      debugPrint("keyStoreType=" + keyStoreType);
      debugPrint("keyStoreProvider=" + keyStoreProvider);
      debugPrint("keyStoreAlias=" + keyStoreAlias);
      debugPrint("keyStorePasswordURL=" + keyStorePasswordURL);
      debugPrint("privateKeyPasswordURL=" + privateKeyPasswordURL);
      debugPrint("protectedPath=" + protectedPath);
      debugPrint(null);
    }
  }
  
  /* Error */
  public boolean login()
    throws LoginException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 501	com/sun/security/auth/module/KeyStoreLoginModule:status	I
    //   4: tableswitch	default:+32->36, 0:+32->36, 1:+42->46, 2:+42->46, 3:+275->279
    //   36: new 353	javax/security/auth/login/LoginException
    //   39: dup
    //   40: ldc 33
    //   42: invokespecial 596	javax/security/auth/login/LoginException:<init>	(Ljava/lang/String;)V
    //   45: athrow
    //   46: aload_0
    //   47: getfield 505	com/sun/security/auth/module/KeyStoreLoginModule:token	Z
    //   50: ifeq +20 -> 70
    //   53: aload_0
    //   54: getfield 503	com/sun/security/auth/module/KeyStoreLoginModule:nullStream	Z
    //   57: ifne +13 -> 70
    //   60: new 353	javax/security/auth/login/LoginException
    //   63: dup
    //   64: ldc 45
    //   66: invokespecial 596	javax/security/auth/login/LoginException:<init>	(Ljava/lang/String;)V
    //   69: athrow
    //   70: aload_0
    //   71: getfield 505	com/sun/security/auth/module/KeyStoreLoginModule:token	Z
    //   74: ifeq +20 -> 94
    //   77: aload_0
    //   78: getfield 513	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPasswordURL	Ljava/lang/String;
    //   81: ifnull +13 -> 94
    //   84: new 353	javax/security/auth/login/LoginException
    //   87: dup
    //   88: ldc 46
    //   90: invokespecial 596	javax/security/auth/login/LoginException:<init>	(Ljava/lang/String;)V
    //   93: athrow
    //   94: aload_0
    //   95: getfield 504	com/sun/security/auth/module/KeyStoreLoginModule:protectedPath	Z
    //   98: ifeq +27 -> 125
    //   101: aload_0
    //   102: getfield 509	com/sun/security/auth/module/KeyStoreLoginModule:keyStorePasswordURL	Ljava/lang/String;
    //   105: ifnonnull +10 -> 115
    //   108: aload_0
    //   109: getfield 513	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPasswordURL	Ljava/lang/String;
    //   112: ifnull +13 -> 125
    //   115: new 353	javax/security/auth/login/LoginException
    //   118: dup
    //   119: ldc 47
    //   121: invokespecial 596	javax/security/auth/login/LoginException:<init>	(Ljava/lang/String;)V
    //   124: athrow
    //   125: aload_0
    //   126: getfield 504	com/sun/security/auth/module/KeyStoreLoginModule:protectedPath	Z
    //   129: ifeq +11 -> 140
    //   132: aload_0
    //   133: iconst_0
    //   134: invokespecial 535	com/sun/security/auth/module/KeyStoreLoginModule:getAliasAndPasswords	(I)V
    //   137: goto +23 -> 160
    //   140: aload_0
    //   141: getfield 505	com/sun/security/auth/module/KeyStoreLoginModule:token	Z
    //   144: ifeq +11 -> 155
    //   147: aload_0
    //   148: iconst_1
    //   149: invokespecial 535	com/sun/security/auth/module/KeyStoreLoginModule:getAliasAndPasswords	(I)V
    //   152: goto +8 -> 160
    //   155: aload_0
    //   156: iconst_2
    //   157: invokespecial 535	com/sun/security/auth/module/KeyStoreLoginModule:getAliasAndPasswords	(I)V
    //   160: aload_0
    //   161: invokespecial 531	com/sun/security/auth/module/KeyStoreLoginModule:getKeyStoreInfo	()V
    //   164: aload_0
    //   165: getfield 507	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPassword	[C
    //   168: ifnull +27 -> 195
    //   171: aload_0
    //   172: getfield 507	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPassword	[C
    //   175: aload_0
    //   176: getfield 506	com/sun/security/auth/module/KeyStoreLoginModule:keyStorePassword	[C
    //   179: if_acmpeq +16 -> 195
    //   182: aload_0
    //   183: getfield 507	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPassword	[C
    //   186: iconst_0
    //   187: invokestatic 576	java/util/Arrays:fill	([CC)V
    //   190: aload_0
    //   191: aconst_null
    //   192: putfield 507	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPassword	[C
    //   195: aload_0
    //   196: getfield 506	com/sun/security/auth/module/KeyStoreLoginModule:keyStorePassword	[C
    //   199: ifnull +73 -> 272
    //   202: aload_0
    //   203: getfield 506	com/sun/security/auth/module/KeyStoreLoginModule:keyStorePassword	[C
    //   206: iconst_0
    //   207: invokestatic 576	java/util/Arrays:fill	([CC)V
    //   210: aload_0
    //   211: aconst_null
    //   212: putfield 506	com/sun/security/auth/module/KeyStoreLoginModule:keyStorePassword	[C
    //   215: goto +57 -> 272
    //   218: astore_1
    //   219: aload_0
    //   220: getfield 507	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPassword	[C
    //   223: ifnull +27 -> 250
    //   226: aload_0
    //   227: getfield 507	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPassword	[C
    //   230: aload_0
    //   231: getfield 506	com/sun/security/auth/module/KeyStoreLoginModule:keyStorePassword	[C
    //   234: if_acmpeq +16 -> 250
    //   237: aload_0
    //   238: getfield 507	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPassword	[C
    //   241: iconst_0
    //   242: invokestatic 576	java/util/Arrays:fill	([CC)V
    //   245: aload_0
    //   246: aconst_null
    //   247: putfield 507	com/sun/security/auth/module/KeyStoreLoginModule:privateKeyPassword	[C
    //   250: aload_0
    //   251: getfield 506	com/sun/security/auth/module/KeyStoreLoginModule:keyStorePassword	[C
    //   254: ifnull +16 -> 270
    //   257: aload_0
    //   258: getfield 506	com/sun/security/auth/module/KeyStoreLoginModule:keyStorePassword	[C
    //   261: iconst_0
    //   262: invokestatic 576	java/util/Arrays:fill	([CC)V
    //   265: aload_0
    //   266: aconst_null
    //   267: putfield 506	com/sun/security/auth/module/KeyStoreLoginModule:keyStorePassword	[C
    //   270: aload_1
    //   271: athrow
    //   272: aload_0
    //   273: iconst_2
    //   274: putfield 501	com/sun/security/auth/module/KeyStoreLoginModule:status	I
    //   277: iconst_1
    //   278: ireturn
    //   279: iconst_1
    //   280: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	281	0	this	KeyStoreLoginModule
    //   218	53	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   160	164	218	finally
  }
  
  private void getAliasAndPasswords(int paramInt)
    throws LoginException
  {
    if (callbackHandler == null)
    {
      switch (paramInt)
      {
      case 0: 
        checkAlias();
        break;
      case 1: 
        checkAlias();
        checkStorePass();
        break;
      case 2: 
        checkAlias();
        checkStorePass();
        checkKeyPass();
      }
    }
    else
    {
      NameCallback localNameCallback;
      if ((keyStoreAlias == null) || (keyStoreAlias.length() == 0)) {
        localNameCallback = new NameCallback(rb.getString("Keystore.alias."));
      } else {
        localNameCallback = new NameCallback(rb.getString("Keystore.alias."), keyStoreAlias);
      }
      PasswordCallback localPasswordCallback1 = null;
      PasswordCallback localPasswordCallback2 = null;
      switch (paramInt)
      {
      case 0: 
        break;
      case 2: 
        localPasswordCallback2 = new PasswordCallback(rb.getString("Private.key.password.optional."), false);
      case 1: 
        localPasswordCallback1 = new PasswordCallback(rb.getString("Keystore.password."), false);
      }
      prompt(localNameCallback, localPasswordCallback1, localPasswordCallback2);
    }
    if (debug) {
      debugPrint("alias=" + keyStoreAlias);
    }
  }
  
  private void checkAlias()
    throws LoginException
  {
    if (keyStoreAlias == null) {
      throw new LoginException("Need to specify an alias option to use KeyStoreLoginModule non-interactively.");
    }
  }
  
  private void checkStorePass()
    throws LoginException
  {
    if (keyStorePasswordURL == null) {
      throw new LoginException("Need to specify keyStorePasswordURL option to use KeyStoreLoginModule non-interactively.");
    }
    InputStream localInputStream = null;
    try
    {
      localInputStream = new URL(keyStorePasswordURL).openStream();
      keyStorePassword = Password.readPassword(localInputStream);
      LoginException localLoginException1;
      LoginException localLoginException2;
      return;
    }
    catch (IOException localIOException2)
    {
      localLoginException1 = new LoginException("Problem accessing keystore password \"" + keyStorePasswordURL + "\"");
      localLoginException1.initCause(localIOException2);
      throw localLoginException1;
    }
    finally
    {
      if (localInputStream != null) {
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException3)
        {
          localLoginException2 = new LoginException("Problem closing the keystore password stream");
          localLoginException2.initCause(localIOException3);
          throw localLoginException2;
        }
      }
    }
  }
  
  private void checkKeyPass()
    throws LoginException
  {
    if (privateKeyPasswordURL == null)
    {
      privateKeyPassword = keyStorePassword;
    }
    else
    {
      InputStream localInputStream = null;
      try
      {
        localInputStream = new URL(privateKeyPasswordURL).openStream();
        privateKeyPassword = Password.readPassword(localInputStream);
        LoginException localLoginException1;
        LoginException localLoginException2;
        return;
      }
      catch (IOException localIOException2)
      {
        localLoginException1 = new LoginException("Problem accessing private key password \"" + privateKeyPasswordURL + "\"");
        localLoginException1.initCause(localIOException2);
        throw localLoginException1;
      }
      finally
      {
        if (localInputStream != null) {
          try
          {
            localInputStream.close();
          }
          catch (IOException localIOException3)
          {
            localLoginException2 = new LoginException("Problem closing the private key password stream");
            localLoginException2.initCause(localIOException3);
            throw localLoginException2;
          }
        }
      }
    }
  }
  
  private void prompt(NameCallback paramNameCallback, PasswordCallback paramPasswordCallback1, PasswordCallback paramPasswordCallback2)
    throws LoginException
  {
    LoginException localLoginException;
    if (paramPasswordCallback1 == null)
    {
      try
      {
        callbackHandler.handle(new Callback[] { bannerCallback, paramNameCallback, confirmationCallback });
      }
      catch (IOException localIOException1)
      {
        localLoginException = new LoginException("Problem retrieving keystore alias");
        localLoginException.initCause(localIOException1);
        throw localLoginException;
      }
      catch (UnsupportedCallbackException localUnsupportedCallbackException1)
      {
        throw new LoginException("Error: " + localUnsupportedCallbackException1.getCallback().toString() + " is not available to retrieve authentication  information from the user");
      }
      int i = confirmationCallback.getSelectedIndex();
      if (i == 2) {
        throw new LoginException("Login cancelled");
      }
      saveAlias(paramNameCallback);
    }
    else if (paramPasswordCallback2 == null)
    {
      try
      {
        callbackHandler.handle(new Callback[] { bannerCallback, paramNameCallback, paramPasswordCallback1, confirmationCallback });
      }
      catch (IOException localIOException2)
      {
        localLoginException = new LoginException("Problem retrieving keystore alias and password");
        localLoginException.initCause(localIOException2);
        throw localLoginException;
      }
      catch (UnsupportedCallbackException localUnsupportedCallbackException2)
      {
        throw new LoginException("Error: " + localUnsupportedCallbackException2.getCallback().toString() + " is not available to retrieve authentication  information from the user");
      }
      int j = confirmationCallback.getSelectedIndex();
      if (j == 2) {
        throw new LoginException("Login cancelled");
      }
      saveAlias(paramNameCallback);
      saveStorePass(paramPasswordCallback1);
    }
    else
    {
      try
      {
        callbackHandler.handle(new Callback[] { bannerCallback, paramNameCallback, paramPasswordCallback1, paramPasswordCallback2, confirmationCallback });
      }
      catch (IOException localIOException3)
      {
        localLoginException = new LoginException("Problem retrieving keystore alias and passwords");
        localLoginException.initCause(localIOException3);
        throw localLoginException;
      }
      catch (UnsupportedCallbackException localUnsupportedCallbackException3)
      {
        throw new LoginException("Error: " + localUnsupportedCallbackException3.getCallback().toString() + " is not available to retrieve authentication  information from the user");
      }
      int k = confirmationCallback.getSelectedIndex();
      if (k == 2) {
        throw new LoginException("Login cancelled");
      }
      saveAlias(paramNameCallback);
      saveStorePass(paramPasswordCallback1);
      saveKeyPass(paramPasswordCallback2);
    }
  }
  
  private void saveAlias(NameCallback paramNameCallback)
  {
    keyStoreAlias = paramNameCallback.getName();
  }
  
  private void saveStorePass(PasswordCallback paramPasswordCallback)
  {
    keyStorePassword = paramPasswordCallback.getPassword();
    if (keyStorePassword == null) {
      keyStorePassword = new char[0];
    }
    paramPasswordCallback.clearPassword();
  }
  
  private void saveKeyPass(PasswordCallback paramPasswordCallback)
  {
    privateKeyPassword = paramPasswordCallback.getPassword();
    if ((privateKeyPassword == null) || (privateKeyPassword.length == 0)) {
      privateKeyPassword = keyStorePassword;
    }
    paramPasswordCallback.clearPassword();
  }
  
  private void getKeyStoreInfo()
    throws LoginException
  {
    try
    {
      if (keyStoreProvider == null) {
        keyStore = KeyStore.getInstance(keyStoreType);
      } else {
        keyStore = KeyStore.getInstance(keyStoreType, keyStoreProvider);
      }
    }
    catch (KeyStoreException localKeyStoreException1)
    {
      localLoginException1 = new LoginException("The specified keystore type was not available");
      localLoginException1.initCause(localKeyStoreException1);
      throw localLoginException1;
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      LoginException localLoginException1 = new LoginException("The specified keystore provider was not available");
      localLoginException1.initCause(localNoSuchProviderException);
      throw localLoginException1;
    }
    InputStream localInputStream = null;
    try
    {
      if (nullStream)
      {
        keyStore.load(null, keyStorePassword);
      }
      else
      {
        localInputStream = new URL(keyStoreURL).openStream();
        keyStore.load(localInputStream, keyStorePassword);
      }
      LoginException localLoginException2;
      if (localInputStream != null) {
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException1)
        {
          localLoginException2 = new LoginException("Error initializing keystore");
          localLoginException2.initCause(localIOException1);
          throw localLoginException2;
        }
      }
      try
      {
        LoginException localLoginException3;
        fromKeyStore = keyStore.getCertificateChain(keyStoreAlias);
        if ((fromKeyStore == null) || (fromKeyStore.length == 0) || (!(fromKeyStore[0] instanceof X509Certificate))) {
          throw new FailedLoginException("Unable to find X.509 certificate chain in keystore");
        }
        localLinkedList = new LinkedList();
        for (i = 0; i < fromKeyStore.length; i++) {
          localLinkedList.add(fromKeyStore[i]);
        }
        localObject1 = CertificateFactory.getInstance("X.509");
        certP = ((CertificateFactory)localObject1).generateCertPath(localLinkedList);
      }
      catch (KeyStoreException localKeyStoreException2)
      {
        localObject1 = new LoginException("Error using keystore");
        ((LoginException)localObject1).initCause(localKeyStoreException2);
        throw ((Throwable)localObject1);
      }
      catch (CertificateException localCertificateException)
      {
        localObject1 = new LoginException("Error: X.509 Certificate type unavailable");
        ((LoginException)localObject1).initCause(localCertificateException);
        throw ((Throwable)localObject1);
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      localLoginException2 = new LoginException("Incorrect keyStoreURL option");
      localLoginException2.initCause(localMalformedURLException);
      throw localLoginException2;
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      localLoginException2 = new LoginException("Error initializing keystore");
      localLoginException2.initCause(localGeneralSecurityException);
      throw localLoginException2;
    }
    catch (IOException localIOException2)
    {
      localLoginException2 = new LoginException("Error initializing keystore");
      localLoginException2.initCause(localIOException2);
      throw localLoginException2;
    }
    finally
    {
      if (localInputStream != null) {
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException3)
        {
          localLoginException3 = new LoginException("Error initializing keystore");
          localLoginException3.initCause(localIOException3);
          throw localLoginException3;
        }
      }
    }
    try
    {
      LinkedList localLinkedList;
      int i;
      X509Certificate localX509Certificate = (X509Certificate)fromKeyStore[0];
      principal = new X500Principal(localX509Certificate.getSubjectDN().getName());
      localObject1 = keyStore.getKey(keyStoreAlias, privateKeyPassword);
      if ((localObject1 == null) || (!(localObject1 instanceof PrivateKey))) {
        throw new FailedLoginException("Unable to recover key from keystore");
      }
      privateCredential = new X500PrivateCredential(localX509Certificate, (PrivateKey)localObject1, keyStoreAlias);
    }
    catch (KeyStoreException localKeyStoreException3)
    {
      localObject1 = new LoginException("Error using keystore");
      ((LoginException)localObject1).initCause(localKeyStoreException3);
      throw ((Throwable)localObject1);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localObject1 = new LoginException("Error using keystore");
      ((LoginException)localObject1).initCause(localNoSuchAlgorithmException);
      throw ((Throwable)localObject1);
    }
    catch (UnrecoverableKeyException localUnrecoverableKeyException)
    {
      Object localObject1 = new FailedLoginException("Unable to recover key from keystore");
      ((FailedLoginException)localObject1).initCause(localUnrecoverableKeyException);
      throw ((Throwable)localObject1);
    }
    if (debug) {
      debugPrint("principal=" + principal + "\n certificate=" + privateCredential.getCertificate() + "\n alias =" + privateCredential.getAlias());
    }
  }
  
  public boolean commit()
    throws LoginException
  {
    switch (status)
    {
    case 0: 
    default: 
      throw new LoginException("The login module is not initialized");
    case 1: 
      logoutInternal();
      throw new LoginException("Authentication failed");
    case 2: 
      if (commitInternal()) {
        return true;
      }
      logoutInternal();
      throw new LoginException("Unable to retrieve certificates");
    }
    return true;
  }
  
  private boolean commitInternal()
    throws LoginException
  {
    if (subject.isReadOnly()) {
      throw new LoginException("Subject is set readonly");
    }
    subject.getPrincipals().add(principal);
    subject.getPublicCredentials().add(certP);
    subject.getPrivateCredentials().add(privateCredential);
    status = 3;
    return true;
  }
  
  public boolean abort()
    throws LoginException
  {
    switch (status)
    {
    case 0: 
    default: 
      return false;
    case 1: 
      return false;
    case 2: 
      logoutInternal();
      return true;
    }
    logoutInternal();
    return true;
  }
  
  public boolean logout()
    throws LoginException
  {
    if (debug) {
      debugPrint("Entering logout " + status);
    }
    switch (status)
    {
    case 0: 
      throw new LoginException("The login module is not initialized");
    case 1: 
    case 2: 
    default: 
      return false;
    }
    logoutInternal();
    return true;
  }
  
  private void logoutInternal()
    throws LoginException
  {
    if (debug) {
      debugPrint("Entering logoutInternal");
    }
    Object localObject1 = null;
    Provider localProvider = keyStore.getProvider();
    Object localObject2;
    if ((localProvider instanceof AuthProvider))
    {
      localObject2 = (AuthProvider)localProvider;
      try
      {
        ((AuthProvider)localObject2).logout();
        if (debug) {
          debugPrint("logged out of KeyStore AuthProvider");
        }
      }
      catch (LoginException localLoginException1)
      {
        localObject1 = localLoginException1;
      }
    }
    if (subject.isReadOnly())
    {
      principal = null;
      certP = null;
      status = 1;
      localObject2 = subject.getPrivateCredentials().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Object localObject3 = ((Iterator)localObject2).next();
        if (privateCredential.equals(localObject3))
        {
          privateCredential = null;
          try
          {
            ((Destroyable)localObject3).destroy();
            if (debug) {
              debugPrint("Destroyed private credential, " + localObject3.getClass().getName());
            }
          }
          catch (DestroyFailedException localDestroyFailedException)
          {
            LoginException localLoginException2 = new LoginException("Unable to destroy private credential, " + localObject3.getClass().getName());
            localLoginException2.initCause(localDestroyFailedException);
            throw localLoginException2;
          }
        }
      }
      throw new LoginException("Unable to remove Principal (X500Principal ) and public credential (certificatepath) from read-only Subject");
    }
    if (principal != null)
    {
      subject.getPrincipals().remove(principal);
      principal = null;
    }
    if (certP != null)
    {
      subject.getPublicCredentials().remove(certP);
      certP = null;
    }
    if (privateCredential != null)
    {
      subject.getPrivateCredentials().remove(privateCredential);
      privateCredential = null;
    }
    if (localObject1 != null) {
      throw ((Throwable)localObject1);
    }
    status = 1;
  }
  
  private void debugPrint(String paramString)
  {
    if (paramString == null) {
      System.err.println();
    } else {
      System.err.println("Debug KeyStoreLoginModule: " + paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\module\KeyStoreLoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */