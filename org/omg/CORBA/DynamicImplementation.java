package org.omg.CORBA;

import org.omg.CORBA.portable.ObjectImpl;

@Deprecated
public class DynamicImplementation
  extends ObjectImpl
{
  public DynamicImplementation() {}
  
  @Deprecated
  public void invoke(ServerRequest paramServerRequest)
  {
    throw new NO_IMPLEMENT();
  }
  
  public String[] _ids()
  {
    throw new NO_IMPLEMENT();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\DynamicImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */