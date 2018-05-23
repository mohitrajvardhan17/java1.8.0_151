package com.sun.xml.internal.ws.api.model;

public final class ParameterBinding
{
  public static final ParameterBinding BODY = new ParameterBinding(Kind.BODY, null);
  public static final ParameterBinding HEADER = new ParameterBinding(Kind.HEADER, null);
  public static final ParameterBinding UNBOUND = new ParameterBinding(Kind.UNBOUND, null);
  public final Kind kind;
  private String mimeType;
  
  public static ParameterBinding createAttachment(String paramString)
  {
    return new ParameterBinding(Kind.ATTACHMENT, paramString);
  }
  
  private ParameterBinding(Kind paramKind, String paramString)
  {
    kind = paramKind;
    mimeType = paramString;
  }
  
  public String toString()
  {
    return kind.toString();
  }
  
  public String getMimeType()
  {
    if (!isAttachment()) {
      throw new IllegalStateException();
    }
    return mimeType;
  }
  
  public boolean isBody()
  {
    return this == BODY;
  }
  
  public boolean isHeader()
  {
    return this == HEADER;
  }
  
  public boolean isUnbound()
  {
    return this == UNBOUND;
  }
  
  public boolean isAttachment()
  {
    return kind == Kind.ATTACHMENT;
  }
  
  public static enum Kind
  {
    BODY,  HEADER,  UNBOUND,  ATTACHMENT;
    
    private Kind() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\ParameterBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */