package java.util;

class RandomAccessSubList<E>
  extends SubList<E>
  implements RandomAccess
{
  RandomAccessSubList(AbstractList<E> paramAbstractList, int paramInt1, int paramInt2)
  {
    super(paramAbstractList, paramInt1, paramInt2);
  }
  
  public List<E> subList(int paramInt1, int paramInt2)
  {
    return new RandomAccessSubList(this, paramInt1, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\RandomAccessSubList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */