package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import java.util.List;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TaggedComponent;

public abstract interface TaggedProfileTemplate
  extends List, Identifiable, WriteContents, MakeImmutable
{
  public abstract Iterator iteratorById(int paramInt);
  
  public abstract TaggedProfile create(ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId);
  
  public abstract void write(ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId, OutputStream paramOutputStream);
  
  public abstract boolean isEquivalent(TaggedProfileTemplate paramTaggedProfileTemplate);
  
  public abstract TaggedComponent[] getIOPComponents(ORB paramORB, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\TaggedProfileTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */