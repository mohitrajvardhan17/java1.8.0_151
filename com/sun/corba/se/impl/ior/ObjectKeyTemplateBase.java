package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class ObjectKeyTemplateBase
  implements ObjectKeyTemplate
{
  public static final String JIDL_ORB_ID = "";
  private static final String[] JIDL_OAID_STRINGS = { "TransientObjectAdapter" };
  public static final ObjectAdapterId JIDL_OAID = new ObjectAdapterIdArray(JIDL_OAID_STRINGS);
  private ORB orb;
  protected IORSystemException wrapper;
  private ORBVersion version;
  private int magic;
  private int scid;
  private int serverid;
  private String orbid;
  private ObjectAdapterId oaid;
  private byte[] adapterId;
  
  public byte[] getAdapterId()
  {
    return (byte[])adapterId.clone();
  }
  
  private byte[] computeAdapterId()
  {
    ByteBuffer localByteBuffer = new ByteBuffer();
    localByteBuffer.append(getServerId());
    localByteBuffer.append(orbid);
    localByteBuffer.append(oaid.getNumLevels());
    Iterator localIterator = oaid.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localByteBuffer.append(str);
    }
    localByteBuffer.trimToSize();
    return localByteBuffer.toArray();
  }
  
  public ObjectKeyTemplateBase(ORB paramORB, int paramInt1, int paramInt2, int paramInt3, String paramString, ObjectAdapterId paramObjectAdapterId)
  {
    orb = paramORB;
    wrapper = IORSystemException.get(paramORB, "oa.ior");
    magic = paramInt1;
    scid = paramInt2;
    serverid = paramInt3;
    orbid = paramString;
    oaid = paramObjectAdapterId;
    adapterId = computeAdapterId();
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ObjectKeyTemplateBase)) {
      return false;
    }
    ObjectKeyTemplateBase localObjectKeyTemplateBase = (ObjectKeyTemplateBase)paramObject;
    return (magic == magic) && (scid == scid) && (serverid == serverid) && (version.equals(version)) && (orbid.equals(orbid)) && (oaid.equals(oaid));
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 37 * i + magic;
    i = 37 * i + scid;
    i = 37 * i + serverid;
    i = 37 * i + version.hashCode();
    i = 37 * i + orbid.hashCode();
    i = 37 * i + oaid.hashCode();
    return i;
  }
  
  public int getSubcontractId()
  {
    return scid;
  }
  
  public int getServerId()
  {
    return serverid;
  }
  
  public String getORBId()
  {
    return orbid;
  }
  
  public ObjectAdapterId getObjectAdapterId()
  {
    return oaid;
  }
  
  public void write(ObjectId paramObjectId, OutputStream paramOutputStream)
  {
    writeTemplate(paramOutputStream);
    paramObjectId.write(paramOutputStream);
  }
  
  public void write(OutputStream paramOutputStream)
  {
    writeTemplate(paramOutputStream);
  }
  
  protected abstract void writeTemplate(OutputStream paramOutputStream);
  
  protected int getMagic()
  {
    return magic;
  }
  
  public void setORBVersion(ORBVersion paramORBVersion)
  {
    version = paramORBVersion;
  }
  
  public ORBVersion getORBVersion()
  {
    return version;
  }
  
  protected byte[] readObjectKey(InputStream paramInputStream)
  {
    int i = paramInputStream.read_long();
    byte[] arrayOfByte = new byte[i];
    paramInputStream.read_octet_array(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public CorbaServerRequestDispatcher getServerRequestDispatcher(ORB paramORB, ObjectId paramObjectId)
  {
    return paramORB.getRequestDispatcherRegistry().getServerRequestDispatcher(scid);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectKeyTemplateBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */