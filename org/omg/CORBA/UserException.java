package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public abstract class UserException
  extends Exception
  implements IDLEntity
{
  protected UserException() {}
  
  protected UserException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\UserException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */