package javax.management.loading;

public abstract interface ClassLoaderRepository
{
  public abstract Class<?> loadClass(String paramString)
    throws ClassNotFoundException;
  
  public abstract Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException;
  
  public abstract Class<?> loadClassBefore(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\loading\ClassLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */