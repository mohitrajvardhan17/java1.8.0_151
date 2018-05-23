package com.sun.corba.se.impl.orb;

class SynchVariable
{
  public boolean _flag = false;
  
  SynchVariable() {}
  
  public void set()
  {
    _flag = true;
  }
  
  public boolean value()
  {
    return _flag;
  }
  
  public void reset()
  {
    _flag = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\SynchVariable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */