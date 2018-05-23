package com.sun.xml.internal.ws.api.model;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.util.Pool.Marshaller;
import java.lang.reflect.Method;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

public abstract interface SEIModel
{
  public abstract Pool.Marshaller getMarshallerPool();
  
  /**
   * @deprecated
   */
  public abstract JAXBContext getJAXBContext();
  
  public abstract JavaMethod getJavaMethod(Method paramMethod);
  
  public abstract JavaMethod getJavaMethod(QName paramQName);
  
  public abstract JavaMethod getJavaMethodForWsdlOperation(QName paramQName);
  
  public abstract Collection<? extends JavaMethod> getJavaMethods();
  
  @NotNull
  public abstract String getWSDLLocation();
  
  @NotNull
  public abstract QName getServiceQName();
  
  @NotNull
  public abstract WSDLPort getPort();
  
  @NotNull
  public abstract QName getPortName();
  
  @NotNull
  public abstract QName getPortTypeName();
  
  @NotNull
  public abstract QName getBoundPortTypeName();
  
  @NotNull
  public abstract String getTargetNamespace();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\SEIModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */