package java.nio;

public abstract class Buffer
{
  static final int SPLITERATOR_CHARACTERISTICS = 16464;
  private int mark = -1;
  private int position = 0;
  private int limit;
  private int capacity;
  long address;
  
  Buffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt4 < 0) {
      throw new IllegalArgumentException("Negative capacity: " + paramInt4);
    }
    capacity = paramInt4;
    limit(paramInt3);
    position(paramInt2);
    if (paramInt1 >= 0)
    {
      if (paramInt1 > paramInt2) {
        throw new IllegalArgumentException("mark > position: (" + paramInt1 + " > " + paramInt2 + ")");
      }
      mark = paramInt1;
    }
  }
  
  public final int capacity()
  {
    return capacity;
  }
  
  public final int position()
  {
    return position;
  }
  
  public final Buffer position(int paramInt)
  {
    if ((paramInt > limit) || (paramInt < 0)) {
      throw new IllegalArgumentException();
    }
    position = paramInt;
    if (mark > position) {
      mark = -1;
    }
    return this;
  }
  
  public final int limit()
  {
    return limit;
  }
  
  public final Buffer limit(int paramInt)
  {
    if ((paramInt > capacity) || (paramInt < 0)) {
      throw new IllegalArgumentException();
    }
    limit = paramInt;
    if (position > limit) {
      position = limit;
    }
    if (mark > limit) {
      mark = -1;
    }
    return this;
  }
  
  public final Buffer mark()
  {
    mark = position;
    return this;
  }
  
  public final Buffer reset()
  {
    int i = mark;
    if (i < 0) {
      throw new InvalidMarkException();
    }
    position = i;
    return this;
  }
  
  public final Buffer clear()
  {
    position = 0;
    limit = capacity;
    mark = -1;
    return this;
  }
  
  public final Buffer flip()
  {
    limit = position;
    position = 0;
    mark = -1;
    return this;
  }
  
  public final Buffer rewind()
  {
    position = 0;
    mark = -1;
    return this;
  }
  
  public final int remaining()
  {
    return limit - position;
  }
  
  public final boolean hasRemaining()
  {
    return position < limit;
  }
  
  public abstract boolean isReadOnly();
  
  public abstract boolean hasArray();
  
  public abstract Object array();
  
  public abstract int arrayOffset();
  
  public abstract boolean isDirect();
  
  final int nextGetIndex()
  {
    if (position >= limit) {
      throw new BufferUnderflowException();
    }
    return position++;
  }
  
  final int nextGetIndex(int paramInt)
  {
    if (limit - position < paramInt) {
      throw new BufferUnderflowException();
    }
    int i = position;
    position += paramInt;
    return i;
  }
  
  final int nextPutIndex()
  {
    if (position >= limit) {
      throw new BufferOverflowException();
    }
    return position++;
  }
  
  final int nextPutIndex(int paramInt)
  {
    if (limit - position < paramInt) {
      throw new BufferOverflowException();
    }
    int i = position;
    position += paramInt;
    return i;
  }
  
  final int checkIndex(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= limit)) {
      throw new IndexOutOfBoundsException();
    }
    return paramInt;
  }
  
  final int checkIndex(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt2 > limit - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    return paramInt1;
  }
  
  final int markValue()
  {
    return mark;
  }
  
  final void truncate()
  {
    mark = -1;
    position = 0;
    limit = 0;
    capacity = 0;
  }
  
  final void discardMark()
  {
    mark = -1;
  }
  
  static void checkBounds(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 | paramInt2 | paramInt1 + paramInt2 | paramInt3 - (paramInt1 + paramInt2)) < 0) {
      throw new IndexOutOfBoundsException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\Buffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */