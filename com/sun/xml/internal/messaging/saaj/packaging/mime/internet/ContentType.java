package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

public final class ContentType
{
  private String primaryType;
  private String subType;
  private ParameterList list;
  
  public ContentType() {}
  
  public ContentType(String paramString1, String paramString2, ParameterList paramParameterList)
  {
    primaryType = paramString1;
    subType = paramString2;
    if (paramParameterList == null) {
      paramParameterList = new ParameterList();
    }
    list = paramParameterList;
  }
  
  public ContentType(String paramString)
    throws ParseException
  {
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    HeaderTokenizer.Token localToken = localHeaderTokenizer.next();
    if (localToken.getType() != -1) {
      throw new ParseException();
    }
    primaryType = localToken.getValue();
    localToken = localHeaderTokenizer.next();
    if ((char)localToken.getType() != '/') {
      throw new ParseException();
    }
    localToken = localHeaderTokenizer.next();
    if (localToken.getType() != -1) {
      throw new ParseException();
    }
    subType = localToken.getValue();
    String str = localHeaderTokenizer.getRemainder();
    if (str != null) {
      list = new ParameterList(str);
    }
  }
  
  public ContentType copy()
  {
    return new ContentType(primaryType, subType, list.copy());
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
  
  public void setPrimaryType(String paramString)
  {
    primaryType = paramString;
  }
  
  public void setSubType(String paramString)
  {
    subType = paramString;
  }
  
  public void setParameter(String paramString1, String paramString2)
  {
    if (list == null) {
      list = new ParameterList();
    }
    list.set(paramString1, paramString2);
  }
  
  public void setParameterList(ParameterList paramParameterList)
  {
    list = paramParameterList;
  }
  
  public String toString()
  {
    if ((primaryType == null) || (subType == null)) {
      return null;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(primaryType).append('/').append(subType);
    if (list != null) {
      localStringBuffer.append(list.toString());
    }
    return localStringBuffer.toString();
  }
  
  public boolean match(ContentType paramContentType)
  {
    if (!primaryType.equalsIgnoreCase(paramContentType.getPrimaryType())) {
      return false;
    }
    String str = paramContentType.getSubType();
    if ((subType.charAt(0) == '*') || (str.charAt(0) == '*')) {
      return true;
    }
    return subType.equalsIgnoreCase(str);
  }
  
  public boolean match(String paramString)
  {
    try
    {
      return match(new ContentType(paramString));
    }
    catch (ParseException localParseException) {}
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\ContentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */