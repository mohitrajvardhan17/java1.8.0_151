package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public abstract class PolicyMapMutator
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapMutator.class);
  private PolicyMap map = null;
  
  PolicyMapMutator() {}
  
  public void connect(PolicyMap paramPolicyMap)
  {
    if (isConnected()) {
      throw ((IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0044_POLICY_MAP_MUTATOR_ALREADY_CONNECTED())));
    }
    map = paramPolicyMap;
  }
  
  public PolicyMap getMap()
  {
    return map;
  }
  
  public void disconnect()
  {
    map = null;
  }
  
  public boolean isConnected()
  {
    return map != null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMapMutator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */