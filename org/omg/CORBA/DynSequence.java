package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidSeq;

@Deprecated
public abstract interface DynSequence
  extends Object, DynAny
{
  public abstract int length();
  
  public abstract void length(int paramInt);
  
  public abstract Any[] get_elements();
  
  public abstract void set_elements(Any[] paramArrayOfAny)
    throws InvalidSeq;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\DynSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */