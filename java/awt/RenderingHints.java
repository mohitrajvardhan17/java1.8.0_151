package java.awt;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import sun.awt.SunHints;

public class RenderingHints
  implements Map<Object, Object>, Cloneable
{
  HashMap<Object, Object> hintmap = new HashMap(7);
  public static final Key KEY_ANTIALIASING = SunHints.KEY_ANTIALIASING;
  public static final Object VALUE_ANTIALIAS_ON = SunHints.VALUE_ANTIALIAS_ON;
  public static final Object VALUE_ANTIALIAS_OFF = SunHints.VALUE_ANTIALIAS_OFF;
  public static final Object VALUE_ANTIALIAS_DEFAULT = SunHints.VALUE_ANTIALIAS_DEFAULT;
  public static final Key KEY_RENDERING = SunHints.KEY_RENDERING;
  public static final Object VALUE_RENDER_SPEED = SunHints.VALUE_RENDER_SPEED;
  public static final Object VALUE_RENDER_QUALITY = SunHints.VALUE_RENDER_QUALITY;
  public static final Object VALUE_RENDER_DEFAULT = SunHints.VALUE_RENDER_DEFAULT;
  public static final Key KEY_DITHERING = SunHints.KEY_DITHERING;
  public static final Object VALUE_DITHER_DISABLE = SunHints.VALUE_DITHER_DISABLE;
  public static final Object VALUE_DITHER_ENABLE = SunHints.VALUE_DITHER_ENABLE;
  public static final Object VALUE_DITHER_DEFAULT = SunHints.VALUE_DITHER_DEFAULT;
  public static final Key KEY_TEXT_ANTIALIASING = SunHints.KEY_TEXT_ANTIALIASING;
  public static final Object VALUE_TEXT_ANTIALIAS_ON = SunHints.VALUE_TEXT_ANTIALIAS_ON;
  public static final Object VALUE_TEXT_ANTIALIAS_OFF = SunHints.VALUE_TEXT_ANTIALIAS_OFF;
  public static final Object VALUE_TEXT_ANTIALIAS_DEFAULT = SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
  public static final Object VALUE_TEXT_ANTIALIAS_GASP = SunHints.VALUE_TEXT_ANTIALIAS_GASP;
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_HRGB = SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_HBGR = SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_VRGB = SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_VBGR = SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
  public static final Key KEY_TEXT_LCD_CONTRAST = SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST;
  public static final Key KEY_FRACTIONALMETRICS = SunHints.KEY_FRACTIONALMETRICS;
  public static final Object VALUE_FRACTIONALMETRICS_OFF = SunHints.VALUE_FRACTIONALMETRICS_OFF;
  public static final Object VALUE_FRACTIONALMETRICS_ON = SunHints.VALUE_FRACTIONALMETRICS_ON;
  public static final Object VALUE_FRACTIONALMETRICS_DEFAULT = SunHints.VALUE_FRACTIONALMETRICS_DEFAULT;
  public static final Key KEY_INTERPOLATION = SunHints.KEY_INTERPOLATION;
  public static final Object VALUE_INTERPOLATION_NEAREST_NEIGHBOR = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
  public static final Object VALUE_INTERPOLATION_BILINEAR = SunHints.VALUE_INTERPOLATION_BILINEAR;
  public static final Object VALUE_INTERPOLATION_BICUBIC = SunHints.VALUE_INTERPOLATION_BICUBIC;
  public static final Key KEY_ALPHA_INTERPOLATION = SunHints.KEY_ALPHA_INTERPOLATION;
  public static final Object VALUE_ALPHA_INTERPOLATION_SPEED = SunHints.VALUE_ALPHA_INTERPOLATION_SPEED;
  public static final Object VALUE_ALPHA_INTERPOLATION_QUALITY = SunHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
  public static final Object VALUE_ALPHA_INTERPOLATION_DEFAULT = SunHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;
  public static final Key KEY_COLOR_RENDERING = SunHints.KEY_COLOR_RENDERING;
  public static final Object VALUE_COLOR_RENDER_SPEED = SunHints.VALUE_COLOR_RENDER_SPEED;
  public static final Object VALUE_COLOR_RENDER_QUALITY = SunHints.VALUE_COLOR_RENDER_QUALITY;
  public static final Object VALUE_COLOR_RENDER_DEFAULT = SunHints.VALUE_COLOR_RENDER_DEFAULT;
  public static final Key KEY_STROKE_CONTROL = SunHints.KEY_STROKE_CONTROL;
  public static final Object VALUE_STROKE_DEFAULT = SunHints.VALUE_STROKE_DEFAULT;
  public static final Object VALUE_STROKE_NORMALIZE = SunHints.VALUE_STROKE_NORMALIZE;
  public static final Object VALUE_STROKE_PURE = SunHints.VALUE_STROKE_PURE;
  
  public RenderingHints(Map<Key, ?> paramMap)
  {
    if (paramMap != null) {
      hintmap.putAll(paramMap);
    }
  }
  
  public RenderingHints(Key paramKey, Object paramObject)
  {
    hintmap.put(paramKey, paramObject);
  }
  
  public int size()
  {
    return hintmap.size();
  }
  
  public boolean isEmpty()
  {
    return hintmap.isEmpty();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return hintmap.containsKey((Key)paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return hintmap.containsValue(paramObject);
  }
  
  public Object get(Object paramObject)
  {
    return hintmap.get((Key)paramObject);
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    if (!((Key)paramObject1).isCompatibleValue(paramObject2)) {
      throw new IllegalArgumentException(paramObject2 + " incompatible with " + paramObject1);
    }
    return hintmap.put((Key)paramObject1, paramObject2);
  }
  
  public void add(RenderingHints paramRenderingHints)
  {
    hintmap.putAll(hintmap);
  }
  
  public void clear()
  {
    hintmap.clear();
  }
  
  public Object remove(Object paramObject)
  {
    return hintmap.remove((Key)paramObject);
  }
  
  public void putAll(Map<?, ?> paramMap)
  {
    Iterator localIterator;
    Map.Entry localEntry;
    if (RenderingHints.class.isInstance(paramMap))
    {
      localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localEntry = (Map.Entry)localIterator.next();
        hintmap.put(localEntry.getKey(), localEntry.getValue());
      }
    }
    else
    {
      localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localEntry = (Map.Entry)localIterator.next();
        put(localEntry.getKey(), localEntry.getValue());
      }
    }
  }
  
  public Set<Object> keySet()
  {
    return hintmap.keySet();
  }
  
  public Collection<Object> values()
  {
    return hintmap.values();
  }
  
  public Set<Map.Entry<Object, Object>> entrySet()
  {
    return Collections.unmodifiableMap(hintmap).entrySet();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof RenderingHints)) {
      return hintmap.equals(hintmap);
    }
    if ((paramObject instanceof Map)) {
      return hintmap.equals(paramObject);
    }
    return false;
  }
  
  public int hashCode()
  {
    return hintmap.hashCode();
  }
  
  public Object clone()
  {
    RenderingHints localRenderingHints;
    try
    {
      localRenderingHints = (RenderingHints)super.clone();
      if (hintmap != null) {
        hintmap = ((HashMap)hintmap.clone());
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
    return localRenderingHints;
  }
  
  public String toString()
  {
    if (hintmap == null) {
      return getClass().getName() + "@" + Integer.toHexString(hashCode()) + " (0 hints)";
    }
    return hintmap.toString();
  }
  
  public static abstract class Key
  {
    private static HashMap<Object, Object> identitymap = new HashMap(17);
    private int privatekey;
    
    private String getIdentity()
    {
      return getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(getClass())) + ":" + Integer.toHexString(privatekey);
    }
    
    private static synchronized void recordIdentity(Key paramKey)
    {
      String str = paramKey.getIdentity();
      Object localObject = identitymap.get(str);
      if (localObject != null)
      {
        Key localKey = (Key)((WeakReference)localObject).get();
        if ((localKey != null) && (localKey.getClass() == paramKey.getClass())) {
          throw new IllegalArgumentException(str + " already registered");
        }
      }
      identitymap.put(str, new WeakReference(paramKey));
    }
    
    protected Key(int paramInt)
    {
      privatekey = paramInt;
      recordIdentity(this);
    }
    
    public abstract boolean isCompatibleValue(Object paramObject);
    
    protected final int intKey()
    {
      return privatekey;
    }
    
    public final int hashCode()
    {
      return super.hashCode();
    }
    
    public final boolean equals(Object paramObject)
    {
      return this == paramObject;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\RenderingHints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */