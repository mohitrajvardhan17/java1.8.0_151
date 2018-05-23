package com.sun.xml.internal.ws.model.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

public class SOAPBindingImpl
  extends SOAPBinding
{
  public SOAPBindingImpl() {}
  
  public SOAPBindingImpl(SOAPBinding paramSOAPBinding)
  {
    use = paramSOAPBinding.getUse();
    style = paramSOAPBinding.getStyle();
    soapVersion = paramSOAPBinding.getSOAPVersion();
    soapAction = paramSOAPBinding.getSOAPAction();
  }
  
  public void setStyle(SOAPBinding.Style paramStyle)
  {
    style = paramStyle;
  }
  
  public void setSOAPVersion(SOAPVersion paramSOAPVersion)
  {
    soapVersion = paramSOAPVersion;
  }
  
  public void setSOAPAction(String paramString)
  {
    soapAction = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\soap\SOAPBindingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */