package org.omg.CORBA;

public abstract interface PolicyOperations
{
  public abstract int policy_type();
  
  public abstract Policy copy();
  
  public abstract void destroy();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\PolicyOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */