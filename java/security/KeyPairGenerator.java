package java.security;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;
import sun.security.jca.JCAUtil;
import sun.security.util.Debug;

public abstract class KeyPairGenerator
  extends KeyPairGeneratorSpi
{
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  private static final boolean skipDebug = (Debug.isOn("engine=")) && (!Debug.isOn("keypairgenerator"));
  private final String algorithm;
  Provider provider;
  
  protected KeyPairGenerator(String paramString)
  {
    algorithm = paramString;
  }
  
  public String getAlgorithm()
  {
    return algorithm;
  }
  
  private static KeyPairGenerator getInstance(GetInstance.Instance paramInstance, String paramString)
  {
    Object localObject;
    if ((impl instanceof KeyPairGenerator))
    {
      localObject = (KeyPairGenerator)impl;
    }
    else
    {
      KeyPairGeneratorSpi localKeyPairGeneratorSpi = (KeyPairGeneratorSpi)impl;
      localObject = new Delegate(localKeyPairGeneratorSpi, paramString);
    }
    provider = provider;
    if ((!skipDebug) && (pdebug != null)) {
      pdebug.println("KeyPairGenerator." + paramString + " algorithm from: " + provider.getName());
    }
    return (KeyPairGenerator)localObject;
  }
  
  public static KeyPairGenerator getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    List localList = GetInstance.getServices("KeyPairGenerator", paramString);
    Iterator localIterator = localList.iterator();
    if (!localIterator.hasNext()) {
      throw new NoSuchAlgorithmException(paramString + " KeyPairGenerator not available");
    }
    Object localObject = null;
    do
    {
      Provider.Service localService = (Provider.Service)localIterator.next();
      try
      {
        GetInstance.Instance localInstance = GetInstance.getInstance(localService, KeyPairGeneratorSpi.class);
        if ((impl instanceof KeyPairGenerator)) {
          return getInstance(localInstance, paramString);
        }
        return new Delegate(localInstance, localIterator, paramString);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        if (localObject == null) {
          localObject = localNoSuchAlgorithmException;
        }
      }
    } while (localIterator.hasNext());
    throw ((Throwable)localObject);
  }
  
  public static KeyPairGenerator getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("KeyPairGenerator", KeyPairGeneratorSpi.class, paramString1, paramString2);
    return getInstance(localInstance, paramString1);
  }
  
  public static KeyPairGenerator getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("KeyPairGenerator", KeyPairGeneratorSpi.class, paramString, paramProvider);
    return getInstance(localInstance, paramString);
  }
  
  public final Provider getProvider()
  {
    disableFailover();
    return provider;
  }
  
  void disableFailover() {}
  
  public void initialize(int paramInt)
  {
    initialize(paramInt, JCAUtil.getSecureRandom());
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {}
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    initialize(paramAlgorithmParameterSpec, JCAUtil.getSecureRandom());
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {}
  
  public final KeyPair genKeyPair()
  {
    return generateKeyPair();
  }
  
  public KeyPair generateKeyPair()
  {
    return null;
  }
  
  private static final class Delegate
    extends KeyPairGenerator
  {
    private volatile KeyPairGeneratorSpi spi;
    private final Object lock = new Object();
    private Iterator<Provider.Service> serviceIterator;
    private static final int I_NONE = 1;
    private static final int I_SIZE = 2;
    private static final int I_PARAMS = 3;
    private int initType;
    private int initKeySize;
    private AlgorithmParameterSpec initParams;
    private SecureRandom initRandom;
    
    Delegate(KeyPairGeneratorSpi paramKeyPairGeneratorSpi, String paramString)
    {
      super();
      spi = paramKeyPairGeneratorSpi;
    }
    
    Delegate(GetInstance.Instance paramInstance, Iterator<Provider.Service> paramIterator, String paramString)
    {
      super();
      spi = ((KeyPairGeneratorSpi)impl);
      provider = provider;
      serviceIterator = paramIterator;
      initType = 1;
      if ((!KeyPairGenerator.skipDebug) && (KeyPairGenerator.pdebug != null)) {
        KeyPairGenerator.pdebug.println("KeyPairGenerator." + paramString + " algorithm from: " + provider.getName());
      }
    }
    
    private KeyPairGeneratorSpi nextSpi(KeyPairGeneratorSpi paramKeyPairGeneratorSpi, boolean paramBoolean)
    {
      synchronized (lock)
      {
        if ((paramKeyPairGeneratorSpi != null) && (paramKeyPairGeneratorSpi != spi)) {
          return spi;
        }
        if (serviceIterator == null) {
          return null;
        }
        while (serviceIterator.hasNext())
        {
          Provider.Service localService = (Provider.Service)serviceIterator.next();
          try
          {
            Object localObject1 = localService.newInstance(null);
            if (((localObject1 instanceof KeyPairGeneratorSpi)) && (!(localObject1 instanceof KeyPairGenerator)))
            {
              KeyPairGeneratorSpi localKeyPairGeneratorSpi = (KeyPairGeneratorSpi)localObject1;
              if (paramBoolean) {
                if (initType == 2) {
                  localKeyPairGeneratorSpi.initialize(initKeySize, initRandom);
                } else if (initType == 3) {
                  localKeyPairGeneratorSpi.initialize(initParams, initRandom);
                } else if (initType != 1) {
                  throw new AssertionError("KeyPairGenerator initType: " + initType);
                }
              }
              provider = localService.getProvider();
              spi = localKeyPairGeneratorSpi;
              return localKeyPairGeneratorSpi;
            }
          }
          catch (Exception localException) {}
        }
        disableFailover();
        return null;
      }
    }
    
    void disableFailover()
    {
      serviceIterator = null;
      initType = 0;
      initParams = null;
      initRandom = null;
    }
    
    public void initialize(int paramInt, SecureRandom paramSecureRandom)
    {
      if (serviceIterator == null)
      {
        spi.initialize(paramInt, paramSecureRandom);
        return;
      }
      Object localObject = null;
      KeyPairGeneratorSpi localKeyPairGeneratorSpi = spi;
      do
      {
        try
        {
          localKeyPairGeneratorSpi.initialize(paramInt, paramSecureRandom);
          initType = 2;
          initKeySize = paramInt;
          initParams = null;
          initRandom = paramSecureRandom;
          return;
        }
        catch (RuntimeException localRuntimeException)
        {
          if (localObject == null) {
            localObject = localRuntimeException;
          }
          localKeyPairGeneratorSpi = nextSpi(localKeyPairGeneratorSpi, false);
        }
      } while (localKeyPairGeneratorSpi != null);
      throw ((Throwable)localObject);
    }
    
    public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
      throws InvalidAlgorithmParameterException
    {
      if (serviceIterator == null)
      {
        spi.initialize(paramAlgorithmParameterSpec, paramSecureRandom);
        return;
      }
      Object localObject = null;
      KeyPairGeneratorSpi localKeyPairGeneratorSpi = spi;
      do
      {
        try
        {
          localKeyPairGeneratorSpi.initialize(paramAlgorithmParameterSpec, paramSecureRandom);
          initType = 3;
          initKeySize = 0;
          initParams = paramAlgorithmParameterSpec;
          initRandom = paramSecureRandom;
          return;
        }
        catch (Exception localException)
        {
          if (localObject == null) {
            localObject = localException;
          }
          localKeyPairGeneratorSpi = nextSpi(localKeyPairGeneratorSpi, false);
        }
      } while (localKeyPairGeneratorSpi != null);
      if ((localObject instanceof RuntimeException)) {
        throw ((RuntimeException)localObject);
      }
      throw ((InvalidAlgorithmParameterException)localObject);
    }
    
    public KeyPair generateKeyPair()
    {
      if (serviceIterator == null) {
        return spi.generateKeyPair();
      }
      Object localObject = null;
      KeyPairGeneratorSpi localKeyPairGeneratorSpi = spi;
      do
      {
        try
        {
          return localKeyPairGeneratorSpi.generateKeyPair();
        }
        catch (RuntimeException localRuntimeException)
        {
          if (localObject == null) {
            localObject = localRuntimeException;
          }
          localKeyPairGeneratorSpi = nextSpi(localKeyPairGeneratorSpi, true);
        }
      } while (localKeyPairGeneratorSpi != null);
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\KeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */