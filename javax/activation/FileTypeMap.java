package javax.activation;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class FileTypeMap
{
  private static FileTypeMap defaultMap = null;
  private static Map<ClassLoader, FileTypeMap> map = new WeakHashMap();
  
  public FileTypeMap() {}
  
  public abstract String getContentType(File paramFile);
  
  public abstract String getContentType(String paramString);
  
  public static synchronized void setDefaultFileTypeMap(FileTypeMap paramFileTypeMap)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkSetFactory();
      }
      catch (SecurityException localSecurityException)
      {
        if ((FileTypeMap.class.getClassLoader() == null) || (FileTypeMap.class.getClassLoader() != paramFileTypeMap.getClass().getClassLoader())) {
          throw localSecurityException;
        }
      }
    }
    map.remove(SecuritySupport.getContextClassLoader());
    defaultMap = paramFileTypeMap;
  }
  
  public static synchronized FileTypeMap getDefaultFileTypeMap()
  {
    if (defaultMap != null) {
      return defaultMap;
    }
    ClassLoader localClassLoader = SecuritySupport.getContextClassLoader();
    Object localObject = (FileTypeMap)map.get(localClassLoader);
    if (localObject == null)
    {
      localObject = new MimetypesFileTypeMap();
      map.put(localClassLoader, localObject);
    }
    return (FileTypeMap)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\FileTypeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */