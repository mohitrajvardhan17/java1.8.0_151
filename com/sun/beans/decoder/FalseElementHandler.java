package com.sun.beans.decoder;

final class FalseElementHandler
  extends NullElementHandler
{
  FalseElementHandler() {}
  
  public Object getValue()
  {
    return Boolean.FALSE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\FalseElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */