package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
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
import java.util.Collections;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.AddressingFeature.Responses;

public class AddressingPolicyMapConfigurator
  implements PolicyMapConfigurator
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingPolicyMapConfigurator.class);
  
  public AddressingPolicyMapConfigurator() {}
  
  public Collection<PolicySubject> update(PolicyMap paramPolicyMap, SEIModel paramSEIModel, WSBinding paramWSBinding)
    throws PolicyException
  {
    LOGGER.entering(new Object[] { paramPolicyMap, paramSEIModel, paramWSBinding });
    ArrayList localArrayList = new ArrayList();
    if (paramPolicyMap != null)
    {
      AddressingFeature localAddressingFeature = (AddressingFeature)paramWSBinding.getFeature(AddressingFeature.class);
      if (LOGGER.isLoggable(Level.FINEST)) {
        LOGGER.finest("addressingFeature = " + localAddressingFeature);
      }
      if ((localAddressingFeature != null) && (localAddressingFeature.isEnabled())) {
        addWsamAddressing(localArrayList, paramPolicyMap, paramSEIModel, localAddressingFeature);
      }
    }
    LOGGER.exiting(localArrayList);
    return localArrayList;
  }
  
  private void addWsamAddressing(Collection<PolicySubject> paramCollection, PolicyMap paramPolicyMap, SEIModel paramSEIModel, AddressingFeature paramAddressingFeature)
    throws PolicyException
  {
    QName localQName = paramSEIModel.getBoundPortTypeName();
    WsdlBindingSubject localWsdlBindingSubject = WsdlBindingSubject.createBindingSubject(localQName);
    Policy localPolicy = createWsamAddressingPolicy(localQName, paramAddressingFeature);
    PolicySubject localPolicySubject = new PolicySubject(localWsdlBindingSubject, localPolicy);
    paramCollection.add(localPolicySubject);
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.fine("Added addressing policy with ID \"" + localPolicy.getIdOrName() + "\" to binding element \"" + localQName + "\"");
    }
  }
  
  private Policy createWsamAddressingPolicy(QName paramQName, AddressingFeature paramAddressingFeature)
  {
    ArrayList localArrayList1 = new ArrayList(1);
    ArrayList localArrayList2 = new ArrayList(1);
    AssertionData localAssertionData1 = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
    if (!paramAddressingFeature.isRequired()) {
      localAssertionData1.setOptionalAttribute(true);
    }
    try
    {
      AddressingFeature.Responses localResponses = paramAddressingFeature.getResponses();
      AssertionData localAssertionData2;
      AddressingAssertion localAddressingAssertion;
      if (localResponses == AddressingFeature.Responses.ANONYMOUS)
      {
        localAssertionData2 = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
        localAddressingAssertion = new AddressingAssertion(localAssertionData2, null);
        localArrayList2.add(new AddressingAssertion(localAssertionData1, AssertionSet.createAssertionSet(Collections.singleton(localAddressingAssertion))));
      }
      else if (localResponses == AddressingFeature.Responses.NON_ANONYMOUS)
      {
        localAssertionData2 = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
        localAddressingAssertion = new AddressingAssertion(localAssertionData2, null);
        localArrayList2.add(new AddressingAssertion(localAssertionData1, AssertionSet.createAssertionSet(Collections.singleton(localAddressingAssertion))));
      }
      else
      {
        localArrayList2.add(new AddressingAssertion(localAssertionData1, AssertionSet.createAssertionSet(null)));
      }
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      localArrayList2.add(new AddressingAssertion(localAssertionData1, AssertionSet.createAssertionSet(null)));
    }
    localArrayList1.add(AssertionSet.createAssertionSet(localArrayList2));
    return Policy.createPolicy(null, paramQName.getLocalPart() + "_WSAM_Addressing_Policy", localArrayList1);
  }
  
  private static final class AddressingAssertion
    extends PolicyAssertion
  {
    AddressingAssertion(AssertionData paramAssertionData, AssertionSet paramAssertionSet)
    {
      super(null, paramAssertionSet);
    }
    
    AddressingAssertion(AssertionData paramAssertionData)
    {
      super(null, null);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\policy\AddressingPolicyMapConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */