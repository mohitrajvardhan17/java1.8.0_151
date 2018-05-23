package org.omg.CORBA;

public final class PolicyError
  extends UserException
{
  public short reason;
  
  public PolicyError() {}
  
  public PolicyError(short paramShort)
  {
    reason = paramShort;
  }
  
  public PolicyError(String paramString, short paramShort)
  {
    super(paramString);
    reason = paramShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\PolicyError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */