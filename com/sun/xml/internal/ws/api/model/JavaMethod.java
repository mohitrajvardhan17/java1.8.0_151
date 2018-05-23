package com.sun.xml.internal.ws.api.model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import java.lang.reflect.Method;
import javax.xml.namespace.QName;

public abstract interface JavaMethod
{
  public abstract SEIModel getOwner();
  
  @NotNull
  public abstract Method getMethod();
  
  @NotNull
  public abstract Method getSEIMethod();
  
  public abstract MEP getMEP();
  
  public abstract SOAPBinding getBinding();
  
  @NotNull
  public abstract String getOperationName();
  
  @NotNull
  public abstract String getRequestMessageName();
  
  @Nullable
  public abstract String getResponseMessageName();
  
  @Nullable
  public abstract QName getRequestPayloadName();
  
  @Nullable
  public abstract QName getResponsePayloadName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\JavaMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */