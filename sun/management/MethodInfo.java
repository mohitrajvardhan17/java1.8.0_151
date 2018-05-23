package sun.management;

import java.io.Serializable;

public class MethodInfo
  implements Serializable
{
  private String name;
  private long type;
  private int compileSize;
  private static final long serialVersionUID = 6992337162326171013L;
  
  MethodInfo(String paramString, long paramLong, int paramInt)
  {
    name = paramString;
    type = paramLong;
    compileSize = paramInt;
  }
  
  public String getName()
  {
    return name;
  }
  
  public long getType()
  {
    return type;
  }
  
  public int getCompileSize()
  {
    return compileSize;
  }
  
  public String toString()
  {
    return getName() + " type = " + getType() + " compileSize = " + getCompileSize();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\MethodInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */