package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class InvalidPolicy
  extends UserException
{
  public short index = 0;
  
  public InvalidPolicy()
  {
    super(InvalidPolicyHelper.id());
  }
  
  public InvalidPolicy(short paramShort)
  {
    super(InvalidPolicyHelper.id());
    index = paramShort;
  }
  
  public InvalidPolicy(String paramString, short paramShort)
  {
    super(InvalidPolicyHelper.id() + "  " + paramString);
    index = paramShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAPackage\InvalidPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */