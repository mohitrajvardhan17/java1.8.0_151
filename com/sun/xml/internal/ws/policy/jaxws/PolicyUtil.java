package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.addressing.policy.AddressingFeatureConfigurator;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.encoding.policy.FastInfosetFeatureConfigurator;
import com.sun.xml.internal.ws.encoding.policy.MtomFeatureConfigurator;
import com.sun.xml.internal.ws.encoding.policy.SelectOptimalEncodingFeatureConfigurator;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class PolicyUtil
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtil.class);
  private static final Collection<PolicyFeatureConfigurator> CONFIGURATORS = new LinkedList();
  
  public PolicyUtil() {}
  
  public static <T> void addServiceProviders(Collection<T> paramCollection, Class<T> paramClass)
  {
    Iterator localIterator = ServiceFinder.find(paramClass).iterator();
    while (localIterator.hasNext()) {
      paramCollection.add(localIterator.next());
    }
  }
  
  public static void configureModel(WSDLModel paramWSDLModel, PolicyMap paramPolicyMap)
    throws PolicyException
  {
    LOGGER.entering(new Object[] { paramWSDLModel, paramPolicyMap });
    Iterator localIterator1 = paramWSDLModel.getServices().values().iterator();
    while (localIterator1.hasNext())
    {
      WSDLService localWSDLService = (WSDLService)localIterator1.next();
      Iterator localIterator2 = localWSDLService.getPorts().iterator();
      while (localIterator2.hasNext())
      {
        WSDLPort localWSDLPort = (WSDLPort)localIterator2.next();
        Collection localCollection = getPortScopedFeatures(paramPolicyMap, localWSDLService.getName(), localWSDLPort.getName());
        Iterator localIterator3 = localCollection.iterator();
        while (localIterator3.hasNext())
        {
          WebServiceFeature localWebServiceFeature = (WebServiceFeature)localIterator3.next();
          localWSDLPort.addFeature(localWebServiceFeature);
          localWSDLPort.getBinding().addFeature(localWebServiceFeature);
        }
      }
    }
    LOGGER.exiting();
  }
  
  public static Collection<WebServiceFeature> getPortScopedFeatures(PolicyMap paramPolicyMap, QName paramQName1, QName paramQName2)
  {
    LOGGER.entering(new Object[] { paramPolicyMap, paramQName1, paramQName2 });
    ArrayList localArrayList = new ArrayList();
    try
    {
      PolicyMapKey localPolicyMapKey = PolicyMap.createWsdlEndpointScopeKey(paramQName1, paramQName2);
      Iterator localIterator = CONFIGURATORS.iterator();
      while (localIterator.hasNext())
      {
        PolicyFeatureConfigurator localPolicyFeatureConfigurator = (PolicyFeatureConfigurator)localIterator.next();
        Collection localCollection = localPolicyFeatureConfigurator.getFeatures(localPolicyMapKey, paramPolicyMap);
        if (localCollection != null) {
          localArrayList.addAll(localCollection);
        }
      }
    }
    catch (PolicyException localPolicyException)
    {
      throw new WebServiceException(localPolicyException);
    }
    LOGGER.exiting(localArrayList);
    return localArrayList;
  }
  
  static
  {
    CONFIGURATORS.add(new AddressingFeatureConfigurator());
    CONFIGURATORS.add(new MtomFeatureConfigurator());
    CONFIGURATORS.add(new FastInfosetFeatureConfigurator());
    CONFIGURATORS.add(new SelectOptimalEncodingFeatureConfigurator());
    addServiceProviders(CONFIGURATORS, PolicyFeatureConfigurator.class);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\PolicyUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */