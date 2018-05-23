package com.sun.xml.internal.ws.api.model.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

public abstract class SOAPBinding
{
  protected SOAPBinding.Use use = SOAPBinding.Use.LITERAL;
  protected SOAPBinding.Style style = SOAPBinding.Style.DOCUMENT;
  protected SOAPVersion soapVersion = SOAPVersion.SOAP_11;
  protected String soapAction = "";
  
  public SOAPBinding() {}
  
  public SOAPBinding.Use getUse()
  {
    return use;
  }
  
  public SOAPBinding.Style getStyle()
  {
    return style;
  }
  
  public SOAPVersion getSOAPVersion()
  {
    return soapVersion;
  }
  
  public boolean isDocLit()
  {
    return (style == SOAPBinding.Style.DOCUMENT) && (use == SOAPBinding.Use.LITERAL);
  }
  
  public boolean isRpcLit()
  {
    return (style == SOAPBinding.Style.RPC) && (use == SOAPBinding.Use.LITERAL);
  }
  
  public String getSOAPAction()
  {
    return soapAction;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\soap\SOAPBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */