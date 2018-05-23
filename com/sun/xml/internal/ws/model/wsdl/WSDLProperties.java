package com.sun.xml.internal.ws.model.wsdl;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import javax.xml.namespace.QName;
import org.xml.sax.InputSource;

public abstract class WSDLProperties
  extends BasePropertySet
{
  private static final BasePropertySet.PropertyMap model = parse(WSDLProperties.class);
  @Nullable
  private final SEIModel seiModel;
  
  protected WSDLProperties(@Nullable SEIModel paramSEIModel)
  {
    seiModel = paramSEIModel;
  }
  
  @PropertySet.Property({"javax.xml.ws.wsdl.service"})
  public abstract QName getWSDLService();
  
  @PropertySet.Property({"javax.xml.ws.wsdl.port"})
  public abstract QName getWSDLPort();
  
  @PropertySet.Property({"javax.xml.ws.wsdl.interface"})
  public abstract QName getWSDLPortType();
  
  @PropertySet.Property({"javax.xml.ws.wsdl.description"})
  public InputSource getWSDLDescription()
  {
    return seiModel != null ? new InputSource(seiModel.getWSDLLocation()) : null;
  }
  
  protected BasePropertySet.PropertyMap getPropertyMap()
  {
    return model;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */