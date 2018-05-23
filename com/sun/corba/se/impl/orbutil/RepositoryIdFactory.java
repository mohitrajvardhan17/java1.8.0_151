package com.sun.corba.se.impl.orbutil;

public abstract class RepositoryIdFactory
{
  private static final RepIdDelegator currentDelegator = new RepIdDelegator();
  
  public RepositoryIdFactory() {}
  
  public static RepositoryIdStrings getRepIdStringsFactory()
  {
    return currentDelegator;
  }
  
  public static RepositoryIdUtility getRepIdUtility()
  {
    return currentDelegator;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\RepositoryIdFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */