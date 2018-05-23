package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.copyobject.ObjectCopier;
import java.io.Serializable;
import java.rmi.Remote;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ORBStreamObjectCopierImpl
  implements ObjectCopier
{
  private ORB orb;
  
  public ORBStreamObjectCopierImpl(ORB paramORB)
  {
    orb = paramORB;
  }
  
  public Object copy(Object paramObject)
  {
    if ((paramObject instanceof Remote)) {
      return Utility.autoConnect(paramObject, orb, true);
    }
    OutputStream localOutputStream = (OutputStream)orb.create_output_stream();
    localOutputStream.write_value((Serializable)paramObject);
    InputStream localInputStream = (InputStream)localOutputStream.create_input_stream();
    return localInputStream.read_value();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\copyobject\ORBStreamObjectCopierImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */