package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.orbutil.ORBUtility;

final class Element
{
  Object servant = null;
  Object servantData = null;
  int index = -1;
  int counter = 0;
  boolean valid = false;
  
  Element(int paramInt, Object paramObject)
  {
    servant = paramObject;
    index = paramInt;
  }
  
  byte[] getKey(Object paramObject1, Object paramObject2)
  {
    servant = paramObject1;
    servantData = paramObject2;
    valid = true;
    return toBytes();
  }
  
  byte[] toBytes()
  {
    byte[] arrayOfByte = new byte[8];
    ORBUtility.intToBytes(index, arrayOfByte, 0);
    ORBUtility.intToBytes(counter, arrayOfByte, 4);
    return arrayOfByte;
  }
  
  void delete(Element paramElement)
  {
    if (!valid) {
      return;
    }
    counter += 1;
    servantData = null;
    valid = false;
    servant = paramElement;
  }
  
  public String toString()
  {
    return "Element[" + index + ", " + counter + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\toa\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */