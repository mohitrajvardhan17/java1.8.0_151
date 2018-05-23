package com.sun.corba.se.org.omg.CORBA;

import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;

public abstract class ORB
  extends org.omg.CORBA_2_3.ORB
{
  public ORB() {}
  
  public void register_initial_reference(String paramString, Object paramObject)
    throws InvalidName
  {
    throw new NO_IMPLEMENT();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\org\omg\CORBA\ORB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */