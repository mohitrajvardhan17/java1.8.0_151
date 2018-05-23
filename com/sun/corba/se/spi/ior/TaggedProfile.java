package com.sun.corba.se.spi.ior;

public abstract interface TaggedProfile
  extends Identifiable, MakeImmutable
{
  public abstract TaggedProfileTemplate getTaggedProfileTemplate();
  
  public abstract ObjectId getObjectId();
  
  public abstract ObjectKeyTemplate getObjectKeyTemplate();
  
  public abstract ObjectKey getObjectKey();
  
  public abstract boolean isEquivalent(TaggedProfile paramTaggedProfile);
  
  public abstract org.omg.IOP.TaggedProfile getIOPProfile();
  
  public abstract boolean isLocal();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\TaggedProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */