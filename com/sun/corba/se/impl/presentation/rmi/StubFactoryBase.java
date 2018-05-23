package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;

public abstract class StubFactoryBase
  implements PresentationManager.StubFactory
{
  private String[] typeIds = null;
  protected final PresentationManager.ClassData classData;
  
  protected StubFactoryBase(PresentationManager.ClassData paramClassData)
  {
    classData = paramClassData;
  }
  
  public synchronized String[] getTypeIds()
  {
    if (typeIds == null) {
      if (classData == null)
      {
        org.omg.CORBA.Object localObject = makeStub();
        typeIds = StubAdapter.getTypeIds(localObject);
      }
      else
      {
        typeIds = classData.getTypeIds();
      }
    }
    return typeIds;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */