package com.sun.xml.internal.ws.config.management.policy;

import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator.Fitness;
import javax.xml.namespace.QName;

public class ManagementPolicyValidator
  implements PolicyAssertionValidator
{
  public ManagementPolicyValidator() {}
  
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion)
  {
    QName localQName = paramPolicyAssertion.getName();
    if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(localQName)) {
      return PolicyAssertionValidator.Fitness.SUPPORTED;
    }
    if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(localQName)) {
      return PolicyAssertionValidator.Fitness.UNSUPPORTED;
    }
    return PolicyAssertionValidator.Fitness.UNKNOWN;
  }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion)
  {
    QName localQName = paramPolicyAssertion.getName();
    if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(localQName)) {
      return PolicyAssertionValidator.Fitness.SUPPORTED;
    }
    if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(localQName)) {
      return PolicyAssertionValidator.Fitness.UNSUPPORTED;
    }
    return PolicyAssertionValidator.Fitness.UNKNOWN;
  }
  
  public String[] declareSupportedDomains()
  {
    return new String[] { "http://java.sun.com/xml/ns/metro/management" };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\config\management\policy\ManagementPolicyValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */