package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.ArrayList;
import java.util.List;

public class SlotTableStack
{
  private List tableContainer;
  private int currentIndex;
  private SlotTablePool tablePool;
  private ORB orb;
  private InterceptorsSystemException wrapper;
  
  SlotTableStack(ORB paramORB, SlotTable paramSlotTable)
  {
    orb = paramORB;
    wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
    currentIndex = 0;
    tableContainer = new ArrayList();
    tablePool = new SlotTablePool();
    tableContainer.add(currentIndex, paramSlotTable);
    currentIndex += 1;
  }
  
  void pushSlotTable()
  {
    SlotTable localSlotTable1 = tablePool.getSlotTable();
    if (localSlotTable1 == null)
    {
      SlotTable localSlotTable2 = peekSlotTable();
      localSlotTable1 = new SlotTable(orb, localSlotTable2.getSize());
    }
    if (currentIndex == tableContainer.size())
    {
      tableContainer.add(currentIndex, localSlotTable1);
    }
    else
    {
      if (currentIndex > tableContainer.size()) {
        throw wrapper.slotTableInvariant(new Integer(currentIndex), new Integer(tableContainer.size()));
      }
      tableContainer.set(currentIndex, localSlotTable1);
    }
    currentIndex += 1;
  }
  
  void popSlotTable()
  {
    if (currentIndex <= 1) {
      throw wrapper.cantPopOnlyPicurrent();
    }
    currentIndex -= 1;
    SlotTable localSlotTable = (SlotTable)tableContainer.get(currentIndex);
    tableContainer.set(currentIndex, null);
    localSlotTable.resetSlots();
    tablePool.putSlotTable(localSlotTable);
  }
  
  SlotTable peekSlotTable()
  {
    return (SlotTable)tableContainer.get(currentIndex - 1);
  }
  
  private class SlotTablePool
  {
    private SlotTable[] pool = new SlotTable[5];
    private final int HIGH_WATER_MARK = 5;
    private int currentIndex = 0;
    
    SlotTablePool() {}
    
    void putSlotTable(SlotTable paramSlotTable)
    {
      if (currentIndex >= 5) {
        return;
      }
      pool[currentIndex] = paramSlotTable;
      currentIndex += 1;
    }
    
    SlotTable getSlotTable()
    {
      if (currentIndex == 0) {
        return null;
      }
      currentIndex -= 1;
      return pool[currentIndex];
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\SlotTableStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */