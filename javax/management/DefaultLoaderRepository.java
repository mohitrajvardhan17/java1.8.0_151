package javax.management;

@Deprecated
public class DefaultLoaderRepository
{
  public DefaultLoaderRepository() {}
  
  public static Class<?> loadClass(String paramString)
    throws ClassNotFoundException
  {
    return javax.management.loading.DefaultLoaderRepository.loadClass(paramString);
  }
  
  public static Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    return javax.management.loading.DefaultLoaderRepository.loadClassWithout(paramClassLoader, paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\DefaultLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */