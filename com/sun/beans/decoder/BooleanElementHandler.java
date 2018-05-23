package com.sun.beans.decoder;

final class BooleanElementHandler
  extends StringElementHandler
{
  BooleanElementHandler() {}
  
  public Object getValue(String paramString)
  {
    if (Boolean.TRUE.toString().equalsIgnoreCase(paramString)) {
      return Boolean.TRUE;
    }
    if (Boolean.FALSE.toString().equalsIgnoreCase(paramString)) {
      return Boolean.FALSE;
    }
    throw new IllegalArgumentException("Unsupported boolean argument: " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\BooleanElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */