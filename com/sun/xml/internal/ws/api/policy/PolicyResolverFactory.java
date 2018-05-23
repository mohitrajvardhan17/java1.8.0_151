package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.jaxws.DefaultPolicyResolver;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Iterator;

public abstract class PolicyResolverFactory
{
  public static final PolicyResolver DEFAULT_POLICY_RESOLVER = new DefaultPolicyResolver();
  
  public PolicyResolverFactory() {}
  
  public abstract PolicyResolver doCreate();
  
  public static PolicyResolver create()
  {
    Iterator localIterator = ServiceFinder.find(PolicyResolverFactory.class).iterator();
    while (localIterator.hasNext())
    {
      PolicyResolverFactory localPolicyResolverFactory = (PolicyResolverFactory)localIterator.next();
      PolicyResolver localPolicyResolver = localPolicyResolverFactory.doCreate();
      if (localPolicyResolver != null) {
        return localPolicyResolver;
      }
    }
    return DEFAULT_POLICY_RESOLVER;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\policy\PolicyResolverFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */