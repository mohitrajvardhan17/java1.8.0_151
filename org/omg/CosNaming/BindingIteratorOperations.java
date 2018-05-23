package org.omg.CosNaming;

public abstract interface BindingIteratorOperations
{
  public abstract boolean next_one(BindingHolder paramBindingHolder);
  
  public abstract boolean next_n(int paramInt, BindingListHolder paramBindingListHolder);
  
  public abstract void destroy();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\BindingIteratorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */