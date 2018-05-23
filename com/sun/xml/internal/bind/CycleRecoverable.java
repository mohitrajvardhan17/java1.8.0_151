package com.sun.xml.internal.bind;

import javax.xml.bind.Marshaller;

public abstract interface CycleRecoverable
{
  public abstract Object onCycleDetected(Context paramContext);
  
  public static abstract interface Context
  {
    public abstract Marshaller getMarshaller();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\CycleRecoverable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */