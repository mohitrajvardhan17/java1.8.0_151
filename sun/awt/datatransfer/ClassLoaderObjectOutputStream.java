package sun.awt.datatransfer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ClassLoaderObjectOutputStream
  extends ObjectOutputStream
{
  private final Map<Set<String>, ClassLoader> map = new HashMap();
  
  ClassLoaderObjectOutputStream(OutputStream paramOutputStream)
    throws IOException
  {
    super(paramOutputStream);
  }
  
  protected void annotateClass(final Class<?> paramClass)
    throws IOException
  {
    ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return paramClass.getClassLoader();
      }
    });
    HashSet localHashSet = new HashSet(1);
    localHashSet.add(paramClass.getName());
    map.put(localHashSet, localClassLoader);
  }
  
  protected void annotateProxyClass(final Class<?> paramClass)
    throws IOException
  {
    ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return paramClass.getClassLoader();
      }
    });
    Class[] arrayOfClass = paramClass.getInterfaces();
    HashSet localHashSet = new HashSet(arrayOfClass.length);
    for (int i = 0; i < arrayOfClass.length; i++) {
      localHashSet.add(arrayOfClass[i].getName());
    }
    map.put(localHashSet, localClassLoader);
  }
  
  Map<Set<String>, ClassLoader> getClassLoaderMap()
  {
    return new HashMap(map);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\datatransfer\ClassLoaderObjectOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */