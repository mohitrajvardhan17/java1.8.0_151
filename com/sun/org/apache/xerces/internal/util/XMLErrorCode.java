package com.sun.org.apache.xerces.internal.util;

final class XMLErrorCode
{
  private String fDomain;
  private String fKey;
  
  public XMLErrorCode(String paramString1, String paramString2)
  {
    fDomain = paramString1;
    fKey = paramString2;
  }
  
  public void setValues(String paramString1, String paramString2)
  {
    fDomain = paramString1;
    fKey = paramString2;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof XMLErrorCode)) {
      return false;
    }
    XMLErrorCode localXMLErrorCode = (XMLErrorCode)paramObject;
    return (fDomain.equals(fDomain)) && (fKey.equals(fKey));
  }
  
  public int hashCode()
  {
    return fDomain.hashCode() + fKey.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\XMLErrorCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */