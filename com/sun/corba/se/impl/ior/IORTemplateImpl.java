package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IdentifiableContainerBase;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class IORTemplateImpl
  extends IdentifiableContainerBase
  implements IORTemplate
{
  private ObjectKeyTemplate oktemp;
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof IORTemplateImpl)) {
      return false;
    }
    IORTemplateImpl localIORTemplateImpl = (IORTemplateImpl)paramObject;
    return (super.equals(paramObject)) && (oktemp.equals(localIORTemplateImpl.getObjectKeyTemplate()));
  }
  
  public int hashCode()
  {
    return super.hashCode() ^ oktemp.hashCode();
  }
  
  public ObjectKeyTemplate getObjectKeyTemplate()
  {
    return oktemp;
  }
  
  public IORTemplateImpl(ObjectKeyTemplate paramObjectKeyTemplate)
  {
    oktemp = paramObjectKeyTemplate;
  }
  
  public IOR makeIOR(ORB paramORB, String paramString, ObjectId paramObjectId)
  {
    return new IORImpl(paramORB, paramString, this, paramObjectId);
  }
  
  public boolean isEquivalent(IORFactory paramIORFactory)
  {
    if (!(paramIORFactory instanceof IORTemplate)) {
      return false;
    }
    IORTemplate localIORTemplate = (IORTemplate)paramIORFactory;
    Iterator localIterator1 = iterator();
    Iterator localIterator2 = localIORTemplate.iterator();
    while ((localIterator1.hasNext()) && (localIterator2.hasNext()))
    {
      TaggedProfileTemplate localTaggedProfileTemplate1 = (TaggedProfileTemplate)localIterator1.next();
      TaggedProfileTemplate localTaggedProfileTemplate2 = (TaggedProfileTemplate)localIterator2.next();
      if (!localTaggedProfileTemplate1.isEquivalent(localTaggedProfileTemplate2)) {
        return false;
      }
    }
    return (localIterator1.hasNext() == localIterator2.hasNext()) && (getObjectKeyTemplate().equals(localIORTemplate.getObjectKeyTemplate()));
  }
  
  public void makeImmutable()
  {
    makeElementsImmutable();
    super.makeImmutable();
  }
  
  public void write(OutputStream paramOutputStream)
  {
    oktemp.write(paramOutputStream);
    EncapsulationUtility.writeIdentifiableSequence(this, paramOutputStream);
  }
  
  public IORTemplateImpl(InputStream paramInputStream)
  {
    ORB localORB = (ORB)paramInputStream.orb();
    IdentifiableFactoryFinder localIdentifiableFactoryFinder = localORB.getTaggedProfileTemplateFactoryFinder();
    oktemp = localORB.getObjectKeyFactory().createTemplate(paramInputStream);
    EncapsulationUtility.readIdentifiableSequence(this, localIdentifiableFactoryFinder, paramInputStream);
    makeImmutable();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\IORTemplateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */