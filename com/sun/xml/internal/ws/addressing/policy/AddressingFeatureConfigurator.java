package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.AddressingFeature.Responses;

public class AddressingFeatureConfigurator
  implements PolicyFeatureConfigurator
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingFeatureConfigurator.class);
  private static final QName[] ADDRESSING_ASSERTIONS = { new QName(MEMBERpolicyNsUri, "UsingAddressing") };
  
  public AddressingFeatureConfigurator() {}
  
  public Collection<WebServiceFeature> getFeatures(PolicyMapKey paramPolicyMapKey, PolicyMap paramPolicyMap)
    throws PolicyException
  {
    LOGGER.entering(new Object[] { paramPolicyMapKey, paramPolicyMap });
    LinkedList localLinkedList = new LinkedList();
    if ((paramPolicyMapKey != null) && (paramPolicyMap != null))
    {
      Policy localPolicy = paramPolicyMap.getEndpointEffectivePolicy(paramPolicyMapKey);
      Object localObject2;
      Object localObject3;
      Object localObject4;
      for (localObject2 : ADDRESSING_ASSERTIONS) {
        if ((localPolicy != null) && (localPolicy.contains((QName)localObject2)))
        {
          localObject3 = localPolicy.iterator();
          while (((Iterator)localObject3).hasNext())
          {
            AssertionSet localAssertionSet2 = (AssertionSet)((Iterator)localObject3).next();
            Iterator localIterator2 = localAssertionSet2.iterator();
            while (localIterator2.hasNext())
            {
              localObject4 = (PolicyAssertion)localIterator2.next();
              if (((PolicyAssertion)localObject4).getName().equals(localObject2))
              {
                WebServiceFeature localWebServiceFeature = AddressingVersion.getFeature(((QName)localObject2).getNamespaceURI(), true, !((PolicyAssertion)localObject4).isOptional());
                if (LOGGER.isLoggable(Level.FINE)) {
                  LOGGER.fine("Added addressing feature \"" + localWebServiceFeature + "\" for element \"" + paramPolicyMapKey + "\"");
                }
                localLinkedList.add(localWebServiceFeature);
              }
            }
          }
        }
      }
      if ((localPolicy != null) && (localPolicy.contains(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)))
      {
        ??? = localPolicy.iterator();
        while (((Iterator)???).hasNext())
        {
          AssertionSet localAssertionSet1 = (AssertionSet)((Iterator)???).next();
          Iterator localIterator1 = localAssertionSet1.iterator();
          while (localIterator1.hasNext())
          {
            localObject2 = (PolicyAssertion)localIterator1.next();
            if (((PolicyAssertion)localObject2).getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION))
            {
              localObject3 = ((PolicyAssertion)localObject2).getNestedPolicy();
              boolean bool1 = false;
              boolean bool2 = false;
              if (localObject3 != null)
              {
                bool1 = ((NestedPolicy)localObject3).contains(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
                bool2 = ((NestedPolicy)localObject3).contains(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
              }
              if ((bool1) && (bool2)) {
                throw new WebServiceException("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
              }
              try
              {
                if (bool1) {
                  localObject4 = new AddressingFeature(true, !((PolicyAssertion)localObject2).isOptional(), AddressingFeature.Responses.ANONYMOUS);
                } else if (bool2) {
                  localObject4 = new AddressingFeature(true, !((PolicyAssertion)localObject2).isOptional(), AddressingFeature.Responses.NON_ANONYMOUS);
                } else {
                  localObject4 = new AddressingFeature(true, !((PolicyAssertion)localObject2).isOptional());
                }
              }
              catch (NoSuchMethodError localNoSuchMethodError)
              {
                throw ((PolicyException)LOGGER.logSevereException(new PolicyException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(toJar(Which.which(AddressingFeature.class))), localNoSuchMethodError)));
              }
              if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Added addressing feature \"" + localObject4 + "\" for element \"" + paramPolicyMapKey + "\"");
              }
              localLinkedList.add(localObject4);
            }
          }
        }
      }
    }
    LOGGER.exiting(localLinkedList);
    return localLinkedList;
  }
  
  private static String toJar(String paramString)
  {
    if (!paramString.startsWith("jar:")) {
      return paramString;
    }
    paramString = paramString.substring(4);
    return paramString.substring(0, paramString.lastIndexOf('!'));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\policy\AddressingFeatureConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */