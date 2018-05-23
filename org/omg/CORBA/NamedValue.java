package org.omg.CORBA;

public abstract class NamedValue
{
  public NamedValue() {}
  
  public abstract String name();
  
  public abstract Any value();
  
  public abstract int flags();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\NamedValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */