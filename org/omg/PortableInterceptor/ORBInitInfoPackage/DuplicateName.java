package org.omg.PortableInterceptor.ORBInitInfoPackage;

import org.omg.CORBA.UserException;

public final class DuplicateName
  extends UserException
{
  public String name = null;
  
  public DuplicateName()
  {
    super(DuplicateNameHelper.id());
  }
  
  public DuplicateName(String paramString)
  {
    super(DuplicateNameHelper.id());
    name = paramString;
  }
  
  public DuplicateName(String paramString1, String paramString2)
  {
    super(DuplicateNameHelper.id() + "  " + paramString1);
    name = paramString2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ORBInitInfoPackage\DuplicateName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */