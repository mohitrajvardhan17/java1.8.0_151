package com.sun.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Provider.Service;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;

final class SSLSecurity
{
  private SSLSecurity() {}
  
  private static Provider.Service getService(String paramString1, String paramString2)
  {
    ProviderList localProviderList = Providers.getProviderList();
    Iterator localIterator = localProviderList.providers().iterator();
    while (localIterator.hasNext())
    {
      Provider localProvider = (Provider)localIterator.next();
      Provider.Service localService = localProvider.getService(paramString1, paramString2);
      if (localService != null) {
        return localService;
      }
    }
    return null;
  }
  
  private static Object[] getImpl1(String paramString1, String paramString2, Provider.Service paramService)
    throws NoSuchAlgorithmException
  {
    Provider localProvider = paramService.getProvider();
    String str = paramService.getClassName();
    Class localClass1;
    try
    {
      ClassLoader localClassLoader = localProvider.getClass().getClassLoader();
      if (localClassLoader == null) {
        localClass1 = Class.forName(str);
      } else {
        localClass1 = localClassLoader.loadClass(str);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      throw new NoSuchAlgorithmException("Class " + str + " configured for " + paramString2 + " not found: " + localClassNotFoundException1.getMessage());
    }
    catch (SecurityException localSecurityException)
    {
      throw new NoSuchAlgorithmException("Class " + str + " configured for " + paramString2 + " cannot be accessed: " + localSecurityException.getMessage());
    }
    try
    {
      Object localObject2 = null;
      Class localClass2;
      if (((localClass2 = Class.forName("javax.net.ssl." + paramString2 + "Spi")) != null) && (checkSuperclass(localClass1, localClass2)))
      {
        if (paramString2.equals("SSLContext")) {
          localObject2 = new SSLContextSpiWrapper(paramString1, localProvider);
        } else if (paramString2.equals("TrustManagerFactory")) {
          localObject2 = new TrustManagerFactorySpiWrapper(paramString1, localProvider);
        } else if (paramString2.equals("KeyManagerFactory")) {
          localObject2 = new KeyManagerFactorySpiWrapper(paramString1, localProvider);
        } else {
          throw new IllegalStateException("Class " + localClass1.getName() + " unknown engineType wrapper:" + paramString2);
        }
      }
      else if (((localObject1 = Class.forName("com.sun.net.ssl." + paramString2 + "Spi")) != null) && (checkSuperclass(localClass1, (Class)localObject1))) {
        localObject2 = paramService.newInstance(null);
      }
      if (localObject2 != null) {
        return new Object[] { localObject2, localProvider };
      }
      throw new NoSuchAlgorithmException("Couldn't locate correct object or wrapper: " + paramString2 + " " + paramString1);
    }
    catch (ClassNotFoundException localClassNotFoundException2)
    {
      Object localObject1 = new IllegalStateException("Engine Class Not Found for " + paramString2);
      ((IllegalStateException)localObject1).initCause(localClassNotFoundException2);
      throw ((Throwable)localObject1);
    }
  }
  
  static Object[] getImpl(String paramString1, String paramString2, String paramString3)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    Provider.Service localService;
    if (paramString3 != null)
    {
      ProviderList localProviderList = Providers.getProviderList();
      Provider localProvider = localProviderList.getProvider(paramString3);
      if (localProvider == null) {
        throw new NoSuchProviderException("No such provider: " + paramString3);
      }
      localService = localProvider.getService(paramString2, paramString1);
    }
    else
    {
      localService = getService(paramString2, paramString1);
    }
    if (localService == null) {
      throw new NoSuchAlgorithmException("Algorithm " + paramString1 + " not available");
    }
    return getImpl1(paramString1, paramString2, localService);
  }
  
  static Object[] getImpl(String paramString1, String paramString2, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    Provider.Service localService = paramProvider.getService(paramString2, paramString1);
    if (localService == null) {
      throw new NoSuchAlgorithmException("No such algorithm: " + paramString1);
    }
    return getImpl1(paramString1, paramString2, localService);
  }
  
  private static boolean checkSuperclass(Class<?> paramClass1, Class<?> paramClass2)
  {
    if ((paramClass1 == null) || (paramClass2 == null)) {
      return false;
    }
    while (!paramClass1.equals(paramClass2))
    {
      paramClass1 = paramClass1.getSuperclass();
      if (paramClass1 == null) {
        return false;
      }
    }
    return true;
  }
  
  static Object[] truncateArray(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2)
  {
    for (int i = 0; i < paramArrayOfObject2.length; i++) {
      paramArrayOfObject2[i] = paramArrayOfObject1[i];
    }
    return paramArrayOfObject2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\SSLSecurity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */