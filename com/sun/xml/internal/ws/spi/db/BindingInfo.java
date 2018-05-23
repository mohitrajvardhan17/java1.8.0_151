package com.sun.xml.internal.ws.spi.db;

import com.sun.xml.internal.ws.api.model.SEIModel;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BindingInfo
{
  private String databindingMode;
  private String defaultNamespace;
  private Collection<Class> contentClasses = new ArrayList();
  private Collection<TypeInfo> typeInfos = new ArrayList();
  private Map<Class, Class> subclassReplacements = new HashMap();
  private Map<String, Object> properties = new HashMap();
  protected ClassLoader classLoader;
  private SEIModel seiModel;
  private URL wsdlURL;
  
  public BindingInfo() {}
  
  public String getDatabindingMode()
  {
    return databindingMode;
  }
  
  public void setDatabindingMode(String paramString)
  {
    databindingMode = paramString;
  }
  
  public String getDefaultNamespace()
  {
    return defaultNamespace;
  }
  
  public void setDefaultNamespace(String paramString)
  {
    defaultNamespace = paramString;
  }
  
  public Collection<Class> contentClasses()
  {
    return contentClasses;
  }
  
  public Collection<TypeInfo> typeInfos()
  {
    return typeInfos;
  }
  
  public Map<Class, Class> subclassReplacements()
  {
    return subclassReplacements;
  }
  
  public Map<String, Object> properties()
  {
    return properties;
  }
  
  public SEIModel getSEIModel()
  {
    return seiModel;
  }
  
  public void setSEIModel(SEIModel paramSEIModel)
  {
    seiModel = paramSEIModel;
  }
  
  public ClassLoader getClassLoader()
  {
    return classLoader;
  }
  
  public void setClassLoader(ClassLoader paramClassLoader)
  {
    classLoader = paramClassLoader;
  }
  
  public URL getWsdlURL()
  {
    return wsdlURL;
  }
  
  public void setWsdlURL(URL paramURL)
  {
    wsdlURL = paramURL;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\BindingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */