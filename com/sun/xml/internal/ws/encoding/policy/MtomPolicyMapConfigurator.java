package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.MTOMFeature;

public class MtomPolicyMapConfigurator
  implements PolicyMapConfigurator
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(MtomPolicyMapConfigurator.class);
  
  public MtomPolicyMapConfigurator() {}
  
  public Collection<PolicySubject> update(PolicyMap paramPolicyMap, SEIModel paramSEIModel, WSBinding paramWSBinding)
    throws PolicyException
  {
    LOGGER.entering(new Object[] { paramPolicyMap, paramSEIModel, paramWSBinding });
    ArrayList localArrayList = new ArrayList();
    if (paramPolicyMap != null)
    {
      MTOMFeature localMTOMFeature = (MTOMFeature)paramWSBinding.getFeature(MTOMFeature.class);
      if (LOGGER.isLoggable(Level.FINEST)) {
        LOGGER.finest("mtomFeature = " + localMTOMFeature);
      }
      if ((localMTOMFeature != null) && (localMTOMFeature.isEnabled()))
      {
        QName localQName = paramSEIModel.getBoundPortTypeName();
        WsdlBindingSubject localWsdlBindingSubject = WsdlBindingSubject.createBindingSubject(localQName);
        Policy localPolicy = createMtomPolicy(localQName);
        PolicySubject localPolicySubject = new PolicySubject(localWsdlBindingSubject, localPolicy);
        localArrayList.add(localPolicySubject);
        if (LOGGER.isLoggable(Level.FINEST)) {
          LOGGER.fine("Added MTOM policy with ID \"" + localPolicy.getIdOrName() + "\" to binding element \"" + localQName + "\"");
        }
      }
    }
    LOGGER.exiting(localArrayList);
    return localArrayList;
  }
  
  private Policy createMtomPolicy(QName paramQName)
  {
    ArrayList localArrayList1 = new ArrayList(1);
    ArrayList localArrayList2 = new ArrayList(1);
    localArrayList2.add(new MtomAssertion());
    localArrayList1.add(AssertionSet.createAssertionSet(localArrayList2));
    return Policy.createPolicy(null, paramQName.getLocalPart() + "_MTOM_Policy", localArrayList1);
  }
  
  static class MtomAssertion
    extends PolicyAssertion
  {
    private static final AssertionData mtomData = AssertionData.createAssertionData(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION);
    
    MtomAssertion()
    {
      super(null, null);
    }
    
    static
    {
      mtomData.setOptionalAttribute(true);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\policy\MtomPolicyMapConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */