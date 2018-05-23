package org.omg.PortableInterceptor.ORBInitInfoPackage;

import org.omg.CORBA.UserException;

public final class InvalidName
  extends UserException
{
  public InvalidName()
  {
    super(InvalidNameHelper.id());
  }
  
  public InvalidName(String paramString)
  {
    super(InvalidNameHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ORBInitInfoPackage\InvalidName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */