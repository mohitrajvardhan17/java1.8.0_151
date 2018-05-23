package java.beans;

import com.sun.beans.finder.PropertyEditorFinder;

public class PropertyEditorManager
{
  public PropertyEditorManager() {}
  
  public static void registerEditor(Class<?> paramClass1, Class<?> paramClass2)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPropertiesAccess();
    }
    ThreadGroupContext.getContext().getPropertyEditorFinder().register(paramClass1, paramClass2);
  }
  
  public static PropertyEditor findEditor(Class<?> paramClass)
  {
    return ThreadGroupContext.getContext().getPropertyEditorFinder().find(paramClass);
  }
  
  public static String[] getEditorSearchPath()
  {
    return ThreadGroupContext.getContext().getPropertyEditorFinder().getPackages();
  }
  
  public static void setEditorSearchPath(String[] paramArrayOfString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPropertiesAccess();
    }
    ThreadGroupContext.getContext().getPropertyEditorFinder().setPackages(paramArrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\PropertyEditorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */