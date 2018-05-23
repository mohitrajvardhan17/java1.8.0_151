package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.naming.cosnaming.BindingIteratorImpl;
import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.PortableServer.POA;

public class PersistentBindingIterator
  extends BindingIteratorImpl
{
  private POA biPOA;
  private int currentSize;
  private Hashtable theHashtable;
  private Enumeration theEnumeration;
  private ORB orb;
  
  public PersistentBindingIterator(ORB paramORB, Hashtable paramHashtable, POA paramPOA)
    throws Exception
  {
    super(paramORB);
    orb = paramORB;
    theHashtable = paramHashtable;
    theEnumeration = theHashtable.keys();
    currentSize = theHashtable.size();
    biPOA = paramPOA;
  }
  
  public final boolean NextOne(BindingHolder paramBindingHolder)
  {
    boolean bool = theEnumeration.hasMoreElements();
    if (bool)
    {
      InternalBindingKey localInternalBindingKey = (InternalBindingKey)theEnumeration.nextElement();
      InternalBindingValue localInternalBindingValue = (InternalBindingValue)theHashtable.get(localInternalBindingKey);
      NameComponent localNameComponent = new NameComponent(id, kind);
      NameComponent[] arrayOfNameComponent = new NameComponent[1];
      arrayOfNameComponent[0] = localNameComponent;
      BindingType localBindingType = theBindingType;
      value = new Binding(arrayOfNameComponent, localBindingType);
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
      byte[] arrayOfByte = biPOA.servant_to_id(this);
      if (arrayOfByte != null) {
        biPOA.deactivate_object(arrayOfByte);
      }
    }
    catch (Exception localException)
    {
      throw new INTERNAL("Exception in BindingIterator.Destroy " + localException);
    }
  }
  
  public final int RemainingElements()
  {
    return currentSize;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\PersistentBindingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */