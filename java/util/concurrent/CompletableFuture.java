package java.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import sun.misc.Unsafe;

public class CompletableFuture<T>
  implements Future<T>, CompletionStage<T>
{
  volatile Object result;
  volatile Completion stack;
  static final AltResult NIL = new AltResult(null);
  private static final boolean useCommonPool = ForkJoinPool.getCommonPoolParallelism() > 1;
  private static final Executor asyncPool = useCommonPool ? ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();
  static final int SYNC = 0;
  static final int ASYNC = 1;
  static final int NESTED = -1;
  private static final Unsafe UNSAFE;
  private static final long RESULT;
  private static final long STACK;
  private static final long NEXT;
  
  final boolean internalComplete(Object paramObject)
  {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, paramObject);
  }
  
  final boolean casStack(Completion paramCompletion1, Completion paramCompletion2)
  {
    return UNSAFE.compareAndSwapObject(this, STACK, paramCompletion1, paramCompletion2);
  }
  
  final boolean tryPushStack(Completion paramCompletion)
  {
    Completion localCompletion = stack;
    lazySetNext(paramCompletion, localCompletion);
    return UNSAFE.compareAndSwapObject(this, STACK, localCompletion, paramCompletion);
  }
  
  final void pushStack(Completion paramCompletion)
  {
    while (!tryPushStack(paramCompletion)) {}
  }
  
  final boolean completeNull()
  {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, NIL);
  }
  
  final Object encodeValue(T paramT)
  {
    return paramT == null ? NIL : paramT;
  }
  
  final boolean completeValue(T paramT)
  {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, paramT == null ? NIL : paramT);
  }
  
  static AltResult encodeThrowable(Throwable paramThrowable)
  {
    return new AltResult((paramThrowable instanceof CompletionException) ? paramThrowable : new CompletionException(paramThrowable));
  }
  
  final boolean completeThrowable(Throwable paramThrowable)
  {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, encodeThrowable(paramThrowable));
  }
  
  static Object encodeThrowable(Throwable paramThrowable, Object paramObject)
  {
    if (!(paramThrowable instanceof CompletionException)) {
      paramThrowable = new CompletionException(paramThrowable);
    } else if (((paramObject instanceof AltResult)) && (paramThrowable == ex)) {
      return paramObject;
    }
    return new AltResult(paramThrowable);
  }
  
  final boolean completeThrowable(Throwable paramThrowable, Object paramObject)
  {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, encodeThrowable(paramThrowable, paramObject));
  }
  
  Object encodeOutcome(T paramT, Throwable paramThrowable)
  {
    return paramThrowable == null ? paramT : paramT == null ? NIL : encodeThrowable(paramThrowable);
  }
  
  static Object encodeRelay(Object paramObject)
  {
    Throwable localThrowable;
    return ((paramObject instanceof AltResult)) && ((localThrowable = ex) != null) && (!(localThrowable instanceof CompletionException)) ? new AltResult(new CompletionException(localThrowable)) : paramObject;
  }
  
  final boolean completeRelay(Object paramObject)
  {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, encodeRelay(paramObject));
  }
  
  private static <T> T reportGet(Object paramObject)
    throws InterruptedException, ExecutionException
  {
    if (paramObject == null) {
      throw new InterruptedException();
    }
    if ((paramObject instanceof AltResult))
    {
      if ((localObject = ex) == null) {
        return null;
      }
      if ((localObject instanceof CancellationException)) {
        throw ((CancellationException)localObject);
      }
      Throwable localThrowable;
      if (((localObject instanceof CompletionException)) && ((localThrowable = ((Throwable)localObject).getCause()) != null)) {
        localObject = localThrowable;
      }
      throw new ExecutionException((Throwable)localObject);
    }
    Object localObject = paramObject;
    return (T)localObject;
  }
  
  private static <T> T reportJoin(Object paramObject)
  {
    if ((paramObject instanceof AltResult))
    {
      if ((localObject = ex) == null) {
        return null;
      }
      if ((localObject instanceof CancellationException)) {
        throw ((CancellationException)localObject);
      }
      if ((localObject instanceof CompletionException)) {
        throw ((CompletionException)localObject);
      }
      throw new CompletionException((Throwable)localObject);
    }
    Object localObject = paramObject;
    return (T)localObject;
  }
  
  static Executor screenExecutor(Executor paramExecutor)
  {
    if ((!useCommonPool) && (paramExecutor == ForkJoinPool.commonPool())) {
      return asyncPool;
    }
    if (paramExecutor == null) {
      throw new NullPointerException();
    }
    return paramExecutor;
  }
  
  static void lazySetNext(Completion paramCompletion1, Completion paramCompletion2)
  {
    UNSAFE.putOrderedObject(paramCompletion1, NEXT, paramCompletion2);
  }
  
  final void postComplete()
  {
    Object localObject = this;
    for (;;)
    {
      Completion localCompletion1;
      if ((localCompletion1 = stack) == null)
      {
        if (localObject == this) {
          break;
        }
        localObject = this;
        if ((localCompletion1 = stack) == null) {
          break;
        }
      }
      Completion localCompletion2;
      if (((CompletableFuture)localObject).casStack(localCompletion1, localCompletion2 = next)) {
        if (localCompletion2 != null)
        {
          if (localObject != this) {
            pushStack(localCompletion1);
          } else {
            next = null;
          }
        }
        else
        {
          CompletableFuture localCompletableFuture;
          localObject = (localCompletableFuture = localCompletion1.tryFire(-1)) == null ? this : localCompletableFuture;
        }
      }
    }
  }
  
  final void cleanStack()
  {
    Object localObject1 = null;
    Object localObject2 = stack;
    while (localObject2 != null)
    {
      Completion localCompletion = next;
      if (((Completion)localObject2).isLive())
      {
        localObject1 = localObject2;
        localObject2 = localCompletion;
      }
      else if (localObject1 == null)
      {
        casStack((Completion)localObject2, localCompletion);
        localObject2 = stack;
      }
      else
      {
        next = localCompletion;
        if (((Completion)localObject1).isLive())
        {
          localObject2 = localCompletion;
        }
        else
        {
          localObject1 = null;
          localObject2 = stack;
        }
      }
    }
  }
  
  final void push(UniCompletion<?, ?> paramUniCompletion)
  {
    if (paramUniCompletion != null) {
      while ((result == null) && (!tryPushStack(paramUniCompletion))) {
        lazySetNext(paramUniCompletion, null);
      }
    }
  }
  
  final CompletableFuture<T> postFire(CompletableFuture<?> paramCompletableFuture, int paramInt)
  {
    if ((paramCompletableFuture != null) && (stack != null)) {
      if ((paramInt < 0) || (result == null)) {
        paramCompletableFuture.cleanStack();
      } else {
        paramCompletableFuture.postComplete();
      }
    }
    if ((result != null) && (stack != null))
    {
      if (paramInt < 0) {
        return this;
      }
      postComplete();
    }
    return null;
  }
  
  final <S> boolean uniApply(CompletableFuture<S> paramCompletableFuture, Function<? super S, ? extends T> paramFunction, UniApply<S, T> paramUniApply)
  {
    Object localObject1;
    if ((paramCompletableFuture == null) || ((localObject1 = result) == null) || (paramFunction == null)) {
      return false;
    }
    if (result == null) {
      if ((localObject1 instanceof AltResult))
      {
        Throwable localThrowable1;
        if ((localThrowable1 = ex) != null) {
          completeThrowable(localThrowable1, localObject1);
        } else {
          localObject1 = null;
        }
      }
      else
      {
        try
        {
          if ((paramUniApply != null) && (!paramUniApply.claim())) {
            return false;
          }
          Object localObject2 = localObject1;
          completeValue(paramFunction.apply(localObject2));
        }
        catch (Throwable localThrowable2)
        {
          completeThrowable(localThrowable2);
        }
      }
    }
    return true;
  }
  
  private <V> CompletableFuture<V> uniApplyStage(Executor paramExecutor, Function<? super T, ? extends V> paramFunction)
  {
    if (paramFunction == null) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture.uniApply(this, paramFunction, null)))
    {
      UniApply localUniApply = new UniApply(paramExecutor, localCompletableFuture, this, paramFunction);
      push(localUniApply);
      localUniApply.tryFire(0);
    }
    return localCompletableFuture;
  }
  
  final <S> boolean uniAccept(CompletableFuture<S> paramCompletableFuture, Consumer<? super S> paramConsumer, UniAccept<S> paramUniAccept)
  {
    Object localObject1;
    if ((paramCompletableFuture == null) || ((localObject1 = result) == null) || (paramConsumer == null)) {
      return false;
    }
    if (result == null) {
      if ((localObject1 instanceof AltResult))
      {
        Throwable localThrowable1;
        if ((localThrowable1 = ex) != null) {
          completeThrowable(localThrowable1, localObject1);
        } else {
          localObject1 = null;
        }
      }
      else
      {
        try
        {
          if ((paramUniAccept != null) && (!paramUniAccept.claim())) {
            return false;
          }
          Object localObject2 = localObject1;
          paramConsumer.accept(localObject2);
          completeNull();
        }
        catch (Throwable localThrowable2)
        {
          completeThrowable(localThrowable2);
        }
      }
    }
    return true;
  }
  
  private CompletableFuture<Void> uniAcceptStage(Executor paramExecutor, Consumer<? super T> paramConsumer)
  {
    if (paramConsumer == null) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture.uniAccept(this, paramConsumer, null)))
    {
      UniAccept localUniAccept = new UniAccept(paramExecutor, localCompletableFuture, this, paramConsumer);
      push(localUniAccept);
      localUniAccept.tryFire(0);
    }
    return localCompletableFuture;
  }
  
  final boolean uniRun(CompletableFuture<?> paramCompletableFuture, Runnable paramRunnable, UniRun<?> paramUniRun)
  {
    Object localObject;
    if ((paramCompletableFuture == null) || ((localObject = result) == null) || (paramRunnable == null)) {
      return false;
    }
    if (result == null)
    {
      Throwable localThrowable1;
      if (((localObject instanceof AltResult)) && ((localThrowable1 = ex) != null)) {
        completeThrowable(localThrowable1, localObject);
      } else {
        try
        {
          if ((paramUniRun != null) && (!paramUniRun.claim())) {
            return false;
          }
          paramRunnable.run();
          completeNull();
        }
        catch (Throwable localThrowable2)
        {
          completeThrowable(localThrowable2);
        }
      }
    }
    return true;
  }
  
  private CompletableFuture<Void> uniRunStage(Executor paramExecutor, Runnable paramRunnable)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture.uniRun(this, paramRunnable, null)))
    {
      UniRun localUniRun = new UniRun(paramExecutor, localCompletableFuture, this, paramRunnable);
      push(localUniRun);
      localUniRun.tryFire(0);
    }
    return localCompletableFuture;
  }
  
  final boolean uniWhenComplete(CompletableFuture<T> paramCompletableFuture, BiConsumer<? super T, ? super Throwable> paramBiConsumer, UniWhenComplete<T> paramUniWhenComplete)
  {
    Object localObject3 = null;
    Object localObject1;
    if ((paramCompletableFuture == null) || ((localObject1 = result) == null) || (paramBiConsumer == null)) {
      return false;
    }
    if (result == null)
    {
      try
      {
        if ((paramUniWhenComplete != null) && (!paramUniWhenComplete.claim())) {
          return false;
        }
        Object localObject2;
        if ((localObject1 instanceof AltResult))
        {
          localObject3 = ex;
          localObject2 = null;
        }
        else
        {
          Object localObject4 = localObject1;
          localObject2 = localObject4;
        }
        paramBiConsumer.accept(localObject2, localObject3);
        if (localObject3 == null)
        {
          internalComplete(localObject1);
          return true;
        }
      }
      catch (Throwable localThrowable)
      {
        if (localObject3 == null) {
          localObject3 = localThrowable;
        }
      }
      completeThrowable((Throwable)localObject3, localObject1);
    }
    return true;
  }
  
  private CompletableFuture<T> uniWhenCompleteStage(Executor paramExecutor, BiConsumer<? super T, ? super Throwable> paramBiConsumer)
  {
    if (paramBiConsumer == null) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture.uniWhenComplete(this, paramBiConsumer, null)))
    {
      UniWhenComplete localUniWhenComplete = new UniWhenComplete(paramExecutor, localCompletableFuture, this, paramBiConsumer);
      push(localUniWhenComplete);
      localUniWhenComplete.tryFire(0);
    }
    return localCompletableFuture;
  }
  
  final <S> boolean uniHandle(CompletableFuture<S> paramCompletableFuture, BiFunction<? super S, Throwable, ? extends T> paramBiFunction, UniHandle<S, T> paramUniHandle)
  {
    Object localObject1;
    if ((paramCompletableFuture == null) || ((localObject1 = result) == null) || (paramBiFunction == null)) {
      return false;
    }
    if (result == null) {
      try
      {
        if ((paramUniHandle != null) && (!paramUniHandle.claim())) {
          return false;
        }
        Throwable localThrowable1;
        Object localObject2;
        if ((localObject1 instanceof AltResult))
        {
          localThrowable1 = ex;
          localObject2 = null;
        }
        else
        {
          localThrowable1 = null;
          Object localObject3 = localObject1;
          localObject2 = localObject3;
        }
        completeValue(paramBiFunction.apply(localObject2, localThrowable1));
      }
      catch (Throwable localThrowable2)
      {
        completeThrowable(localThrowable2);
      }
    }
    return true;
  }
  
  private <V> CompletableFuture<V> uniHandleStage(Executor paramExecutor, BiFunction<? super T, Throwable, ? extends V> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture.uniHandle(this, paramBiFunction, null)))
    {
      UniHandle localUniHandle = new UniHandle(paramExecutor, localCompletableFuture, this, paramBiFunction);
      push(localUniHandle);
      localUniHandle.tryFire(0);
    }
    return localCompletableFuture;
  }
  
  final boolean uniExceptionally(CompletableFuture<T> paramCompletableFuture, Function<? super Throwable, ? extends T> paramFunction, UniExceptionally<T> paramUniExceptionally)
  {
    Object localObject;
    if ((paramCompletableFuture == null) || ((localObject = result) == null) || (paramFunction == null)) {
      return false;
    }
    if (result == null) {
      try
      {
        Throwable localThrowable1;
        if (((localObject instanceof AltResult)) && ((localThrowable1 = ex) != null))
        {
          if ((paramUniExceptionally != null) && (!paramUniExceptionally.claim())) {
            return false;
          }
          completeValue(paramFunction.apply(localThrowable1));
        }
        else
        {
          internalComplete(localObject);
        }
      }
      catch (Throwable localThrowable2)
      {
        completeThrowable(localThrowable2);
      }
    }
    return true;
  }
  
  private CompletableFuture<T> uniExceptionallyStage(Function<Throwable, ? extends T> paramFunction)
  {
    if (paramFunction == null) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture = new CompletableFuture();
    if (!localCompletableFuture.uniExceptionally(this, paramFunction, null))
    {
      UniExceptionally localUniExceptionally = new UniExceptionally(localCompletableFuture, this, paramFunction);
      push(localUniExceptionally);
      localUniExceptionally.tryFire(0);
    }
    return localCompletableFuture;
  }
  
  final boolean uniRelay(CompletableFuture<T> paramCompletableFuture)
  {
    Object localObject;
    if ((paramCompletableFuture == null) || ((localObject = result) == null)) {
      return false;
    }
    if (result == null) {
      completeRelay(localObject);
    }
    return true;
  }
  
  final <S> boolean uniCompose(CompletableFuture<S> paramCompletableFuture, Function<? super S, ? extends CompletionStage<T>> paramFunction, UniCompose<S, T> paramUniCompose)
  {
    Object localObject1;
    if ((paramCompletableFuture == null) || ((localObject1 = result) == null) || (paramFunction == null)) {
      return false;
    }
    if (result == null) {
      if ((localObject1 instanceof AltResult))
      {
        Throwable localThrowable1;
        if ((localThrowable1 = ex) != null) {
          completeThrowable(localThrowable1, localObject1);
        } else {
          localObject1 = null;
        }
      }
      else
      {
        try
        {
          if ((paramUniCompose != null) && (!paramUniCompose.claim())) {
            return false;
          }
          Object localObject2 = localObject1;
          CompletableFuture localCompletableFuture = ((CompletionStage)paramFunction.apply(localObject2)).toCompletableFuture();
          if ((result == null) || (!uniRelay(localCompletableFuture)))
          {
            UniRelay localUniRelay = new UniRelay(this, localCompletableFuture);
            localCompletableFuture.push(localUniRelay);
            localUniRelay.tryFire(0);
            if (result == null) {
              return false;
            }
          }
        }
        catch (Throwable localThrowable2)
        {
          completeThrowable(localThrowable2);
        }
      }
    }
    return true;
  }
  
  private <V> CompletableFuture<V> uniComposeStage(Executor paramExecutor, Function<? super T, ? extends CompletionStage<V>> paramFunction)
  {
    if (paramFunction == null) {
      throw new NullPointerException();
    }
    Object localObject1;
    if ((paramExecutor == null) && ((localObject1 = result) != null))
    {
      if ((localObject1 instanceof AltResult))
      {
        Throwable localThrowable1;
        if ((localThrowable1 = ex) != null) {
          return new CompletableFuture(encodeThrowable(localThrowable1, localObject1));
        }
        localObject1 = null;
      }
      try
      {
        Object localObject2 = localObject1;
        localObject3 = ((CompletionStage)paramFunction.apply(localObject2)).toCompletableFuture();
        Object localObject4 = result;
        if (localObject4 != null) {
          return new CompletableFuture(encodeRelay(localObject4));
        }
        CompletableFuture localCompletableFuture2 = new CompletableFuture();
        UniRelay localUniRelay = new UniRelay(localCompletableFuture2, (CompletableFuture)localObject3);
        ((CompletableFuture)localObject3).push(localUniRelay);
        localUniRelay.tryFire(0);
        return localCompletableFuture2;
      }
      catch (Throwable localThrowable2)
      {
        return new CompletableFuture(encodeThrowable(localThrowable2));
      }
    }
    CompletableFuture localCompletableFuture1 = new CompletableFuture();
    Object localObject3 = new UniCompose(paramExecutor, localCompletableFuture1, this, paramFunction);
    push((UniCompletion)localObject3);
    ((UniCompose)localObject3).tryFire(0);
    return localCompletableFuture1;
  }
  
  final void bipush(CompletableFuture<?> paramCompletableFuture, BiCompletion<?, ?, ?> paramBiCompletion)
  {
    if (paramBiCompletion != null)
    {
      Object localObject;
      while (((localObject = result) == null) && (!tryPushStack(paramBiCompletion))) {
        lazySetNext(paramBiCompletion, null);
      }
      if ((paramCompletableFuture != null) && (paramCompletableFuture != this) && (result == null))
      {
        CoCompletion localCoCompletion = localObject != null ? paramBiCompletion : new CoCompletion(paramBiCompletion);
        while ((result == null) && (!paramCompletableFuture.tryPushStack(localCoCompletion))) {
          lazySetNext(localCoCompletion, null);
        }
      }
    }
  }
  
  final CompletableFuture<T> postFire(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2, int paramInt)
  {
    if ((paramCompletableFuture2 != null) && (stack != null)) {
      if ((paramInt < 0) || (result == null)) {
        paramCompletableFuture2.cleanStack();
      } else {
        paramCompletableFuture2.postComplete();
      }
    }
    return postFire(paramCompletableFuture1, paramInt);
  }
  
  final <R, S> boolean biApply(CompletableFuture<R> paramCompletableFuture, CompletableFuture<S> paramCompletableFuture1, BiFunction<? super R, ? super S, ? extends T> paramBiFunction, BiApply<R, S, T> paramBiApply)
  {
    Object localObject1;
    Object localObject2;
    if ((paramCompletableFuture == null) || ((localObject1 = result) == null) || (paramCompletableFuture1 == null) || ((localObject2 = result) == null) || (paramBiFunction == null)) {
      return false;
    }
    if (result == null)
    {
      Throwable localThrowable1;
      if ((localObject1 instanceof AltResult))
      {
        if ((localThrowable1 = ex) != null) {
          completeThrowable(localThrowable1, localObject1);
        } else {
          localObject1 = null;
        }
      }
      else if ((localObject2 instanceof AltResult))
      {
        if ((localThrowable1 = ex) != null) {
          completeThrowable(localThrowable1, localObject2);
        } else {
          localObject2 = null;
        }
      }
      else {
        try
        {
          if ((paramBiApply != null) && (!paramBiApply.claim())) {
            return false;
          }
          Object localObject3 = localObject1;
          Object localObject4 = localObject2;
          completeValue(paramBiFunction.apply(localObject3, localObject4));
        }
        catch (Throwable localThrowable2)
        {
          completeThrowable(localThrowable2);
        }
      }
    }
    return true;
  }
  
  private <U, V> CompletableFuture<V> biApplyStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction)
  {
    CompletableFuture localCompletableFuture1;
    if ((paramBiFunction == null) || ((localCompletableFuture1 = paramCompletionStage.toCompletableFuture()) == null)) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture2 = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture2.biApply(this, localCompletableFuture1, paramBiFunction, null)))
    {
      BiApply localBiApply = new BiApply(paramExecutor, localCompletableFuture2, this, localCompletableFuture1, paramBiFunction);
      bipush(localCompletableFuture1, localBiApply);
      localBiApply.tryFire(0);
    }
    return localCompletableFuture2;
  }
  
  final <R, S> boolean biAccept(CompletableFuture<R> paramCompletableFuture, CompletableFuture<S> paramCompletableFuture1, BiConsumer<? super R, ? super S> paramBiConsumer, BiAccept<R, S> paramBiAccept)
  {
    Object localObject1;
    Object localObject2;
    if ((paramCompletableFuture == null) || ((localObject1 = result) == null) || (paramCompletableFuture1 == null) || ((localObject2 = result) == null) || (paramBiConsumer == null)) {
      return false;
    }
    if (result == null)
    {
      Throwable localThrowable1;
      if ((localObject1 instanceof AltResult))
      {
        if ((localThrowable1 = ex) != null) {
          completeThrowable(localThrowable1, localObject1);
        } else {
          localObject1 = null;
        }
      }
      else if ((localObject2 instanceof AltResult))
      {
        if ((localThrowable1 = ex) != null) {
          completeThrowable(localThrowable1, localObject2);
        } else {
          localObject2 = null;
        }
      }
      else {
        try
        {
          if ((paramBiAccept != null) && (!paramBiAccept.claim())) {
            return false;
          }
          Object localObject3 = localObject1;
          Object localObject4 = localObject2;
          paramBiConsumer.accept(localObject3, localObject4);
          completeNull();
        }
        catch (Throwable localThrowable2)
        {
          completeThrowable(localThrowable2);
        }
      }
    }
    return true;
  }
  
  private <U> CompletableFuture<Void> biAcceptStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer)
  {
    CompletableFuture localCompletableFuture1;
    if ((paramBiConsumer == null) || ((localCompletableFuture1 = paramCompletionStage.toCompletableFuture()) == null)) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture2 = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture2.biAccept(this, localCompletableFuture1, paramBiConsumer, null)))
    {
      BiAccept localBiAccept = new BiAccept(paramExecutor, localCompletableFuture2, this, localCompletableFuture1, paramBiConsumer);
      bipush(localCompletableFuture1, localBiAccept);
      localBiAccept.tryFire(0);
    }
    return localCompletableFuture2;
  }
  
  final boolean biRun(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2, Runnable paramRunnable, BiRun<?, ?> paramBiRun)
  {
    Object localObject1;
    Object localObject2;
    if ((paramCompletableFuture1 == null) || ((localObject1 = result) == null) || (paramCompletableFuture2 == null) || ((localObject2 = result) == null) || (paramRunnable == null)) {
      return false;
    }
    if (result == null)
    {
      Throwable localThrowable1;
      if (((localObject1 instanceof AltResult)) && ((localThrowable1 = ex) != null)) {
        completeThrowable(localThrowable1, localObject1);
      } else if (((localObject2 instanceof AltResult)) && ((localThrowable1 = ex) != null)) {
        completeThrowable(localThrowable1, localObject2);
      } else {
        try
        {
          if ((paramBiRun != null) && (!paramBiRun.claim())) {
            return false;
          }
          paramRunnable.run();
          completeNull();
        }
        catch (Throwable localThrowable2)
        {
          completeThrowable(localThrowable2);
        }
      }
    }
    return true;
  }
  
  private CompletableFuture<Void> biRunStage(Executor paramExecutor, CompletionStage<?> paramCompletionStage, Runnable paramRunnable)
  {
    CompletableFuture localCompletableFuture1;
    if ((paramRunnable == null) || ((localCompletableFuture1 = paramCompletionStage.toCompletableFuture()) == null)) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture2 = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture2.biRun(this, localCompletableFuture1, paramRunnable, null)))
    {
      BiRun localBiRun = new BiRun(paramExecutor, localCompletableFuture2, this, localCompletableFuture1, paramRunnable);
      bipush(localCompletableFuture1, localBiRun);
      localBiRun.tryFire(0);
    }
    return localCompletableFuture2;
  }
  
  boolean biRelay(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2)
  {
    Object localObject1;
    Object localObject2;
    if ((paramCompletableFuture1 == null) || ((localObject1 = result) == null) || (paramCompletableFuture2 == null) || ((localObject2 = result) == null)) {
      return false;
    }
    if (result == null)
    {
      Throwable localThrowable;
      if (((localObject1 instanceof AltResult)) && ((localThrowable = ex) != null)) {
        completeThrowable(localThrowable, localObject1);
      } else if (((localObject2 instanceof AltResult)) && ((localThrowable = ex) != null)) {
        completeThrowable(localThrowable, localObject2);
      } else {
        completeNull();
      }
    }
    return true;
  }
  
  static CompletableFuture<Void> andTree(CompletableFuture<?>[] paramArrayOfCompletableFuture, int paramInt1, int paramInt2)
  {
    CompletableFuture localCompletableFuture1 = new CompletableFuture();
    if (paramInt1 > paramInt2)
    {
      result = NIL;
    }
    else
    {
      int i = paramInt1 + paramInt2 >>> 1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if ((localCompletableFuture2 = paramInt1 == i ? paramArrayOfCompletableFuture[paramInt1] : andTree(paramArrayOfCompletableFuture, paramInt1, i)) != null)
      {
        if ((localCompletableFuture3 = paramInt2 == i + 1 ? paramArrayOfCompletableFuture[paramInt2] : paramInt1 == paramInt2 ? localCompletableFuture2 : andTree(paramArrayOfCompletableFuture, i + 1, paramInt2)) != null) {}
      }
      else {
        throw new NullPointerException();
      }
      if (!localCompletableFuture1.biRelay(localCompletableFuture2, localCompletableFuture3))
      {
        BiRelay localBiRelay = new BiRelay(localCompletableFuture1, localCompletableFuture2, localCompletableFuture3);
        localCompletableFuture2.bipush(localCompletableFuture3, localBiRelay);
        localBiRelay.tryFire(0);
      }
    }
    return localCompletableFuture1;
  }
  
  final void orpush(CompletableFuture<?> paramCompletableFuture, BiCompletion<?, ?, ?> paramBiCompletion)
  {
    if (paramBiCompletion != null) {
      while (((paramCompletableFuture == null) || (result == null)) && (result == null))
      {
        if (tryPushStack(paramBiCompletion))
        {
          if ((paramCompletableFuture == null) || (paramCompletableFuture == this) || (result != null)) {
            break;
          }
          CoCompletion localCoCompletion = new CoCompletion(paramBiCompletion);
          while ((result == null) && (result == null) && (!paramCompletableFuture.tryPushStack(localCoCompletion))) {
            lazySetNext(localCoCompletion, null);
          }
          break;
        }
        lazySetNext(paramBiCompletion, null);
      }
    }
  }
  
  final <R, S extends R> boolean orApply(CompletableFuture<R> paramCompletableFuture, CompletableFuture<S> paramCompletableFuture1, Function<? super R, ? extends T> paramFunction, OrApply<R, S, T> paramOrApply)
  {
    Object localObject1;
    if ((paramCompletableFuture == null) || (paramCompletableFuture1 == null) || (((localObject1 = result) == null) && ((localObject1 = result) == null)) || (paramFunction == null)) {
      return false;
    }
    if (result == null) {
      try
      {
        if ((paramOrApply != null) && (!paramOrApply.claim())) {
          return false;
        }
        if ((localObject1 instanceof AltResult))
        {
          Throwable localThrowable1;
          if ((localThrowable1 = ex) != null) {
            completeThrowable(localThrowable1, localObject1);
          } else {
            localObject1 = null;
          }
        }
        else
        {
          Object localObject2 = localObject1;
          completeValue(paramFunction.apply(localObject2));
        }
      }
      catch (Throwable localThrowable2)
      {
        completeThrowable(localThrowable2);
      }
    }
    return true;
  }
  
  private <U extends T, V> CompletableFuture<V> orApplyStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, Function<? super T, ? extends V> paramFunction)
  {
    CompletableFuture localCompletableFuture1;
    if ((paramFunction == null) || ((localCompletableFuture1 = paramCompletionStage.toCompletableFuture()) == null)) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture2 = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture2.orApply(this, localCompletableFuture1, paramFunction, null)))
    {
      OrApply localOrApply = new OrApply(paramExecutor, localCompletableFuture2, this, localCompletableFuture1, paramFunction);
      orpush(localCompletableFuture1, localOrApply);
      localOrApply.tryFire(0);
    }
    return localCompletableFuture2;
  }
  
  final <R, S extends R> boolean orAccept(CompletableFuture<R> paramCompletableFuture, CompletableFuture<S> paramCompletableFuture1, Consumer<? super R> paramConsumer, OrAccept<R, S> paramOrAccept)
  {
    Object localObject1;
    if ((paramCompletableFuture == null) || (paramCompletableFuture1 == null) || (((localObject1 = result) == null) && ((localObject1 = result) == null)) || (paramConsumer == null)) {
      return false;
    }
    if (result == null) {
      try
      {
        if ((paramOrAccept != null) && (!paramOrAccept.claim())) {
          return false;
        }
        if ((localObject1 instanceof AltResult))
        {
          Throwable localThrowable1;
          if ((localThrowable1 = ex) != null) {
            completeThrowable(localThrowable1, localObject1);
          } else {
            localObject1 = null;
          }
        }
        else
        {
          Object localObject2 = localObject1;
          paramConsumer.accept(localObject2);
          completeNull();
        }
      }
      catch (Throwable localThrowable2)
      {
        completeThrowable(localThrowable2);
      }
    }
    return true;
  }
  
  private <U extends T> CompletableFuture<Void> orAcceptStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, Consumer<? super T> paramConsumer)
  {
    CompletableFuture localCompletableFuture1;
    if ((paramConsumer == null) || ((localCompletableFuture1 = paramCompletionStage.toCompletableFuture()) == null)) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture2 = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture2.orAccept(this, localCompletableFuture1, paramConsumer, null)))
    {
      OrAccept localOrAccept = new OrAccept(paramExecutor, localCompletableFuture2, this, localCompletableFuture1, paramConsumer);
      orpush(localCompletableFuture1, localOrAccept);
      localOrAccept.tryFire(0);
    }
    return localCompletableFuture2;
  }
  
  final boolean orRun(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2, Runnable paramRunnable, OrRun<?, ?> paramOrRun)
  {
    Object localObject;
    if ((paramCompletableFuture1 == null) || (paramCompletableFuture2 == null) || (((localObject = result) == null) && ((localObject = result) == null)) || (paramRunnable == null)) {
      return false;
    }
    if (result == null) {
      try
      {
        if ((paramOrRun != null) && (!paramOrRun.claim())) {
          return false;
        }
        Throwable localThrowable1;
        if (((localObject instanceof AltResult)) && ((localThrowable1 = ex) != null))
        {
          completeThrowable(localThrowable1, localObject);
        }
        else
        {
          paramRunnable.run();
          completeNull();
        }
      }
      catch (Throwable localThrowable2)
      {
        completeThrowable(localThrowable2);
      }
    }
    return true;
  }
  
  private CompletableFuture<Void> orRunStage(Executor paramExecutor, CompletionStage<?> paramCompletionStage, Runnable paramRunnable)
  {
    CompletableFuture localCompletableFuture1;
    if ((paramRunnable == null) || ((localCompletableFuture1 = paramCompletionStage.toCompletableFuture()) == null)) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture2 = new CompletableFuture();
    if ((paramExecutor != null) || (!localCompletableFuture2.orRun(this, localCompletableFuture1, paramRunnable, null)))
    {
      OrRun localOrRun = new OrRun(paramExecutor, localCompletableFuture2, this, localCompletableFuture1, paramRunnable);
      orpush(localCompletableFuture1, localOrRun);
      localOrRun.tryFire(0);
    }
    return localCompletableFuture2;
  }
  
  final boolean orRelay(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2)
  {
    Object localObject;
    if ((paramCompletableFuture1 == null) || (paramCompletableFuture2 == null) || (((localObject = result) == null) && ((localObject = result) == null))) {
      return false;
    }
    if (result == null) {
      completeRelay(localObject);
    }
    return true;
  }
  
  static CompletableFuture<Object> orTree(CompletableFuture<?>[] paramArrayOfCompletableFuture, int paramInt1, int paramInt2)
  {
    CompletableFuture localCompletableFuture1 = new CompletableFuture();
    if (paramInt1 <= paramInt2)
    {
      int i = paramInt1 + paramInt2 >>> 1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if ((localCompletableFuture2 = paramInt1 == i ? paramArrayOfCompletableFuture[paramInt1] : orTree(paramArrayOfCompletableFuture, paramInt1, i)) != null)
      {
        if ((localCompletableFuture3 = paramInt2 == i + 1 ? paramArrayOfCompletableFuture[paramInt2] : paramInt1 == paramInt2 ? localCompletableFuture2 : orTree(paramArrayOfCompletableFuture, i + 1, paramInt2)) != null) {}
      }
      else {
        throw new NullPointerException();
      }
      if (!localCompletableFuture1.orRelay(localCompletableFuture2, localCompletableFuture3))
      {
        OrRelay localOrRelay = new OrRelay(localCompletableFuture1, localCompletableFuture2, localCompletableFuture3);
        localCompletableFuture2.orpush(localCompletableFuture3, localOrRelay);
        localOrRelay.tryFire(0);
      }
    }
    return localCompletableFuture1;
  }
  
  static <U> CompletableFuture<U> asyncSupplyStage(Executor paramExecutor, Supplier<U> paramSupplier)
  {
    if (paramSupplier == null) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture = new CompletableFuture();
    paramExecutor.execute(new AsyncSupply(localCompletableFuture, paramSupplier));
    return localCompletableFuture;
  }
  
  static CompletableFuture<Void> asyncRunStage(Executor paramExecutor, Runnable paramRunnable)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    CompletableFuture localCompletableFuture = new CompletableFuture();
    paramExecutor.execute(new AsyncRun(localCompletableFuture, paramRunnable));
    return localCompletableFuture;
  }
  
  private Object waitingGet(boolean paramBoolean)
  {
    Signaller localSignaller = null;
    boolean bool = false;
    int i = -1;
    Object localObject;
    while ((localObject = result) == null) {
      if (i < 0)
      {
        i = Runtime.getRuntime().availableProcessors() > 1 ? 256 : 0;
      }
      else if (i > 0)
      {
        if (ThreadLocalRandom.nextSecondarySeed() >= 0) {
          i--;
        }
      }
      else if (localSignaller == null)
      {
        localSignaller = new Signaller(paramBoolean, 0L, 0L);
      }
      else if (!bool)
      {
        bool = tryPushStack(localSignaller);
      }
      else
      {
        if ((paramBoolean) && (interruptControl < 0))
        {
          thread = null;
          cleanStack();
          return null;
        }
        if ((thread != null) && (result == null)) {
          try
          {
            ForkJoinPool.managedBlock(localSignaller);
          }
          catch (InterruptedException localInterruptedException)
          {
            interruptControl = -1;
          }
        }
      }
    }
    if (localSignaller != null)
    {
      thread = null;
      if (interruptControl < 0) {
        if (paramBoolean) {
          localObject = null;
        } else {
          Thread.currentThread().interrupt();
        }
      }
    }
    postComplete();
    return localObject;
  }
  
  private Object timedGet(long paramLong)
    throws TimeoutException
  {
    if (Thread.interrupted()) {
      return null;
    }
    if (paramLong <= 0L) {
      throw new TimeoutException();
    }
    long l = System.nanoTime() + paramLong;
    Signaller localSignaller = new Signaller(true, paramLong, l == 0L ? 1L : l);
    boolean bool = false;
    Object localObject;
    while ((localObject = result) == null) {
      if (!bool)
      {
        bool = tryPushStack(localSignaller);
      }
      else
      {
        if ((interruptControl < 0) || (nanos <= 0L))
        {
          thread = null;
          cleanStack();
          if (interruptControl < 0) {
            return null;
          }
          throw new TimeoutException();
        }
        if ((thread != null) && (result == null)) {
          try
          {
            ForkJoinPool.managedBlock(localSignaller);
          }
          catch (InterruptedException localInterruptedException)
          {
            interruptControl = -1;
          }
        }
      }
    }
    if (interruptControl < 0) {
      localObject = null;
    }
    thread = null;
    postComplete();
    return localObject;
  }
  
  public CompletableFuture() {}
  
  private CompletableFuture(Object paramObject)
  {
    result = paramObject;
  }
  
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> paramSupplier)
  {
    return asyncSupplyStage(asyncPool, paramSupplier);
  }
  
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> paramSupplier, Executor paramExecutor)
  {
    return asyncSupplyStage(screenExecutor(paramExecutor), paramSupplier);
  }
  
  public static CompletableFuture<Void> runAsync(Runnable paramRunnable)
  {
    return asyncRunStage(asyncPool, paramRunnable);
  }
  
  public static CompletableFuture<Void> runAsync(Runnable paramRunnable, Executor paramExecutor)
  {
    return asyncRunStage(screenExecutor(paramExecutor), paramRunnable);
  }
  
  public static <U> CompletableFuture<U> completedFuture(U paramU)
  {
    return new CompletableFuture(paramU == null ? NIL : paramU);
  }
  
  public boolean isDone()
  {
    return result != null;
  }
  
  public T get()
    throws InterruptedException, ExecutionException
  {
    Object localObject;
    return (T)reportGet((localObject = result) == null ? waitingGet(true) : localObject);
  }
  
  public T get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    long l = paramTimeUnit.toNanos(paramLong);
    Object localObject;
    return (T)reportGet((localObject = result) == null ? timedGet(l) : localObject);
  }
  
  public T join()
  {
    Object localObject;
    return (T)reportJoin((localObject = result) == null ? waitingGet(false) : localObject);
  }
  
  public T getNow(T paramT)
  {
    Object localObject;
    return (localObject = result) == null ? paramT : reportJoin(localObject);
  }
  
  public boolean complete(T paramT)
  {
    boolean bool = completeValue(paramT);
    postComplete();
    return bool;
  }
  
  public boolean completeExceptionally(Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      throw new NullPointerException();
    }
    boolean bool = internalComplete(new AltResult(paramThrowable));
    postComplete();
    return bool;
  }
  
  public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> paramFunction)
  {
    return uniApplyStage(null, paramFunction);
  }
  
  public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction)
  {
    return uniApplyStage(asyncPool, paramFunction);
  }
  
  public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction, Executor paramExecutor)
  {
    return uniApplyStage(screenExecutor(paramExecutor), paramFunction);
  }
  
  public CompletableFuture<Void> thenAccept(Consumer<? super T> paramConsumer)
  {
    return uniAcceptStage(null, paramConsumer);
  }
  
  public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> paramConsumer)
  {
    return uniAcceptStage(asyncPool, paramConsumer);
  }
  
  public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> paramConsumer, Executor paramExecutor)
  {
    return uniAcceptStage(screenExecutor(paramExecutor), paramConsumer);
  }
  
  public CompletableFuture<Void> thenRun(Runnable paramRunnable)
  {
    return uniRunStage(null, paramRunnable);
  }
  
  public CompletableFuture<Void> thenRunAsync(Runnable paramRunnable)
  {
    return uniRunStage(asyncPool, paramRunnable);
  }
  
  public CompletableFuture<Void> thenRunAsync(Runnable paramRunnable, Executor paramExecutor)
  {
    return uniRunStage(screenExecutor(paramExecutor), paramRunnable);
  }
  
  public <U, V> CompletableFuture<V> thenCombine(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction)
  {
    return biApplyStage(null, paramCompletionStage, paramBiFunction);
  }
  
  public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction)
  {
    return biApplyStage(asyncPool, paramCompletionStage, paramBiFunction);
  }
  
  public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction, Executor paramExecutor)
  {
    return biApplyStage(screenExecutor(paramExecutor), paramCompletionStage, paramBiFunction);
  }
  
  public <U> CompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer)
  {
    return biAcceptStage(null, paramCompletionStage, paramBiConsumer);
  }
  
  public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer)
  {
    return biAcceptStage(asyncPool, paramCompletionStage, paramBiConsumer);
  }
  
  public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer, Executor paramExecutor)
  {
    return biAcceptStage(screenExecutor(paramExecutor), paramCompletionStage, paramBiConsumer);
  }
  
  public CompletableFuture<Void> runAfterBoth(CompletionStage<?> paramCompletionStage, Runnable paramRunnable)
  {
    return biRunStage(null, paramCompletionStage, paramRunnable);
  }
  
  public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable)
  {
    return biRunStage(asyncPool, paramCompletionStage, paramRunnable);
  }
  
  public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor)
  {
    return biRunStage(screenExecutor(paramExecutor), paramCompletionStage, paramRunnable);
  }
  
  public <U> CompletableFuture<U> applyToEither(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction)
  {
    return orApplyStage(null, paramCompletionStage, paramFunction);
  }
  
  public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction)
  {
    return orApplyStage(asyncPool, paramCompletionStage, paramFunction);
  }
  
  public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction, Executor paramExecutor)
  {
    return orApplyStage(screenExecutor(paramExecutor), paramCompletionStage, paramFunction);
  }
  
  public CompletableFuture<Void> acceptEither(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer)
  {
    return orAcceptStage(null, paramCompletionStage, paramConsumer);
  }
  
  public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer)
  {
    return orAcceptStage(asyncPool, paramCompletionStage, paramConsumer);
  }
  
  public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer, Executor paramExecutor)
  {
    return orAcceptStage(screenExecutor(paramExecutor), paramCompletionStage, paramConsumer);
  }
  
  public CompletableFuture<Void> runAfterEither(CompletionStage<?> paramCompletionStage, Runnable paramRunnable)
  {
    return orRunStage(null, paramCompletionStage, paramRunnable);
  }
  
  public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable)
  {
    return orRunStage(asyncPool, paramCompletionStage, paramRunnable);
  }
  
  public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor)
  {
    return orRunStage(screenExecutor(paramExecutor), paramCompletionStage, paramRunnable);
  }
  
  public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> paramFunction)
  {
    return uniComposeStage(null, paramFunction);
  }
  
  public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction)
  {
    return uniComposeStage(asyncPool, paramFunction);
  }
  
  public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction, Executor paramExecutor)
  {
    return uniComposeStage(screenExecutor(paramExecutor), paramFunction);
  }
  
  public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> paramBiConsumer)
  {
    return uniWhenCompleteStage(null, paramBiConsumer);
  }
  
  public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer)
  {
    return uniWhenCompleteStage(asyncPool, paramBiConsumer);
  }
  
  public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer, Executor paramExecutor)
  {
    return uniWhenCompleteStage(screenExecutor(paramExecutor), paramBiConsumer);
  }
  
  public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> paramBiFunction)
  {
    return uniHandleStage(null, paramBiFunction);
  }
  
  public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction)
  {
    return uniHandleStage(asyncPool, paramBiFunction);
  }
  
  public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction, Executor paramExecutor)
  {
    return uniHandleStage(screenExecutor(paramExecutor), paramBiFunction);
  }
  
  public CompletableFuture<T> toCompletableFuture()
  {
    return this;
  }
  
  public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> paramFunction)
  {
    return uniExceptionallyStage(paramFunction);
  }
  
  public static CompletableFuture<Void> allOf(CompletableFuture<?>... paramVarArgs)
  {
    return andTree(paramVarArgs, 0, paramVarArgs.length - 1);
  }
  
  public static CompletableFuture<Object> anyOf(CompletableFuture<?>... paramVarArgs)
  {
    return orTree(paramVarArgs, 0, paramVarArgs.length - 1);
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    int i = (result == null) && (internalComplete(new AltResult(new CancellationException()))) ? 1 : 0;
    postComplete();
    return (i != 0) || (isCancelled());
  }
  
  public boolean isCancelled()
  {
    Object localObject;
    return (((localObject = result) instanceof AltResult)) && ((ex instanceof CancellationException));
  }
  
  public boolean isCompletedExceptionally()
  {
    Object localObject;
    return (((localObject = result) instanceof AltResult)) && (localObject != NIL);
  }
  
  public void obtrudeValue(T paramT)
  {
    result = (paramT == null ? NIL : paramT);
    postComplete();
  }
  
  public void obtrudeException(Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      throw new NullPointerException();
    }
    result = new AltResult(paramThrowable);
    postComplete();
  }
  
  public int getNumberOfDependents()
  {
    int i = 0;
    for (Completion localCompletion = stack; localCompletion != null; localCompletion = next) {
      i++;
    }
    return i;
  }
  
  public String toString()
  {
    Object localObject = result;
    int i;
    return super.toString() + (((localObject instanceof AltResult)) && (ex != null) ? "[Completed exceptionally]" : localObject == null ? "[Not completed, " + i + " dependents]" : (i = getNumberOfDependents()) == 0 ? "[Not completed]" : "[Completed normally]");
  }
  
  static
  {
    try
    {
      Unsafe localUnsafe;
      UNSAFE = localUnsafe = Unsafe.getUnsafe();
      Class localClass = CompletableFuture.class;
      RESULT = localUnsafe.objectFieldOffset(localClass.getDeclaredField("result"));
      STACK = localUnsafe.objectFieldOffset(localClass.getDeclaredField("stack"));
      NEXT = localUnsafe.objectFieldOffset(Completion.class.getDeclaredField("next"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class AltResult
  {
    final Throwable ex;
    
    AltResult(Throwable paramThrowable)
    {
      ex = paramThrowable;
    }
  }
  
  static final class AsyncRun
    extends ForkJoinTask<Void>
    implements Runnable, CompletableFuture.AsynchronousCompletionTask
  {
    CompletableFuture<Void> dep;
    Runnable fn;
    
    AsyncRun(CompletableFuture<Void> paramCompletableFuture, Runnable paramRunnable)
    {
      dep = paramCompletableFuture;
      fn = paramRunnable;
    }
    
    public final Void getRawResult()
    {
      return null;
    }
    
    public final void setRawResult(Void paramVoid) {}
    
    public final boolean exec()
    {
      run();
      return true;
    }
    
    public void run()
    {
      CompletableFuture localCompletableFuture;
      Runnable localRunnable;
      if (((localCompletableFuture = dep) != null) && ((localRunnable = fn) != null))
      {
        dep = null;
        fn = null;
        if (result == null) {
          try
          {
            localRunnable.run();
            localCompletableFuture.completeNull();
          }
          catch (Throwable localThrowable)
          {
            localCompletableFuture.completeThrowable(localThrowable);
          }
        }
        localCompletableFuture.postComplete();
      }
    }
  }
  
  static final class AsyncSupply<T>
    extends ForkJoinTask<Void>
    implements Runnable, CompletableFuture.AsynchronousCompletionTask
  {
    CompletableFuture<T> dep;
    Supplier<T> fn;
    
    AsyncSupply(CompletableFuture<T> paramCompletableFuture, Supplier<T> paramSupplier)
    {
      dep = paramCompletableFuture;
      fn = paramSupplier;
    }
    
    public final Void getRawResult()
    {
      return null;
    }
    
    public final void setRawResult(Void paramVoid) {}
    
    public final boolean exec()
    {
      run();
      return true;
    }
    
    public void run()
    {
      CompletableFuture localCompletableFuture;
      Supplier localSupplier;
      if (((localCompletableFuture = dep) != null) && ((localSupplier = fn) != null))
      {
        dep = null;
        fn = null;
        if (result == null) {
          try
          {
            localCompletableFuture.completeValue(localSupplier.get());
          }
          catch (Throwable localThrowable)
          {
            localCompletableFuture.completeThrowable(localThrowable);
          }
        }
        localCompletableFuture.postComplete();
      }
    }
  }
  
  public static abstract interface AsynchronousCompletionTask {}
  
  static final class BiAccept<T, U>
    extends CompletableFuture.BiCompletion<T, U, Void>
  {
    BiConsumer<? super T, ? super U> fn;
    
    BiAccept(Executor paramExecutor, CompletableFuture<Void> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<U> paramCompletableFuture2, BiConsumer<? super T, ? super U> paramBiConsumer)
    {
      super(paramCompletableFuture, paramCompletableFuture1, paramCompletableFuture2);
      fn = paramBiConsumer;
    }
    
    final CompletableFuture<Void> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.biAccept(localCompletableFuture2 = src, localCompletableFuture3 = snd, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      snd = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, localCompletableFuture3, paramInt);
    }
  }
  
  static final class BiApply<T, U, V>
    extends CompletableFuture.BiCompletion<T, U, V>
  {
    BiFunction<? super T, ? super U, ? extends V> fn;
    
    BiApply(Executor paramExecutor, CompletableFuture<V> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<U> paramCompletableFuture2, BiFunction<? super T, ? super U, ? extends V> paramBiFunction)
    {
      super(paramCompletableFuture, paramCompletableFuture1, paramCompletableFuture2);
      fn = paramBiFunction;
    }
    
    final CompletableFuture<V> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.biApply(localCompletableFuture2 = src, localCompletableFuture3 = snd, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      snd = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, localCompletableFuture3, paramInt);
    }
  }
  
  static abstract class BiCompletion<T, U, V>
    extends CompletableFuture.UniCompletion<T, V>
  {
    CompletableFuture<U> snd;
    
    BiCompletion(Executor paramExecutor, CompletableFuture<V> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<U> paramCompletableFuture2)
    {
      super(paramCompletableFuture, paramCompletableFuture1);
      snd = paramCompletableFuture2;
    }
  }
  
  static final class BiRelay<T, U>
    extends CompletableFuture.BiCompletion<T, U, Void>
  {
    BiRelay(CompletableFuture<Void> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<U> paramCompletableFuture2)
    {
      super(paramCompletableFuture, paramCompletableFuture1, paramCompletableFuture2);
    }
    
    final CompletableFuture<Void> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if (((localCompletableFuture1 = dep) == null) || (!localCompletableFuture1.biRelay(localCompletableFuture2 = src, localCompletableFuture3 = snd))) {
        return null;
      }
      src = null;
      snd = null;
      dep = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, localCompletableFuture3, paramInt);
    }
  }
  
  static final class BiRun<T, U>
    extends CompletableFuture.BiCompletion<T, U, Void>
  {
    Runnable fn;
    
    BiRun(Executor paramExecutor, CompletableFuture<Void> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<U> paramCompletableFuture2, Runnable paramRunnable)
    {
      super(paramCompletableFuture, paramCompletableFuture1, paramCompletableFuture2);
      fn = paramRunnable;
    }
    
    final CompletableFuture<Void> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.biRun(localCompletableFuture2 = src, localCompletableFuture3 = snd, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      snd = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, localCompletableFuture3, paramInt);
    }
  }
  
  static final class CoCompletion
    extends CompletableFuture.Completion
  {
    CompletableFuture.BiCompletion<?, ?, ?> base;
    
    CoCompletion(CompletableFuture.BiCompletion<?, ?, ?> paramBiCompletion)
    {
      base = paramBiCompletion;
    }
    
    final CompletableFuture<?> tryFire(int paramInt)
    {
      CompletableFuture.BiCompletion localBiCompletion;
      CompletableFuture localCompletableFuture;
      if (((localBiCompletion = base) == null) || ((localCompletableFuture = localBiCompletion.tryFire(paramInt)) == null)) {
        return null;
      }
      base = null;
      return localCompletableFuture;
    }
    
    final boolean isLive()
    {
      CompletableFuture.BiCompletion localBiCompletion;
      return ((localBiCompletion = base) != null) && (dep != null);
    }
  }
  
  static abstract class Completion
    extends ForkJoinTask<Void>
    implements Runnable, CompletableFuture.AsynchronousCompletionTask
  {
    volatile Completion next;
    
    Completion() {}
    
    abstract CompletableFuture<?> tryFire(int paramInt);
    
    abstract boolean isLive();
    
    public final void run()
    {
      tryFire(1);
    }
    
    public final boolean exec()
    {
      tryFire(1);
      return true;
    }
    
    public final Void getRawResult()
    {
      return null;
    }
    
    public final void setRawResult(Void paramVoid) {}
  }
  
  static final class OrAccept<T, U extends T>
    extends CompletableFuture.BiCompletion<T, U, Void>
  {
    Consumer<? super T> fn;
    
    OrAccept(Executor paramExecutor, CompletableFuture<Void> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<U> paramCompletableFuture2, Consumer<? super T> paramConsumer)
    {
      super(paramCompletableFuture, paramCompletableFuture1, paramCompletableFuture2);
      fn = paramConsumer;
    }
    
    final CompletableFuture<Void> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.orAccept(localCompletableFuture2 = src, localCompletableFuture3 = snd, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      snd = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, localCompletableFuture3, paramInt);
    }
  }
  
  static final class OrApply<T, U extends T, V>
    extends CompletableFuture.BiCompletion<T, U, V>
  {
    Function<? super T, ? extends V> fn;
    
    OrApply(Executor paramExecutor, CompletableFuture<V> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<U> paramCompletableFuture2, Function<? super T, ? extends V> paramFunction)
    {
      super(paramCompletableFuture, paramCompletableFuture1, paramCompletableFuture2);
      fn = paramFunction;
    }
    
    final CompletableFuture<V> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.orApply(localCompletableFuture2 = src, localCompletableFuture3 = snd, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      snd = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, localCompletableFuture3, paramInt);
    }
  }
  
  static final class OrRelay<T, U>
    extends CompletableFuture.BiCompletion<T, U, Object>
  {
    OrRelay(CompletableFuture<Object> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<U> paramCompletableFuture2)
    {
      super(paramCompletableFuture, paramCompletableFuture1, paramCompletableFuture2);
    }
    
    final CompletableFuture<Object> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if (((localCompletableFuture1 = dep) == null) || (!localCompletableFuture1.orRelay(localCompletableFuture2 = src, localCompletableFuture3 = snd))) {
        return null;
      }
      src = null;
      snd = null;
      dep = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, localCompletableFuture3, paramInt);
    }
  }
  
  static final class OrRun<T, U>
    extends CompletableFuture.BiCompletion<T, U, Void>
  {
    Runnable fn;
    
    OrRun(Executor paramExecutor, CompletableFuture<Void> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<U> paramCompletableFuture2, Runnable paramRunnable)
    {
      super(paramCompletableFuture, paramCompletableFuture1, paramCompletableFuture2);
      fn = paramRunnable;
    }
    
    final CompletableFuture<Void> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      CompletableFuture localCompletableFuture3;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.orRun(localCompletableFuture2 = src, localCompletableFuture3 = snd, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      snd = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, localCompletableFuture3, paramInt);
    }
  }
  
  static final class Signaller
    extends CompletableFuture.Completion
    implements ForkJoinPool.ManagedBlocker
  {
    long nanos;
    final long deadline;
    volatile int interruptControl = paramBoolean ? 1 : 0;
    volatile Thread thread = Thread.currentThread();
    
    Signaller(boolean paramBoolean, long paramLong1, long paramLong2)
    {
      nanos = paramLong1;
      deadline = paramLong2;
    }
    
    final CompletableFuture<?> tryFire(int paramInt)
    {
      Thread localThread;
      if ((localThread = thread) != null)
      {
        thread = null;
        LockSupport.unpark(localThread);
      }
      return null;
    }
    
    public boolean isReleasable()
    {
      if (thread == null) {
        return true;
      }
      if (Thread.interrupted())
      {
        int i = interruptControl;
        interruptControl = -1;
        if (i > 0) {
          return true;
        }
      }
      if ((deadline != 0L) && ((nanos <= 0L) || ((nanos = deadline - System.nanoTime()) <= 0L)))
      {
        thread = null;
        return true;
      }
      return false;
    }
    
    public boolean block()
    {
      if (isReleasable()) {
        return true;
      }
      if (deadline == 0L) {
        LockSupport.park(this);
      } else if (nanos > 0L) {
        LockSupport.parkNanos(this, nanos);
      }
      return isReleasable();
    }
    
    final boolean isLive()
    {
      return thread != null;
    }
  }
  
  static final class ThreadPerTaskExecutor
    implements Executor
  {
    ThreadPerTaskExecutor() {}
    
    public void execute(Runnable paramRunnable)
    {
      new Thread(paramRunnable).start();
    }
  }
  
  static final class UniAccept<T>
    extends CompletableFuture.UniCompletion<T, Void>
  {
    Consumer<? super T> fn;
    
    UniAccept(Executor paramExecutor, CompletableFuture<Void> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, Consumer<? super T> paramConsumer)
    {
      super(paramCompletableFuture, paramCompletableFuture1);
      fn = paramConsumer;
    }
    
    final CompletableFuture<Void> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.uniAccept(localCompletableFuture2 = src, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, paramInt);
    }
  }
  
  static final class UniApply<T, V>
    extends CompletableFuture.UniCompletion<T, V>
  {
    Function<? super T, ? extends V> fn;
    
    UniApply(Executor paramExecutor, CompletableFuture<V> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, Function<? super T, ? extends V> paramFunction)
    {
      super(paramCompletableFuture, paramCompletableFuture1);
      fn = paramFunction;
    }
    
    final CompletableFuture<V> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.uniApply(localCompletableFuture2 = src, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, paramInt);
    }
  }
  
  static abstract class UniCompletion<T, V>
    extends CompletableFuture.Completion
  {
    Executor executor;
    CompletableFuture<V> dep;
    CompletableFuture<T> src;
    
    UniCompletion(Executor paramExecutor, CompletableFuture<V> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1)
    {
      executor = paramExecutor;
      dep = paramCompletableFuture;
      src = paramCompletableFuture1;
    }
    
    final boolean claim()
    {
      Executor localExecutor = executor;
      if (compareAndSetForkJoinTaskTag((short)0, (short)1))
      {
        if (localExecutor == null) {
          return true;
        }
        executor = null;
        localExecutor.execute(this);
      }
      return false;
    }
    
    final boolean isLive()
    {
      return dep != null;
    }
  }
  
  static final class UniCompose<T, V>
    extends CompletableFuture.UniCompletion<T, V>
  {
    Function<? super T, ? extends CompletionStage<V>> fn;
    
    UniCompose(Executor paramExecutor, CompletableFuture<V> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, Function<? super T, ? extends CompletionStage<V>> paramFunction)
    {
      super(paramCompletableFuture, paramCompletableFuture1);
      fn = paramFunction;
    }
    
    final CompletableFuture<V> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.uniCompose(localCompletableFuture2 = src, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, paramInt);
    }
  }
  
  static final class UniExceptionally<T>
    extends CompletableFuture.UniCompletion<T, T>
  {
    Function<? super Throwable, ? extends T> fn;
    
    UniExceptionally(CompletableFuture<T> paramCompletableFuture1, CompletableFuture<T> paramCompletableFuture2, Function<? super Throwable, ? extends T> paramFunction)
    {
      super(paramCompletableFuture1, paramCompletableFuture2);
      fn = paramFunction;
    }
    
    final CompletableFuture<T> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      if (((localCompletableFuture1 = dep) == null) || (!localCompletableFuture1.uniExceptionally(localCompletableFuture2 = src, fn, this))) {
        return null;
      }
      dep = null;
      src = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, paramInt);
    }
  }
  
  static final class UniHandle<T, V>
    extends CompletableFuture.UniCompletion<T, V>
  {
    BiFunction<? super T, Throwable, ? extends V> fn;
    
    UniHandle(Executor paramExecutor, CompletableFuture<V> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, BiFunction<? super T, Throwable, ? extends V> paramBiFunction)
    {
      super(paramCompletableFuture, paramCompletableFuture1);
      fn = paramBiFunction;
    }
    
    final CompletableFuture<V> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.uniHandle(localCompletableFuture2 = src, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, paramInt);
    }
  }
  
  static final class UniRelay<T>
    extends CompletableFuture.UniCompletion<T, T>
  {
    UniRelay(CompletableFuture<T> paramCompletableFuture1, CompletableFuture<T> paramCompletableFuture2)
    {
      super(paramCompletableFuture1, paramCompletableFuture2);
    }
    
    final CompletableFuture<T> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      if (((localCompletableFuture1 = dep) == null) || (!localCompletableFuture1.uniRelay(localCompletableFuture2 = src))) {
        return null;
      }
      src = null;
      dep = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, paramInt);
    }
  }
  
  static final class UniRun<T>
    extends CompletableFuture.UniCompletion<T, Void>
  {
    Runnable fn;
    
    UniRun(Executor paramExecutor, CompletableFuture<Void> paramCompletableFuture, CompletableFuture<T> paramCompletableFuture1, Runnable paramRunnable)
    {
      super(paramCompletableFuture, paramCompletableFuture1);
      fn = paramRunnable;
    }
    
    final CompletableFuture<Void> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.uniRun(localCompletableFuture2 = src, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, paramInt);
    }
  }
  
  static final class UniWhenComplete<T>
    extends CompletableFuture.UniCompletion<T, T>
  {
    BiConsumer<? super T, ? super Throwable> fn;
    
    UniWhenComplete(Executor paramExecutor, CompletableFuture<T> paramCompletableFuture1, CompletableFuture<T> paramCompletableFuture2, BiConsumer<? super T, ? super Throwable> paramBiConsumer)
    {
      super(paramCompletableFuture1, paramCompletableFuture2);
      fn = paramBiConsumer;
    }
    
    final CompletableFuture<T> tryFire(int paramInt)
    {
      CompletableFuture localCompletableFuture1;
      CompletableFuture localCompletableFuture2;
      if ((localCompletableFuture1 = dep) != null)
      {
        if (localCompletableFuture1.uniWhenComplete(localCompletableFuture2 = src, fn, paramInt > 0 ? null : this)) {}
      }
      else {
        return null;
      }
      dep = null;
      src = null;
      fn = null;
      return localCompletableFuture1.postFire(localCompletableFuture2, paramInt);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\CompletableFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */