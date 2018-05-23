package sun.reflect;

import java.lang.reflect.Modifier;

public class FieldInfo
{
  private String name;
  private String signature;
  private int modifiers;
  private int slot;
  
  FieldInfo() {}
  
  public String name()
  {
    return name;
  }
  
  public String signature()
  {
    return signature;
  }
  
  public int modifiers()
  {
    return modifiers;
  }
  
  public int slot()
  {
    return slot;
  }
  
  public boolean isPublic()
  {
    return Modifier.isPublic(modifiers());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\FieldInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */