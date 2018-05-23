package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.HashMap;

public class StAXManager
{
  protected static final String STAX_NOTATIONS = "javax.xml.stream.notations";
  protected static final String STAX_ENTITIES = "javax.xml.stream.entities";
  HashMap features = new HashMap();
  public static final int CONTEXT_READER = 1;
  public static final int CONTEXT_WRITER = 2;
  
  public StAXManager() {}
  
  public StAXManager(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      initConfigurableReaderProperties();
      break;
    case 2: 
      initWriterProps();
    }
  }
  
  public StAXManager(StAXManager paramStAXManager)
  {
    HashMap localHashMap = paramStAXManager.getProperties();
    features.putAll(localHashMap);
  }
  
  private HashMap getProperties()
  {
    return features;
  }
  
  private void initConfigurableReaderProperties()
  {
    features.put("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
    features.put("javax.xml.stream.isValidating", Boolean.FALSE);
    features.put("javax.xml.stream.isReplacingEntityReferences", Boolean.TRUE);
    features.put("javax.xml.stream.isSupportingExternalEntities", Boolean.TRUE);
    features.put("javax.xml.stream.isCoalescing", Boolean.FALSE);
    features.put("javax.xml.stream.supportDTD", Boolean.FALSE);
    features.put("javax.xml.stream.reporter", null);
    features.put("javax.xml.stream.resolver", null);
    features.put("javax.xml.stream.allocator", null);
    features.put("javax.xml.stream.notations", null);
  }
  
  private void initWriterProps()
  {
    features.put("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
  }
  
  public boolean containsProperty(String paramString)
  {
    return features.containsKey(paramString);
  }
  
  public Object getProperty(String paramString)
  {
    checkProperty(paramString);
    return features.get(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    checkProperty(paramString);
    if ((paramString.equals("javax.xml.stream.isValidating")) && (Boolean.TRUE.equals(paramObject))) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.validationNotSupported") + CommonResourceBundle.getInstance().getString("support_validation"));
    }
    if ((paramString.equals("javax.xml.stream.isSupportingExternalEntities")) && (Boolean.TRUE.equals(paramObject))) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.externalEntities") + CommonResourceBundle.getInstance().getString("resolve_external_entities_"));
    }
    features.put(paramString, paramObject);
  }
  
  public void checkProperty(String paramString)
  {
    if (!features.containsKey(paramString)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { paramString }));
    }
  }
  
  public String toString()
  {
    return features.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\StAXManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */