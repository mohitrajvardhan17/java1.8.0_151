package com.sun.org.apache.xerces.internal.util;

public final class SecurityManager
{
  private static final int DEFAULT_ENTITY_EXPANSION_LIMIT = 64000;
  private static final int DEFAULT_MAX_OCCUR_NODE_LIMIT = 5000;
  private static final int DEFAULT_ELEMENT_ATTRIBUTE_LIMIT = 10000;
  private int entityExpansionLimit = 64000;
  private int maxOccurLimit = 5000;
  private int fElementAttributeLimit = 10000;
  
  public SecurityManager()
  {
    readSystemProperties();
  }
  
  public void setEntityExpansionLimit(int paramInt)
  {
    entityExpansionLimit = paramInt;
  }
  
  public int getEntityExpansionLimit()
  {
    return entityExpansionLimit;
  }
  
  public void setMaxOccurNodeLimit(int paramInt)
  {
    maxOccurLimit = paramInt;
  }
  
  public int getMaxOccurNodeLimit()
  {
    return maxOccurLimit;
  }
  
  public int getElementAttrLimit()
  {
    return fElementAttributeLimit;
  }
  
  public void setElementAttrLimit(int paramInt)
  {
    fElementAttributeLimit = paramInt;
  }
  
  private void readSystemProperties()
  {
    try
    {
      String str1 = System.getProperty("entityExpansionLimit");
      if ((str1 != null) && (!str1.equals("")))
      {
        entityExpansionLimit = Integer.parseInt(str1);
        if (entityExpansionLimit < 0) {
          entityExpansionLimit = 64000;
        }
      }
      else
      {
        entityExpansionLimit = 64000;
      }
    }
    catch (Exception localException1) {}
    try
    {
      String str2 = System.getProperty("maxOccurLimit");
      if ((str2 != null) && (!str2.equals("")))
      {
        maxOccurLimit = Integer.parseInt(str2);
        if (maxOccurLimit < 0) {
          maxOccurLimit = 5000;
        }
      }
      else
      {
        maxOccurLimit = 5000;
      }
    }
    catch (Exception localException2) {}
    try
    {
      String str3 = System.getProperty("elementAttributeLimit");
      if ((str3 != null) && (!str3.equals("")))
      {
        fElementAttributeLimit = Integer.parseInt(str3);
        if (fElementAttributeLimit < 0) {
          fElementAttributeLimit = 10000;
        }
      }
      else
      {
        fElementAttributeLimit = 10000;
      }
    }
    catch (Exception localException3) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\SecurityManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */