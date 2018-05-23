package com.sun.org.apache.xerces.internal.xs;

public class XSException
  extends RuntimeException
{
  static final long serialVersionUID = 3111893084677917742L;
  public short code;
  public static final short NOT_SUPPORTED_ERR = 1;
  public static final short INDEX_SIZE_ERR = 2;
  
  public XSException(short paramShort, String paramString)
  {
    super(paramString);
    code = paramShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */