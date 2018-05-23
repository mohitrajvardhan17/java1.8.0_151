package java.beans;

import com.sun.beans.TypeResolver;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class FeatureDescriptor
{
  private static final String TRANSIENT = "transient";
  private Reference<? extends Class<?>> classRef;
  private boolean expert;
  private boolean hidden;
  private boolean preferred;
  private String shortDescription;
  private String name;
  private String displayName;
  private Hashtable<String, Object> table;
  
  public FeatureDescriptor() {}
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public String getDisplayName()
  {
    if (displayName == null) {
      return getName();
    }
    return displayName;
  }
  
  public void setDisplayName(String paramString)
  {
    displayName = paramString;
  }
  
  public boolean isExpert()
  {
    return expert;
  }
  
  public void setExpert(boolean paramBoolean)
  {
    expert = paramBoolean;
  }
  
  public boolean isHidden()
  {
    return hidden;
  }
  
  public void setHidden(boolean paramBoolean)
  {
    hidden = paramBoolean;
  }
  
  public boolean isPreferred()
  {
    return preferred;
  }
  
  public void setPreferred(boolean paramBoolean)
  {
    preferred = paramBoolean;
  }
  
  public String getShortDescription()
  {
    if (shortDescription == null) {
      return getDisplayName();
    }
    return shortDescription;
  }
  
  public void setShortDescription(String paramString)
  {
    shortDescription = paramString;
  }
  
  public void setValue(String paramString, Object paramObject)
  {
    getTable().put(paramString, paramObject);
  }
  
  public Object getValue(String paramString)
  {
    return table != null ? table.get(paramString) : null;
  }
  
  public Enumeration<String> attributeNames()
  {
    return getTable().keys();
  }
  
  FeatureDescriptor(FeatureDescriptor paramFeatureDescriptor1, FeatureDescriptor paramFeatureDescriptor2)
  {
    expert |= expert;
    hidden |= hidden;
    preferred |= preferred;
    name = name;
    shortDescription = shortDescription;
    if (shortDescription != null) {
      shortDescription = shortDescription;
    }
    displayName = displayName;
    if (displayName != null) {
      displayName = displayName;
    }
    classRef = classRef;
    if (classRef != null) {
      classRef = classRef;
    }
    addTable(table);
    addTable(table);
  }
  
  FeatureDescriptor(FeatureDescriptor paramFeatureDescriptor)
  {
    expert = expert;
    hidden = hidden;
    preferred = preferred;
    name = name;
    shortDescription = shortDescription;
    displayName = displayName;
    classRef = classRef;
    addTable(table);
  }
  
  private void addTable(Hashtable<String, Object> paramHashtable)
  {
    if ((paramHashtable != null) && (!paramHashtable.isEmpty())) {
      getTable().putAll(paramHashtable);
    }
  }
  
  private Hashtable<String, Object> getTable()
  {
    if (table == null) {
      table = new Hashtable();
    }
    return table;
  }
  
  void setTransient(Transient paramTransient)
  {
    if ((paramTransient != null) && (null == getValue("transient"))) {
      setValue("transient", Boolean.valueOf(paramTransient.value()));
    }
  }
  
  boolean isTransient()
  {
    Object localObject = getValue("transient");
    return (localObject instanceof Boolean) ? ((Boolean)localObject).booleanValue() : false;
  }
  
  void setClass0(Class<?> paramClass)
  {
    classRef = getWeakReference(paramClass);
  }
  
  Class<?> getClass0()
  {
    return classRef != null ? (Class)classRef.get() : null;
  }
  
  static <T> Reference<T> getSoftReference(T paramT)
  {
    return paramT != null ? new SoftReference(paramT) : null;
  }
  
  static <T> Reference<T> getWeakReference(T paramT)
  {
    return paramT != null ? new WeakReference(paramT) : null;
  }
  
  static Class<?> getReturnType(Class<?> paramClass, Method paramMethod)
  {
    if (paramClass == null) {
      paramClass = paramMethod.getDeclaringClass();
    }
    return TypeResolver.erase(TypeResolver.resolveInClass(paramClass, paramMethod.getGenericReturnType()));
  }
  
  static Class<?>[] getParameterTypes(Class<?> paramClass, Method paramMethod)
  {
    if (paramClass == null) {
      paramClass = paramMethod.getDeclaringClass();
    }
    return TypeResolver.erase(TypeResolver.resolveInClass(paramClass, paramMethod.getGenericParameterTypes()));
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(getClass().getName());
    localStringBuilder.append("[name=").append(name);
    appendTo(localStringBuilder, "displayName", displayName);
    appendTo(localStringBuilder, "shortDescription", shortDescription);
    appendTo(localStringBuilder, "preferred", preferred);
    appendTo(localStringBuilder, "hidden", hidden);
    appendTo(localStringBuilder, "expert", expert);
    if ((table != null) && (!table.isEmpty()))
    {
      localStringBuilder.append("; values={");
      Iterator localIterator = table.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localStringBuilder.append((String)localEntry.getKey()).append("=").append(localEntry.getValue()).append("; ");
      }
      localStringBuilder.setLength(localStringBuilder.length() - 2);
      localStringBuilder.append("}");
    }
    appendTo(localStringBuilder);
    return "]";
  }
  
  void appendTo(StringBuilder paramStringBuilder) {}
  
  static void appendTo(StringBuilder paramStringBuilder, String paramString, Reference<?> paramReference)
  {
    if (paramReference != null) {
      appendTo(paramStringBuilder, paramString, paramReference.get());
    }
  }
  
  static void appendTo(StringBuilder paramStringBuilder, String paramString, Object paramObject)
  {
    if (paramObject != null) {
      paramStringBuilder.append("; ").append(paramString).append("=").append(paramObject);
    }
  }
  
  static void appendTo(StringBuilder paramStringBuilder, String paramString, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramStringBuilder.append("; ").append(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\FeatureDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */