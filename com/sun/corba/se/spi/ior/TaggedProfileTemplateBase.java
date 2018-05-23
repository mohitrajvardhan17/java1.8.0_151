package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class TaggedProfileTemplateBase
  extends IdentifiableContainerBase
  implements TaggedProfileTemplate
{
  public TaggedProfileTemplateBase() {}
  
  public void write(OutputStream paramOutputStream)
  {
    EncapsulationUtility.writeEncapsulation(this, paramOutputStream);
  }
  
  public org.omg.IOP.TaggedComponent[] getIOPComponents(ORB paramORB, int paramInt)
  {
    int i = 0;
    Iterator localIterator = iteratorById(paramInt);
    while (localIterator.hasNext())
    {
      localIterator.next();
      i++;
    }
    org.omg.IOP.TaggedComponent[] arrayOfTaggedComponent = new org.omg.IOP.TaggedComponent[i];
    int j = 0;
    localIterator = iteratorById(paramInt);
    while (localIterator.hasNext())
    {
      TaggedComponent localTaggedComponent = (TaggedComponent)localIterator.next();
      arrayOfTaggedComponent[(j++)] = localTaggedComponent.getIOPComponent(paramORB);
    }
    return arrayOfTaggedComponent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\TaggedProfileTemplateBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */