package com.sun.org.apache.xerces.internal.xni.parser;

public abstract interface XMLComponent
{
  public abstract void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException;
  
  public abstract String[] getRecognizedFeatures();
  
  public abstract void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException;
  
  public abstract String[] getRecognizedProperties();
  
  public abstract void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException;
  
  public abstract Boolean getFeatureDefault(String paramString);
  
  public abstract Object getPropertyDefault(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */