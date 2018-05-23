package com.sun.corba.se.impl.ior;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ObjectAdapterIdArray
  extends ObjectAdapterIdBase
{
  private final String[] objectAdapterId;
  
  public ObjectAdapterIdArray(String[] paramArrayOfString)
  {
    objectAdapterId = paramArrayOfString;
  }
  
  public ObjectAdapterIdArray(String paramString1, String paramString2)
  {
    objectAdapterId = new String[2];
    objectAdapterId[0] = paramString1;
    objectAdapterId[1] = paramString2;
  }
  
  public int getNumLevels()
  {
    return objectAdapterId.length;
  }
  
  public Iterator iterator()
  {
    return Arrays.asList(objectAdapterId).iterator();
  }
  
  public String[] getAdapterName()
  {
    return (String[])objectAdapterId.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectAdapterIdArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */