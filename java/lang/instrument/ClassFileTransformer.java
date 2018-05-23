package java.lang.instrument;

import java.security.ProtectionDomain;

public abstract interface ClassFileTransformer
{
  public abstract byte[] transform(ClassLoader paramClassLoader, String paramString, Class<?> paramClass, ProtectionDomain paramProtectionDomain, byte[] paramArrayOfByte)
    throws IllegalClassFormatException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\instrument\ClassFileTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */