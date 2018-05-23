package javax.management.loading;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;

class MLetObjectInputStream
  extends ObjectInputStream
{
  private MLet loader;
  
  public MLetObjectInputStream(InputStream paramInputStream, MLet paramMLet)
    throws IOException, StreamCorruptedException
  {
    super(paramInputStream);
    if (paramMLet == null) {
      throw new IllegalArgumentException("Illegal null argument to MLetObjectInputStream");
    }
    loader = paramMLet;
  }
  
  private Class<?> primitiveType(char paramChar)
  {
    switch (paramChar)
    {
    case 'B': 
      return Byte.TYPE;
    case 'C': 
      return Character.TYPE;
    case 'D': 
      return Double.TYPE;
    case 'F': 
      return Float.TYPE;
    case 'I': 
      return Integer.TYPE;
    case 'J': 
      return Long.TYPE;
    case 'S': 
      return Short.TYPE;
    case 'Z': 
      return Boolean.TYPE;
    }
    return null;
  }
  
  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
    throws IOException, ClassNotFoundException
  {
    String str = paramObjectStreamClass.getName();
    if (str.startsWith("["))
    {
      for (int i = 1; str.charAt(i) == '['; i++) {}
      Class localClass;
      if (str.charAt(i) == 'L')
      {
        localClass = loader.loadClass(str.substring(i + 1, str.length() - 1));
      }
      else
      {
        if (str.length() != i + 1) {
          throw new ClassNotFoundException(str);
        }
        localClass = primitiveType(str.charAt(i));
      }
      int[] arrayOfInt = new int[i];
      for (int j = 0; j < i; j++) {
        arrayOfInt[j] = 0;
      }
      return Array.newInstance(localClass, arrayOfInt).getClass();
    }
    return loader.loadClass(str);
  }
  
  public ClassLoader getClassLoader()
  {
    return loader;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\loading\MLetObjectInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */