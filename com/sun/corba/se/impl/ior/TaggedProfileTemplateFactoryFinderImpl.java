package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

public class TaggedProfileTemplateFactoryFinderImpl
  extends IdentifiableFactoryFinderBase
{
  public TaggedProfileTemplateFactoryFinderImpl(ORB paramORB)
  {
    super(paramORB);
  }
  
  public Identifiable handleMissingFactory(int paramInt, InputStream paramInputStream)
  {
    throw wrapper.taggedProfileTemplateFactoryNotFound(new Integer(paramInt));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\TaggedProfileTemplateFactoryFinderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */