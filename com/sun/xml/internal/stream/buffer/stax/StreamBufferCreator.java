package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.stream.buffer.AbstractCreator;
import java.util.ArrayList;
import java.util.List;

abstract class StreamBufferCreator
  extends AbstractCreator
{
  private boolean checkAttributeValue = false;
  protected List<String> attributeValuePrefixes = new ArrayList();
  
  StreamBufferCreator() {}
  
  protected void storeQualifiedName(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if ((paramString2 != null) && (paramString2.length() > 0))
    {
      if ((paramString1 != null) && (paramString1.length() > 0))
      {
        paramInt |= 0x1;
        storeStructureString(paramString1);
      }
      paramInt |= 0x2;
      storeStructureString(paramString2);
    }
    storeStructureString(paramString3);
    storeStructure(paramInt);
  }
  
  protected final void storeNamespaceAttribute(String paramString1, String paramString2)
  {
    int i = 64;
    if ((paramString1 != null) && (paramString1.length() > 0))
    {
      i |= 0x1;
      storeStructureString(paramString1);
    }
    if ((paramString2 != null) && (paramString2.length() > 0))
    {
      i |= 0x2;
      storeStructureString(paramString2);
    }
    storeStructure(i);
  }
  
  protected final void storeAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    storeQualifiedName(48, paramString1, paramString2, paramString3);
    storeStructureString(paramString4);
    storeContentString(paramString5);
    if ((checkAttributeValue) && (paramString5.indexOf("://") == -1))
    {
      int i = paramString5.indexOf(":");
      int j = paramString5.lastIndexOf(":");
      if ((i != -1) && (j == i))
      {
        String str = paramString5.substring(0, i);
        if (!attributeValuePrefixes.contains(str)) {
          attributeValuePrefixes.add(str);
        }
      }
    }
  }
  
  public final List getAttributeValuePrefixes()
  {
    return attributeValuePrefixes;
  }
  
  protected final void storeProcessingInstruction(String paramString1, String paramString2)
  {
    storeStructure(112);
    storeStructureString(paramString1);
    storeStructureString(paramString2);
  }
  
  public final boolean isCheckAttributeValue()
  {
    return checkAttributeValue;
  }
  
  public final void setCheckAttributeValue(boolean paramBoolean)
  {
    checkAttributeValue = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\stax\StreamBufferCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */