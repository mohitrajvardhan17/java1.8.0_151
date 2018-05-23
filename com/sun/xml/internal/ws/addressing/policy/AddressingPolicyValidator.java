package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator.Fitness;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class AddressingPolicyValidator
  implements PolicyAssertionValidator
{
  private static final ArrayList<QName> supportedAssertions = new ArrayList();
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingPolicyValidator.class);
  
  public AddressingPolicyValidator() {}
  
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion)
  {
    return supportedAssertions.contains(paramPolicyAssertion.getName()) ? PolicyAssertionValidator.Fitness.SUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN;
  }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion)
  {
    if (!supportedAssertions.contains(paramPolicyAssertion.getName())) {
      return PolicyAssertionValidator.Fitness.UNKNOWN;
    }
    if (paramPolicyAssertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION))
    {
      NestedPolicy localNestedPolicy = paramPolicyAssertion.getNestedPolicy();
      if (localNestedPolicy != null)
      {
        int i = 0;
        int j = 0;
        Iterator localIterator = localNestedPolicy.getAssertionSet().iterator();
        while (localIterator.hasNext())
        {
          PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator.next();
          if (localPolicyAssertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION))
          {
            i = 1;
          }
          else if (localPolicyAssertion.getName().equals(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION))
          {
            j = 1;
          }
          else
          {
            LOGGER.warning("Found unsupported assertion:\n" + localPolicyAssertion + "\nnested into assertion:\n" + paramPolicyAssertion);
            return PolicyAssertionValidator.Fitness.UNSUPPORTED;
          }
        }
        if ((i != 0) && (j != 0))
        {
          LOGGER.warning("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
          return PolicyAssertionValidator.Fitness.INVALID;
        }
      }
    }
    return PolicyAssertionValidator.Fitness.SUPPORTED;
  }
  
  public String[] declareSupportedDomains()
  {
    return new String[] { MEMBERpolicyNsUri, W3CpolicyNsUri, "http://www.w3.org/2007/05/addressing/metadata" };
  }
  
  static
  {
    supportedAssertions.add(new QName(MEMBERpolicyNsUri, "UsingAddressing"));
    supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
    supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
    supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\policy\AddressingPolicyValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */