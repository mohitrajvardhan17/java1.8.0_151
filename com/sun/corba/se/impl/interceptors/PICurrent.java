package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.InvalidSlot;

public class PICurrent
  extends LocalObject
  implements Current
{
  private int slotCounter;
  private ORB myORB;
  private OMGSystemException wrapper;
  private boolean orbInitializing;
  private ThreadLocal threadLocalSlotTable = new ThreadLocal()
  {
    protected Object initialValue()
    {
      SlotTable localSlotTable = new SlotTable(myORB, slotCounter);
      return new SlotTableStack(myORB, localSlotTable);
    }
  };
  
  PICurrent(ORB paramORB)
  {
    myORB = paramORB;
    wrapper = OMGSystemException.get(paramORB, "rpc.protocol");
    orbInitializing = true;
    slotCounter = 0;
  }
  
  int allocateSlotId()
  {
    int i = slotCounter;
    slotCounter += 1;
    return i;
  }
  
  SlotTable getSlotTable()
  {
    SlotTable localSlotTable = ((SlotTableStack)threadLocalSlotTable.get()).peekSlotTable();
    return localSlotTable;
  }
  
  void pushSlotTable()
  {
    SlotTableStack localSlotTableStack = (SlotTableStack)threadLocalSlotTable.get();
    localSlotTableStack.pushSlotTable();
  }
  
  void popSlotTable()
  {
    SlotTableStack localSlotTableStack = (SlotTableStack)threadLocalSlotTable.get();
    localSlotTableStack.popSlotTable();
  }
  
  public void set_slot(int paramInt, Any paramAny)
    throws InvalidSlot
  {
    if (orbInitializing) {
      throw wrapper.invalidPiCall3();
    }
    getSlotTable().set_slot(paramInt, paramAny);
  }
  
  public Any get_slot(int paramInt)
    throws InvalidSlot
  {
    if (orbInitializing) {
      throw wrapper.invalidPiCall4();
    }
    return getSlotTable().get_slot(paramInt);
  }
  
  void resetSlotTable()
  {
    getSlotTable().resetSlots();
  }
  
  void setORBInitializing(boolean paramBoolean)
  {
    orbInitializing = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\PICurrent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */