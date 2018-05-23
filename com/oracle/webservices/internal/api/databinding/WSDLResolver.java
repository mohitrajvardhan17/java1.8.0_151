package com.oracle.webservices.internal.api.databinding;

import javax.xml.transform.Result;
import javax.xml.ws.Holder;

public abstract interface WSDLResolver
{
  public abstract Result getWSDL(String paramString);
  
  public abstract Result getAbstractWSDL(Holder<String> paramHolder);
  
  public abstract Result getSchemaOutput(String paramString, Holder<String> paramHolder);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\databinding\WSDLResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */