package org.omg.CORBA;

public abstract class Context
{
  public Context() {}
  
  public abstract String context_name();
  
  public abstract Context parent();
  
  public abstract Context create_child(String paramString);
  
  public abstract void set_one_value(String paramString, Any paramAny);
  
  public abstract void set_values(NVList paramNVList);
  
  public abstract void delete_values(String paramString);
  
  public abstract NVList get_values(String paramString1, int paramInt, String paramString2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\Context.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */