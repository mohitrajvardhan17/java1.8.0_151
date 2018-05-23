package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

public class ContentDisposition
{
  private String disposition;
  private ParameterList list;
  
  public ContentDisposition() {}
  
  public ContentDisposition(String paramString, ParameterList paramParameterList)
  {
    disposition = paramString;
    list = paramParameterList;
  }
  
  public ContentDisposition(String paramString)
    throws ParseException
  {
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    HeaderTokenizer.Token localToken = localHeaderTokenizer.next();
    if (localToken.getType() != -1) {
      throw new ParseException();
    }
    disposition = localToken.getValue();
    String str = localHeaderTokenizer.getRemainder();
    if (str != null) {
      list = new ParameterList(str);
    }
  }
  
  public String getDisposition()
  {
    return disposition;
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
  
  public void setDisposition(String paramString)
  {
    disposition = paramString;
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
    if (disposition == null) {
      return null;
    }
    if (list == null) {
      return disposition;
    }
    StringBuffer localStringBuffer = new StringBuffer(disposition);
    localStringBuffer.append(list.toString(localStringBuffer.length() + 21));
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\ContentDisposition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */