package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceException;

public abstract class BindingIDFactory
{
  public BindingIDFactory() {}
  
  @Nullable
  public abstract BindingID parse(@NotNull String paramString)
    throws WebServiceException;
  
  @Nullable
  public BindingID create(@NotNull String paramString, @NotNull SOAPVersion paramSOAPVersion)
    throws WebServiceException
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\BindingIDFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */