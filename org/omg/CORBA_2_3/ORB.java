package org.omg.CORBA_2_3;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.ValueFactory;

public abstract class ORB
  extends org.omg.CORBA.ORB
{
  public ORB() {}
  
  public ValueFactory register_value_factory(String paramString, ValueFactory paramValueFactory)
  {
    throw new NO_IMPLEMENT();
  }
  
  public void unregister_value_factory(String paramString)
  {
    throw new NO_IMPLEMENT();
  }
  
  public ValueFactory lookup_value_factory(String paramString)
  {
    throw new NO_IMPLEMENT();
  }
  
  public org.omg.CORBA.Object get_value_def(String paramString)
    throws BAD_PARAM
  {
    throw new NO_IMPLEMENT();
  }
  
  public void set_delegate(Object paramObject)
  {
    throw new NO_IMPLEMENT();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA_2_3\ORB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */