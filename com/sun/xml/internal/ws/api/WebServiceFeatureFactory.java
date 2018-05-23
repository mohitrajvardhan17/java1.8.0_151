package com.sun.xml.internal.ws.api;

import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.lang.annotation.Annotation;
import javax.xml.ws.WebServiceFeature;

public class WebServiceFeatureFactory
{
  public WebServiceFeatureFactory() {}
  
  public static WSFeatureList getWSFeatureList(Iterable<Annotation> paramIterable)
  {
    WebServiceFeatureList localWebServiceFeatureList = new WebServiceFeatureList();
    localWebServiceFeatureList.parseAnnotations(paramIterable);
    return localWebServiceFeatureList;
  }
  
  public static WebServiceFeature getWebServiceFeature(Annotation paramAnnotation)
  {
    return WebServiceFeatureList.getFeature(paramAnnotation);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\WebServiceFeatureFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */