package java.nio.channels;

public abstract interface CompletionHandler<V, A>
{
  public abstract void completed(V paramV, A paramA);
  
  public abstract void failed(Throwable paramThrowable, A paramA);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\CompletionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */