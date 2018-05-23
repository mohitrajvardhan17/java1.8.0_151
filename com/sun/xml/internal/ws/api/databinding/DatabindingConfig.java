package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public class DatabindingConfig
{
  protected Class contractClass;
  protected Class endpointClass;
  protected Set<Class> additionalValueTypes = new HashSet();
  protected MappingInfo mappingInfo = new MappingInfo();
  protected URL wsdlURL;
  protected ClassLoader classLoader;
  protected Iterable<WebServiceFeature> features;
  protected WSBinding wsBinding;
  protected WSDLPort wsdlPort;
  protected MetadataReader metadataReader;
  protected Map<String, Object> properties = new HashMap();
  protected Source wsdlSource;
  protected EntityResolver entityResolver;
  
  public DatabindingConfig() {}
  
  public Class getContractClass()
  {
    return contractClass;
  }
  
  public void setContractClass(Class paramClass)
  {
    contractClass = paramClass;
  }
  
  public Class getEndpointClass()
  {
    return endpointClass;
  }
  
  public void setEndpointClass(Class paramClass)
  {
    endpointClass = paramClass;
  }
  
  public MappingInfo getMappingInfo()
  {
    return mappingInfo;
  }
  
  public void setMappingInfo(MappingInfo paramMappingInfo)
  {
    mappingInfo = paramMappingInfo;
  }
  
  public URL getWsdlURL()
  {
    return wsdlURL;
  }
  
  public void setWsdlURL(URL paramURL)
  {
    wsdlURL = paramURL;
  }
  
  public ClassLoader getClassLoader()
  {
    return classLoader;
  }
  
  public void setClassLoader(ClassLoader paramClassLoader)
  {
    classLoader = paramClassLoader;
  }
  
  public Iterable<WebServiceFeature> getFeatures()
  {
    if ((features == null) && (wsBinding != null)) {
      return wsBinding.getFeatures();
    }
    return features;
  }
  
  public void setFeatures(WebServiceFeature[] paramArrayOfWebServiceFeature)
  {
    setFeatures(new WebServiceFeatureList(paramArrayOfWebServiceFeature));
  }
  
  public void setFeatures(Iterable<WebServiceFeature> paramIterable)
  {
    features = WebServiceFeatureList.toList(paramIterable);
  }
  
  public WSDLPort getWsdlPort()
  {
    return wsdlPort;
  }
  
  public void setWsdlPort(WSDLPort paramWSDLPort)
  {
    wsdlPort = paramWSDLPort;
  }
  
  public Set<Class> additionalValueTypes()
  {
    return additionalValueTypes;
  }
  
  public Map<String, Object> properties()
  {
    return properties;
  }
  
  public WSBinding getWSBinding()
  {
    return wsBinding;
  }
  
  public void setWSBinding(WSBinding paramWSBinding)
  {
    wsBinding = paramWSBinding;
  }
  
  public MetadataReader getMetadataReader()
  {
    return metadataReader;
  }
  
  public void setMetadataReader(MetadataReader paramMetadataReader)
  {
    metadataReader = paramMetadataReader;
  }
  
  public Source getWsdlSource()
  {
    return wsdlSource;
  }
  
  public void setWsdlSource(Source paramSource)
  {
    wsdlSource = paramSource;
  }
  
  public EntityResolver getEntityResolver()
  {
    return entityResolver;
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
  {
    entityResolver = paramEntityResolver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\databinding\DatabindingConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */