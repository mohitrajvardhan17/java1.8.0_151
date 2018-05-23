package sun.awt.datatransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ClassLoaderObjectInputStream
  extends ObjectInputStream
{
  private final Map<Set<String>, ClassLoader> map;
  
  ClassLoaderObjectInputStream(InputStream paramInputStream, Map<Set<String>, ClassLoader> paramMap)
    throws IOException
  {
    super(paramInputStream);
    if (paramMap == null) {
      throw new NullPointerException("Null map");
    }
    map = paramMap;
  }
  
  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
    throws IOException, ClassNotFoundException
  {
    String str = paramObjectStreamClass.getName();
    HashSet localHashSet = new HashSet(1);
    localHashSet.add(str);
    ClassLoader localClassLoader = (ClassLoader)map.get(localHashSet);
    if (localClassLoader != null) {
      return Class.forName(str, false, localClassLoader);
    }
    return super.resolveClass(paramObjectStreamClass);
  }
  
  protected Class<?> resolveProxyClass(String[] paramArrayOfString)
    throws IOException, ClassNotFoundException
  {
    HashSet localHashSet = new HashSet(paramArrayOfString.length);
    for (int i = 0; i < paramArrayOfString.length; i++) {
      localHashSet.add(paramArrayOfString[i]);
    }
    ClassLoader localClassLoader1 = (ClassLoader)map.get(localHashSet);
    if (localClassLoader1 == null) {
      return super.resolveProxyClass(paramArrayOfString);
    }
    ClassLoader localClassLoader2 = null;
    int j = 0;
    Class[] arrayOfClass = new Class[paramArrayOfString.length];
    for (int k = 0; k < paramArrayOfString.length; k++)
    {
      Class localClass = Class.forName(paramArrayOfString[k], false, localClassLoader1);
      if ((localClass.getModifiers() & 0x1) == 0) {
        if (j != 0)
        {
          if (localClassLoader2 != localClass.getClassLoader()) {
            throw new IllegalAccessError("conflicting non-public interface class loaders");
          }
        }
        else
        {
          localClassLoader2 = localClass.getClassLoader();
          j = 1;
        }
      }
      arrayOfClass[k] = localClass;
    }
    try
    {
      return Proxy.getProxyClass(j != 0 ? localClassLoader2 : localClassLoader1, arrayOfClass);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ClassNotFoundException(null, localIllegalArgumentException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\datatransfer\ClassLoaderObjectInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */