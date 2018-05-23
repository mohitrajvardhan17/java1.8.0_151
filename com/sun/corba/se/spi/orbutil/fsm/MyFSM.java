package com.sun.corba.se.spi.orbutil.fsm;

class MyFSM
  extends FSMImpl
{
  public int counter = 0;
  
  public MyFSM(StateEngine paramStateEngine)
  {
    super(paramStateEngine, FSMTest.STATE1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\MyFSM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */