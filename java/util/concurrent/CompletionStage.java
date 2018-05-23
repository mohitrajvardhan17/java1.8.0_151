package java.util.concurrent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract interface CompletionStage<T>
{
  public abstract <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> paramFunction);
  
  public abstract <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction);
  
  public abstract <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction, Executor paramExecutor);
  
  public abstract CompletionStage<Void> thenAccept(Consumer<? super T> paramConsumer);
  
  public abstract CompletionStage<Void> thenAcceptAsync(Consumer<? super T> paramConsumer);
  
  public abstract CompletionStage<Void> thenAcceptAsync(Consumer<? super T> paramConsumer, Executor paramExecutor);
  
  public abstract CompletionStage<Void> thenRun(Runnable paramRunnable);
  
  public abstract CompletionStage<Void> thenRunAsync(Runnable paramRunnable);
  
  public abstract CompletionStage<Void> thenRunAsync(Runnable paramRunnable, Executor paramExecutor);
  
  public abstract <U, V> CompletionStage<V> thenCombine(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction);
  
  public abstract <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction);
  
  public abstract <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction, Executor paramExecutor);
  
  public abstract <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer);
  
  public abstract <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer);
  
  public abstract <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer, Executor paramExecutor);
  
  public abstract CompletionStage<Void> runAfterBoth(CompletionStage<?> paramCompletionStage, Runnable paramRunnable);
  
  public abstract CompletionStage<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable);
  
  public abstract CompletionStage<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor);
  
  public abstract <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction);
  
  public abstract <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction);
  
  public abstract <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction, Executor paramExecutor);
  
  public abstract CompletionStage<Void> acceptEither(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer);
  
  public abstract CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer);
  
  public abstract CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer, Executor paramExecutor);
  
  public abstract CompletionStage<Void> runAfterEither(CompletionStage<?> paramCompletionStage, Runnable paramRunnable);
  
  public abstract CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable);
  
  public abstract CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor);
  
  public abstract <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> paramFunction);
  
  public abstract <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction);
  
  public abstract <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction, Executor paramExecutor);
  
  public abstract CompletionStage<T> exceptionally(Function<Throwable, ? extends T> paramFunction);
  
  public abstract CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> paramBiConsumer);
  
  public abstract CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer);
  
  public abstract CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer, Executor paramExecutor);
  
  public abstract <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> paramBiFunction);
  
  public abstract <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction);
  
  public abstract <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction, Executor paramExecutor);
  
  public abstract CompletableFuture<T> toCompletableFuture();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\CompletionStage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */