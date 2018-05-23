package com.sun.corba.se.impl.util;

class StubEntry
{
  org.omg.CORBA.Object stub;
  boolean mostDerived;
  
  StubEntry(org.omg.CORBA.Object paramObject, boolean paramBoolean)
  {
    stub = paramObject;
    mostDerived = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\StubEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */