package org.w3c.dom.ranges;

public class RangeException
  extends RuntimeException
{
  public short code;
  public static final short BAD_BOUNDARYPOINTS_ERR = 1;
  public static final short INVALID_NODE_TYPE_ERR = 2;
  
  public RangeException(short paramShort, String paramString)
  {
    super(paramString);
    code = paramShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\ranges\RangeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */