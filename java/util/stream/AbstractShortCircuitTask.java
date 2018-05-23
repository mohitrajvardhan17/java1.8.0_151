package java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;

abstract class AbstractShortCircuitTask<P_IN, P_OUT, R, K extends AbstractShortCircuitTask<P_IN, P_OUT, R, K>>
  extends AbstractTask<P_IN, P_OUT, R, K>
{
  protected final AtomicReference<R> sharedResult;
  protected volatile boolean canceled;
  
  protected AbstractShortCircuitTask(PipelineHelper<P_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator)
  {
    super(paramPipelineHelper, paramSpliterator);
    sharedResult = new AtomicReference(null);
  }
  
  protected AbstractShortCircuitTask(K paramK, Spliterator<P_IN> paramSpliterator)
  {
    super(paramK, paramSpliterator);
    sharedResult = sharedResult;
  }
  
  protected abstract R getEmptyResult();
  
  public void compute()
  {
    Object localObject1 = spliterator;
    long l1 = ((Spliterator)localObject1).estimateSize();
    long l2 = getTargetSize(l1);
    int i = 0;
    Object localObject2 = this;
    AtomicReference localAtomicReference = sharedResult;
    Object localObject3;
    while ((localObject3 = localAtomicReference.get()) == null)
    {
      if (((AbstractShortCircuitTask)localObject2).taskCanceled())
      {
        localObject3 = ((AbstractShortCircuitTask)localObject2).getEmptyResult();
        break;
      }
      Spliterator localSpliterator;
      if ((l1 <= l2) || ((localSpliterator = ((Spliterator)localObject1).trySplit()) == null))
      {
        localObject3 = ((AbstractShortCircuitTask)localObject2).doLeaf();
        break;
      }
      AbstractShortCircuitTask localAbstractShortCircuitTask1;
      leftChild = (localAbstractShortCircuitTask1 = (AbstractShortCircuitTask)((AbstractShortCircuitTask)localObject2).makeChild(localSpliterator));
      AbstractShortCircuitTask localAbstractShortCircuitTask2;
      rightChild = (localAbstractShortCircuitTask2 = (AbstractShortCircuitTask)((AbstractShortCircuitTask)localObject2).makeChild((Spliterator)localObject1));
      ((AbstractShortCircuitTask)localObject2).setPendingCount(1);
      AbstractShortCircuitTask localAbstractShortCircuitTask3;
      if (i != 0)
      {
        i = 0;
        localObject1 = localSpliterator;
        localObject2 = localAbstractShortCircuitTask1;
        localAbstractShortCircuitTask3 = localAbstractShortCircuitTask2;
      }
      else
      {
        i = 1;
        localObject2 = localAbstractShortCircuitTask2;
        localAbstractShortCircuitTask3 = localAbstractShortCircuitTask1;
      }
      localAbstractShortCircuitTask3.fork();
      l1 = ((Spliterator)localObject1).estimateSize();
    }
    ((AbstractShortCircuitTask)localObject2).setLocalResult(localObject3);
    ((AbstractShortCircuitTask)localObject2).tryComplete();
  }
  
  protected void shortCircuit(R paramR)
  {
    if (paramR != null) {
      sharedResult.compareAndSet(null, paramR);
    }
  }
  
  protected void setLocalResult(R paramR)
  {
    if (isRoot())
    {
      if (paramR != null) {
        sharedResult.compareAndSet(null, paramR);
      }
    }
    else {
      super.setLocalResult(paramR);
    }
  }
  
  public R getRawResult()
  {
    return (R)getLocalResult();
  }
  
  public R getLocalResult()
  {
    if (isRoot())
    {
      Object localObject = sharedResult.get();
      return (R)(localObject == null ? getEmptyResult() : localObject);
    }
    return (R)super.getLocalResult();
  }
  
  protected void cancel()
  {
    canceled = true;
  }
  
  protected boolean taskCanceled()
  {
    boolean bool = canceled;
    if (!bool) {
      for (AbstractShortCircuitTask localAbstractShortCircuitTask = (AbstractShortCircuitTask)getParent(); (!bool) && (localAbstractShortCircuitTask != null); localAbstractShortCircuitTask = (AbstractShortCircuitTask)localAbstractShortCircuitTask.getParent()) {
        bool = canceled;
      }
    }
    return bool;
  }
  
  protected void cancelLaterNodes()
  {
    AbstractShortCircuitTask localAbstractShortCircuitTask1 = (AbstractShortCircuitTask)getParent();
    AbstractShortCircuitTask localAbstractShortCircuitTask2 = this;
    while (localAbstractShortCircuitTask1 != null)
    {
      if (leftChild == localAbstractShortCircuitTask2)
      {
        AbstractShortCircuitTask localAbstractShortCircuitTask3 = (AbstractShortCircuitTask)rightChild;
        if (!canceled) {
          localAbstractShortCircuitTask3.cancel();
        }
      }
      localAbstractShortCircuitTask2 = localAbstractShortCircuitTask1;
      localAbstractShortCircuitTask1 = (AbstractShortCircuitTask)localAbstractShortCircuitTask1.getParent();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\stream\AbstractShortCircuitTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */