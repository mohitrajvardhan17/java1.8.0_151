package sun.security.jca;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.ProviderException;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;

final class ProviderConfig
{
  private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
  private static final String P11_SOL_NAME = "sun.security.pkcs11.SunPKCS11";
  private static final String P11_SOL_ARG = "${java.home}/lib/security/sunpkcs11-solaris.cfg";
  private static final int MAX_LOAD_TRIES = 30;
  private static final Class[] CL_STRING = { String.class };
  private final String className;
  private final String argument;
  private int tries;
  private volatile Provider provider;
  private boolean isLoading;
  
  ProviderConfig(String paramString1, String paramString2)
  {
    if ((paramString1.equals("sun.security.pkcs11.SunPKCS11")) && (paramString2.equals("${java.home}/lib/security/sunpkcs11-solaris.cfg"))) {
      checkSunPKCS11Solaris();
    }
    className = paramString1;
    argument = expand(paramString2);
  }
  
  ProviderConfig(String paramString)
  {
    this(paramString, "");
  }
  
  ProviderConfig(Provider paramProvider)
  {
    className = paramProvider.getClass().getName();
    argument = "";
    provider = paramProvider;
  }
  
  private void checkSunPKCS11Solaris()
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        File localFile = new File("/usr/lib/libpkcs11.so");
        if (!localFile.exists()) {
          return Boolean.FALSE;
        }
        if ("false".equalsIgnoreCase(System.getProperty("sun.security.pkcs11.enable-solaris"))) {
          return Boolean.FALSE;
        }
        return Boolean.TRUE;
      }
    });
    if (localBoolean == Boolean.FALSE) {
      tries = 30;
    }
  }
  
  private boolean hasArgument()
  {
    return argument.length() != 0;
  }
  
  private boolean shouldLoad()
  {
    return tries < 30;
  }
  
  private void disableLoad()
  {
    tries = 30;
  }
  
  boolean isLoaded()
  {
    return provider != null;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ProviderConfig)) {
      return false;
    }
    ProviderConfig localProviderConfig = (ProviderConfig)paramObject;
    return (className.equals(className)) && (argument.equals(argument));
  }
  
  public int hashCode()
  {
    return className.hashCode() + argument.hashCode();
  }
  
  public String toString()
  {
    if (hasArgument()) {
      return className + "('" + argument + "')";
    }
    return className;
  }
  
  /* Error */
  synchronized Provider getProvider()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 158	sun/security/jca/ProviderConfig:provider	Ljava/security/Provider;
    //   4: astore_1
    //   5: aload_1
    //   6: ifnull +5 -> 11
    //   9: aload_1
    //   10: areturn
    //   11: aload_0
    //   12: invokespecial 177	sun/security/jca/ProviderConfig:shouldLoad	()Z
    //   15: ifne +5 -> 20
    //   18: aconst_null
    //   19: areturn
    //   20: aload_0
    //   21: getfield 154	sun/security/jca/ProviderConfig:isLoading	Z
    //   24: ifeq +48 -> 72
    //   27: getstatic 159	sun/security/jca/ProviderConfig:debug	Lsun/security/util/Debug;
    //   30: ifnull +40 -> 70
    //   33: getstatic 159	sun/security/jca/ProviderConfig:debug	Lsun/security/util/Debug;
    //   36: new 96	java/lang/StringBuilder
    //   39: dup
    //   40: invokespecial 169	java/lang/StringBuilder:<init>	()V
    //   43: ldc 8
    //   45: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: aload_0
    //   49: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   52: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokevirtual 184	sun/security/util/Debug:println	(Ljava/lang/String;)V
    //   58: new 94	java/lang/Exception
    //   61: dup
    //   62: ldc 6
    //   64: invokespecial 162	java/lang/Exception:<init>	(Ljava/lang/String;)V
    //   67: invokevirtual 161	java/lang/Exception:printStackTrace	()V
    //   70: aconst_null
    //   71: areturn
    //   72: aload_0
    //   73: iconst_1
    //   74: putfield 154	sun/security/jca/ProviderConfig:isLoading	Z
    //   77: aload_0
    //   78: dup
    //   79: getfield 153	sun/security/jca/ProviderConfig:tries	I
    //   82: iconst_1
    //   83: iadd
    //   84: putfield 153	sun/security/jca/ProviderConfig:tries	I
    //   87: aload_0
    //   88: invokespecial 178	sun/security/jca/ProviderConfig:doLoadProvider	()Ljava/security/Provider;
    //   91: astore_1
    //   92: aload_0
    //   93: iconst_0
    //   94: putfield 154	sun/security/jca/ProviderConfig:isLoading	Z
    //   97: goto +11 -> 108
    //   100: astore_2
    //   101: aload_0
    //   102: iconst_0
    //   103: putfield 154	sun/security/jca/ProviderConfig:isLoading	Z
    //   106: aload_2
    //   107: athrow
    //   108: aload_0
    //   109: aload_1
    //   110: putfield 158	sun/security/jca/ProviderConfig:provider	Ljava/security/Provider;
    //   113: aload_1
    //   114: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	115	0	this	ProviderConfig
    //   4	110	1	localProvider	Provider
    //   100	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   72	92	100	finally
  }
  
  private Provider doLoadProvider()
  {
    (Provider)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Provider run()
      {
        if (ProviderConfig.debug != null) {
          ProviderConfig.debug.println("Loading provider: " + ProviderConfig.this);
        }
        try
        {
          ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
          if (localClassLoader != null) {
            localObject1 = localClassLoader.loadClass(className);
          } else {
            localObject1 = Class.forName(className);
          }
          Object localObject2;
          if (!ProviderConfig.this.hasArgument())
          {
            localObject2 = ((Class)localObject1).newInstance();
          }
          else
          {
            Constructor localConstructor = ((Class)localObject1).getConstructor(ProviderConfig.CL_STRING);
            localObject2 = localConstructor.newInstance(new Object[] { argument });
          }
          if ((localObject2 instanceof Provider))
          {
            if (ProviderConfig.debug != null) {
              ProviderConfig.debug.println("Loaded provider " + localObject2);
            }
            return (Provider)localObject2;
          }
          if (ProviderConfig.debug != null) {
            ProviderConfig.debug.println(className + " is not a provider");
          }
          ProviderConfig.this.disableLoad();
          return null;
        }
        catch (Exception localException)
        {
          Object localObject1;
          if ((localException instanceof InvocationTargetException)) {
            localObject1 = ((InvocationTargetException)localException).getCause();
          } else {
            localObject1 = localException;
          }
          if (ProviderConfig.debug != null)
          {
            ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
            ((Throwable)localObject1).printStackTrace();
          }
          if ((localObject1 instanceof ProviderException)) {
            throw ((ProviderException)localObject1);
          }
          if ((localObject1 instanceof UnsupportedOperationException)) {
            ProviderConfig.this.disableLoad();
          }
          return null;
        }
        catch (ExceptionInInitializerError localExceptionInInitializerError)
        {
          if (ProviderConfig.debug != null)
          {
            ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
            localExceptionInInitializerError.printStackTrace();
          }
          ProviderConfig.this.disableLoad();
        }
        return null;
      }
    });
  }
  
  private static String expand(String paramString)
  {
    if (!paramString.contains("${")) {
      return paramString;
    }
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        try
        {
          return PropertyExpander.expand(val$value);
        }
        catch (GeneralSecurityException localGeneralSecurityException)
        {
          throw new ProviderException(localGeneralSecurityException);
        }
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jca\ProviderConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */