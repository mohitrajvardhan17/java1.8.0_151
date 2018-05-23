package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplateBase;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public class IIOPProfileTemplateImpl
  extends TaggedProfileTemplateBase
  implements IIOPProfileTemplate
{
  private ORB orb;
  private GIOPVersion giopVersion;
  private IIOPAddress primary;
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof IIOPProfileTemplateImpl)) {
      return false;
    }
    IIOPProfileTemplateImpl localIIOPProfileTemplateImpl = (IIOPProfileTemplateImpl)paramObject;
    return (super.equals(paramObject)) && (giopVersion.equals(giopVersion)) && (primary.equals(primary));
  }
  
  public int hashCode()
  {
    return super.hashCode() ^ giopVersion.hashCode() ^ primary.hashCode();
  }
  
  public TaggedProfile create(ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId)
  {
    return IIOPFactories.makeIIOPProfile(orb, paramObjectKeyTemplate, paramObjectId, this);
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return giopVersion;
  }
  
  public IIOPAddress getPrimaryAddress()
  {
    return primary;
  }
  
  public IIOPProfileTemplateImpl(ORB paramORB, GIOPVersion paramGIOPVersion, IIOPAddress paramIIOPAddress)
  {
    orb = paramORB;
    giopVersion = paramGIOPVersion;
    primary = paramIIOPAddress;
    if (giopVersion.getMinor() == 0) {
      makeImmutable();
    }
  }
  
  public IIOPProfileTemplateImpl(InputStream paramInputStream)
  {
    byte b1 = paramInputStream.read_octet();
    byte b2 = paramInputStream.read_octet();
    giopVersion = GIOPVersion.getInstance(b1, b2);
    primary = new IIOPAddressImpl(paramInputStream);
    orb = ((ORB)paramInputStream.orb());
    if (b2 > 0) {
      EncapsulationUtility.readIdentifiableSequence(this, orb.getTaggedComponentFactoryFinder(), paramInputStream);
    }
    makeImmutable();
  }
  
  public void write(ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId, OutputStream paramOutputStream)
  {
    giopVersion.write(paramOutputStream);
    primary.write(paramOutputStream);
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)paramOutputStream.orb(), ((CDROutputStream)paramOutputStream).isLittleEndian());
    paramObjectKeyTemplate.write(paramObjectId, localEncapsOutputStream);
    EncapsulationUtility.writeOutputStream(localEncapsOutputStream, paramOutputStream);
    if (giopVersion.getMinor() > 0) {
      EncapsulationUtility.writeIdentifiableSequence(this, paramOutputStream);
    }
  }
  
  public void writeContents(OutputStream paramOutputStream)
  {
    giopVersion.write(paramOutputStream);
    primary.write(paramOutputStream);
    if (giopVersion.getMinor() > 0) {
      EncapsulationUtility.writeIdentifiableSequence(this, paramOutputStream);
    }
  }
  
  public int getId()
  {
    return 0;
  }
  
  public boolean isEquivalent(TaggedProfileTemplate paramTaggedProfileTemplate)
  {
    if (!(paramTaggedProfileTemplate instanceof IIOPProfileTemplateImpl)) {
      return false;
    }
    IIOPProfileTemplateImpl localIIOPProfileTemplateImpl = (IIOPProfileTemplateImpl)paramTaggedProfileTemplate;
    return primary.equals(primary);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\IIOPProfileTemplateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */