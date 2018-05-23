package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import java.util.ArrayList;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class IORTemplateListImpl
  extends FreezableList
  implements IORTemplateList
{
  public Object set(int paramInt, Object paramObject)
  {
    if ((paramObject instanceof IORTemplate)) {
      return super.set(paramInt, paramObject);
    }
    if ((paramObject instanceof IORTemplateList))
    {
      Object localObject = remove(paramInt);
      add(paramInt, paramObject);
      return localObject;
    }
    throw new IllegalArgumentException();
  }
  
  public void add(int paramInt, Object paramObject)
  {
    if ((paramObject instanceof IORTemplate))
    {
      super.add(paramInt, paramObject);
    }
    else if ((paramObject instanceof IORTemplateList))
    {
      IORTemplateList localIORTemplateList = (IORTemplateList)paramObject;
      addAll(paramInt, localIORTemplateList);
    }
    else
    {
      throw new IllegalArgumentException();
    }
  }
  
  public IORTemplateListImpl()
  {
    super(new ArrayList());
  }
  
  public IORTemplateListImpl(InputStream paramInputStream)
  {
    this();
    int i = paramInputStream.read_long();
    for (int j = 0; j < i; j++)
    {
      IORTemplate localIORTemplate = IORFactories.makeIORTemplate(paramInputStream);
      add(localIORTemplate);
    }
    makeImmutable();
  }
  
  public void makeImmutable()
  {
    makeElementsImmutable();
    super.makeImmutable();
  }
  
  public void write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_long(size());
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      IORTemplate localIORTemplate = (IORTemplate)localIterator.next();
      localIORTemplate.write(paramOutputStream);
    }
  }
  
  public IOR makeIOR(ORB paramORB, String paramString, ObjectId paramObjectId)
  {
    return new IORImpl(paramORB, paramString, this, paramObjectId);
  }
  
  public boolean isEquivalent(IORFactory paramIORFactory)
  {
    if (!(paramIORFactory instanceof IORTemplateList)) {
      return false;
    }
    IORTemplateList localIORTemplateList = (IORTemplateList)paramIORFactory;
    Iterator localIterator1 = iterator();
    Iterator localIterator2 = localIORTemplateList.iterator();
    while ((localIterator1.hasNext()) && (localIterator2.hasNext()))
    {
      IORTemplate localIORTemplate1 = (IORTemplate)localIterator1.next();
      IORTemplate localIORTemplate2 = (IORTemplate)localIterator2.next();
      if (!localIORTemplate1.isEquivalent(localIORTemplate2)) {
        return false;
      }
    }
    return localIterator1.hasNext() == localIterator2.hasNext();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\IORTemplateListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */