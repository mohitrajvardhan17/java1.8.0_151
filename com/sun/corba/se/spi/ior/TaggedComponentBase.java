package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedComponentHelper;
import sun.corba.OutputStreamFactory;

public abstract class TaggedComponentBase
  extends IdentifiableBase
  implements TaggedComponent
{
  public TaggedComponentBase() {}
  
  public org.omg.IOP.TaggedComponent getIOPComponent(org.omg.CORBA.ORB paramORB)
  {
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)paramORB);
    write(localEncapsOutputStream);
    InputStream localInputStream = (InputStream)localEncapsOutputStream.create_input_stream();
    return TaggedComponentHelper.read(localInputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\TaggedComponentBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */