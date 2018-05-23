package sun.security.action;

import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.Map;

public class PutAllAction
  implements PrivilegedAction<Void>
{
  private final Provider provider;
  private final Map<?, ?> map;
  
  public PutAllAction(Provider paramProvider, Map<?, ?> paramMap)
  {
    provider = paramProvider;
    map = paramMap;
  }
  
  public Void run()
  {
    provider.putAll(map);
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\action\PutAllAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */