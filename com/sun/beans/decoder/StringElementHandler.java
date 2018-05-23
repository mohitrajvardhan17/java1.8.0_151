package com.sun.beans.decoder;

public class StringElementHandler
  extends ElementHandler
{
  private StringBuilder sb = new StringBuilder();
  private ValueObject value = ValueObjectImpl.NULL;
  
  public StringElementHandler() {}
  
  public final void addCharacter(char paramChar)
  {
    if (sb == null) {
      throw new IllegalStateException("Could not add chararcter to evaluated string element");
    }
    sb.append(paramChar);
  }
  
  protected final void addArgument(Object paramObject)
  {
    if (sb == null) {
      throw new IllegalStateException("Could not add argument to evaluated string element");
    }
    sb.append(paramObject);
  }
  
  protected final ValueObject getValueObject()
  {
    if (sb != null) {
      try
      {
        value = ValueObjectImpl.create(getValue(sb.toString()));
      }
      catch (RuntimeException localRuntimeException)
      {
        getOwner().handleException(localRuntimeException);
      }
      finally
      {
        sb = null;
      }
    }
    return value;
  }
  
  protected Object getValue(String paramString)
  {
    return paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\StringElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */