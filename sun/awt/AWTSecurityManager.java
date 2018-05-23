package sun.awt;

public class AWTSecurityManager
  extends SecurityManager
{
  public AWTSecurityManager() {}
  
  public AppContext getAppContext()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\AWTSecurityManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */