package jdk.internal.org.objectweb.asm;

public final class Handle
{
  final int tag;
  final String owner;
  final String name;
  final String desc;
  
  public Handle(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    tag = paramInt;
    owner = paramString1;
    name = paramString2;
    desc = paramString3;
  }
  
  public int getTag()
  {
    return tag;
  }
  
  public String getOwner()
  {
    return owner;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDesc()
  {
    return desc;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Handle)) {
      return false;
    }
    Handle localHandle = (Handle)paramObject;
    return (tag == tag) && (owner.equals(owner)) && (name.equals(name)) && (desc.equals(desc));
  }
  
  public int hashCode()
  {
    return tag + owner.hashCode() * name.hashCode() * desc.hashCode();
  }
  
  public String toString()
  {
    return owner + '.' + name + desc + " (" + tag + ')';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\Handle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */