package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.util.Iterator;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceFeature;

abstract class AbstractFeaturedObjectImpl
  extends AbstractExtensibleImpl
  implements WSDLFeaturedObject
{
  protected WebServiceFeatureList features;
  
  protected AbstractFeaturedObjectImpl(XMLStreamReader paramXMLStreamReader)
  {
    super(paramXMLStreamReader);
  }
  
  protected AbstractFeaturedObjectImpl(String paramString, int paramInt)
  {
    super(paramString, paramInt);
  }
  
  public final void addFeature(WebServiceFeature paramWebServiceFeature)
  {
    if (features == null) {
      features = new WebServiceFeatureList();
    }
    features.add(paramWebServiceFeature);
  }
  
  @NotNull
  public WebServiceFeatureList getFeatures()
  {
    if (features == null) {
      return new WebServiceFeatureList();
    }
    return features;
  }
  
  public final WebServiceFeature getFeature(String paramString)
  {
    if (features != null)
    {
      Iterator localIterator = features.iterator();
      while (localIterator.hasNext())
      {
        WebServiceFeature localWebServiceFeature = (WebServiceFeature)localIterator.next();
        if (localWebServiceFeature.getID().equals(paramString)) {
          return localWebServiceFeature;
        }
      }
    }
    return null;
  }
  
  @Nullable
  public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> paramClass)
  {
    if (features == null) {
      return null;
    }
    return features.get(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\AbstractFeaturedObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */