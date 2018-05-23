package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import javax.xml.ws.WebServiceFeature;

public final class SEIPortInfo
  extends PortInfo
{
  public final Class sei;
  public final SOAPSEIModel model;
  
  public SEIPortInfo(WSServiceDelegate paramWSServiceDelegate, Class paramClass, SOAPSEIModel paramSOAPSEIModel, @NotNull WSDLPort paramWSDLPort)
  {
    super(paramWSServiceDelegate, paramWSDLPort);
    sei = paramClass;
    model = paramSOAPSEIModel;
    assert ((paramClass != null) && (paramSOAPSEIModel != null));
  }
  
  public BindingImpl createBinding(WebServiceFeature[] paramArrayOfWebServiceFeature, Class<?> paramClass)
  {
    BindingImpl localBindingImpl = super.createBinding(paramArrayOfWebServiceFeature, paramClass);
    return setKnownHeaders(localBindingImpl);
  }
  
  public BindingImpl createBinding(WebServiceFeatureList paramWebServiceFeatureList, Class<?> paramClass)
  {
    BindingImpl localBindingImpl = super.createBinding(paramWebServiceFeatureList, paramClass, null);
    return setKnownHeaders(localBindingImpl);
  }
  
  private BindingImpl setKnownHeaders(BindingImpl paramBindingImpl)
  {
    if ((paramBindingImpl instanceof SOAPBindingImpl)) {
      ((SOAPBindingImpl)paramBindingImpl).setPortKnownHeaders(model.getKnownHeaders());
    }
    return paramBindingImpl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\SEIPortInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */