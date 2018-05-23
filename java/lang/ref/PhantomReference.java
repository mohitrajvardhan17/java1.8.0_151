package java.lang.ref;

public class PhantomReference<T>
  extends Reference<T>
{
  public T get()
  {
    return null;
  }
  
  public PhantomReference(T paramT, ReferenceQueue<? super T> paramReferenceQueue)
  {
    super(paramT, paramReferenceQueue);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ref\PhantomReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */