package com.sun.corba.se.impl.encoding;

abstract interface RestorableInputStream
{
  public abstract Object createStreamMemento();
  
  public abstract void restoreInternalState(Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\RestorableInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */