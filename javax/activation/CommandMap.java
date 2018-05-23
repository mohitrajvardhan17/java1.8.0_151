package javax.activation;

import java.util.Map;
import java.util.WeakHashMap;

public abstract class CommandMap
{
  private static CommandMap defaultCommandMap = null;
  private static Map<ClassLoader, CommandMap> map = new WeakHashMap();
  
  public CommandMap() {}
  
  public static synchronized CommandMap getDefaultCommandMap()
  {
    if (defaultCommandMap != null) {
      return defaultCommandMap;
    }
    ClassLoader localClassLoader = SecuritySupport.getContextClassLoader();
    Object localObject = (CommandMap)map.get(localClassLoader);
    if (localObject == null)
    {
      localObject = new MailcapCommandMap();
      map.put(localClassLoader, localObject);
    }
    return (CommandMap)localObject;
  }
  
  public static synchronized void setDefaultCommandMap(CommandMap paramCommandMap)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkSetFactory();
      }
      catch (SecurityException localSecurityException)
      {
        if ((CommandMap.class.getClassLoader() == null) || (CommandMap.class.getClassLoader() != paramCommandMap.getClass().getClassLoader())) {
          throw localSecurityException;
        }
      }
    }
    map.remove(SecuritySupport.getContextClassLoader());
    defaultCommandMap = paramCommandMap;
  }
  
  public abstract CommandInfo[] getPreferredCommands(String paramString);
  
  public CommandInfo[] getPreferredCommands(String paramString, DataSource paramDataSource)
  {
    return getPreferredCommands(paramString);
  }
  
  public abstract CommandInfo[] getAllCommands(String paramString);
  
  public CommandInfo[] getAllCommands(String paramString, DataSource paramDataSource)
  {
    return getAllCommands(paramString);
  }
  
  public abstract CommandInfo getCommand(String paramString1, String paramString2);
  
  public CommandInfo getCommand(String paramString1, String paramString2, DataSource paramDataSource)
  {
    return getCommand(paramString1, paramString2);
  }
  
  public abstract DataContentHandler createDataContentHandler(String paramString);
  
  public DataContentHandler createDataContentHandler(String paramString, DataSource paramDataSource)
  {
    return createDataContentHandler(paramString);
  }
  
  public String[] getMimeTypes()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\CommandMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */