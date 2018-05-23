package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;

public final class TransientObjectManager
{
  private ORB orb;
  private int maxSize = 128;
  private Element[] elementArray;
  private Element freeList;
  
  void dprint(String paramString)
  {
    ORBUtility.dprint(this, paramString);
  }
  
  public TransientObjectManager(ORB paramORB)
  {
    orb = paramORB;
    elementArray = new Element[maxSize];
    elementArray[(maxSize - 1)] = new Element(maxSize - 1, null);
    for (int i = maxSize - 2; i >= 0; i--) {
      elementArray[i] = new Element(i, elementArray[(i + 1)]);
    }
    freeList = elementArray[0];
  }
  
  public synchronized byte[] storeServant(Object paramObject1, Object paramObject2)
  {
    if (freeList == null) {
      doubleSize();
    }
    Element localElement = freeList;
    freeList = ((Element)freeList.servant);
    byte[] arrayOfByte = localElement.getKey(paramObject1, paramObject2);
    if (orb.transientObjectManagerDebugFlag) {
      dprint("storeServant returns key for element " + localElement);
    }
    return arrayOfByte;
  }
  
  public synchronized Object lookupServant(byte[] paramArrayOfByte)
  {
    int i = ORBUtility.bytesToInt(paramArrayOfByte, 0);
    int j = ORBUtility.bytesToInt(paramArrayOfByte, 4);
    if (orb.transientObjectManagerDebugFlag) {
      dprint("lookupServant called with index=" + i + ", counter=" + j);
    }
    if ((elementArray[i].counter == j) && (elementArray[i].valid))
    {
      if (orb.transientObjectManagerDebugFlag) {
        dprint("\tcounter is valid");
      }
      return elementArray[i].servant;
    }
    if (orb.transientObjectManagerDebugFlag) {
      dprint("\tcounter is invalid");
    }
    return null;
  }
  
  public synchronized Object lookupServantData(byte[] paramArrayOfByte)
  {
    int i = ORBUtility.bytesToInt(paramArrayOfByte, 0);
    int j = ORBUtility.bytesToInt(paramArrayOfByte, 4);
    if (orb.transientObjectManagerDebugFlag) {
      dprint("lookupServantData called with index=" + i + ", counter=" + j);
    }
    if ((elementArray[i].counter == j) && (elementArray[i].valid))
    {
      if (orb.transientObjectManagerDebugFlag) {
        dprint("\tcounter is valid");
      }
      return elementArray[i].servantData;
    }
    if (orb.transientObjectManagerDebugFlag) {
      dprint("\tcounter is invalid");
    }
    return null;
  }
  
  public synchronized void deleteServant(byte[] paramArrayOfByte)
  {
    int i = ORBUtility.bytesToInt(paramArrayOfByte, 0);
    if (orb.transientObjectManagerDebugFlag) {
      dprint("deleting servant at index=" + i);
    }
    elementArray[i].delete(freeList);
    freeList = elementArray[i];
  }
  
  public synchronized byte[] getKey(Object paramObject)
  {
    for (int i = 0; i < maxSize; i++) {
      if ((elementArray[i].valid) && (elementArray[i].servant == paramObject)) {
        return elementArray[i].toBytes();
      }
    }
    return null;
  }
  
  private void doubleSize()
  {
    Element[] arrayOfElement = elementArray;
    int i = maxSize;
    maxSize *= 2;
    elementArray = new Element[maxSize];
    for (int j = 0; j < i; j++) {
      elementArray[j] = arrayOfElement[j];
    }
    elementArray[(maxSize - 1)] = new Element(maxSize - 1, null);
    for (j = maxSize - 2; j >= i; j--) {
      elementArray[j] = new Element(j, elementArray[(j + 1)]);
    }
    freeList = elementArray[i];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\toa\TransientObjectManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */