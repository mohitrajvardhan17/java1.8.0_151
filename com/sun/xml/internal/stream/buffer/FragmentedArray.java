package com.sun.xml.internal.stream.buffer;

final class FragmentedArray<T>
{
  private T _item;
  private FragmentedArray<T> _next;
  private FragmentedArray<T> _previous;
  
  FragmentedArray(T paramT)
  {
    this(paramT, null);
  }
  
  FragmentedArray(T paramT, FragmentedArray<T> paramFragmentedArray)
  {
    setArray(paramT);
    if (paramFragmentedArray != null)
    {
      _next = this;
      _previous = paramFragmentedArray;
    }
  }
  
  T getArray()
  {
    return (T)_item;
  }
  
  void setArray(T paramT)
  {
    assert (paramT.getClass().isArray());
    _item = paramT;
  }
  
  FragmentedArray<T> getNext()
  {
    return _next;
  }
  
  void setNext(FragmentedArray<T> paramFragmentedArray)
  {
    _next = paramFragmentedArray;
    if (paramFragmentedArray != null) {
      _previous = this;
    }
  }
  
  FragmentedArray<T> getPrevious()
  {
    return _previous;
  }
  
  void setPrevious(FragmentedArray<T> paramFragmentedArray)
  {
    _previous = paramFragmentedArray;
    if (paramFragmentedArray != null) {
      _next = this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\FragmentedArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */