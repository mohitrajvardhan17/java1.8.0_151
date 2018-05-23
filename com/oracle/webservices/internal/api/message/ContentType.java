package com.oracle.webservices.internal.api.message;

import com.sun.xml.internal.ws.encoding.ContentTypeImpl;

public abstract interface ContentType
{
  public abstract String getContentType();
  
  public abstract String getSOAPActionHeader();
  
  public abstract String getAcceptHeader();
  
  public static class Builder
  {
    private String contentType;
    private String soapAction;
    private String accept;
    private String charset;
    
    public Builder() {}
    
    public Builder contentType(String paramString)
    {
      contentType = paramString;
      return this;
    }
    
    public Builder soapAction(String paramString)
    {
      soapAction = paramString;
      return this;
    }
    
    public Builder accept(String paramString)
    {
      accept = paramString;
      return this;
    }
    
    public Builder charset(String paramString)
    {
      charset = paramString;
      return this;
    }
    
    public ContentType build()
    {
      return new ContentTypeImpl(contentType, soapAction, accept, charset);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\message\ContentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */