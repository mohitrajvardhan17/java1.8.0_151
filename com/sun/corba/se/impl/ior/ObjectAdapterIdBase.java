package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.OutputStream;

abstract class ObjectAdapterIdBase
  implements ObjectAdapterId
{
  ObjectAdapterIdBase() {}
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ObjectAdapterId)) {
      return false;
    }
    ObjectAdapterId localObjectAdapterId = (ObjectAdapterId)paramObject;
    Iterator localIterator1 = iterator();
    Iterator localIterator2 = localObjectAdapterId.iterator();
    while ((localIterator1.hasNext()) && (localIterator2.hasNext()))
    {
      String str1 = (String)localIterator1.next();
      String str2 = (String)localIterator2.next();
      if (!str1.equals(str2)) {
        return false;
      }
    }
    return localIterator1.hasNext() == localIterator2.hasNext();
  }
  
  public int hashCode()
  {
    int i = 17;
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      i = 37 * i + str.hashCode();
    }
    return i;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("ObjectAdapterID[");
    Iterator localIterator = iterator();
    int i = 1;
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (i != 0) {
        i = 0;
      } else {
        localStringBuffer.append("/");
      }
      localStringBuffer.append(str);
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
  
  public void write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_long(getNumLevels());
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramOutputStream.write_string(str);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectAdapterIdBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */