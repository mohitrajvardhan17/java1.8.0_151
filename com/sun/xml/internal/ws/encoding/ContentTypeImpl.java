package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public final class ContentTypeImpl
  implements com.sun.xml.internal.ws.api.pipe.ContentType
{
  @NotNull
  private final String contentType;
  @NotNull
  private final String soapAction;
  private String accept;
  @Nullable
  private final String charset;
  private String boundary;
  private String boundaryParameter;
  private String rootId;
  private ContentType internalContentType;
  
  public ContentTypeImpl(String paramString)
  {
    this(paramString, null, null);
  }
  
  public ContentTypeImpl(String paramString1, @Nullable String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public ContentTypeImpl(String paramString1, @Nullable String paramString2, @Nullable String paramString3)
  {
    this(paramString1, paramString2, paramString3, null);
  }
  
  public ContentTypeImpl(String paramString1, @Nullable String paramString2, @Nullable String paramString3, String paramString4)
  {
    contentType = paramString1;
    accept = paramString3;
    soapAction = getQuotedSOAPAction(paramString2);
    if (paramString4 == null)
    {
      String str = null;
      try
      {
        internalContentType = new ContentType(paramString1);
        str = internalContentType.getParameter("charset");
      }
      catch (Exception localException) {}
      charset = str;
    }
    else
    {
      charset = paramString4;
    }
  }
  
  @Nullable
  public String getCharSet()
  {
    return charset;
  }
  
  private String getQuotedSOAPAction(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return "\"\"";
    }
    if ((paramString.charAt(0) != '"') && (paramString.charAt(paramString.length() - 1) != '"')) {
      return "\"" + paramString + "\"";
    }
    return paramString;
  }
  
  public String getContentType()
  {
    return contentType;
  }
  
  public String getSOAPActionHeader()
  {
    return soapAction;
  }
  
  public String getAcceptHeader()
  {
    return accept;
  }
  
  public void setAcceptHeader(String paramString)
  {
    accept = paramString;
  }
  
  public String getBoundary()
  {
    if (boundary == null)
    {
      if (internalContentType == null) {
        internalContentType = new ContentType(contentType);
      }
      boundary = internalContentType.getParameter("boundary");
    }
    return boundary;
  }
  
  public void setBoundary(String paramString)
  {
    boundary = paramString;
  }
  
  public String getBoundaryParameter()
  {
    return boundaryParameter;
  }
  
  public void setBoundaryParameter(String paramString)
  {
    boundaryParameter = paramString;
  }
  
  public String getRootId()
  {
    if (rootId == null)
    {
      if (internalContentType == null) {
        internalContentType = new ContentType(contentType);
      }
      rootId = internalContentType.getParameter("start");
    }
    return rootId;
  }
  
  public void setRootId(String paramString)
  {
    rootId = paramString;
  }
  
  public static class Builder
  {
    public String contentType;
    public String soapAction;
    public String accept;
    public String charset;
    
    public Builder() {}
    
    public ContentTypeImpl build()
    {
      return new ContentTypeImpl(contentType, soapAction, accept, charset);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\ContentTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */