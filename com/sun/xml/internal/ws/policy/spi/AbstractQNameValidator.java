package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract class AbstractQNameValidator
  implements PolicyAssertionValidator
{
  private final Set<String> supportedDomains = new HashSet();
  private final Collection<QName> serverAssertions;
  private final Collection<QName> clientAssertions;
  
  protected AbstractQNameValidator(Collection<QName> paramCollection1, Collection<QName> paramCollection2)
  {
    Iterator localIterator;
    QName localQName;
    if (paramCollection1 != null)
    {
      serverAssertions = new HashSet(paramCollection1);
      localIterator = serverAssertions.iterator();
      while (localIterator.hasNext())
      {
        localQName = (QName)localIterator.next();
        supportedDomains.add(localQName.getNamespaceURI());
      }
    }
    else
    {
      serverAssertions = new HashSet(0);
    }
    if (paramCollection2 != null)
    {
      clientAssertions = new HashSet(paramCollection2);
      localIterator = clientAssertions.iterator();
      while (localIterator.hasNext())
      {
        localQName = (QName)localIterator.next();
        supportedDomains.add(localQName.getNamespaceURI());
      }
    }
    else
    {
      clientAssertions = new HashSet(0);
    }
  }
  
  public String[] declareSupportedDomains()
  {
    return (String[])supportedDomains.toArray(new String[supportedDomains.size()]);
  }
  
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion)
  {
    return validateAssertion(paramPolicyAssertion, clientAssertions, serverAssertions);
  }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion)
  {
    return validateAssertion(paramPolicyAssertion, serverAssertions, clientAssertions);
  }
  
  private PolicyAssertionValidator.Fitness validateAssertion(PolicyAssertion paramPolicyAssertion, Collection<QName> paramCollection1, Collection<QName> paramCollection2)
  {
    QName localQName = paramPolicyAssertion.getName();
    if (paramCollection1.contains(localQName)) {
      return PolicyAssertionValidator.Fitness.SUPPORTED;
    }
    if (paramCollection2.contains(localQName)) {
      return PolicyAssertionValidator.Fitness.UNSUPPORTED;
    }
    return PolicyAssertionValidator.Fitness.UNKNOWN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\spi\AbstractQNameValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */