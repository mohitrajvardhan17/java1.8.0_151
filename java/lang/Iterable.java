package java.lang;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public abstract interface Iterable<T>
{
  public abstract Iterator<T> iterator();
  
  public void forEach(Consumer<? super T> paramConsumer)
  {
    Objects.requireNonNull(paramConsumer);
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      paramConsumer.accept(localObject);
    }
  }
  
  public Spliterator<T> spliterator()
  {
    return Spliterators.spliteratorUnknownSize(iterator(), 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Iterable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */