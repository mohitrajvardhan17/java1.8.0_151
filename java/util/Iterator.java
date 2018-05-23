package java.util;

import java.util.function.Consumer;

public abstract interface Iterator<E>
{
  public abstract boolean hasNext();
  
  public abstract E next();
  
  public void remove()
  {
    throw new UnsupportedOperationException("remove");
  }
  
  public void forEachRemaining(Consumer<? super E> paramConsumer)
  {
    Objects.requireNonNull(paramConsumer);
    while (hasNext()) {
      paramConsumer.accept(next());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Iterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */