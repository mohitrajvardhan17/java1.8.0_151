package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedProfileHelper;
import sun.corba.OutputStreamFactory;

public class GenericTaggedProfile
  extends GenericIdentifiable
  implements com.sun.corba.se.spi.ior.TaggedProfile
{
  private ORB orb;
  
  public GenericTaggedProfile(int paramInt, InputStream paramInputStream)
  {
    super(paramInt, paramInputStream);
    orb = ((ORB)paramInputStream.orb());
  }
  
  public GenericTaggedProfile(ORB paramORB, int paramInt, byte[] paramArrayOfByte)
  {
    super(paramInt, paramArrayOfByte);
    orb = paramORB;
  }
  
  public TaggedProfileTemplate getTaggedProfileTemplate()
  {
    return null;
  }
  
  public ObjectId getObjectId()
  {
    return null;
  }
  
  public ObjectKeyTemplate getObjectKeyTemplate()
  {
    return null;
  }
  
  public ObjectKey getObjectKey()
  {
    return null;
  }
  
  public boolean isEquivalent(com.sun.corba.se.spi.ior.TaggedProfile paramTaggedProfile)
  {
    return equals(paramTaggedProfile);
  }
  
  public void makeImmutable() {}
  
  public boolean isLocal()
  {
    return false;
  }
  
  public org.omg.IOP.TaggedProfile getIOPProfile()
  {
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream(orb);
    write(localEncapsOutputStream);
    InputStream localInputStream = (InputStream)localEncapsOutputStream.create_input_stream();
    return TaggedProfileHelper.read(localInputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\GenericTaggedProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */