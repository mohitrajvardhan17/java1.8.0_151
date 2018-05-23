package org.omg.CORBA;

public abstract class ContextList
{
  public ContextList() {}
  
  public abstract int count();
  
  public abstract void add(String paramString);
  
  public abstract String item(int paramInt)
    throws Bounds;
  
  public abstract void remove(int paramInt)
    throws Bounds;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ContextList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */