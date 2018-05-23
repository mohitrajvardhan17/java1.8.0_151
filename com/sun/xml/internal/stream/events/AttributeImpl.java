package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public class AttributeImpl
  extends DummyEvent
  implements Attribute
{
  private String fValue;
  private String fNonNormalizedvalue;
  private QName fQName;
  private String fAttributeType = "CDATA";
  private boolean fIsSpecified;
  
  public AttributeImpl()
  {
    init();
  }
  
  public AttributeImpl(String paramString1, String paramString2)
  {
    init();
    fQName = new QName(paramString1);
    fValue = paramString2;
  }
  
  public AttributeImpl(String paramString1, String paramString2, String paramString3)
  {
    this(paramString1, null, paramString2, paramString3, null, null, false);
  }
  
  public AttributeImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    this(paramString1, paramString2, paramString3, paramString4, null, paramString5, false);
  }
  
  public AttributeImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, boolean paramBoolean)
  {
    this(new QName(paramString2, paramString3, paramString1), paramString4, paramString5, paramString6, paramBoolean);
  }
  
  public AttributeImpl(QName paramQName, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    init();
    fQName = paramQName;
    fValue = paramString1;
    if ((paramString3 != null) && (!paramString3.equals(""))) {
      fAttributeType = paramString3;
    }
    fNonNormalizedvalue = paramString2;
    fIsSpecified = paramBoolean;
  }
  
  public String toString()
  {
    if ((fQName.getPrefix() != null) && (fQName.getPrefix().length() > 0)) {
      return fQName.getPrefix() + ":" + fQName.getLocalPart() + "='" + fValue + "'";
    }
    return fQName.getLocalPart() + "='" + fValue + "'";
  }
  
  public void setName(QName paramQName)
  {
    fQName = paramQName;
  }
  
  public QName getName()
  {
    return fQName;
  }
  
  public void setValue(String paramString)
  {
    fValue = paramString;
  }
  
  public String getValue()
  {
    return fValue;
  }
  
  public void setNonNormalizedValue(String paramString)
  {
    fNonNormalizedvalue = paramString;
  }
  
  public String getNonNormalizedValue()
  {
    return fNonNormalizedvalue;
  }
  
  public void setAttributeType(String paramString)
  {
    fAttributeType = paramString;
  }
  
  public String getDTDType()
  {
    return fAttributeType;
  }
  
  public void setSpecified(boolean paramBoolean)
  {
    fIsSpecified = paramBoolean;
  }
  
  public boolean isSpecified()
  {
    return fIsSpecified;
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write(toString());
  }
  
  protected void init()
  {
    setEventType(10);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\AttributeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */