package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

public class WSDLGenInfo
{
  WSDLResolver wsdlResolver;
  Container container;
  boolean inlineSchemas;
  boolean secureXmlProcessingDisabled;
  WSDLGeneratorExtension[] extensions;
  
  public WSDLGenInfo() {}
  
  public WSDLResolver getWsdlResolver()
  {
    return wsdlResolver;
  }
  
  public void setWsdlResolver(WSDLResolver paramWSDLResolver)
  {
    wsdlResolver = paramWSDLResolver;
  }
  
  public Container getContainer()
  {
    return container;
  }
  
  public void setContainer(Container paramContainer)
  {
    container = paramContainer;
  }
  
  public boolean isInlineSchemas()
  {
    return inlineSchemas;
  }
  
  public void setInlineSchemas(boolean paramBoolean)
  {
    inlineSchemas = paramBoolean;
  }
  
  public WSDLGeneratorExtension[] getExtensions()
  {
    if (extensions == null) {
      return new WSDLGeneratorExtension[0];
    }
    return extensions;
  }
  
  public void setExtensions(WSDLGeneratorExtension[] paramArrayOfWSDLGeneratorExtension)
  {
    extensions = paramArrayOfWSDLGeneratorExtension;
  }
  
  public void setSecureXmlProcessingDisabled(boolean paramBoolean)
  {
    secureXmlProcessingDisabled = paramBoolean;
  }
  
  public boolean isSecureXmlProcessingDisabled()
  {
    return secureXmlProcessingDisabled;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\databinding\WSDLGenInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */