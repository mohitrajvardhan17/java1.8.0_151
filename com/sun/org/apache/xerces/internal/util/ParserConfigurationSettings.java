package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParserConfigurationSettings
  implements XMLComponentManager
{
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  protected Set<String> fRecognizedProperties = new HashSet();
  protected Map<String, Object> fProperties = new HashMap();
  protected Set<String> fRecognizedFeatures = new HashSet();
  protected Map<String, Boolean> fFeatures = new HashMap();
  protected XMLComponentManager fParentSettings;
  
  public ParserConfigurationSettings()
  {
    this(null);
  }
  
  public ParserConfigurationSettings(XMLComponentManager paramXMLComponentManager)
  {
    fParentSettings = paramXMLComponentManager;
  }
  
  public void addRecognizedFeatures(String[] paramArrayOfString)
  {
    int i = paramArrayOfString != null ? paramArrayOfString.length : 0;
    for (int j = 0; j < i; j++)
    {
      String str = paramArrayOfString[j];
      if (!fRecognizedFeatures.contains(str)) {
        fRecognizedFeatures.add(str);
      }
    }
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    FeatureState localFeatureState = checkFeature(paramString);
    if (localFeatureState.isExceptional()) {
      throw new XMLConfigurationException(status, paramString);
    }
    fFeatures.put(paramString, Boolean.valueOf(paramBoolean));
  }
  
  public void addRecognizedProperties(String[] paramArrayOfString)
  {
    fRecognizedProperties.addAll(Arrays.asList(paramArrayOfString));
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    PropertyState localPropertyState = checkProperty(paramString);
    if (localPropertyState.isExceptional()) {
      throw new XMLConfigurationException(status, paramString);
    }
    fProperties.put(paramString, paramObject);
  }
  
  public final boolean getFeature(String paramString)
    throws XMLConfigurationException
  {
    FeatureState localFeatureState = getFeatureState(paramString);
    if (localFeatureState.isExceptional()) {
      throw new XMLConfigurationException(status, paramString);
    }
    return state;
  }
  
  public final boolean getFeature(String paramString, boolean paramBoolean)
  {
    FeatureState localFeatureState = getFeatureState(paramString);
    if (localFeatureState.isExceptional()) {
      return paramBoolean;
    }
    return state;
  }
  
  public FeatureState getFeatureState(String paramString)
  {
    Boolean localBoolean = (Boolean)fFeatures.get(paramString);
    if (localBoolean == null)
    {
      FeatureState localFeatureState = checkFeature(paramString);
      if (localFeatureState.isExceptional()) {
        return localFeatureState;
      }
      return FeatureState.is(false);
    }
    return FeatureState.is(localBoolean.booleanValue());
  }
  
  public final Object getProperty(String paramString)
    throws XMLConfigurationException
  {
    PropertyState localPropertyState = getPropertyState(paramString);
    if (localPropertyState.isExceptional()) {
      throw new XMLConfigurationException(status, paramString);
    }
    return state;
  }
  
  public final Object getProperty(String paramString, Object paramObject)
  {
    PropertyState localPropertyState = getPropertyState(paramString);
    if (localPropertyState.isExceptional()) {
      return paramObject;
    }
    return state;
  }
  
  public PropertyState getPropertyState(String paramString)
  {
    Object localObject = fProperties.get(paramString);
    if (localObject == null)
    {
      PropertyState localPropertyState = checkProperty(paramString);
      if (localPropertyState.isExceptional()) {
        return localPropertyState;
      }
    }
    return PropertyState.is(localObject);
  }
  
  protected FeatureState checkFeature(String paramString)
    throws XMLConfigurationException
  {
    if (!fRecognizedFeatures.contains(paramString))
    {
      if (fParentSettings != null) {
        return fParentSettings.getFeatureState(paramString);
      }
      return FeatureState.NOT_RECOGNIZED;
    }
    return FeatureState.RECOGNIZED;
  }
  
  protected PropertyState checkProperty(String paramString)
    throws XMLConfigurationException
  {
    if (!fRecognizedProperties.contains(paramString)) {
      if (fParentSettings != null)
      {
        PropertyState localPropertyState = fParentSettings.getPropertyState(paramString);
        if (localPropertyState.isExceptional()) {
          return localPropertyState;
        }
      }
      else
      {
        return PropertyState.NOT_RECOGNIZED;
      }
    }
    return PropertyState.RECOGNIZED;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\ParserConfigurationSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */