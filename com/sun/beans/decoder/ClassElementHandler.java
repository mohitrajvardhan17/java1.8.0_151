package com.sun.beans.decoder;

final class ClassElementHandler
  extends StringElementHandler
{
  ClassElementHandler() {}
  
  public Object getValue(String paramString)
  {
    return getOwner().findClass(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\ClassElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */