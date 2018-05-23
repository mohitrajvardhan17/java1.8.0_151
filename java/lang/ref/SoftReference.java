package java.lang.ref;

public class SoftReference<T>
  extends Reference<T>
{
  private static long clock;
  private long timestamp = clock;
  
  public SoftReference(T paramT)
  {
    super(paramT);
  }
  
  public SoftReference(T paramT, ReferenceQueue<? super T> paramReferenceQueue)
  {
    super(paramT, paramReferenceQueue);
  }
  
  public T get()
  {
    Object localObject = super.get();
    if ((localObject != null) && (timestamp != clock)) {
      timestamp = clock;
    }
    return (T)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ref\SoftReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */