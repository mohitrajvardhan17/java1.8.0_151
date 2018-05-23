package com.sun.xml.internal.ws.api.config.management.policy;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.SimpleAssertion;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public abstract class ManagementAssertion
  extends SimpleAssertion
{
  protected static final QName MANAGEMENT_ATTRIBUTE_QNAME = new QName("management");
  protected static final QName MONITORING_ATTRIBUTE_QNAME = new QName("monitoring");
  private static final QName ID_ATTRIBUTE_QNAME = new QName("id");
  private static final QName START_ATTRIBUTE_QNAME = new QName("start");
  private static final Logger LOGGER = Logger.getLogger(ManagementAssertion.class);
  
  protected static <T extends ManagementAssertion> T getAssertion(QName paramQName1, PolicyMap paramPolicyMap, QName paramQName2, QName paramQName3, Class<T> paramClass)
    throws WebServiceException
  {
    try
    {
      PolicyAssertion localPolicyAssertion = null;
      if (paramPolicyMap != null)
      {
        PolicyMapKey localPolicyMapKey = PolicyMap.createWsdlEndpointScopeKey(paramQName2, paramQName3);
        Policy localPolicy = paramPolicyMap.getEndpointEffectivePolicy(localPolicyMapKey);
        if (localPolicy != null)
        {
          Iterator localIterator1 = localPolicy.iterator();
          if (localIterator1.hasNext())
          {
            AssertionSet localAssertionSet = (AssertionSet)localIterator1.next();
            Iterator localIterator2 = localAssertionSet.get(paramQName1).iterator();
            if (localIterator2.hasNext()) {
              localPolicyAssertion = (PolicyAssertion)localIterator2.next();
            }
          }
        }
      }
      return localPolicyAssertion == null ? null : (ManagementAssertion)localPolicyAssertion.getImplementation(paramClass);
    }
    catch (PolicyException localPolicyException)
    {
      throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1001_FAILED_ASSERTION(paramQName1), localPolicyException)));
    }
  }
  
  protected ManagementAssertion(QName paramQName, AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection)
    throws AssertionCreationException
  {
    super(paramAssertionData, paramCollection);
    if (!paramQName.equals(paramAssertionData.getName())) {
      throw ((AssertionCreationException)LOGGER.logSevereException(new AssertionCreationException(paramAssertionData, ManagementMessages.WSM_1002_EXPECTED_MANAGEMENT_ASSERTION(paramQName))));
    }
    if ((isManagementEnabled()) && (!paramAssertionData.containsAttribute(ID_ATTRIBUTE_QNAME))) {
      throw ((AssertionCreationException)LOGGER.logSevereException(new AssertionCreationException(paramAssertionData, ManagementMessages.WSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(paramQName))));
    }
  }
  
  public String getId()
  {
    return getAttributeValue(ID_ATTRIBUTE_QNAME);
  }
  
  public String getStart()
  {
    return getAttributeValue(START_ATTRIBUTE_QNAME);
  }
  
  public abstract boolean isManagementEnabled();
  
  public Setting monitoringAttribute()
  {
    String str = getAttributeValue(MONITORING_ATTRIBUTE_QNAME);
    Setting localSetting = Setting.NOT_SET;
    if (str != null) {
      if ((str.trim().toLowerCase().equals("on")) || (Boolean.parseBoolean(str))) {
        localSetting = Setting.ON;
      } else {
        localSetting = Setting.OFF;
      }
    }
    return localSetting;
  }
  
  public static enum Setting
  {
    NOT_SET,  OFF,  ON;
    
    private Setting() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\config\management\policy\ManagementAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */