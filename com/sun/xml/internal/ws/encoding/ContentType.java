package com.sun.xml.internal.ws.encoding;

import javax.xml.ws.WebServiceException;

public final class ContentType
{
  private String primaryType;
  private String subType;
  private ParameterList list;
  
  public ContentType(String paramString)
    throws WebServiceException
  {
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    HeaderTokenizer.Token localToken = localHeaderTokenizer.next();
    if (localToken.getType() != -1) {
      throw new WebServiceException();
    }
    primaryType = localToken.getValue();
    localToken = localHeaderTokenizer.next();
    if ((char)localToken.getType() != '/') {
      throw new WebServiceException();
    }
    localToken = localHeaderTokenizer.next();
    if (localToken.getType() != -1) {
      throw new WebServiceException();
    }
    subType = localToken.getValue();
    String str = localHeaderTokenizer.getRemainder();
    if (str != null) {
      list = new ParameterList(str);
    }
  }
  
  public String getPrimaryType()
  {
    return primaryType;
  }
  
  public String getSubType()
  {
    return subType;
  }
  
  public String getBaseType()
  {
    return primaryType + '/' + subType;
  }
  
  public String getParameter(String paramString)
  {
    if (list == null) {
      return null;
    }
    return list.get(paramString);
  }
  
  public ParameterList getParameterList()
  {
    return list;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\ContentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */