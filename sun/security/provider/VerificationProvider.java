package sun.security.provider;

import java.security.AccessController;
import java.security.Provider;
import java.util.LinkedHashMap;
import sun.security.action.PutAllAction;
import sun.security.rsa.SunRsaSignEntries;

public final class VerificationProvider
  extends Provider
{
  private static final long serialVersionUID = 7482667077568930381L;
  private static final boolean ACTIVE;
  
  public VerificationProvider()
  {
    super("SunJarVerification", 1.8D, "Jar Verification Provider");
    if (!ACTIVE) {
      return;
    }
    if (System.getSecurityManager() == null)
    {
      SunEntries.putEntries(this);
      SunRsaSignEntries.putEntries(this);
    }
    else
    {
      LinkedHashMap localLinkedHashMap = new LinkedHashMap();
      SunEntries.putEntries(localLinkedHashMap);
      SunRsaSignEntries.putEntries(localLinkedHashMap);
      AccessController.doPrivileged(new PutAllAction(this, localLinkedHashMap));
    }
  }
  
  static
  {
    boolean bool;
    try
    {
      Class.forName("sun.security.provider.Sun");
      Class.forName("sun.security.rsa.SunRsaSign");
      bool = false;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      bool = true;
    }
    ACTIVE = bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\VerificationProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */