package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorPOA;
import org.omg.CosNaming.BindingListHolder;

public abstract class BindingIteratorImpl
  extends BindingIteratorPOA
{
  protected ORB orb;
  
  public BindingIteratorImpl(ORB paramORB)
    throws Exception
  {
    orb = paramORB;
  }
  
  public synchronized boolean next_one(BindingHolder paramBindingHolder)
  {
    return NextOne(paramBindingHolder);
  }
  
  public synchronized boolean next_n(int paramInt, BindingListHolder paramBindingListHolder)
  {
    if (paramInt == 0) {
      throw new BAD_PARAM(" 'how_many' parameter is set to 0 which is invalid");
    }
    return list(paramInt, paramBindingListHolder);
  }
  
  public boolean list(int paramInt, BindingListHolder paramBindingListHolder)
  {
    int i = Math.min(RemainingElements(), paramInt);
    Binding[] arrayOfBinding = new Binding[i];
    BindingHolder localBindingHolder = new BindingHolder();
    for (int j = 0; (j < i) && (NextOne(localBindingHolder) == true); j++) {
      arrayOfBinding[j] = value;
    }
    if (j == 0)
    {
      value = new Binding[0];
      return false;
    }
    value = arrayOfBinding;
    return true;
  }
  
  public synchronized void destroy()
  {
    Destroy();
  }
  
  protected abstract boolean NextOne(BindingHolder paramBindingHolder);
  
  protected abstract void Destroy();
  
  protected abstract int RemainingElements();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\BindingIteratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */