package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PrefixArray
  extends ValueArray
{
  public static final int PREFIX_MAP_SIZE = 64;
  private int _initialCapacity;
  public String[] _array;
  private PrefixArray _readOnlyArray;
  private PrefixEntry[] _prefixMap = new PrefixEntry[64];
  private PrefixEntry _prefixPool;
  private NamespaceEntry _namespacePool;
  private NamespaceEntry[] _inScopeNamespaces;
  public int[] _currentInScope;
  public int _declarationId;
  
  public PrefixArray(int paramInt1, int paramInt2)
  {
    _initialCapacity = paramInt1;
    _maximumCapacity = paramInt2;
    _array = new String[paramInt1];
    _inScopeNamespaces = new NamespaceEntry[paramInt1 + 2];
    _currentInScope = new int[paramInt1 + 2];
    increaseNamespacePool(paramInt1);
    increasePrefixPool(paramInt1);
    initializeEntries();
  }
  
  public PrefixArray()
  {
    this(10, Integer.MAX_VALUE);
  }
  
  private final void initializeEntries()
  {
    _inScopeNamespaces[0] = _namespacePool;
    _namespacePool = _namespacePool.next;
    _inScopeNamespaces[0].next = null;
    _inScopeNamespaces[0].prefix = "";
    _inScopeNamespaces[0].namespaceName = "";
    _inScopeNamespaces[0].namespaceIndex = (_currentInScope[0] = 0);
    int i = KeyIntMap.indexFor(KeyIntMap.hashHash(_inScopeNamespaces[0].prefix.hashCode()), _prefixMap.length);
    _prefixMap[i] = _prefixPool;
    _prefixPool = _prefixPool.next;
    _prefixMap[i].next = null;
    _prefixMap[i].prefixId = 0;
    _inScopeNamespaces[1] = _namespacePool;
    _namespacePool = _namespacePool.next;
    _inScopeNamespaces[1].next = null;
    _inScopeNamespaces[1].prefix = "xml";
    _inScopeNamespaces[1].namespaceName = "http://www.w3.org/XML/1998/namespace";
    _inScopeNamespaces[1].namespaceIndex = (_currentInScope[1] = 1);
    i = KeyIntMap.indexFor(KeyIntMap.hashHash(_inScopeNamespaces[1].prefix.hashCode()), _prefixMap.length);
    if (_prefixMap[i] == null)
    {
      _prefixMap[i] = _prefixPool;
      _prefixPool = _prefixPool.next;
      _prefixMap[i].next = null;
    }
    else
    {
      PrefixEntry localPrefixEntry = _prefixMap[i];
      _prefixMap[i] = _prefixPool;
      _prefixPool = _prefixPool.next;
      _prefixMap[i].next = localPrefixEntry;
    }
    _prefixMap[i].prefixId = 1;
  }
  
  private final void increaseNamespacePool(int paramInt)
  {
    if (_namespacePool == null) {
      _namespacePool = new NamespaceEntry(null);
    }
    for (int i = 0; i < paramInt; i++)
    {
      NamespaceEntry localNamespaceEntry = new NamespaceEntry(null);
      next = _namespacePool;
      _namespacePool = localNamespaceEntry;
    }
  }
  
  private final void increasePrefixPool(int paramInt)
  {
    if (_prefixPool == null) {
      _prefixPool = new PrefixEntry(null);
    }
    for (int i = 0; i < paramInt; i++)
    {
      PrefixEntry localPrefixEntry = new PrefixEntry(null);
      next = _prefixPool;
      _prefixPool = localPrefixEntry;
    }
  }
  
  public int countNamespacePool()
  {
    int i = 0;
    for (NamespaceEntry localNamespaceEntry = _namespacePool; localNamespaceEntry != null; localNamespaceEntry = next) {
      i++;
    }
    return i;
  }
  
  public int countPrefixPool()
  {
    int i = 0;
    for (PrefixEntry localPrefixEntry = _prefixPool; localPrefixEntry != null; localPrefixEntry = next) {
      i++;
    }
    return i;
  }
  
  public final void clear()
  {
    for (int i = _readOnlyArraySize; i < _size; i++) {
      _array[i] = null;
    }
    _size = _readOnlyArraySize;
  }
  
  public final void clearCompletely()
  {
    _prefixPool = null;
    _namespacePool = null;
    for (int i = 0; i < _size + 2; i++)
    {
      _currentInScope[i] = 0;
      _inScopeNamespaces[i] = null;
    }
    for (i = 0; i < _prefixMap.length; i++) {
      _prefixMap[i] = null;
    }
    increaseNamespacePool(_initialCapacity);
    increasePrefixPool(_initialCapacity);
    initializeEntries();
    _declarationId = 0;
    clear();
  }
  
  public final String[] getArray()
  {
    if (_array == null) {
      return null;
    }
    String[] arrayOfString = new String[_array.length];
    System.arraycopy(_array, 0, arrayOfString, 0, _array.length);
    return arrayOfString;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean)
  {
    if (!(paramValueArray instanceof PrefixArray)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray }));
    }
    setReadOnlyArray((PrefixArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(PrefixArray paramPrefixArray, boolean paramBoolean)
  {
    if (paramPrefixArray != null)
    {
      _readOnlyArray = paramPrefixArray;
      _readOnlyArraySize = paramPrefixArray.getSize();
      clearCompletely();
      _inScopeNamespaces = new NamespaceEntry[_readOnlyArraySize + _inScopeNamespaces.length];
      _currentInScope = new int[_readOnlyArraySize + _currentInScope.length];
      initializeEntries();
      if (paramBoolean) {
        clear();
      }
      _array = getCompleteArray();
      _size = _readOnlyArraySize;
    }
  }
  
  public final String[] getCompleteArray()
  {
    if (_readOnlyArray == null) {
      return getArray();
    }
    String[] arrayOfString1 = _readOnlyArray.getCompleteArray();
    String[] arrayOfString2 = new String[_readOnlyArraySize + _array.length];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, _readOnlyArraySize);
    return arrayOfString2;
  }
  
  public final String get(int paramInt)
  {
    return _array[paramInt];
  }
  
  public final int add(String paramString)
  {
    if (_size == _array.length) {
      resize();
    }
    _array[(_size++)] = paramString;
    return _size;
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
    String[] arrayOfString = new String[i];
    System.arraycopy(_array, 0, arrayOfString, 0, _size);
    _array = arrayOfString;
    i += 2;
    NamespaceEntry[] arrayOfNamespaceEntry = new NamespaceEntry[i];
    System.arraycopy(_inScopeNamespaces, 0, arrayOfNamespaceEntry, 0, _inScopeNamespaces.length);
    _inScopeNamespaces = arrayOfNamespaceEntry;
    int[] arrayOfInt = new int[i];
    System.arraycopy(_currentInScope, 0, arrayOfInt, 0, _currentInScope.length);
    _currentInScope = arrayOfInt;
  }
  
  public final void clearDeclarationIds()
  {
    for (int i = 0; i < _size; i++)
    {
      NamespaceEntry localNamespaceEntry = _inScopeNamespaces[i];
      if (localNamespaceEntry != null) {
        declarationId = 0;
      }
    }
    _declarationId = 1;
  }
  
  public final void pushScope(int paramInt1, int paramInt2)
    throws FastInfosetException
  {
    if (_namespacePool == null) {
      increaseNamespacePool(16);
    }
    NamespaceEntry localNamespaceEntry1 = _namespacePool;
    _namespacePool = next;
    NamespaceEntry localNamespaceEntry2 = _inScopeNamespaces[(++paramInt1)];
    if (localNamespaceEntry2 == null)
    {
      declarationId = _declarationId;
      namespaceIndex = (_currentInScope[paramInt1] = ++paramInt2);
      next = null;
      _inScopeNamespaces[paramInt1] = localNamespaceEntry1;
    }
    else if (declarationId < _declarationId)
    {
      declarationId = _declarationId;
      namespaceIndex = (_currentInScope[paramInt1] = ++paramInt2);
      next = localNamespaceEntry2;
      declarationId = 0;
      _inScopeNamespaces[paramInt1] = localNamespaceEntry1;
    }
    else
    {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateNamespaceAttribute"));
    }
  }
  
  public final void pushScopeWithPrefixEntry(String paramString1, String paramString2, int paramInt1, int paramInt2)
    throws FastInfosetException
  {
    if (_namespacePool == null) {
      increaseNamespacePool(16);
    }
    if (_prefixPool == null) {
      increasePrefixPool(16);
    }
    NamespaceEntry localNamespaceEntry1 = _namespacePool;
    _namespacePool = next;
    NamespaceEntry localNamespaceEntry2 = _inScopeNamespaces[(++paramInt1)];
    if (localNamespaceEntry2 == null)
    {
      declarationId = _declarationId;
      namespaceIndex = (_currentInScope[paramInt1] = ++paramInt2);
      next = null;
      _inScopeNamespaces[paramInt1] = localNamespaceEntry1;
    }
    else if (declarationId < _declarationId)
    {
      declarationId = _declarationId;
      namespaceIndex = (_currentInScope[paramInt1] = ++paramInt2);
      next = localNamespaceEntry2;
      declarationId = 0;
      _inScopeNamespaces[paramInt1] = localNamespaceEntry1;
    }
    else
    {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateNamespaceAttribute"));
    }
    PrefixEntry localPrefixEntry1 = _prefixPool;
    _prefixPool = _prefixPool.next;
    prefixId = paramInt1;
    prefix = paramString1;
    namespaceName = paramString2;
    prefixEntryIndex = KeyIntMap.indexFor(KeyIntMap.hashHash(paramString1.hashCode()), _prefixMap.length);
    PrefixEntry localPrefixEntry2 = _prefixMap[prefixEntryIndex];
    next = localPrefixEntry2;
    _prefixMap[prefixEntryIndex] = localPrefixEntry1;
  }
  
  public final void popScope(int paramInt)
  {
    NamespaceEntry localNamespaceEntry = _inScopeNamespaces[(++paramInt)];
    _inScopeNamespaces[paramInt] = next;
    _currentInScope[paramInt] = (next != null ? access$000namespaceIndex : 0);
    next = _namespacePool;
    _namespacePool = localNamespaceEntry;
  }
  
  public final void popScopeWithPrefixEntry(int paramInt)
  {
    NamespaceEntry localNamespaceEntry = _inScopeNamespaces[(++paramInt)];
    _inScopeNamespaces[paramInt] = next;
    _currentInScope[paramInt] = (next != null ? access$000namespaceIndex : 0);
    prefix = NamespaceEntry.access$202(localNamespaceEntry, null);
    next = _namespacePool;
    _namespacePool = localNamespaceEntry;
    PrefixEntry localPrefixEntry1 = _prefixMap[prefixEntryIndex];
    if (prefixId == paramInt)
    {
      _prefixMap[prefixEntryIndex] = next;
      next = _prefixPool;
      _prefixPool = localPrefixEntry1;
    }
    else
    {
      PrefixEntry localPrefixEntry2 = localPrefixEntry1;
      for (localPrefixEntry1 = next; localPrefixEntry1 != null; localPrefixEntry1 = next)
      {
        if (prefixId == paramInt)
        {
          next = next;
          next = _prefixPool;
          _prefixPool = localPrefixEntry1;
          break;
        }
        localPrefixEntry2 = localPrefixEntry1;
      }
    }
  }
  
  public final String getNamespaceFromPrefix(String paramString)
  {
    int i = KeyIntMap.indexFor(KeyIntMap.hashHash(paramString.hashCode()), _prefixMap.length);
    for (PrefixEntry localPrefixEntry = _prefixMap[i]; localPrefixEntry != null; localPrefixEntry = next)
    {
      NamespaceEntry localNamespaceEntry = _inScopeNamespaces[prefixId];
      if ((paramString == prefix) || (paramString.equals(prefix))) {
        return namespaceName;
      }
    }
    return null;
  }
  
  public final String getPrefixFromNamespace(String paramString)
  {
    int i = 0;
    for (;;)
    {
      i++;
      if (i >= _size + 2) {
        break;
      }
      NamespaceEntry localNamespaceEntry = _inScopeNamespaces[i];
      if ((localNamespaceEntry != null) && (paramString.equals(namespaceName))) {
        return prefix;
      }
    }
    return null;
  }
  
  public final Iterator getPrefixes()
  {
    new Iterator()
    {
      int _position = 1;
      PrefixArray.NamespaceEntry _ne = _inScopeNamespaces[_position];
      
      public boolean hasNext()
      {
        return _ne != null;
      }
      
      public Object next()
      {
        if (_position == _size + 2) {
          throw new NoSuchElementException();
        }
        String str = PrefixArray.NamespaceEntry.access$100(_ne);
        moveToNext();
        return str;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
      
      private final void moveToNext()
      {
        while (++_position < _size + 2)
        {
          _ne = _inScopeNamespaces[_position];
          if (_ne != null) {
            return;
          }
        }
        _ne = null;
      }
    };
  }
  
  public final Iterator getPrefixesFromNamespace(final String paramString)
  {
    new Iterator()
    {
      String _namespaceName = paramString;
      int _position = 0;
      PrefixArray.NamespaceEntry _ne;
      
      public boolean hasNext()
      {
        return _ne != null;
      }
      
      public Object next()
      {
        if (_position == _size + 2) {
          throw new NoSuchElementException();
        }
        String str = PrefixArray.NamespaceEntry.access$100(_ne);
        moveToNext();
        return str;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
      
      private final void moveToNext()
      {
        while (++_position < _size + 2)
        {
          _ne = _inScopeNamespaces[_position];
          if ((_ne != null) && (_namespaceName.equals(PrefixArray.NamespaceEntry.access$200(_ne)))) {
            return;
          }
        }
        _ne = null;
      }
    };
  }
  
  private static class NamespaceEntry
  {
    private NamespaceEntry next;
    private int declarationId;
    private int namespaceIndex;
    private String prefix;
    private String namespaceName;
    private int prefixEntryIndex;
    
    private NamespaceEntry() {}
  }
  
  private static class PrefixEntry
  {
    private PrefixEntry next;
    private int prefixId;
    
    private PrefixEntry() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\PrefixArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */