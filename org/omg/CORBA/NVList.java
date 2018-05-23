package org.omg.CORBA;

public abstract class NVList
{
  public NVList() {}
  
  public abstract int count();
  
  public abstract NamedValue add(int paramInt);
  
  public abstract NamedValue add_item(String paramString, int paramInt);
  
  public abstract NamedValue add_value(String paramString, Any paramAny, int paramInt);
  
  public abstract NamedValue item(int paramInt)
    throws Bounds;
  
  public abstract void remove(int paramInt)
    throws Bounds;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\NVList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */