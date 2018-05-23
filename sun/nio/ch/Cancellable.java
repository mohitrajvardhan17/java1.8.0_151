package sun.nio.ch;

abstract interface Cancellable
{
  public abstract void onCancel(PendingFuture<?, ?> paramPendingFuture);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\Cancellable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */