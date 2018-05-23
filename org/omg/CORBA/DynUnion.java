package org.omg.CORBA;

@Deprecated
public abstract interface DynUnion
  extends Object, DynAny
{
  public abstract boolean set_as_default();
  
  public abstract void set_as_default(boolean paramBoolean);
  
  public abstract DynAny discriminator();
  
  public abstract TCKind discriminator_kind();
  
  public abstract DynAny member();
  
  public abstract String member_name();
  
  public abstract void member_name(String paramString);
  
  public abstract TCKind member_kind();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\DynUnion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */