package javax.management.remote.rmi;

import java.security.ProtectionDomain;

class NoCallStackClassLoader
  extends ClassLoader
{
  private final String[] classNames;
  private final byte[][] byteCodes;
  private final String[] referencedClassNames;
  private final ClassLoader referencedClassLoader;
  private final ProtectionDomain protectionDomain;
  
  public NoCallStackClassLoader(String paramString, byte[] paramArrayOfByte, String[] paramArrayOfString, ClassLoader paramClassLoader, ProtectionDomain paramProtectionDomain)
  {
    this(new String[] { paramString }, new byte[][] { paramArrayOfByte }, paramArrayOfString, paramClassLoader, paramProtectionDomain);
  }
  
  public NoCallStackClassLoader(String[] paramArrayOfString1, byte[][] paramArrayOfByte, String[] paramArrayOfString2, ClassLoader paramClassLoader, ProtectionDomain paramProtectionDomain)
  {
    super(null);
    if ((paramArrayOfString1 == null) || (paramArrayOfString1.length == 0) || (paramArrayOfByte == null) || (paramArrayOfString1.length != paramArrayOfByte.length) || (paramArrayOfString2 == null) || (paramProtectionDomain == null)) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < paramArrayOfString1.length; i++) {
      if ((paramArrayOfString1[i] == null) || (paramArrayOfByte[i] == null)) {
        throw new IllegalArgumentException();
      }
    }
    for (i = 0; i < paramArrayOfString2.length; i++) {
      if (paramArrayOfString2[i] == null) {
        throw new IllegalArgumentException();
      }
    }
    classNames = paramArrayOfString1;
    byteCodes = paramArrayOfByte;
    referencedClassNames = paramArrayOfString2;
    referencedClassLoader = paramClassLoader;
    protectionDomain = paramProtectionDomain;
  }
  
  protected Class<?> findClass(String paramString)
    throws ClassNotFoundException
  {
    for (int i = 0; i < classNames.length; i++) {
      if (paramString.equals(classNames[i])) {
        return defineClass(classNames[i], byteCodes[i], 0, byteCodes[i].length, protectionDomain);
      }
    }
    if (referencedClassLoader != null) {
      for (i = 0; i < referencedClassNames.length; i++) {
        if (paramString.equals(referencedClassNames[i])) {
          return referencedClassLoader.loadClass(paramString);
        }
      }
    }
    throw new ClassNotFoundException(paramString);
  }
  
  public static byte[] stringToBytes(String paramString)
  {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[i];
    for (int j = 0; j < i; j++) {
      arrayOfByte[j] = ((byte)paramString.charAt(j));
    }
    return arrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\rmi\NoCallStackClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */