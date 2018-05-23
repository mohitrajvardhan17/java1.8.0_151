package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.namespace.QName;

public class MappingInfo
{
  protected String targetNamespace;
  protected String databindingMode;
  protected SoapBodyStyle soapBodyStyle;
  protected BindingID bindingID;
  protected QName serviceName;
  protected QName portName;
  protected String defaultSchemaNamespaceSuffix;
  
  public MappingInfo() {}
  
  public String getTargetNamespace()
  {
    return targetNamespace;
  }
  
  public void setTargetNamespace(String paramString)
  {
    targetNamespace = paramString;
  }
  
  public String getDatabindingMode()
  {
    return databindingMode;
  }
  
  public void setDatabindingMode(String paramString)
  {
    databindingMode = paramString;
  }
  
  public SoapBodyStyle getSoapBodyStyle()
  {
    return soapBodyStyle;
  }
  
  public void setSoapBodyStyle(SoapBodyStyle paramSoapBodyStyle)
  {
    soapBodyStyle = paramSoapBodyStyle;
  }
  
  public BindingID getBindingID()
  {
    return bindingID;
  }
  
  public void setBindingID(BindingID paramBindingID)
  {
    bindingID = paramBindingID;
  }
  
  public QName getServiceName()
  {
    return serviceName;
  }
  
  public void setServiceName(QName paramQName)
  {
    serviceName = paramQName;
  }
  
  public QName getPortName()
  {
    return portName;
  }
  
  public void setPortName(QName paramQName)
  {
    portName = paramQName;
  }
  
  public String getDefaultSchemaNamespaceSuffix()
  {
    return defaultSchemaNamespaceSuffix;
  }
  
  public void setDefaultSchemaNamespaceSuffix(String paramString)
  {
    defaultSchemaNamespaceSuffix = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\databinding\MappingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */