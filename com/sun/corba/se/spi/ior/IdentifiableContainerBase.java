package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.FreezableList;
import java.util.ArrayList;
import java.util.Iterator;

public class IdentifiableContainerBase
  extends FreezableList
{
  public IdentifiableContainerBase()
  {
    super(new ArrayList());
  }
  
  public Iterator iteratorById(final int paramInt)
  {
    new Iterator()
    {
      Iterator iter = iterator();
      Object current = advance();
      
      private Object advance()
      {
        while (iter.hasNext())
        {
          Identifiable localIdentifiable = (Identifiable)iter.next();
          if (localIdentifiable.getId() == paramInt) {
            return localIdentifiable;
          }
        }
        return null;
      }
      
      public boolean hasNext()
      {
        return current != null;
      }
      
      public Object next()
      {
        Object localObject = current;
        current = advance();
        return localObject;
      }
      
      public void remove()
      {
        iter.remove();
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\IdentifiableContainerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */