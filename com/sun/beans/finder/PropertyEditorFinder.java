package com.sun.beans.finder;

import com.sun.beans.WeakCache;
import com.sun.beans.editors.BooleanEditor;
import com.sun.beans.editors.ByteEditor;
import com.sun.beans.editors.DoubleEditor;
import com.sun.beans.editors.EnumEditor;
import com.sun.beans.editors.FloatEditor;
import com.sun.beans.editors.IntegerEditor;
import com.sun.beans.editors.LongEditor;
import com.sun.beans.editors.ShortEditor;
import java.beans.PropertyEditor;

public final class PropertyEditorFinder
  extends InstanceFinder<PropertyEditor>
{
  private static final String DEFAULT = "sun.beans.editors";
  private static final String DEFAULT_NEW = "com.sun.beans.editors";
  private final WeakCache<Class<?>, Class<?>> registry = new WeakCache();
  
  public PropertyEditorFinder()
  {
    super(PropertyEditor.class, false, "Editor", new String[] { "sun.beans.editors" });
    registry.put(Byte.TYPE, ByteEditor.class);
    registry.put(Short.TYPE, ShortEditor.class);
    registry.put(Integer.TYPE, IntegerEditor.class);
    registry.put(Long.TYPE, LongEditor.class);
    registry.put(Boolean.TYPE, BooleanEditor.class);
    registry.put(Float.TYPE, FloatEditor.class);
    registry.put(Double.TYPE, DoubleEditor.class);
  }
  
  public void register(Class<?> paramClass1, Class<?> paramClass2)
  {
    synchronized (registry)
    {
      registry.put(paramClass1, paramClass2);
    }
  }
  
  public PropertyEditor find(Class<?> paramClass)
  {
    Class localClass;
    synchronized (registry)
    {
      localClass = (Class)registry.get(paramClass);
    }
    ??? = (PropertyEditor)instantiate(localClass, null);
    if (??? == null)
    {
      ??? = (PropertyEditor)super.find(paramClass);
      if ((??? == null) && (null != paramClass.getEnumConstants())) {
        ??? = new EnumEditor(paramClass);
      }
    }
    return (PropertyEditor)???;
  }
  
  protected PropertyEditor instantiate(Class<?> paramClass, String paramString1, String paramString2)
  {
    return (PropertyEditor)super.instantiate(paramClass, "sun.beans.editors".equals(paramString1) ? "com.sun.beans.editors" : paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\PropertyEditorFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */