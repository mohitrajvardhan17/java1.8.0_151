package org.omg.CORBA;

public final class UnknownUserException
  extends UserException
{
  public Any except;
  
  public UnknownUserException() {}
  
  public UnknownUserException(Any paramAny)
  {
    except = paramAny;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\UnknownUserException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */