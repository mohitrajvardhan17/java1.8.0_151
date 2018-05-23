package org.w3c.dom.ls;

public class LSException
  extends RuntimeException
{
  public short code;
  public static final short PARSE_ERR = 81;
  public static final short SERIALIZE_ERR = 82;
  
  public LSException(short paramShort, String paramString)
  {
    super(paramString);
    code = paramShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\ls\LSException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */