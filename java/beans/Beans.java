package java.beans;

import com.sun.beans.finder.ClassFinder;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.beans.beancontext.BeanContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Modifier;
import java.net.URL;

public class Beans
{
  public Beans() {}
  
  public static Object instantiate(ClassLoader paramClassLoader, String paramString)
    throws IOException, ClassNotFoundException
  {
    return instantiate(paramClassLoader, paramString, null, null);
  }
  
  public static Object instantiate(ClassLoader paramClassLoader, String paramString, BeanContext paramBeanContext)
    throws IOException, ClassNotFoundException
  {
    return instantiate(paramClassLoader, paramString, paramBeanContext, null);
  }
  
  public static Object instantiate(ClassLoader paramClassLoader, String paramString, BeanContext paramBeanContext, AppletInitializer paramAppletInitializer)
    throws IOException, ClassNotFoundException
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int i = 0;
    Object localObject3 = null;
    if (paramClassLoader == null) {
      try
      {
        paramClassLoader = ClassLoader.getSystemClassLoader();
      }
      catch (SecurityException localSecurityException) {}
    }
    String str1 = paramString.replace('.', '/').concat(".ser");
    InputStream localInputStream;
    if (paramClassLoader == null) {
      localInputStream = ClassLoader.getSystemResourceAsStream(str1);
    } else {
      localInputStream = paramClassLoader.getResourceAsStream(str1);
    }
    if (localInputStream != null) {
      try
      {
        if (paramClassLoader == null) {
          localObject1 = new ObjectInputStream(localInputStream);
        } else {
          localObject1 = new ObjectInputStreamWithLoader(localInputStream, paramClassLoader);
        }
        localObject2 = ((ObjectInputStream)localObject1).readObject();
        i = 1;
        ((ObjectInputStream)localObject1).close();
      }
      catch (IOException localIOException)
      {
        localInputStream.close();
        localObject3 = localIOException;
      }
      catch (ClassNotFoundException localClassNotFoundException1)
      {
        localInputStream.close();
        throw localClassNotFoundException1;
      }
    }
    Object localObject4;
    if (localObject2 == null)
    {
      try
      {
        localObject4 = ClassFinder.findClass(paramString, paramClassLoader);
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        if (localObject3 != null) {
          throw ((Throwable)localObject3);
        }
        throw localClassNotFoundException2;
      }
      if (!Modifier.isPublic(((Class)localObject4).getModifiers())) {
        throw new ClassNotFoundException("" + localObject4 + " : no public access");
      }
      try
      {
        localObject2 = ((Class)localObject4).newInstance();
      }
      catch (Exception localException)
      {
        throw new ClassNotFoundException("" + localObject4 + " : " + localException, localException);
      }
    }
    if (localObject2 != null)
    {
      localObject4 = null;
      if ((localObject2 instanceof Applet))
      {
        Applet localApplet = (Applet)localObject2;
        int j = paramAppletInitializer == null ? 1 : 0;
        if (j != 0)
        {
          String str2;
          if (i != 0) {
            str2 = paramString.replace('.', '/').concat(".ser");
          } else {
            str2 = paramString.replace('.', '/').concat(".class");
          }
          URL localURL1 = null;
          URL localURL2 = null;
          URL localURL3 = null;
          if (paramClassLoader == null) {
            localURL1 = ClassLoader.getSystemResource(str2);
          } else {
            localURL1 = paramClassLoader.getResource(str2);
          }
          if (localURL1 != null)
          {
            localObject5 = localURL1.toExternalForm();
            if (((String)localObject5).endsWith(str2))
            {
              int k = ((String)localObject5).length() - str2.length();
              localURL2 = new URL(((String)localObject5).substring(0, k));
              localURL3 = localURL2;
              k = ((String)localObject5).lastIndexOf('/');
              if (k >= 0) {
                localURL3 = new URL(((String)localObject5).substring(0, k + 1));
              }
            }
          }
          Object localObject5 = new BeansAppletContext(localApplet);
          localObject4 = new BeansAppletStub(localApplet, (AppletContext)localObject5, localURL2, localURL3);
          localApplet.setStub((AppletStub)localObject4);
        }
        else
        {
          paramAppletInitializer.initialize(localApplet, paramBeanContext);
        }
        if (paramBeanContext != null) {
          unsafeBeanContextAdd(paramBeanContext, localObject2);
        }
        if (i == 0)
        {
          localApplet.setSize(100, 100);
          localApplet.init();
        }
        if (j != 0) {
          active = true;
        } else {
          paramAppletInitializer.activate(localApplet);
        }
      }
      else if (paramBeanContext != null)
      {
        unsafeBeanContextAdd(paramBeanContext, localObject2);
      }
    }
    return localObject2;
  }
  
  private static void unsafeBeanContextAdd(BeanContext paramBeanContext, Object paramObject)
  {
    paramBeanContext.add(paramObject);
  }
  
  public static Object getInstanceOf(Object paramObject, Class<?> paramClass)
  {
    return paramObject;
  }
  
  public static boolean isInstanceOf(Object paramObject, Class<?> paramClass)
  {
    return Introspector.isSubclass(paramObject.getClass(), paramClass);
  }
  
  public static boolean isDesignTime()
  {
    return ThreadGroupContext.getContext().isDesignTime();
  }
  
  public static boolean isGuiAvailable()
  {
    return ThreadGroupContext.getContext().isGuiAvailable();
  }
  
  public static void setDesignTime(boolean paramBoolean)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPropertiesAccess();
    }
    ThreadGroupContext.getContext().setDesignTime(paramBoolean);
  }
  
  public static void setGuiAvailable(boolean paramBoolean)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPropertiesAccess();
    }
    ThreadGroupContext.getContext().setGuiAvailable(paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\Beans.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */