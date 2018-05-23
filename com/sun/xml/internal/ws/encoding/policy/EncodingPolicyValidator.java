package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator.Fitness;
import java.util.ArrayList;
import javax.xml.namespace.QName;

public class EncodingPolicyValidator
  implements PolicyAssertionValidator
{
  private static final ArrayList<QName> serverSideSupportedAssertions = new ArrayList(3);
  private static final ArrayList<QName> clientSideSupportedAssertions = new ArrayList(4);
  
  public EncodingPolicyValidator() {}
  
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion)
  {
    return clientSideSupportedAssertions.contains(paramPolicyAssertion.getName()) ? PolicyAssertionValidator.Fitness.SUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN;
  }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion)
  {
    QName localQName = paramPolicyAssertion.getName();
    if (serverSideSupportedAssertions.contains(localQName)) {
      return PolicyAssertionValidator.Fitness.SUPPORTED;
    }
    if (clientSideSupportedAssertions.contains(localQName)) {
      return PolicyAssertionValidator.Fitness.UNSUPPORTED;
    }
    return PolicyAssertionValidator.Fitness.UNKNOWN;
  }
  
  public String[] declareSupportedDomains()
  {
    return new String[] { "http://schemas.xmlsoap.org/ws/2004/09/policy/optimizedmimeserialization", "http://schemas.xmlsoap.org/ws/2004/09/policy/encoding", "http://java.sun.com/xml/ns/wsit/2006/09/policy/encoding/client", "http://java.sun.com/xml/ns/wsit/2006/09/policy/fastinfoset/service" };
  }
  
  static
  {
    serverSideSupportedAssertions.add(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION);
    serverSideSupportedAssertions.add(EncodingConstants.UTF816FFFE_CHARACTER_ENCODING_ASSERTION);
    serverSideSupportedAssertions.add(EncodingConstants.OPTIMIZED_FI_SERIALIZATION_ASSERTION);
    clientSideSupportedAssertions.add(EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION);
    clientSideSupportedAssertions.addAll(serverSideSupportedAssertions);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\policy\EncodingPolicyValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */