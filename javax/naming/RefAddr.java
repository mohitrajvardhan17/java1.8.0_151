package javax.naming;

import java.io.Serializable;

public abstract class RefAddr
  implements Serializable
{
  protected String addrType;
  private static final long serialVersionUID = -1468165120479154358L;
  
  protected RefAddr(String paramString)
  {
    addrType = paramString;
  }
  
  public String getType()
  {
    return addrType;
  }
  
  public abstract Object getContent();
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof RefAddr)))
    {
      RefAddr localRefAddr = (RefAddr)paramObject;
      if (addrType.compareTo(addrType) == 0)
      {
        Object localObject1 = getContent();
        Object localObject2 = localRefAddr.getContent();
        if (localObject1 == localObject2) {
          return true;
        }
        if (localObject1 != null) {
          return localObject1.equals(localObject2);
        }
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return getContent() == null ? addrType.hashCode() : addrType.hashCode() + getContent().hashCode();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("Type: " + addrType + "\n");
    localStringBuffer.append("Content: " + getContent() + "\n");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\RefAddr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */