package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSFeatureList;
import javax.xml.ws.WebServiceFeature;

public abstract interface WSDLFeaturedObject
  extends WSDLObject
{
  @Nullable
  public abstract <F extends WebServiceFeature> F getFeature(@NotNull Class<F> paramClass);
  
  @NotNull
  public abstract WSFeatureList getFeatures();
  
  public abstract void addFeature(@NotNull WebServiceFeature paramWebServiceFeature);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLFeaturedObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */