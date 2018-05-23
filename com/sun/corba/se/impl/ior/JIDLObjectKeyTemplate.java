package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class JIDLObjectKeyTemplate
  extends NewObjectKeyTemplateBase
{
  public JIDLObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, InputStream paramInputStream)
  {
    super(paramORB, paramInt1, paramInt2, paramInputStream.read_long(), "", JIDL_OAID);
    setORBVersion(paramInputStream);
  }
  
  public JIDLObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2, InputStream paramInputStream, OctetSeqHolder paramOctetSeqHolder)
  {
    super(paramORB, paramInt1, paramInt2, paramInputStream.read_long(), "", JIDL_OAID);
    value = readObjectKey(paramInputStream);
    setORBVersion(paramInputStream);
  }
  
  public JIDLObjectKeyTemplate(ORB paramORB, int paramInt1, int paramInt2)
  {
    super(paramORB, -1347695872, paramInt1, paramInt2, "", JIDL_OAID);
    setORBVersion(ORBVersionFactory.getORBVersion());
  }
  
  protected void writeTemplate(OutputStream paramOutputStream)
  {
    paramOutputStream.write_long(getMagic());
    paramOutputStream.write_long(getSubcontractId());
    paramOutputStream.write_long(getServerId());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\JIDLObjectKeyTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */