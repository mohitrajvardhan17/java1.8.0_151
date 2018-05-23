package org.w3c.dom.xpath;

public class XPathException
  extends RuntimeException
{
  public short code;
  public static final short INVALID_EXPRESSION_ERR = 1;
  public static final short TYPE_ERR = 2;
  
  public XPathException(short paramShort, String paramString)
  {
    super(paramString);
    code = paramShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\xpath\XPathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */