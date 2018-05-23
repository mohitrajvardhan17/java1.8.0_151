package com.sun.corba.se.spi.orbutil.fsm;

class TestInput
{
  Input value;
  String msg;
  
  TestInput(Input paramInput, String paramString)
  {
    value = paramInput;
    msg = paramString;
  }
  
  public String toString()
  {
    return "Input " + value + " : " + msg;
  }
  
  public Input getInput()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\TestInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */