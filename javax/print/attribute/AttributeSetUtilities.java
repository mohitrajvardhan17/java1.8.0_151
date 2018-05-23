package javax.print.attribute;

import java.io.Serializable;

public final class AttributeSetUtilities
{
  private AttributeSetUtilities() {}
  
  public static AttributeSet unmodifiableView(AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet == null) {
      throw new NullPointerException();
    }
    return new UnmodifiableAttributeSet(paramAttributeSet);
  }
  
  public static DocAttributeSet unmodifiableView(DocAttributeSet paramDocAttributeSet)
  {
    if (paramDocAttributeSet == null) {
      throw new NullPointerException();
    }
    return new UnmodifiableDocAttributeSet(paramDocAttributeSet);
  }
  
  public static PrintRequestAttributeSet unmodifiableView(PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    if (paramPrintRequestAttributeSet == null) {
      throw new NullPointerException();
    }
    return new UnmodifiablePrintRequestAttributeSet(paramPrintRequestAttributeSet);
  }
  
  public static PrintJobAttributeSet unmodifiableView(PrintJobAttributeSet paramPrintJobAttributeSet)
  {
    if (paramPrintJobAttributeSet == null) {
      throw new NullPointerException();
    }
    return new UnmodifiablePrintJobAttributeSet(paramPrintJobAttributeSet);
  }
  
  public static PrintServiceAttributeSet unmodifiableView(PrintServiceAttributeSet paramPrintServiceAttributeSet)
  {
    if (paramPrintServiceAttributeSet == null) {
      throw new NullPointerException();
    }
    return new UnmodifiablePrintServiceAttributeSet(paramPrintServiceAttributeSet);
  }
  
  public static AttributeSet synchronizedView(AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet == null) {
      throw new NullPointerException();
    }
    return new SynchronizedAttributeSet(paramAttributeSet);
  }
  
  public static DocAttributeSet synchronizedView(DocAttributeSet paramDocAttributeSet)
  {
    if (paramDocAttributeSet == null) {
      throw new NullPointerException();
    }
    return new SynchronizedDocAttributeSet(paramDocAttributeSet);
  }
  
  public static PrintRequestAttributeSet synchronizedView(PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    if (paramPrintRequestAttributeSet == null) {
      throw new NullPointerException();
    }
    return new SynchronizedPrintRequestAttributeSet(paramPrintRequestAttributeSet);
  }
  
  public static PrintJobAttributeSet synchronizedView(PrintJobAttributeSet paramPrintJobAttributeSet)
  {
    if (paramPrintJobAttributeSet == null) {
      throw new NullPointerException();
    }
    return new SynchronizedPrintJobAttributeSet(paramPrintJobAttributeSet);
  }
  
  public static PrintServiceAttributeSet synchronizedView(PrintServiceAttributeSet paramPrintServiceAttributeSet)
  {
    if (paramPrintServiceAttributeSet == null) {
      throw new NullPointerException();
    }
    return new SynchronizedPrintServiceAttributeSet(paramPrintServiceAttributeSet);
  }
  
  public static Class<?> verifyAttributeCategory(Object paramObject, Class<?> paramClass)
  {
    Class localClass = (Class)paramObject;
    if (paramClass.isAssignableFrom(localClass)) {
      return localClass;
    }
    throw new ClassCastException();
  }
  
  public static Attribute verifyAttributeValue(Object paramObject, Class<?> paramClass)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    if (paramClass.isInstance(paramObject)) {
      return (Attribute)paramObject;
    }
    throw new ClassCastException();
  }
  
  public static void verifyCategoryForValue(Class<?> paramClass, Attribute paramAttribute)
  {
    if (!paramClass.equals(paramAttribute.getCategory())) {
      throw new IllegalArgumentException();
    }
  }
  
  private static class SynchronizedAttributeSet
    implements AttributeSet, Serializable
  {
    private AttributeSet attrset;
    
    public SynchronizedAttributeSet(AttributeSet paramAttributeSet)
    {
      attrset = paramAttributeSet;
    }
    
    public synchronized Attribute get(Class<?> paramClass)
    {
      return attrset.get(paramClass);
    }
    
    public synchronized boolean add(Attribute paramAttribute)
    {
      return attrset.add(paramAttribute);
    }
    
    public synchronized boolean remove(Class<?> paramClass)
    {
      return attrset.remove(paramClass);
    }
    
    public synchronized boolean remove(Attribute paramAttribute)
    {
      return attrset.remove(paramAttribute);
    }
    
    public synchronized boolean containsKey(Class<?> paramClass)
    {
      return attrset.containsKey(paramClass);
    }
    
    public synchronized boolean containsValue(Attribute paramAttribute)
    {
      return attrset.containsValue(paramAttribute);
    }
    
    public synchronized boolean addAll(AttributeSet paramAttributeSet)
    {
      return attrset.addAll(paramAttributeSet);
    }
    
    public synchronized int size()
    {
      return attrset.size();
    }
    
    public synchronized Attribute[] toArray()
    {
      return attrset.toArray();
    }
    
    public synchronized void clear()
    {
      attrset.clear();
    }
    
    public synchronized boolean isEmpty()
    {
      return attrset.isEmpty();
    }
    
    public synchronized boolean equals(Object paramObject)
    {
      return attrset.equals(paramObject);
    }
    
    public synchronized int hashCode()
    {
      return attrset.hashCode();
    }
  }
  
  private static class SynchronizedDocAttributeSet
    extends AttributeSetUtilities.SynchronizedAttributeSet
    implements DocAttributeSet, Serializable
  {
    public SynchronizedDocAttributeSet(DocAttributeSet paramDocAttributeSet)
    {
      super();
    }
  }
  
  private static class SynchronizedPrintJobAttributeSet
    extends AttributeSetUtilities.SynchronizedAttributeSet
    implements PrintJobAttributeSet, Serializable
  {
    public SynchronizedPrintJobAttributeSet(PrintJobAttributeSet paramPrintJobAttributeSet)
    {
      super();
    }
  }
  
  private static class SynchronizedPrintRequestAttributeSet
    extends AttributeSetUtilities.SynchronizedAttributeSet
    implements PrintRequestAttributeSet, Serializable
  {
    public SynchronizedPrintRequestAttributeSet(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    {
      super();
    }
  }
  
  private static class SynchronizedPrintServiceAttributeSet
    extends AttributeSetUtilities.SynchronizedAttributeSet
    implements PrintServiceAttributeSet, Serializable
  {
    public SynchronizedPrintServiceAttributeSet(PrintServiceAttributeSet paramPrintServiceAttributeSet)
    {
      super();
    }
  }
  
  private static class UnmodifiableAttributeSet
    implements AttributeSet, Serializable
  {
    private AttributeSet attrset;
    
    public UnmodifiableAttributeSet(AttributeSet paramAttributeSet)
    {
      attrset = paramAttributeSet;
    }
    
    public Attribute get(Class<?> paramClass)
    {
      return attrset.get(paramClass);
    }
    
    public boolean add(Attribute paramAttribute)
    {
      throw new UnmodifiableSetException();
    }
    
    public synchronized boolean remove(Class<?> paramClass)
    {
      throw new UnmodifiableSetException();
    }
    
    public boolean remove(Attribute paramAttribute)
    {
      throw new UnmodifiableSetException();
    }
    
    public boolean containsKey(Class<?> paramClass)
    {
      return attrset.containsKey(paramClass);
    }
    
    public boolean containsValue(Attribute paramAttribute)
    {
      return attrset.containsValue(paramAttribute);
    }
    
    public boolean addAll(AttributeSet paramAttributeSet)
    {
      throw new UnmodifiableSetException();
    }
    
    public int size()
    {
      return attrset.size();
    }
    
    public Attribute[] toArray()
    {
      return attrset.toArray();
    }
    
    public void clear()
    {
      throw new UnmodifiableSetException();
    }
    
    public boolean isEmpty()
    {
      return attrset.isEmpty();
    }
    
    public boolean equals(Object paramObject)
    {
      return attrset.equals(paramObject);
    }
    
    public int hashCode()
    {
      return attrset.hashCode();
    }
  }
  
  private static class UnmodifiableDocAttributeSet
    extends AttributeSetUtilities.UnmodifiableAttributeSet
    implements DocAttributeSet, Serializable
  {
    public UnmodifiableDocAttributeSet(DocAttributeSet paramDocAttributeSet)
    {
      super();
    }
  }
  
  private static class UnmodifiablePrintJobAttributeSet
    extends AttributeSetUtilities.UnmodifiableAttributeSet
    implements PrintJobAttributeSet, Serializable
  {
    public UnmodifiablePrintJobAttributeSet(PrintJobAttributeSet paramPrintJobAttributeSet)
    {
      super();
    }
  }
  
  private static class UnmodifiablePrintRequestAttributeSet
    extends AttributeSetUtilities.UnmodifiableAttributeSet
    implements PrintRequestAttributeSet, Serializable
  {
    public UnmodifiablePrintRequestAttributeSet(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    {
      super();
    }
  }
  
  private static class UnmodifiablePrintServiceAttributeSet
    extends AttributeSetUtilities.UnmodifiableAttributeSet
    implements PrintServiceAttributeSet, Serializable
  {
    public UnmodifiablePrintServiceAttributeSet(PrintServiceAttributeSet paramPrintServiceAttributeSet)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\AttributeSetUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */