package com.sun.beans.finder;

import com.sun.beans.util.Cache;
import com.sun.beans.util.Cache.Kind;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;

public final class ConstructorFinder
  extends AbstractFinder<Constructor<?>>
{
  private static final Cache<Signature, Constructor<?>> CACHE = new Cache(Cache.Kind.SOFT, Cache.Kind.SOFT)
  {
    public Constructor create(Signature paramAnonymousSignature)
    {
      try
      {
        ConstructorFinder localConstructorFinder = new ConstructorFinder(paramAnonymousSignature.getArgs(), null);
        return (Constructor)localConstructorFinder.find(paramAnonymousSignature.getType().getConstructors());
      }
      catch (Exception localException)
      {
        throw new SignatureException(localException);
      }
    }
  };
  
  public static Constructor<?> findConstructor(Class<?> paramClass, Class<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    if (paramClass.isPrimitive()) {
      throw new NoSuchMethodException("Primitive wrapper does not contain constructors");
    }
    if (paramClass.isInterface()) {
      throw new NoSuchMethodException("Interface does not contain constructors");
    }
    if (Modifier.isAbstract(paramClass.getModifiers())) {
      throw new NoSuchMethodException("Abstract class cannot be instantiated");
    }
    if ((!Modifier.isPublic(paramClass.getModifiers())) || (!ReflectUtil.isPackageAccessible(paramClass))) {
      throw new NoSuchMethodException("Class is not accessible");
    }
    PrimitiveWrapperMap.replacePrimitivesWithWrappers(paramVarArgs);
    Signature localSignature = new Signature(paramClass, paramVarArgs);
    try
    {
      return (Constructor)CACHE.get(localSignature);
    }
    catch (SignatureException localSignatureException)
    {
      throw localSignatureException.toNoSuchMethodException("Constructor is not found");
    }
  }
  
  private ConstructorFinder(Class<?>[] paramArrayOfClass)
  {
    super(paramArrayOfClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\ConstructorFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */