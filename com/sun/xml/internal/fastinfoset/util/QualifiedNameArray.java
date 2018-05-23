package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;

public class QualifiedNameArray
  extends ValueArray
{
  public QualifiedName[] _array;
  private QualifiedNameArray _readOnlyArray;
  
  public QualifiedNameArray(int paramInt1, int paramInt2)
  {
    _array = new QualifiedName[paramInt1];
    _maximumCapacity = paramInt2;
  }
  
  public QualifiedNameArray()
  {
    this(10, Integer.MAX_VALUE);
  }
  
  public final void clear()
  {
    _size = _readOnlyArraySize;
  }
  
  public final QualifiedName[] getArray()
  {
    if (_array == null) {
      return null;
    }
    QualifiedName[] arrayOfQualifiedName = new QualifiedName[_array.length];
    System.arraycopy(_array, 0, arrayOfQualifiedName, 0, _array.length);
    return arrayOfQualifiedName;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean)
  {
    if (!(paramValueArray instanceof QualifiedNameArray)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray }));
    }
    setReadOnlyArray((QualifiedNameArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(QualifiedNameArray paramQualifiedNameArray, boolean paramBoolean)
  {
    if (paramQualifiedNameArray != null)
    {
      _readOnlyArray = paramQualifiedNameArray;
      _readOnlyArraySize = paramQualifiedNameArray.getSize();
      if (paramBoolean) {
        clear();
      }
      _array = getCompleteArray();
      _size = _readOnlyArraySize;
    }
  }
  
  public final QualifiedName[] getCompleteArray()
  {
    if (_readOnlyArray == null) {
      return getArray();
    }
    QualifiedName[] arrayOfQualifiedName1 = _readOnlyArray.getCompleteArray();
    QualifiedName[] arrayOfQualifiedName2 = new QualifiedName[_readOnlyArraySize + _array.length];
    System.arraycopy(arrayOfQualifiedName1, 0, arrayOfQualifiedName2, 0, _readOnlyArraySize);
    return arrayOfQualifiedName2;
  }
  
  public final QualifiedName getNext()
  {
    return _size == _array.length ? null : _array[_size];
  }
  
  public final void add(QualifiedName paramQualifiedName)
  {
    if (_size == _array.length) {
      resize();
    }
    _array[(_size++)] = paramQualifiedName;
  }
  
  protected final void resize()
  {
    if (_size == _maximumCapacity) {
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
    }
    int i = _size * 3 / 2 + 1;
    if (i > _maximumCapacity) {
      i = _maximumCapacity;
    }
    QualifiedName[] arrayOfQualifiedName = new QualifiedName[i];
    System.arraycopy(_array, 0, arrayOfQualifiedName, 0, _size);
    _array = arrayOfQualifiedName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\QualifiedNameArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */