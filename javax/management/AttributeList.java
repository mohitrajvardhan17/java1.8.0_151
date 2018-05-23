package javax.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AttributeList
  extends ArrayList<Object>
{
  private volatile transient boolean typeSafe;
  private volatile transient boolean tainted;
  private static final long serialVersionUID = -4077085769279709076L;
  
  public AttributeList() {}
  
  public AttributeList(int paramInt)
  {
    super(paramInt);
  }
  
  public AttributeList(AttributeList paramAttributeList)
  {
    super(paramAttributeList);
  }
  
  public AttributeList(List<Attribute> paramList)
  {
    if (paramList == null) {
      throw new IllegalArgumentException("Null parameter");
    }
    adding(paramList);
    super.addAll(paramList);
  }
  
  public List<Attribute> asList()
  {
    typeSafe = true;
    if (tainted) {
      adding(this);
    }
    return this;
  }
  
  public void add(Attribute paramAttribute)
  {
    super.add(paramAttribute);
  }
  
  public void add(int paramInt, Attribute paramAttribute)
  {
    try
    {
      super.add(paramInt, paramAttribute);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new RuntimeOperationsException(localIndexOutOfBoundsException, "The specified index is out of range");
    }
  }
  
  public void set(int paramInt, Attribute paramAttribute)
  {
    try
    {
      super.set(paramInt, paramAttribute);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new RuntimeOperationsException(localIndexOutOfBoundsException, "The specified index is out of range");
    }
  }
  
  public boolean addAll(AttributeList paramAttributeList)
  {
    return super.addAll(paramAttributeList);
  }
  
  public boolean addAll(int paramInt, AttributeList paramAttributeList)
  {
    try
    {
      return super.addAll(paramInt, paramAttributeList);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new RuntimeOperationsException(localIndexOutOfBoundsException, "The specified index is out of range");
    }
  }
  
  public boolean add(Object paramObject)
  {
    adding(paramObject);
    return super.add(paramObject);
  }
  
  public void add(int paramInt, Object paramObject)
  {
    adding(paramObject);
    super.add(paramInt, paramObject);
  }
  
  public boolean addAll(Collection<?> paramCollection)
  {
    adding(paramCollection);
    return super.addAll(paramCollection);
  }
  
  public boolean addAll(int paramInt, Collection<?> paramCollection)
  {
    adding(paramCollection);
    return super.addAll(paramInt, paramCollection);
  }
  
  public Object set(int paramInt, Object paramObject)
  {
    adding(paramObject);
    return super.set(paramInt, paramObject);
  }
  
  private void adding(Object paramObject)
  {
    if ((paramObject == null) || ((paramObject instanceof Attribute))) {
      return;
    }
    if (typeSafe) {
      throw new IllegalArgumentException("Not an Attribute: " + paramObject);
    }
    tainted = true;
  }
  
  private void adding(Collection<?> paramCollection)
  {
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      adding(localObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\AttributeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */