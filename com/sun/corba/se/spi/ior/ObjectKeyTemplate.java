package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract interface ObjectKeyTemplate
  extends Writeable
{
  public abstract ORBVersion getORBVersion();
  
  public abstract int getSubcontractId();
  
  public abstract int getServerId();
  
  public abstract String getORBId();
  
  public abstract ObjectAdapterId getObjectAdapterId();
  
  public abstract byte[] getAdapterId();
  
  public abstract void write(ObjectId paramObjectId, OutputStream paramOutputStream);
  
  public abstract CorbaServerRequestDispatcher getServerRequestDispatcher(ORB paramORB, ObjectId paramObjectId);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\ObjectKeyTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */