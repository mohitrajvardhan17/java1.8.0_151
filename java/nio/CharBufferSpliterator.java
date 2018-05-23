package java.nio;

import java.util.Spliterator.OfInt;
import java.util.function.IntConsumer;

class CharBufferSpliterator
  implements Spliterator.OfInt
{
  private final CharBuffer buffer;
  private int index;
  private final int limit;
  
  CharBufferSpliterator(CharBuffer paramCharBuffer)
  {
    this(paramCharBuffer, paramCharBuffer.position(), paramCharBuffer.limit());
  }
  
  CharBufferSpliterator(CharBuffer paramCharBuffer, int paramInt1, int paramInt2)
  {
    assert (paramInt1 <= paramInt2);
    buffer = paramCharBuffer;
    index = (paramInt1 <= paramInt2 ? paramInt1 : paramInt2);
    limit = paramInt2;
  }
  
  public Spliterator.OfInt trySplit()
  {
    int i = index;
    int j = i + limit >>> 1;
    return i >= j ? null : new CharBufferSpliterator(buffer, i, index = j);
  }
  
  public void forEachRemaining(IntConsumer paramIntConsumer)
  {
    if (paramIntConsumer == null) {
      throw new NullPointerException();
    }
    CharBuffer localCharBuffer = buffer;
    int i = index;
    int j = limit;
    index = j;
    while (i < j) {
      paramIntConsumer.accept(localCharBuffer.getUnchecked(i++));
    }
  }
  
  public boolean tryAdvance(IntConsumer paramIntConsumer)
  {
    if (paramIntConsumer == null) {
      throw new NullPointerException();
    }
    if ((index >= 0) && (index < limit))
    {
      paramIntConsumer.accept(buffer.getUnchecked(index++));
      return true;
    }
    return false;
  }
  
  public long estimateSize()
  {
    return limit - index;
  }
  
  public int characteristics()
  {
    return 16464;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\CharBufferSpliterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */