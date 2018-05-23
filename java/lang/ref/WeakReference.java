package java.lang.ref;

public class WeakReference<T>
  extends Reference<T>
{
  public WeakReference(T paramT)
  {
    super(paramT);
  }
  
  public WeakReference(T paramT, ReferenceQueue<? super T> paramReferenceQueue)
  {
    super(paramT, paramReferenceQueue);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ref\WeakReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */