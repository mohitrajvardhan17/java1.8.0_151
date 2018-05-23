package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.ArrayList;
import java.util.Collection;

final class FinalArrayList<T>
  extends ArrayList<T>
{
  public FinalArrayList(int paramInt)
  {
    super(paramInt);
  }
  
  public FinalArrayList() {}
  
  public FinalArrayList(Collection<? extends T> paramCollection)
  {
    super(paramCollection);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\FinalArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */