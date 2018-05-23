package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import org.xml.sax.Locator;

public abstract interface WSDLObject
{
  @NotNull
  public abstract Locator getLocation();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */