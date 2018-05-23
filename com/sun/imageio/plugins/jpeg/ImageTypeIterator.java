package com.sun.imageio.plugins.jpeg;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.imageio.ImageTypeSpecifier;

class ImageTypeIterator
  implements Iterator<ImageTypeSpecifier>
{
  private Iterator<ImageTypeProducer> producers;
  private ImageTypeSpecifier theNext = null;
  
  public ImageTypeIterator(Iterator<ImageTypeProducer> paramIterator)
  {
    producers = paramIterator;
  }
  
  public boolean hasNext()
  {
    if (theNext != null) {
      return true;
    }
    if (!producers.hasNext()) {
      return false;
    }
    do
    {
      theNext = ((ImageTypeProducer)producers.next()).getType();
    } while ((theNext == null) && (producers.hasNext()));
    return theNext != null;
  }
  
  public ImageTypeSpecifier next()
  {
    if ((theNext != null) || (hasNext()))
    {
      ImageTypeSpecifier localImageTypeSpecifier = theNext;
      theNext = null;
      return localImageTypeSpecifier;
    }
    throw new NoSuchElementException();
  }
  
  public void remove()
  {
    producers.remove();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\ImageTypeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */