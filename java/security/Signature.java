package java.security;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;
import sun.security.jca.ServiceId;
import sun.security.util.Debug;

public abstract class Signature
  extends SignatureSpi
{
  private static final Debug debug = Debug.getInstance("jca", "Signature");
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  private static final boolean skipDebug = (Debug.isOn("engine=")) && (!Debug.isOn("signature"));
  private String algorithm;
  Provider provider;
  protected static final int UNINITIALIZED = 0;
  protected static final int SIGN = 2;
  protected static final int VERIFY = 3;
  protected int state = 0;
  private static final String RSA_SIGNATURE = "NONEwithRSA";
  private static final String RSA_CIPHER = "RSA/ECB/PKCS1Padding";
  private static final List<ServiceId> rsaIds = Arrays.asList(new ServiceId[] { new ServiceId("Signature", "NONEwithRSA"), new ServiceId("Cipher", "RSA/ECB/PKCS1Padding"), new ServiceId("Cipher", "RSA/ECB"), new ServiceId("Cipher", "RSA//PKCS1Padding"), new ServiceId("Cipher", "RSA") });
  private static final Map<String, Boolean> signatureInfo = new ConcurrentHashMap();
  
  protected Signature(String paramString)
  {
    algorithm = paramString;
  }
  
  public static Signature getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    List localList;
    if (paramString.equalsIgnoreCase("NONEwithRSA")) {
      localList = GetInstance.getServices(rsaIds);
    } else {
      localList = GetInstance.getServices("Signature", paramString);
    }
    Iterator localIterator = localList.iterator();
    if (!localIterator.hasNext()) {
      throw new NoSuchAlgorithmException(paramString + " Signature not available");
    }
    NoSuchAlgorithmException localNoSuchAlgorithmException1;
    do
    {
      Provider.Service localService = (Provider.Service)localIterator.next();
      if (isSpi(localService)) {
        return new Delegate(localService, localIterator, paramString);
      }
      try
      {
        GetInstance.Instance localInstance = GetInstance.getInstance(localService, SignatureSpi.class);
        return getInstance(localInstance, paramString);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException2)
      {
        localNoSuchAlgorithmException1 = localNoSuchAlgorithmException2;
      }
    } while (localIterator.hasNext());
    throw localNoSuchAlgorithmException1;
  }
  
  private static Signature getInstance(GetInstance.Instance paramInstance, String paramString)
  {
    Object localObject;
    if ((impl instanceof Signature))
    {
      localObject = (Signature)impl;
      algorithm = paramString;
    }
    else
    {
      SignatureSpi localSignatureSpi = (SignatureSpi)impl;
      localObject = new Delegate(localSignatureSpi, paramString);
    }
    provider = provider;
    return (Signature)localObject;
  }
  
  private static boolean isSpi(Provider.Service paramService)
  {
    if (paramService.getType().equals("Cipher")) {
      return true;
    }
    String str = paramService.getClassName();
    Boolean localBoolean = (Boolean)signatureInfo.get(str);
    if (localBoolean == null) {
      try
      {
        Object localObject = paramService.newInstance(null);
        boolean bool = ((localObject instanceof SignatureSpi)) && (!(localObject instanceof Signature));
        if ((debug != null) && (!bool))
        {
          debug.println("Not a SignatureSpi " + str);
          debug.println("Delayed provider selection may not be available for algorithm " + paramService.getAlgorithm());
        }
        localBoolean = Boolean.valueOf(bool);
        signatureInfo.put(str, localBoolean);
      }
      catch (Exception localException)
      {
        return false;
      }
    }
    return localBoolean.booleanValue();
  }
  
  public static Signature getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    if (paramString1.equalsIgnoreCase("NONEwithRSA"))
    {
      if ((paramString2 == null) || (paramString2.length() == 0)) {
        throw new IllegalArgumentException("missing provider");
      }
      localObject = Security.getProvider(paramString2);
      if (localObject == null) {
        throw new NoSuchProviderException("no such provider: " + paramString2);
      }
      return getInstanceRSA((Provider)localObject);
    }
    Object localObject = GetInstance.getInstance("Signature", SignatureSpi.class, paramString1, paramString2);
    return getInstance((GetInstance.Instance)localObject, paramString1);
  }
  
  public static Signature getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    if (paramString.equalsIgnoreCase("NONEwithRSA"))
    {
      if (paramProvider == null) {
        throw new IllegalArgumentException("missing provider");
      }
      return getInstanceRSA(paramProvider);
    }
    GetInstance.Instance localInstance = GetInstance.getInstance("Signature", SignatureSpi.class, paramString, paramProvider);
    return getInstance(localInstance, paramString);
  }
  
  private static Signature getInstanceRSA(Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    Provider.Service localService = paramProvider.getService("Signature", "NONEwithRSA");
    Object localObject;
    if (localService != null)
    {
      localObject = GetInstance.getInstance(localService, SignatureSpi.class);
      return getInstance((GetInstance.Instance)localObject, "NONEwithRSA");
    }
    try
    {
      localObject = Cipher.getInstance("RSA/ECB/PKCS1Padding", paramProvider);
      return new Delegate(new CipherAdapter((Cipher)localObject), "NONEwithRSA");
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      throw new NoSuchAlgorithmException("no such algorithm: NONEwithRSA for provider " + paramProvider.getName(), localGeneralSecurityException);
    }
  }
  
  public final Provider getProvider()
  {
    chooseFirstProvider();
    return provider;
  }
  
  void chooseFirstProvider() {}
  
  public final void initVerify(PublicKey paramPublicKey)
    throws InvalidKeyException
  {
    engineInitVerify(paramPublicKey);
    state = 3;
    if ((!skipDebug) && (pdebug != null)) {
      pdebug.println("Signature." + algorithm + " verification algorithm from: " + provider.getName());
    }
  }
  
  public final void initVerify(Certificate paramCertificate)
    throws InvalidKeyException
  {
    if ((paramCertificate instanceof X509Certificate))
    {
      localObject = (X509Certificate)paramCertificate;
      Set localSet = ((X509Certificate)localObject).getCriticalExtensionOIDs();
      if ((localSet != null) && (!localSet.isEmpty()) && (localSet.contains("2.5.29.15")))
      {
        boolean[] arrayOfBoolean = ((X509Certificate)localObject).getKeyUsage();
        if ((arrayOfBoolean != null) && (arrayOfBoolean[0] == 0)) {
          throw new InvalidKeyException("Wrong key usage");
        }
      }
    }
    Object localObject = paramCertificate.getPublicKey();
    engineInitVerify((PublicKey)localObject);
    state = 3;
    if ((!skipDebug) && (pdebug != null)) {
      pdebug.println("Signature." + algorithm + " verification algorithm from: " + provider.getName());
    }
  }
  
  public final void initSign(PrivateKey paramPrivateKey)
    throws InvalidKeyException
  {
    engineInitSign(paramPrivateKey);
    state = 2;
    if ((!skipDebug) && (pdebug != null)) {
      pdebug.println("Signature." + algorithm + " signing algorithm from: " + provider.getName());
    }
  }
  
  public final void initSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom)
    throws InvalidKeyException
  {
    engineInitSign(paramPrivateKey, paramSecureRandom);
    state = 2;
    if ((!skipDebug) && (pdebug != null)) {
      pdebug.println("Signature." + algorithm + " signing algorithm from: " + provider.getName());
    }
  }
  
  public final byte[] sign()
    throws SignatureException
  {
    if (state == 2) {
      return engineSign();
    }
    throw new SignatureException("object not initialized for signing");
  }
  
  public final int sign(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("No output buffer given");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("offset or len is less than 0");
    }
    if (paramArrayOfByte.length - paramInt1 < paramInt2) {
      throw new IllegalArgumentException("Output buffer too small for specified offset and length");
    }
    if (state != 2) {
      throw new SignatureException("object not initialized for signing");
    }
    return engineSign(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public final boolean verify(byte[] paramArrayOfByte)
    throws SignatureException
  {
    if (state == 3) {
      return engineVerify(paramArrayOfByte);
    }
    throw new SignatureException("object not initialized for verification");
  }
  
  public final boolean verify(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    if (state == 3)
    {
      if (paramArrayOfByte == null) {
        throw new IllegalArgumentException("signature is null");
      }
      if ((paramInt1 < 0) || (paramInt2 < 0)) {
        throw new IllegalArgumentException("offset or length is less than 0");
      }
      if (paramArrayOfByte.length - paramInt1 < paramInt2) {
        throw new IllegalArgumentException("signature too small for specified offset and length");
      }
      return engineVerify(paramArrayOfByte, paramInt1, paramInt2);
    }
    throw new SignatureException("object not initialized for verification");
  }
  
  public final void update(byte paramByte)
    throws SignatureException
  {
    if ((state == 3) || (state == 2)) {
      engineUpdate(paramByte);
    } else {
      throw new SignatureException("object not initialized for signature or verification");
    }
  }
  
  public final void update(byte[] paramArrayOfByte)
    throws SignatureException
  {
    update(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public final void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    if ((state == 2) || (state == 3))
    {
      if (paramArrayOfByte == null) {
        throw new IllegalArgumentException("data is null");
      }
      if ((paramInt1 < 0) || (paramInt2 < 0)) {
        throw new IllegalArgumentException("off or len is less than 0");
      }
      if (paramArrayOfByte.length - paramInt1 < paramInt2) {
        throw new IllegalArgumentException("data too small for specified offset and length");
      }
      engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
    }
    else
    {
      throw new SignatureException("object not initialized for signature or verification");
    }
  }
  
  public final void update(ByteBuffer paramByteBuffer)
    throws SignatureException
  {
    if ((state != 2) && (state != 3)) {
      throw new SignatureException("object not initialized for signature or verification");
    }
    if (paramByteBuffer == null) {
      throw new NullPointerException();
    }
    engineUpdate(paramByteBuffer);
  }
  
  public final String getAlgorithm()
  {
    return algorithm;
  }
  
  public String toString()
  {
    String str = "";
    switch (state)
    {
    case 0: 
      str = "<not initialized>";
      break;
    case 3: 
      str = "<initialized for verifying>";
      break;
    case 2: 
      str = "<initialized for signing>";
    }
    return "Signature object: " + getAlgorithm() + str;
  }
  
  @Deprecated
  public final void setParameter(String paramString, Object paramObject)
    throws InvalidParameterException
  {
    engineSetParameter(paramString, paramObject);
  }
  
  public final void setParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    engineSetParameter(paramAlgorithmParameterSpec);
  }
  
  public final AlgorithmParameters getParameters()
  {
    return engineGetParameters();
  }
  
  @Deprecated
  public final Object getParameter(String paramString)
    throws InvalidParameterException
  {
    return engineGetParameter(paramString);
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    if ((this instanceof Cloneable)) {
      return super.clone();
    }
    throw new CloneNotSupportedException();
  }
  
  static
  {
    Boolean localBoolean = Boolean.TRUE;
    signatureInfo.put("sun.security.provider.DSA$RawDSA", localBoolean);
    signatureInfo.put("sun.security.provider.DSA$SHA1withDSA", localBoolean);
    signatureInfo.put("sun.security.rsa.RSASignature$MD2withRSA", localBoolean);
    signatureInfo.put("sun.security.rsa.RSASignature$MD5withRSA", localBoolean);
    signatureInfo.put("sun.security.rsa.RSASignature$SHA1withRSA", localBoolean);
    signatureInfo.put("sun.security.rsa.RSASignature$SHA256withRSA", localBoolean);
    signatureInfo.put("sun.security.rsa.RSASignature$SHA384withRSA", localBoolean);
    signatureInfo.put("sun.security.rsa.RSASignature$SHA512withRSA", localBoolean);
    signatureInfo.put("com.sun.net.ssl.internal.ssl.RSASignature", localBoolean);
    signatureInfo.put("sun.security.pkcs11.P11Signature", localBoolean);
  }
  
  private static class CipherAdapter
    extends SignatureSpi
  {
    private final Cipher cipher;
    private ByteArrayOutputStream data;
    
    CipherAdapter(Cipher paramCipher)
    {
      cipher = paramCipher;
    }
    
    protected void engineInitVerify(PublicKey paramPublicKey)
      throws InvalidKeyException
    {
      cipher.init(2, paramPublicKey);
      if (data == null) {
        data = new ByteArrayOutputStream(128);
      } else {
        data.reset();
      }
    }
    
    protected void engineInitSign(PrivateKey paramPrivateKey)
      throws InvalidKeyException
    {
      cipher.init(1, paramPrivateKey);
      data = null;
    }
    
    protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom)
      throws InvalidKeyException
    {
      cipher.init(1, paramPrivateKey, paramSecureRandom);
      data = null;
    }
    
    protected void engineUpdate(byte paramByte)
      throws SignatureException
    {
      engineUpdate(new byte[] { paramByte }, 0, 1);
    }
    
    protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws SignatureException
    {
      if (data != null)
      {
        data.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      byte[] arrayOfByte = cipher.update(paramArrayOfByte, paramInt1, paramInt2);
      if ((arrayOfByte != null) && (arrayOfByte.length != 0)) {
        throw new SignatureException("Cipher unexpectedly returned data");
      }
    }
    
    protected byte[] engineSign()
      throws SignatureException
    {
      try
      {
        return cipher.doFinal();
      }
      catch (IllegalBlockSizeException localIllegalBlockSizeException)
      {
        throw new SignatureException("doFinal() failed", localIllegalBlockSizeException);
      }
      catch (BadPaddingException localBadPaddingException)
      {
        throw new SignatureException("doFinal() failed", localBadPaddingException);
      }
    }
    
    protected boolean engineVerify(byte[] paramArrayOfByte)
      throws SignatureException
    {
      try
      {
        byte[] arrayOfByte1 = cipher.doFinal(paramArrayOfByte);
        byte[] arrayOfByte2 = data.toByteArray();
        data.reset();
        return MessageDigest.isEqual(arrayOfByte1, arrayOfByte2);
      }
      catch (BadPaddingException localBadPaddingException)
      {
        return false;
      }
      catch (IllegalBlockSizeException localIllegalBlockSizeException)
      {
        throw new SignatureException("doFinal() failed", localIllegalBlockSizeException);
      }
    }
    
    protected void engineSetParameter(String paramString, Object paramObject)
      throws InvalidParameterException
    {
      throw new InvalidParameterException("Parameters not supported");
    }
    
    protected Object engineGetParameter(String paramString)
      throws InvalidParameterException
    {
      throw new InvalidParameterException("Parameters not supported");
    }
  }
  
  private static class Delegate
    extends Signature
  {
    private SignatureSpi sigSpi;
    private final Object lock;
    private Provider.Service firstService;
    private Iterator<Provider.Service> serviceIterator;
    private static int warnCount = 10;
    private static final int I_PUB = 1;
    private static final int I_PRIV = 2;
    private static final int I_PRIV_SR = 3;
    
    Delegate(SignatureSpi paramSignatureSpi, String paramString)
    {
      super();
      sigSpi = paramSignatureSpi;
      lock = null;
    }
    
    Delegate(Provider.Service paramService, Iterator<Provider.Service> paramIterator, String paramString)
    {
      super();
      firstService = paramService;
      serviceIterator = paramIterator;
      lock = new Object();
    }
    
    public Object clone()
      throws CloneNotSupportedException
    {
      chooseFirstProvider();
      if ((sigSpi instanceof Cloneable))
      {
        SignatureSpi localSignatureSpi = (SignatureSpi)sigSpi.clone();
        Delegate localDelegate = new Delegate(localSignatureSpi, algorithm);
        provider = provider;
        return localDelegate;
      }
      throw new CloneNotSupportedException();
    }
    
    private static SignatureSpi newInstance(Provider.Service paramService)
      throws NoSuchAlgorithmException
    {
      if (paramService.getType().equals("Cipher")) {
        try
        {
          Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", paramService.getProvider());
          return new Signature.CipherAdapter(localCipher);
        }
        catch (NoSuchPaddingException localNoSuchPaddingException)
        {
          throw new NoSuchAlgorithmException(localNoSuchPaddingException);
        }
      }
      Object localObject = paramService.newInstance(null);
      if (!(localObject instanceof SignatureSpi)) {
        throw new NoSuchAlgorithmException("Not a SignatureSpi: " + localObject.getClass().getName());
      }
      return (SignatureSpi)localObject;
    }
    
    void chooseFirstProvider()
    {
      if (sigSpi != null) {
        return;
      }
      synchronized (lock)
      {
        if (sigSpi != null) {
          return;
        }
        if (Signature.debug != null)
        {
          int i = --warnCount;
          if (i >= 0)
          {
            Signature.debug.println("Signature.init() not first method called, disabling delayed provider selection");
            if (i == 0) {
              Signature.debug.println("Further warnings of this type will be suppressed");
            }
            new Exception("Call trace").printStackTrace();
          }
        }
        Object localObject1 = null;
        while ((firstService != null) || (serviceIterator.hasNext()))
        {
          if (firstService != null)
          {
            localObject2 = firstService;
            firstService = null;
          }
          else
          {
            localObject2 = (Provider.Service)serviceIterator.next();
          }
          if (Signature.isSpi((Provider.Service)localObject2)) {
            try
            {
              sigSpi = newInstance((Provider.Service)localObject2);
              provider = ((Provider.Service)localObject2).getProvider();
              firstService = null;
              serviceIterator = null;
              return;
            }
            catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
            {
              localObject1 = localNoSuchAlgorithmException;
            }
          }
        }
        Object localObject2 = new ProviderException("Could not construct SignatureSpi instance");
        if (localObject1 != null) {
          ((ProviderException)localObject2).initCause((Throwable)localObject1);
        }
        throw ((Throwable)localObject2);
      }
    }
    
    private void chooseProvider(int paramInt, Key paramKey, SecureRandom paramSecureRandom)
      throws InvalidKeyException
    {
      synchronized (lock)
      {
        if (sigSpi != null)
        {
          init(sigSpi, paramInt, paramKey, paramSecureRandom);
          return;
        }
        Object localObject1 = null;
        while ((firstService != null) || (serviceIterator.hasNext()))
        {
          if (firstService != null)
          {
            localObject2 = firstService;
            firstService = null;
          }
          else
          {
            localObject2 = (Provider.Service)serviceIterator.next();
          }
          if ((((Provider.Service)localObject2).supportsParameter(paramKey)) && (Signature.isSpi((Provider.Service)localObject2))) {
            try
            {
              SignatureSpi localSignatureSpi = newInstance((Provider.Service)localObject2);
              init(localSignatureSpi, paramInt, paramKey, paramSecureRandom);
              provider = ((Provider.Service)localObject2).getProvider();
              sigSpi = localSignatureSpi;
              firstService = null;
              serviceIterator = null;
              return;
            }
            catch (Exception localException)
            {
              if (localObject1 == null) {
                localObject1 = localException;
              }
            }
          }
        }
        if ((localObject1 instanceof InvalidKeyException)) {
          throw ((InvalidKeyException)localObject1);
        }
        if ((localObject1 instanceof RuntimeException)) {
          throw ((RuntimeException)localObject1);
        }
        Object localObject2 = paramKey != null ? paramKey.getClass().getName() : "(null)";
        throw new InvalidKeyException("No installed provider supports this key: " + (String)localObject2, (Throwable)localObject1);
      }
    }
    
    private void init(SignatureSpi paramSignatureSpi, int paramInt, Key paramKey, SecureRandom paramSecureRandom)
      throws InvalidKeyException
    {
      switch (paramInt)
      {
      case 1: 
        paramSignatureSpi.engineInitVerify((PublicKey)paramKey);
        break;
      case 2: 
        paramSignatureSpi.engineInitSign((PrivateKey)paramKey);
        break;
      case 3: 
        paramSignatureSpi.engineInitSign((PrivateKey)paramKey, paramSecureRandom);
        break;
      default: 
        throw new AssertionError("Internal error: " + paramInt);
      }
    }
    
    protected void engineInitVerify(PublicKey paramPublicKey)
      throws InvalidKeyException
    {
      if (sigSpi != null) {
        sigSpi.engineInitVerify(paramPublicKey);
      } else {
        chooseProvider(1, paramPublicKey, null);
      }
    }
    
    protected void engineInitSign(PrivateKey paramPrivateKey)
      throws InvalidKeyException
    {
      if (sigSpi != null) {
        sigSpi.engineInitSign(paramPrivateKey);
      } else {
        chooseProvider(2, paramPrivateKey, null);
      }
    }
    
    protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom)
      throws InvalidKeyException
    {
      if (sigSpi != null) {
        sigSpi.engineInitSign(paramPrivateKey, paramSecureRandom);
      } else {
        chooseProvider(3, paramPrivateKey, paramSecureRandom);
      }
    }
    
    protected void engineUpdate(byte paramByte)
      throws SignatureException
    {
      chooseFirstProvider();
      sigSpi.engineUpdate(paramByte);
    }
    
    protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws SignatureException
    {
      chooseFirstProvider();
      sigSpi.engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    protected void engineUpdate(ByteBuffer paramByteBuffer)
    {
      chooseFirstProvider();
      sigSpi.engineUpdate(paramByteBuffer);
    }
    
    protected byte[] engineSign()
      throws SignatureException
    {
      chooseFirstProvider();
      return sigSpi.engineSign();
    }
    
    protected int engineSign(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws SignatureException
    {
      chooseFirstProvider();
      return sigSpi.engineSign(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    protected boolean engineVerify(byte[] paramArrayOfByte)
      throws SignatureException
    {
      chooseFirstProvider();
      return sigSpi.engineVerify(paramArrayOfByte);
    }
    
    protected boolean engineVerify(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws SignatureException
    {
      chooseFirstProvider();
      return sigSpi.engineVerify(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    protected void engineSetParameter(String paramString, Object paramObject)
      throws InvalidParameterException
    {
      chooseFirstProvider();
      sigSpi.engineSetParameter(paramString, paramObject);
    }
    
    protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      chooseFirstProvider();
      sigSpi.engineSetParameter(paramAlgorithmParameterSpec);
    }
    
    protected Object engineGetParameter(String paramString)
      throws InvalidParameterException
    {
      chooseFirstProvider();
      return sigSpi.engineGetParameter(paramString);
    }
    
    protected AlgorithmParameters engineGetParameters()
    {
      chooseFirstProvider();
      return sigSpi.engineGetParameters();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Signature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */