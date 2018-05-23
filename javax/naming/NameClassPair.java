package javax.naming;

import java.io.Serializable;

public class NameClassPair
  implements Serializable
{
  private String name;
  private String className;
  private String fullName = null;
  private boolean isRel = true;
  private static final long serialVersionUID = 5620776610160863339L;
  
  public NameClassPair(String paramString1, String paramString2)
  {
    name = paramString1;
    className = paramString2;
  }
  
  public NameClassPair(String paramString1, String paramString2, boolean paramBoolean)
  {
    name = paramString1;
    className = paramString2;
    isRel = paramBoolean;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public void setClassName(String paramString)
  {
    className = paramString;
  }
  
  public boolean isRelative()
  {
    return isRel;
  }
  
  public void setRelative(boolean paramBoolean)
  {
    isRel = paramBoolean;
  }
  
  public String getNameInNamespace()
  {
    if (fullName == null) {
      throw new UnsupportedOperationException();
    }
    return fullName;
  }
  
  public void setNameInNamespace(String paramString)
  {
    fullName = paramString;
  }
  
  public String toString()
  {
    return (isRelative() ? "" : "(not relative)") + getName() + ": " + getClassName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\NameClassPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */