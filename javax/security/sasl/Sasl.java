package javax.security.sasl;

import java.security.Provider;
import java.security.Security;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.callback.CallbackHandler;

public class Sasl
{
  public static final String QOP = "javax.security.sasl.qop";
  public static final String STRENGTH = "javax.security.sasl.strength";
  public static final String SERVER_AUTH = "javax.security.sasl.server.authentication";
  public static final String BOUND_SERVER_NAME = "javax.security.sasl.bound.server.name";
  public static final String MAX_BUFFER = "javax.security.sasl.maxbuffer";
  public static final String RAW_SEND_SIZE = "javax.security.sasl.rawsendsize";
  public static final String REUSE = "javax.security.sasl.reuse";
  public static final String POLICY_NOPLAINTEXT = "javax.security.sasl.policy.noplaintext";
  public static final String POLICY_NOACTIVE = "javax.security.sasl.policy.noactive";
  public static final String POLICY_NODICTIONARY = "javax.security.sasl.policy.nodictionary";
  public static final String POLICY_NOANONYMOUS = "javax.security.sasl.policy.noanonymous";
  public static final String POLICY_FORWARD_SECRECY = "javax.security.sasl.policy.forward";
  public static final String POLICY_PASS_CREDENTIALS = "javax.security.sasl.policy.credentials";
  public static final String CREDENTIALS = "javax.security.sasl.credentials";
  
  private Sasl() {}
  
  public static SaslClient createSaslClient(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    SaslClient localSaslClient = null;
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str2;
      if ((str2 = paramArrayOfString[i]) == null) {
        throw new NullPointerException("Mechanism name cannot be null");
      }
      if (str2.length() != 0)
      {
        String str3 = "SaslClientFactory." + str2;
        Provider[] arrayOfProvider = Security.getProviders(str3);
        for (int j = 0; (arrayOfProvider != null) && (j < arrayOfProvider.length); j++)
        {
          String str1 = arrayOfProvider[j].getProperty(str3);
          if (str1 != null)
          {
            SaslClientFactory localSaslClientFactory = (SaslClientFactory)loadFactory(arrayOfProvider[j], str1);
            if (localSaslClientFactory != null)
            {
              localSaslClient = localSaslClientFactory.createSaslClient(new String[] { paramArrayOfString[i] }, paramString1, paramString2, paramString3, paramMap, paramCallbackHandler);
              if (localSaslClient != null) {
                return localSaslClient;
              }
            }
          }
        }
      }
    }
    return null;
  }
  
  private static Object loadFactory(Provider paramProvider, String paramString)
    throws SaslException
  {
    try
    {
      ClassLoader localClassLoader = paramProvider.getClass().getClassLoader();
      Class localClass = Class.forName(paramString, true, localClassLoader);
      return localClass.newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new SaslException("Cannot load class " + paramString, localClassNotFoundException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new SaslException("Cannot instantiate class " + paramString, localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new SaslException("Cannot access class " + paramString, localIllegalAccessException);
    }
    catch (SecurityException localSecurityException)
    {
      throw new SaslException("Cannot access class " + paramString, localSecurityException);
    }
  }
  
  public static SaslServer createSaslServer(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    SaslServer localSaslServer = null;
    if (paramString1 == null) {
      throw new NullPointerException("Mechanism name cannot be null");
    }
    if (paramString1.length() == 0) {
      return null;
    }
    String str2 = "SaslServerFactory." + paramString1;
    Provider[] arrayOfProvider = Security.getProviders(str2);
    for (int i = 0; (arrayOfProvider != null) && (i < arrayOfProvider.length); i++)
    {
      String str1 = arrayOfProvider[i].getProperty(str2);
      if (str1 == null) {
        throw new SaslException("Provider does not support " + str2);
      }
      SaslServerFactory localSaslServerFactory = (SaslServerFactory)loadFactory(arrayOfProvider[i], str1);
      if (localSaslServerFactory != null)
      {
        localSaslServer = localSaslServerFactory.createSaslServer(paramString1, paramString2, paramString3, paramMap, paramCallbackHandler);
        if (localSaslServer != null) {
          return localSaslServer;
        }
      }
    }
    return null;
  }
  
  public static Enumeration<SaslClientFactory> getSaslClientFactories()
  {
    Set localSet = getFactories("SaslClientFactory");
    Iterator localIterator = localSet.iterator();
    new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return val$iter.hasNext();
      }
      
      public SaslClientFactory nextElement()
      {
        return (SaslClientFactory)val$iter.next();
      }
    };
  }
  
  public static Enumeration<SaslServerFactory> getSaslServerFactories()
  {
    Set localSet = getFactories("SaslServerFactory");
    Iterator localIterator = localSet.iterator();
    new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return val$iter.hasNext();
      }
      
      public SaslServerFactory nextElement()
      {
        return (SaslServerFactory)val$iter.next();
      }
    };
  }
  
  private static Set<Object> getFactories(String paramString)
  {
    HashSet localHashSet1 = new HashSet();
    if ((paramString == null) || (paramString.length() == 0) || (paramString.endsWith("."))) {
      return localHashSet1;
    }
    Provider[] arrayOfProvider = Security.getProviders();
    HashSet localHashSet2 = new HashSet();
    for (int i = 0; i < arrayOfProvider.length; i++)
    {
      localHashSet2.clear();
      Enumeration localEnumeration = arrayOfProvider[i].keys();
      while (localEnumeration.hasMoreElements())
      {
        String str1 = (String)localEnumeration.nextElement();
        if ((str1.startsWith(paramString)) && (str1.indexOf(" ") < 0))
        {
          String str2 = arrayOfProvider[i].getProperty(str1);
          if (!localHashSet2.contains(str2))
          {
            localHashSet2.add(str2);
            try
            {
              Object localObject = loadFactory(arrayOfProvider[i], str2);
              if (localObject != null) {
                localHashSet1.add(localObject);
              }
            }
            catch (Exception localException) {}
          }
        }
      }
    }
    return Collections.unmodifiableSet(localHashSet1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\sasl\Sasl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */