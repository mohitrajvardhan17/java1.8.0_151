package com.sun.xml.internal.ws.api.policy;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.ws.WebServiceException;

public abstract interface PolicyResolver
{
  public abstract PolicyMap resolve(ServerContext paramServerContext)
    throws WebServiceException;
  
  public abstract PolicyMap resolve(ClientContext paramClientContext)
    throws WebServiceException;
  
  public static class ClientContext
  {
    private PolicyMap policyMap;
    private Container container;
    
    public ClientContext(@Nullable PolicyMap paramPolicyMap, Container paramContainer)
    {
      policyMap = paramPolicyMap;
      container = paramContainer;
    }
    
    @Nullable
    public PolicyMap getPolicyMap()
    {
      return policyMap;
    }
    
    public Container getContainer()
    {
      return container;
    }
  }
  
  public static class ServerContext
  {
    private final PolicyMap policyMap;
    private final Class endpointClass;
    private final Container container;
    private final boolean hasWsdl;
    private final Collection<PolicyMapMutator> mutators;
    
    public ServerContext(@Nullable PolicyMap paramPolicyMap, Container paramContainer, Class paramClass, PolicyMapMutator... paramVarArgs)
    {
      policyMap = paramPolicyMap;
      endpointClass = paramClass;
      container = paramContainer;
      hasWsdl = true;
      mutators = Arrays.asList(paramVarArgs);
    }
    
    public ServerContext(@Nullable PolicyMap paramPolicyMap, Container paramContainer, Class paramClass, boolean paramBoolean, PolicyMapMutator... paramVarArgs)
    {
      policyMap = paramPolicyMap;
      endpointClass = paramClass;
      container = paramContainer;
      hasWsdl = paramBoolean;
      mutators = Arrays.asList(paramVarArgs);
    }
    
    @Nullable
    public PolicyMap getPolicyMap()
    {
      return policyMap;
    }
    
    @Nullable
    public Class getEndpointClass()
    {
      return endpointClass;
    }
    
    public Container getContainer()
    {
      return container;
    }
    
    public boolean hasWsdl()
    {
      return hasWsdl;
    }
    
    public Collection<PolicyMapMutator> getMutators()
    {
      return mutators;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\policy\PolicyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */