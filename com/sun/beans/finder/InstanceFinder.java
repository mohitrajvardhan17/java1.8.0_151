package com.sun.beans.finder;

class InstanceFinder<T>
{
  private static final String[] EMPTY = new String[0];
  private final Class<? extends T> type;
  private final boolean allow;
  private final String suffix;
  private volatile String[] packages;
  
  InstanceFinder(Class<? extends T> paramClass, boolean paramBoolean, String paramString, String... paramVarArgs)
  {
    type = paramClass;
    allow = paramBoolean;
    suffix = paramString;
    packages = ((String[])paramVarArgs.clone());
  }
  
  public String[] getPackages()
  {
    return (String[])packages.clone();
  }
  
  public void setPackages(String... paramVarArgs)
  {
    packages = ((paramVarArgs != null) && (paramVarArgs.length > 0) ? (String[])paramVarArgs.clone() : EMPTY);
  }
  
  public T find(Class<?> paramClass)
  {
    if (paramClass == null) {
      return null;
    }
    String str1 = paramClass.getName() + suffix;
    Object localObject = instantiate(paramClass, str1);
    if (localObject != null) {
      return (T)localObject;
    }
    if (allow)
    {
      localObject = instantiate(paramClass, null);
      if (localObject != null) {
        return (T)localObject;
      }
    }
    int i = str1.lastIndexOf('.') + 1;
    if (i > 0) {
      str1 = str1.substring(i);
    }
    for (String str2 : packages)
    {
      localObject = instantiate(paramClass, str2, str1);
      if (localObject != null) {
        return (T)localObject;
      }
    }
    return null;
  }
  
  protected T instantiate(Class<?> paramClass, String paramString)
  {
    if (paramClass != null) {
      try
      {
        if (paramString != null) {
          paramClass = ClassFinder.findClass(paramString, paramClass.getClassLoader());
        }
        if (type.isAssignableFrom(paramClass)) {
          return (T)paramClass.newInstance();
        }
      }
      catch (Exception localException) {}
    }
    return null;
  }
  
  protected T instantiate(Class<?> paramClass, String paramString1, String paramString2)
  {
    return (T)instantiate(paramClass, paramString1 + '.' + paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\InstanceFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */