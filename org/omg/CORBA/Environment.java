package org.omg.CORBA;

public abstract class Environment
{
  public Environment() {}
  
  public abstract Exception exception();
  
  public abstract void exception(Exception paramException);
  
  public abstract void clear();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\Environment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */