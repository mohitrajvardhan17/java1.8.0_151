package com.sun.jmx.mbeanserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import sun.reflect.misc.ReflectUtil;

class ObjectInputStreamWithLoader
  extends ObjectInputStream
{
  private ClassLoader loader;
  
  public ObjectInputStreamWithLoader(InputStream paramInputStream, ClassLoader paramClassLoader)
    throws IOException
  {
    super(paramInputStream);
    loader = paramClassLoader;
  }
  
  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
    throws IOException, ClassNotFoundException
  {
    if (loader == null) {
      return super.resolveClass(paramObjectStreamClass);
    }
    String str = paramObjectStreamClass.getName();
    ReflectUtil.checkPackageAccess(str);
    return Class.forName(str, false, loader);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\ObjectInputStreamWithLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */