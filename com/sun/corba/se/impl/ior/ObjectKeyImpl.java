package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public class ObjectKeyImpl
  implements ObjectKey
{
  private ObjectKeyTemplate oktemp;
  private ObjectId id;
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof ObjectKeyImpl)) {
      return false;
    }
    ObjectKeyImpl localObjectKeyImpl = (ObjectKeyImpl)paramObject;
    return (oktemp.equals(oktemp)) && (id.equals(id));
  }
  
  public int hashCode()
  {
    return oktemp.hashCode() ^ id.hashCode();
  }
  
  public ObjectKeyTemplate getTemplate()
  {
    return oktemp;
  }
  
  public ObjectId getId()
  {
    return id;
  }
  
  public ObjectKeyImpl(ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId)
  {
    oktemp = paramObjectKeyTemplate;
    id = paramObjectId;
  }
  
  public void write(OutputStream paramOutputStream)
  {
    oktemp.write(id, paramOutputStream);
  }
  
  public byte[] getBytes(org.omg.CORBA.ORB paramORB)
  {
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)paramORB);
    write(localEncapsOutputStream);
    return localEncapsOutputStream.toByteArray();
  }
  
  public CorbaServerRequestDispatcher getServerRequestDispatcher(com.sun.corba.se.spi.orb.ORB paramORB)
  {
    return oktemp.getServerRequestDispatcher(paramORB, id);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */