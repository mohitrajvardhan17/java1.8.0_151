package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class WireObjectKeyTemplate
  implements ObjectKeyTemplate
{
  private ORB orb;
  private IORSystemException wrapper;
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    return paramObject instanceof WireObjectKeyTemplate;
  }
  
  public int hashCode()
  {
    return 53;
  }
  
  private byte[] getId(InputStream paramInputStream)
  {
    CDRInputStream localCDRInputStream = (CDRInputStream)paramInputStream;
    int i = localCDRInputStream.getBufferLength();
    byte[] arrayOfByte = new byte[i];
    localCDRInputStream.read_octet_array(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public WireObjectKeyTemplate(ORB paramORB)
  {
    initORB(paramORB);
  }
  
  public WireObjectKeyTemplate(InputStream paramInputStream, OctetSeqHolder paramOctetSeqHolder)
  {
    value = getId(paramInputStream);
    initORB((ORB)paramInputStream.orb());
  }
  
  private void initORB(ORB paramORB)
  {
    orb = paramORB;
    wrapper = IORSystemException.get(paramORB, "oa.ior");
  }
  
  public void write(ObjectId paramObjectId, OutputStream paramOutputStream)
  {
    byte[] arrayOfByte = paramObjectId.getId();
    paramOutputStream.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public void write(OutputStream paramOutputStream) {}
  
  public int getSubcontractId()
  {
    return 2;
  }
  
  public int getServerId()
  {
    return -1;
  }
  
  public String getORBId()
  {
    throw wrapper.orbIdNotAvailable();
  }
  
  public ObjectAdapterId getObjectAdapterId()
  {
    throw wrapper.objectAdapterIdNotAvailable();
  }
  
  public byte[] getAdapterId()
  {
    throw wrapper.adapterIdNotAvailable();
  }
  
  public ORBVersion getORBVersion()
  {
    return ORBVersionFactory.getFOREIGN();
  }
  
  public CorbaServerRequestDispatcher getServerRequestDispatcher(ORB paramORB, ObjectId paramObjectId)
  {
    byte[] arrayOfByte = paramObjectId.getId();
    String str = new String(arrayOfByte);
    return paramORB.getRequestDispatcherRegistry().getServerRequestDispatcher(str);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\WireObjectKeyTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */