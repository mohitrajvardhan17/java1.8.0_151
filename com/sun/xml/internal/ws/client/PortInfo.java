package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.client.ServiceInterceptor;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolver.ClientContext;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.jaxws.PolicyUtil;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

public class PortInfo
  implements WSPortInfo
{
  @NotNull
  private final WSServiceDelegate owner;
  @NotNull
  public final QName portName;
  @NotNull
  public final EndpointAddress targetEndpoint;
  @NotNull
  public final BindingID bindingId;
  @NotNull
  public final PolicyMap policyMap;
  @Nullable
  public final WSDLPort portModel;
  
  public PortInfo(WSServiceDelegate paramWSServiceDelegate, EndpointAddress paramEndpointAddress, QName paramQName, BindingID paramBindingID)
  {
    owner = paramWSServiceDelegate;
    targetEndpoint = paramEndpointAddress;
    portName = paramQName;
    bindingId = paramBindingID;
    portModel = getPortModel(paramWSServiceDelegate, paramQName);
    policyMap = createPolicyMap();
  }
  
  public PortInfo(@NotNull WSServiceDelegate paramWSServiceDelegate, @NotNull WSDLPort paramWSDLPort)
  {
    owner = paramWSServiceDelegate;
    targetEndpoint = paramWSDLPort.getAddress();
    portName = paramWSDLPort.getName();
    bindingId = paramWSDLPort.getBinding().getBindingId();
    portModel = paramWSDLPort;
    policyMap = createPolicyMap();
  }
  
  public PolicyMap getPolicyMap()
  {
    return policyMap;
  }
  
  public PolicyMap createPolicyMap()
  {
    PolicyMap localPolicyMap;
    if (portModel != null) {
      localPolicyMap = portModel.getOwner().getParent().getPolicyMap();
    } else {
      localPolicyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ClientContext(null, owner.getContainer()));
    }
    if (localPolicyMap == null) {
      localPolicyMap = PolicyMap.createPolicyMap(null);
    }
    return localPolicyMap;
  }
  
  public BindingImpl createBinding(WebServiceFeature[] paramArrayOfWebServiceFeature, Class<?> paramClass)
  {
    return createBinding(new WebServiceFeatureList(paramArrayOfWebServiceFeature), paramClass, null);
  }
  
  public BindingImpl createBinding(WebServiceFeatureList paramWebServiceFeatureList, Class<?> paramClass, BindingImpl paramBindingImpl)
  {
    if (paramBindingImpl != null) {
      paramWebServiceFeatureList.addAll(paramBindingImpl.getFeatures());
    }
    Object localObject;
    if (portModel != null) {
      localObject = portModel.getFeatures();
    } else {
      localObject = PolicyUtil.getPortScopedFeatures(policyMap, owner.getServiceName(), portName);
    }
    paramWebServiceFeatureList.mergeFeatures((Iterable)localObject, false);
    paramWebServiceFeatureList.mergeFeatures(owner.serviceInterceptor.preCreateBinding(this, paramClass, paramWebServiceFeatureList), false);
    BindingImpl localBindingImpl = BindingImpl.create(bindingId, paramWebServiceFeatureList.toArray());
    owner.getHandlerConfigurator().configureHandlers(this, localBindingImpl);
    return localBindingImpl;
  }
  
  private WSDLPort getPortModel(WSServiceDelegate paramWSServiceDelegate, QName paramQName)
  {
    if (paramWSServiceDelegate.getWsdlService() != null)
    {
      Iterable localIterable = paramWSServiceDelegate.getWsdlService().getPorts();
      Iterator localIterator = localIterable.iterator();
      while (localIterator.hasNext())
      {
        WSDLPort localWSDLPort = (WSDLPort)localIterator.next();
        if (localWSDLPort.getName().equals(paramQName)) {
          return localWSDLPort;
        }
      }
    }
    return null;
  }
  
  @Nullable
  public WSDLPort getPort()
  {
    return portModel;
  }
  
  @NotNull
  public WSService getOwner()
  {
    return owner;
  }
  
  @NotNull
  public BindingID getBindingId()
  {
    return bindingId;
  }
  
  @NotNull
  public EndpointAddress getEndpointAddress()
  {
    return targetEndpoint;
  }
  
  /**
   * @deprecated
   */
  public QName getServiceName()
  {
    return owner.getServiceName();
  }
  
  public QName getPortName()
  {
    return portName;
  }
  
  /**
   * @deprecated
   */
  public String getBindingID()
  {
    return bindingId.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\PortInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */