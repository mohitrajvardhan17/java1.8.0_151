package java.util.stream;

import java.util.Spliterator;

abstract interface TerminalOp<E_IN, R>
{
  public StreamShape inputShape()
  {
    return StreamShape.REFERENCE;
  }
  
  public int getOpFlags()
  {
    return 0;
  }
  
  public <P_IN> R evaluateParallel(PipelineHelper<E_IN> paramPipelineHelper, Spliterator<P_IN> paramSpliterator)
  {
    if (Tripwire.ENABLED) {
      Tripwire.trip(getClass(), "{0} triggering TerminalOp.evaluateParallel serial default");
    }
    return (R)evaluateSequential(paramPipelineHelper, paramSpliterator);
  }
  
  public abstract <P_IN> R evaluateSequential(PipelineHelper<E_IN> paramPipelineHelper, Spliterator<P_IN> paramSpliterator);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\stream\TerminalOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */