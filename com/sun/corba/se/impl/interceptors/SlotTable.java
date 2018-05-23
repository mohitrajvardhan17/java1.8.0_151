package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.PortableInterceptor.InvalidSlot;

public class SlotTable
{
  private Any[] theSlotData;
  private ORB orb;
  private boolean dirtyFlag = false;
  
  SlotTable(ORB paramORB, int paramInt)
  {
    orb = paramORB;
    theSlotData = new Any[paramInt];
  }
  
  public void set_slot(int paramInt, Any paramAny)
    throws InvalidSlot
  {
    if (paramInt >= theSlotData.length) {
      throw new InvalidSlot();
    }
    dirtyFlag = true;
    theSlotData[paramInt] = paramAny;
  }
  
  public Any get_slot(int paramInt)
    throws InvalidSlot
  {
    if (paramInt >= theSlotData.length) {
      throw new InvalidSlot();
    }
    if (theSlotData[paramInt] == null) {
      theSlotData[paramInt] = new AnyImpl(orb);
    }
    return theSlotData[paramInt];
  }
  
  void resetSlots()
  {
    if (dirtyFlag == true) {
      for (int i = 0; i < theSlotData.length; i++) {
        theSlotData[i] = null;
      }
    }
  }
  
  int getSize()
  {
    return theSlotData.length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\SlotTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */