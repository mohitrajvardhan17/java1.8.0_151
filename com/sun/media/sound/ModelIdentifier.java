package com.sun.media.sound;

public final class ModelIdentifier
{
  private String object = null;
  private String variable = null;
  private int instance = 0;
  
  public ModelIdentifier(String paramString)
  {
    object = paramString;
  }
  
  public ModelIdentifier(String paramString, int paramInt)
  {
    object = paramString;
    instance = paramInt;
  }
  
  public ModelIdentifier(String paramString1, String paramString2)
  {
    object = paramString1;
    variable = paramString2;
  }
  
  public ModelIdentifier(String paramString1, String paramString2, int paramInt)
  {
    object = paramString1;
    variable = paramString2;
    instance = paramInt;
  }
  
  public int getInstance()
  {
    return instance;
  }
  
  public void setInstance(int paramInt)
  {
    instance = paramInt;
  }
  
  public String getObject()
  {
    return object;
  }
  
  public void setObject(String paramString)
  {
    object = paramString;
  }
  
  public String getVariable()
  {
    return variable;
  }
  
  public void setVariable(String paramString)
  {
    variable = paramString;
  }
  
  public int hashCode()
  {
    int i = instance;
    if (object != null) {
      i |= object.hashCode();
    }
    if (variable != null) {
      i |= variable.hashCode();
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ModelIdentifier)) {
      return false;
    }
    ModelIdentifier localModelIdentifier = (ModelIdentifier)paramObject;
    if ((object == null ? 1 : 0) != (object == null ? 1 : 0)) {
      return false;
    }
    if ((variable == null ? 1 : 0) != (variable == null ? 1 : 0)) {
      return false;
    }
    if (localModelIdentifier.getInstance() != getInstance()) {
      return false;
    }
    if ((object != null) && (!object.equals(object))) {
      return false;
    }
    return (variable == null) || (variable.equals(variable));
  }
  
  public String toString()
  {
    if (variable == null) {
      return object + "[" + instance + "]";
    }
    return object + "[" + instance + "]." + variable;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelIdentifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */