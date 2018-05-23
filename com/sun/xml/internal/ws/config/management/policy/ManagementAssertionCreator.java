package com.sun.xml.internal.ws.config.management.policy;

import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import java.util.Collection;
import javax.xml.namespace.QName;

public class ManagementAssertionCreator
  implements PolicyAssertionCreator
{
  public ManagementAssertionCreator() {}
  
  public String[] getSupportedDomainNamespaceURIs()
  {
    return new String[] { "http://java.sun.com/xml/ns/metro/management" };
  }
  
  public PolicyAssertion createAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection, AssertionSet paramAssertionSet, PolicyAssertionCreator paramPolicyAssertionCreator)
    throws AssertionCreationException
  {
    QName localQName = paramAssertionData.getName();
    if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(localQName)) {
      return new ManagedServiceAssertion(paramAssertionData, paramCollection);
    }
    if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(localQName)) {
      return new ManagedClientAssertion(paramAssertionData, paramCollection);
    }
    return paramPolicyAssertionCreator.createAssertion(paramAssertionData, paramCollection, paramAssertionSet, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\config\management\policy\ManagementAssertionCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */