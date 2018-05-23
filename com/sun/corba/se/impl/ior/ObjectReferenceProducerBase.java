package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;

public abstract class ObjectReferenceProducerBase
{
  protected transient ORB orb;
  
  public abstract IORFactory getIORFactory();
  
  public abstract IORTemplateList getIORTemplateList();
  
  public ObjectReferenceProducerBase(ORB paramORB)
  {
    orb = paramORB;
  }
  
  public org.omg.CORBA.Object make_object(String paramString, byte[] paramArrayOfByte)
  {
    ObjectId localObjectId = IORFactories.makeObjectId(paramArrayOfByte);
    IOR localIOR = getIORFactory().makeIOR(orb, paramString, localObjectId);
    return ORBUtility.makeObjectReference(localIOR);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectReferenceProducerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */