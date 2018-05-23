package sun.java2d.loops;

public final class RenderCache
{
  private Entry[] entries;
  
  public RenderCache(int paramInt)
  {
    entries = new Entry[paramInt];
  }
  
  public synchronized Object get(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    int i = entries.length - 1;
    for (int j = i; j >= 0; j--)
    {
      Entry localEntry = entries[j];
      if (localEntry == null) {
        break;
      }
      if (localEntry.matches(paramSurfaceType1, paramCompositeType, paramSurfaceType2))
      {
        if (j < i - 4)
        {
          System.arraycopy(entries, j + 1, entries, j, i - j);
          entries[i] = localEntry;
        }
        return localEntry.getValue();
      }
    }
    return null;
  }
  
  public synchronized void put(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2, Object paramObject)
  {
    Entry localEntry = new Entry(paramSurfaceType1, paramCompositeType, paramSurfaceType2, paramObject);
    int i = entries.length;
    System.arraycopy(entries, 1, entries, 0, i - 1);
    entries[(i - 1)] = localEntry;
  }
  
  final class Entry
  {
    private SurfaceType src;
    private CompositeType comp;
    private SurfaceType dst;
    private Object value;
    
    public Entry(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2, Object paramObject)
    {
      src = paramSurfaceType1;
      comp = paramCompositeType;
      dst = paramSurfaceType2;
      value = paramObject;
    }
    
    public boolean matches(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
    {
      return (src == paramSurfaceType1) && (comp == paramCompositeType) && (dst == paramSurfaceType2);
    }
    
    public Object getValue()
    {
      return value;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\RenderCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */