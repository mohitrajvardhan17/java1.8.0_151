package com.sun.corba.se.impl.corba;

import org.omg.CORBA_2_3.portable.ObjectImpl;

public class CORBAObjectImpl
  extends ObjectImpl
{
  public CORBAObjectImpl() {}
  
  public String[] _ids()
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = "IDL:omg.org/CORBA/Object:1.0";
    return arrayOfString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\CORBAObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */