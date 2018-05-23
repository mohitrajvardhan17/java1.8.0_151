package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedComponentHelper;
import sun.corba.OutputStreamFactory;

public class TaggedComponentFactoryFinderImpl
  extends IdentifiableFactoryFinderBase
  implements TaggedComponentFactoryFinder
{
  public TaggedComponentFactoryFinderImpl(com.sun.corba.se.spi.orb.ORB paramORB)
  {
    super(paramORB);
  }
  
  public Identifiable handleMissingFactory(int paramInt, InputStream paramInputStream)
  {
    return new GenericTaggedComponent(paramInt, paramInputStream);
  }
  
  public com.sun.corba.se.spi.ior.TaggedComponent create(org.omg.CORBA.ORB paramORB, org.omg.IOP.TaggedComponent paramTaggedComponent)
  {
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)paramORB);
    TaggedComponentHelper.write(localEncapsOutputStream, paramTaggedComponent);
    InputStream localInputStream = (InputStream)localEncapsOutputStream.create_input_stream();
    localInputStream.read_ulong();
    return (com.sun.corba.se.spi.ior.TaggedComponent)create(tag, localInputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\TaggedComponentFactoryFinderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */