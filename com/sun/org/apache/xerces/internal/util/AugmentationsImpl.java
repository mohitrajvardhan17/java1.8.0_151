package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class AugmentationsImpl
  implements Augmentations
{
  private AugmentationsItemsContainer fAugmentationsContainer = new SmallContainer();
  
  public AugmentationsImpl() {}
  
  public Object putItem(String paramString, Object paramObject)
  {
    Object localObject = fAugmentationsContainer.putItem(paramString, paramObject);
    if ((localObject == null) && (fAugmentationsContainer.isFull())) {
      fAugmentationsContainer = fAugmentationsContainer.expand();
    }
    return localObject;
  }
  
  public Object getItem(String paramString)
  {
    return fAugmentationsContainer.getItem(paramString);
  }
  
  public Object removeItem(String paramString)
  {
    return fAugmentationsContainer.removeItem(paramString);
  }
  
  public Enumeration keys()
  {
    return fAugmentationsContainer.keys();
  }
  
  public void removeAllItems()
  {
    fAugmentationsContainer.clear();
  }
  
  public String toString()
  {
    return fAugmentationsContainer.toString();
  }
  
  abstract class AugmentationsItemsContainer
  {
    AugmentationsItemsContainer() {}
    
    public abstract Object putItem(Object paramObject1, Object paramObject2);
    
    public abstract Object getItem(Object paramObject);
    
    public abstract Object removeItem(Object paramObject);
    
    public abstract Enumeration keys();
    
    public abstract void clear();
    
    public abstract boolean isFull();
    
    public abstract AugmentationsItemsContainer expand();
  }
  
  class LargeContainer
    extends AugmentationsImpl.AugmentationsItemsContainer
  {
    final Map<Object, Object> fAugmentations = new HashMap();
    
    LargeContainer()
    {
      super();
    }
    
    public Object getItem(Object paramObject)
    {
      return fAugmentations.get(paramObject);
    }
    
    public Object putItem(Object paramObject1, Object paramObject2)
    {
      return fAugmentations.put(paramObject1, paramObject2);
    }
    
    public Object removeItem(Object paramObject)
    {
      return fAugmentations.remove(paramObject);
    }
    
    public Enumeration keys()
    {
      return Collections.enumeration(fAugmentations.keySet());
    }
    
    public void clear()
    {
      fAugmentations.clear();
    }
    
    public boolean isFull()
    {
      return false;
    }
    
    public AugmentationsImpl.AugmentationsItemsContainer expand()
    {
      return this;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("LargeContainer");
      Iterator localIterator = fAugmentations.keySet().iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        localStringBuilder.append("\nkey == ");
        localStringBuilder.append(localObject);
        localStringBuilder.append("; value == ");
        localStringBuilder.append(fAugmentations.get(localObject));
      }
      return localStringBuilder.toString();
    }
  }
  
  class SmallContainer
    extends AugmentationsImpl.AugmentationsItemsContainer
  {
    static final int SIZE_LIMIT = 10;
    final Object[] fAugmentations = new Object[20];
    int fNumEntries = 0;
    
    SmallContainer()
    {
      super();
    }
    
    public Enumeration keys()
    {
      return new SmallContainerKeyEnumeration();
    }
    
    public Object getItem(Object paramObject)
    {
      int i = 0;
      while (i < fNumEntries * 2)
      {
        if (fAugmentations[i].equals(paramObject)) {
          return fAugmentations[(i + 1)];
        }
        i += 2;
      }
      return null;
    }
    
    public Object putItem(Object paramObject1, Object paramObject2)
    {
      int i = 0;
      while (i < fNumEntries * 2)
      {
        if (fAugmentations[i].equals(paramObject1))
        {
          Object localObject = fAugmentations[(i + 1)];
          fAugmentations[(i + 1)] = paramObject2;
          return localObject;
        }
        i += 2;
      }
      fAugmentations[(fNumEntries * 2)] = paramObject1;
      fAugmentations[(fNumEntries * 2 + 1)] = paramObject2;
      fNumEntries += 1;
      return null;
    }
    
    public Object removeItem(Object paramObject)
    {
      int i = 0;
      while (i < fNumEntries * 2)
      {
        if (fAugmentations[i].equals(paramObject))
        {
          Object localObject = fAugmentations[(i + 1)];
          int j = i;
          while (j < fNumEntries * 2 - 2)
          {
            fAugmentations[j] = fAugmentations[(j + 2)];
            fAugmentations[(j + 1)] = fAugmentations[(j + 3)];
            j += 2;
          }
          fAugmentations[(fNumEntries * 2 - 2)] = null;
          fAugmentations[(fNumEntries * 2 - 1)] = null;
          fNumEntries -= 1;
          return localObject;
        }
        i += 2;
      }
      return null;
    }
    
    public void clear()
    {
      int i = 0;
      while (i < fNumEntries * 2)
      {
        fAugmentations[i] = null;
        fAugmentations[(i + 1)] = null;
        i += 2;
      }
      fNumEntries = 0;
    }
    
    public boolean isFull()
    {
      return fNumEntries == 10;
    }
    
    public AugmentationsImpl.AugmentationsItemsContainer expand()
    {
      AugmentationsImpl.LargeContainer localLargeContainer = new AugmentationsImpl.LargeContainer(AugmentationsImpl.this);
      int i = 0;
      while (i < fNumEntries * 2)
      {
        localLargeContainer.putItem(fAugmentations[i], fAugmentations[(i + 1)]);
        i += 2;
      }
      return localLargeContainer;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("SmallContainer - fNumEntries == ").append(fNumEntries);
      int i = 0;
      while (i < 20)
      {
        localStringBuilder.append("\nfAugmentations[").append(i).append("] == ").append(fAugmentations[i]).append("; fAugmentations[").append(i + 1).append("] == ").append(fAugmentations[(i + 1)]);
        i += 2;
      }
      return localStringBuilder.toString();
    }
    
    class SmallContainerKeyEnumeration
      implements Enumeration
    {
      Object[] enumArray = new Object[fNumEntries];
      int next = 0;
      
      SmallContainerKeyEnumeration()
      {
        for (int i = 0; i < fNumEntries; i++) {
          enumArray[i] = fAugmentations[(i * 2)];
        }
      }
      
      public boolean hasMoreElements()
      {
        return next < enumArray.length;
      }
      
      public Object nextElement()
      {
        if (next >= enumArray.length) {
          throw new NoSuchElementException();
        }
        Object localObject = enumArray[next];
        enumArray[next] = null;
        next += 1;
        return localObject;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\AugmentationsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */