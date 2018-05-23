package com.sun.corba.se.impl.naming.cosnaming;

import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.PortableServer.POA;

public class TransientBindingIterator
  extends BindingIteratorImpl
{
  private POA nsPOA;
  private int currentSize;
  private Hashtable theHashtable;
  private Enumeration theEnumeration;
  
  public TransientBindingIterator(ORB paramORB, Hashtable paramHashtable, POA paramPOA)
    throws Exception
  {
    super(paramORB);
    theHashtable = paramHashtable;
    theEnumeration = theHashtable.elements();
    currentSize = theHashtable.size();
    nsPOA = paramPOA;
  }
  
  public final boolean NextOne(BindingHolder paramBindingHolder)
  {
    boolean bool = theEnumeration.hasMoreElements();
    if (bool)
    {
      value = theEnumeration.nextElement()).theBinding;
      currentSize -= 1;
    }
    else
    {
      value = new Binding(new NameComponent[0], BindingType.nobject);
    }
    return bool;
  }
  
  public final void Destroy()
  {
    try
    {
      byte[] arrayOfByte = nsPOA.servant_to_id(this);
      if (arrayOfByte != null) {
        nsPOA.deactivate_object(arrayOfByte);
      }
    }
    catch (Exception localException)
    {
      NamingUtils.errprint("BindingIterator.Destroy():caught exception:");
      NamingUtils.printException(localException);
    }
  }
  
  public final int RemainingElements()
  {
    return currentSize;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\TransientBindingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */