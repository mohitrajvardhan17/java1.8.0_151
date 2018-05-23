package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import sun.misc.Contended;
import sun.misc.Unsafe;

public class ConcurrentHashMap<K, V>
  extends AbstractMap<K, V>
  implements ConcurrentMap<K, V>, Serializable
{
  private static final long serialVersionUID = 7249069246763182397L;
  private static final int MAXIMUM_CAPACITY = 1073741824;
  private static final int DEFAULT_CAPACITY = 16;
  static final int MAX_ARRAY_SIZE = 2147483639;
  private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
  private static final float LOAD_FACTOR = 0.75F;
  static final int TREEIFY_THRESHOLD = 8;
  static final int UNTREEIFY_THRESHOLD = 6;
  static final int MIN_TREEIFY_CAPACITY = 64;
  private static final int MIN_TRANSFER_STRIDE = 16;
  private static int RESIZE_STAMP_BITS = 16;
  private static final int MAX_RESIZERS = (1 << 32 - RESIZE_STAMP_BITS) - 1;
  private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
  static final int MOVED = -1;
  static final int TREEBIN = -2;
  static final int RESERVED = -3;
  static final int HASH_BITS = Integer.MAX_VALUE;
  static final int NCPU = Runtime.getRuntime().availableProcessors();
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("segments", Segment[].class), new ObjectStreamField("segmentMask", Integer.TYPE), new ObjectStreamField("segmentShift", Integer.TYPE) };
  volatile transient Node<K, V>[] table;
  private volatile transient Node<K, V>[] nextTable;
  private volatile transient long baseCount;
  private volatile transient int sizeCtl;
  private volatile transient int transferIndex;
  private volatile transient int cellsBusy;
  private volatile transient CounterCell[] counterCells;
  private transient KeySetView<K, V> keySet;
  private transient ValuesView<K, V> values;
  private transient EntrySetView<K, V> entrySet;
  private static final Unsafe U;
  private static final long SIZECTL;
  private static final long TRANSFERINDEX;
  private static final long BASECOUNT;
  private static final long CELLSBUSY;
  private static final long CELLVALUE;
  private static final long ABASE;
  private static final int ASHIFT;
  
  static final int spread(int paramInt)
  {
    return (paramInt ^ paramInt >>> 16) & 0x7FFFFFFF;
  }
  
  private static final int tableSizeFor(int paramInt)
  {
    int i = paramInt - 1;
    i |= i >>> 1;
    i |= i >>> 2;
    i |= i >>> 4;
    i |= i >>> 8;
    i |= i >>> 16;
    return i >= 1073741824 ? 1073741824 : i < 0 ? 1 : i + 1;
  }
  
  static Class<?> comparableClassFor(Object paramObject)
  {
    if ((paramObject instanceof Comparable))
    {
      Class localClass;
      if ((localClass = paramObject.getClass()) == String.class) {
        return localClass;
      }
      Type[] arrayOfType1;
      if ((arrayOfType1 = localClass.getGenericInterfaces()) != null) {
        for (int i = 0; i < arrayOfType1.length; i++)
        {
          Type localType;
          ParameterizedType localParameterizedType;
          Type[] arrayOfType2;
          if ((((localType = arrayOfType1[i]) instanceof ParameterizedType)) && ((localParameterizedType = (ParameterizedType)localType).getRawType() == Comparable.class) && ((arrayOfType2 = localParameterizedType.getActualTypeArguments()) != null) && (arrayOfType2.length == 1) && (arrayOfType2[0] == localClass)) {
            return localClass;
          }
        }
      }
    }
    return null;
  }
  
  static int compareComparables(Class<?> paramClass, Object paramObject1, Object paramObject2)
  {
    return (paramObject2 == null) || (paramObject2.getClass() != paramClass) ? 0 : ((Comparable)paramObject1).compareTo(paramObject2);
  }
  
  static final <K, V> Node<K, V> tabAt(Node<K, V>[] paramArrayOfNode, int paramInt)
  {
    return (Node)U.getObjectVolatile(paramArrayOfNode, (paramInt << ASHIFT) + ABASE);
  }
  
  static final <K, V> boolean casTabAt(Node<K, V>[] paramArrayOfNode, int paramInt, Node<K, V> paramNode1, Node<K, V> paramNode2)
  {
    return U.compareAndSwapObject(paramArrayOfNode, (paramInt << ASHIFT) + ABASE, paramNode1, paramNode2);
  }
  
  static final <K, V> void setTabAt(Node<K, V>[] paramArrayOfNode, int paramInt, Node<K, V> paramNode)
  {
    U.putObjectVolatile(paramArrayOfNode, (paramInt << ASHIFT) + ABASE, paramNode);
  }
  
  public ConcurrentHashMap() {}
  
  public ConcurrentHashMap(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    int i = paramInt >= 536870912 ? 1073741824 : tableSizeFor(paramInt + (paramInt >>> 1) + 1);
    sizeCtl = i;
  }
  
  public ConcurrentHashMap(Map<? extends K, ? extends V> paramMap)
  {
    sizeCtl = 16;
    putAll(paramMap);
  }
  
  public ConcurrentHashMap(int paramInt, float paramFloat)
  {
    this(paramInt, paramFloat, 1);
  }
  
  public ConcurrentHashMap(int paramInt1, float paramFloat, int paramInt2)
  {
    if ((paramFloat <= 0.0F) || (paramInt1 < 0) || (paramInt2 <= 0)) {
      throw new IllegalArgumentException();
    }
    if (paramInt1 < paramInt2) {
      paramInt1 = paramInt2;
    }
    long l = (1.0D + (float)paramInt1 / paramFloat);
    int i = l >= 1073741824L ? 1073741824 : tableSizeFor((int)l);
    sizeCtl = i;
  }
  
  public int size()
  {
    long l = sumCount();
    return l > 2147483647L ? Integer.MAX_VALUE : l < 0L ? 0 : (int)l;
  }
  
  public boolean isEmpty()
  {
    return sumCount() <= 0L;
  }
  
  public V get(Object paramObject)
  {
    int k = spread(paramObject.hashCode());
    Node[] arrayOfNode;
    int i;
    Node localNode1;
    if (((arrayOfNode = table) != null) && ((i = arrayOfNode.length) > 0) && ((localNode1 = tabAt(arrayOfNode, i - 1 & k)) != null))
    {
      int j;
      Object localObject;
      if ((j = hash) == k)
      {
        if (((localObject = key) == paramObject) || ((localObject != null) && (paramObject.equals(localObject)))) {
          return (V)val;
        }
      }
      else if (j < 0)
      {
        Node localNode2;
        return (V)((localNode2 = localNode1.find(k, paramObject)) != null ? val : null);
      }
      while ((localNode1 = next) != null) {
        if ((hash == k) && (((localObject = key) == paramObject) || ((localObject != null) && (paramObject.equals(localObject))))) {
          return (V)val;
        }
      }
    }
    return null;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return get(paramObject) != null;
  }
  
  public boolean containsValue(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    Node[] arrayOfNode;
    if ((arrayOfNode = table) != null)
    {
      Traverser localTraverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node localNode;
      while ((localNode = localTraverser.advance()) != null)
      {
        Object localObject;
        if (((localObject = val) == paramObject) || ((localObject != null) && (paramObject.equals(localObject)))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public V put(K paramK, V paramV)
  {
    return (V)putVal(paramK, paramV, false);
  }
  
  final V putVal(K paramK, V paramV, boolean paramBoolean)
  {
    if ((paramK == null) || (paramV == null)) {
      throw new NullPointerException();
    }
    int i = spread(paramK.hashCode());
    int j = 0;
    Node[] arrayOfNode = table;
    for (;;)
    {
      int k;
      if ((arrayOfNode == null) || ((k = arrayOfNode.length) == 0))
      {
        arrayOfNode = initTable();
      }
      else
      {
        int m;
        Node localNode;
        if ((localNode = tabAt(arrayOfNode, m = k - 1 & i)) == null)
        {
          if (casTabAt(arrayOfNode, m, null, new Node(i, paramK, paramV, null))) {
            break;
          }
        }
        else
        {
          int n;
          if ((n = hash) == -1)
          {
            arrayOfNode = helpTransfer(arrayOfNode, localNode);
          }
          else
          {
            Object localObject1 = null;
            synchronized (localNode)
            {
              if (tabAt(arrayOfNode, m) == localNode)
              {
                Object localObject2;
                if (n >= 0)
                {
                  j = 1;
                  localObject2 = localNode;
                  for (;;)
                  {
                    Object localObject3;
                    if ((hash == i) && (((localObject3 = key) == paramK) || ((localObject3 != null) && (paramK.equals(localObject3)))))
                    {
                      localObject1 = val;
                      if (paramBoolean) {
                        break;
                      }
                      val = paramV;
                      break;
                    }
                    Object localObject4 = localObject2;
                    if ((localObject2 = next) == null)
                    {
                      next = new Node(i, paramK, paramV, null);
                      break;
                    }
                    j++;
                  }
                }
                else if ((localNode instanceof TreeBin))
                {
                  j = 2;
                  if ((localObject2 = ((TreeBin)localNode).putTreeVal(i, paramK, paramV)) != null)
                  {
                    localObject1 = val;
                    if (!paramBoolean) {
                      val = paramV;
                    }
                  }
                }
              }
            }
            if (j != 0)
            {
              if (j >= 8) {
                treeifyBin(arrayOfNode, m);
              }
              if (localObject1 == null) {
                break;
              }
              return (V)localObject1;
            }
          }
        }
      }
    }
    addCount(1L, j);
    return null;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    tryPresize(paramMap.size());
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      putVal(localEntry.getKey(), localEntry.getValue(), false);
    }
  }
  
  public V remove(Object paramObject)
  {
    return (V)replaceNode(paramObject, null, null);
  }
  
  final V replaceNode(Object paramObject1, V paramV, Object paramObject2)
  {
    int i = spread(paramObject1.hashCode());
    Node[] arrayOfNode = table;
    int j;
    int k;
    Node localNode;
    while ((arrayOfNode != null) && ((j = arrayOfNode.length) != 0) && ((localNode = tabAt(arrayOfNode, k = j - 1 & i)) != null))
    {
      int m;
      if ((m = hash) == -1)
      {
        arrayOfNode = helpTransfer(arrayOfNode, localNode);
      }
      else
      {
        Object localObject1 = null;
        int n = 0;
        synchronized (localNode)
        {
          if (tabAt(arrayOfNode, k) == localNode)
          {
            Object localObject2;
            Object localObject3;
            Object localObject4;
            Object localObject5;
            if (m >= 0)
            {
              n = 1;
              localObject2 = localNode;
              localObject3 = null;
              for (;;)
              {
                if ((hash == i) && (((localObject4 = key) == paramObject1) || ((localObject4 != null) && (paramObject1.equals(localObject4)))))
                {
                  localObject5 = val;
                  if ((paramObject2 == null) || (paramObject2 == localObject5) || ((localObject5 != null) && (paramObject2.equals(localObject5))))
                  {
                    localObject1 = localObject5;
                    if (paramV != null) {
                      val = paramV;
                    } else if (localObject3 != null) {
                      next = next;
                    } else {
                      setTabAt(arrayOfNode, k, next);
                    }
                  }
                }
                else
                {
                  localObject3 = localObject2;
                  if ((localObject2 = next) == null) {
                    break;
                  }
                }
              }
            }
            else if ((localNode instanceof TreeBin))
            {
              n = 1;
              localObject2 = (TreeBin)localNode;
              if (((localObject3 = root) != null) && ((localObject4 = ((TreeNode)localObject3).findTreeNode(i, paramObject1, null)) != null))
              {
                localObject5 = val;
                if ((paramObject2 == null) || (paramObject2 == localObject5) || ((localObject5 != null) && (paramObject2.equals(localObject5))))
                {
                  localObject1 = localObject5;
                  if (paramV != null) {
                    val = paramV;
                  } else if (((TreeBin)localObject2).removeTreeNode((TreeNode)localObject4)) {
                    setTabAt(arrayOfNode, k, untreeify(first));
                  }
                }
              }
            }
          }
        }
        if (n != 0)
        {
          if (localObject1 == null) {
            break;
          }
          if (paramV == null) {
            addCount(-1L, -1);
          }
          return (V)localObject1;
        }
      }
    }
    return null;
  }
  
  public void clear()
  {
    long l = 0L;
    int i = 0;
    Node[] arrayOfNode = table;
    while ((arrayOfNode != null) && (i < arrayOfNode.length))
    {
      Node localNode1 = tabAt(arrayOfNode, i);
      if (localNode1 == null)
      {
        i++;
      }
      else
      {
        int j;
        if ((j = hash) == -1)
        {
          arrayOfNode = helpTransfer(arrayOfNode, localNode1);
          i = 0;
        }
        else
        {
          synchronized (localNode1)
          {
            if (tabAt(arrayOfNode, i) == localNode1)
            {
              for (Node localNode2 = (localNode1 instanceof TreeBin) ? first : j >= 0 ? localNode1 : null; localNode2 != null; localNode2 = next) {
                l -= 1L;
              }
              setTabAt(arrayOfNode, i++, null);
            }
          }
        }
      }
    }
    if (l != 0L) {
      addCount(l, -1);
    }
  }
  
  public KeySetView<K, V> keySet()
  {
    KeySetView localKeySetView;
    return (localKeySetView = keySet) != null ? localKeySetView : (keySet = new KeySetView(this, null));
  }
  
  public Collection<V> values()
  {
    ValuesView localValuesView;
    return (localValuesView = values) != null ? localValuesView : (values = new ValuesView(this));
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    EntrySetView localEntrySetView;
    return (localEntrySetView = entrySet) != null ? localEntrySetView : (entrySet = new EntrySetView(this));
  }
  
  public int hashCode()
  {
    int i = 0;
    Node[] arrayOfNode;
    if ((arrayOfNode = table) != null)
    {
      Traverser localTraverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node localNode;
      while ((localNode = localTraverser.advance()) != null) {
        i += (key.hashCode() ^ val.hashCode());
      }
    }
    return i;
  }
  
  public String toString()
  {
    Node[] arrayOfNode;
    int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
    Traverser localTraverser = new Traverser(arrayOfNode, i, 0, i);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('{');
    Node localNode;
    if ((localNode = localTraverser.advance()) != null) {
      for (;;)
      {
        Object localObject1 = key;
        Object localObject2 = val;
        localStringBuilder.append(localObject1 == this ? "(this Map)" : localObject1);
        localStringBuilder.append('=');
        localStringBuilder.append(localObject2 == this ? "(this Map)" : localObject2);
        if ((localNode = localTraverser.advance()) == null) {
          break;
        }
        localStringBuilder.append(',').append(' ');
      }
    }
    return '}';
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject != this)
    {
      if (!(paramObject instanceof Map)) {
        return false;
      }
      Map localMap = (Map)paramObject;
      Node[] arrayOfNode;
      int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
      Traverser localTraverser = new Traverser(arrayOfNode, i, 0, i);
      Object localObject2;
      Object localObject3;
      while ((localObject1 = localTraverser.advance()) != null)
      {
        localObject2 = val;
        localObject3 = localMap.get(key);
        if ((localObject3 == null) || ((localObject3 != localObject2) && (!localObject3.equals(localObject2)))) {
          return false;
        }
      }
      Object localObject1 = localMap.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        Object localObject4;
        Object localObject5;
        if (((localObject3 = ((Map.Entry)localObject2).getKey()) == null) || ((localObject4 = ((Map.Entry)localObject2).getValue()) == null) || ((localObject5 = get(localObject3)) == null) || ((localObject4 != localObject5) && (!localObject4.equals(localObject5)))) {
          return false;
        }
      }
    }
    return true;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    int i = 0;
    int j = 1;
    while (j < 16)
    {
      i++;
      j <<= 1;
    }
    int k = 32 - i;
    int m = j - 1;
    Segment[] arrayOfSegment = (Segment[])new Segment[16];
    for (int n = 0; n < arrayOfSegment.length; n++) {
      arrayOfSegment[n] = new Segment(0.75F);
    }
    paramObjectOutputStream.putFields().put("segments", arrayOfSegment);
    paramObjectOutputStream.putFields().put("segmentShift", k);
    paramObjectOutputStream.putFields().put("segmentMask", m);
    paramObjectOutputStream.writeFields();
    Node[] arrayOfNode;
    if ((arrayOfNode = table) != null)
    {
      Traverser localTraverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node localNode;
      while ((localNode = localTraverser.advance()) != null)
      {
        paramObjectOutputStream.writeObject(key);
        paramObjectOutputStream.writeObject(val);
      }
    }
    paramObjectOutputStream.writeObject(null);
    paramObjectOutputStream.writeObject(null);
    arrayOfSegment = null;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    sizeCtl = -1;
    paramObjectInputStream.defaultReadObject();
    long l1 = 0L;
    Object localObject1 = null;
    for (;;)
    {
      Object localObject2 = paramObjectInputStream.readObject();
      Object localObject3 = paramObjectInputStream.readObject();
      if ((localObject2 == null) || (localObject3 == null)) {
        break;
      }
      localObject1 = new Node(spread(localObject2.hashCode()), localObject2, localObject3, (Node)localObject1);
      l1 += 1L;
    }
    if (l1 == 0L)
    {
      sizeCtl = 0;
    }
    else
    {
      int i;
      if (l1 >= 536870912L)
      {
        i = 1073741824;
      }
      else
      {
        int j = (int)l1;
        i = tableSizeFor(j + (j >>> 1) + 1);
      }
      Node[] arrayOfNode = (Node[])new Node[i];
      int k = i - 1;
      long l2 = 0L;
      while (localObject1 != null)
      {
        Node localNode1 = next;
        int n = hash;
        int i1 = n & k;
        Node localNode2;
        int m;
        if ((localNode2 = tabAt(arrayOfNode, i1)) == null)
        {
          m = 1;
        }
        else
        {
          Object localObject4 = key;
          if (hash < 0)
          {
            TreeBin localTreeBin = (TreeBin)localNode2;
            if (localTreeBin.putTreeVal(n, localObject4, val) == null) {
              l2 += 1L;
            }
            m = 0;
          }
          else
          {
            int i2 = 0;
            m = 1;
            for (Object localObject5 = localNode2; localObject5 != null; localObject5 = next)
            {
              Object localObject6;
              if ((hash == n) && (((localObject6 = key) == localObject4) || ((localObject6 != null) && (localObject4.equals(localObject6)))))
              {
                m = 0;
                break;
              }
              i2++;
            }
            if ((m != 0) && (i2 >= 8))
            {
              m = 0;
              l2 += 1L;
              next = localNode2;
              Object localObject7 = null;
              Object localObject8 = null;
              for (localObject5 = localObject1; localObject5 != null; localObject5 = next)
              {
                TreeNode localTreeNode = new TreeNode(hash, key, val, null, null);
                if ((prev = localObject8) == null) {
                  localObject7 = localTreeNode;
                } else {
                  next = localTreeNode;
                }
                localObject8 = localTreeNode;
              }
              setTabAt(arrayOfNode, i1, new TreeBin((TreeNode)localObject7));
            }
          }
        }
        if (m != 0)
        {
          l2 += 1L;
          next = localNode2;
          setTabAt(arrayOfNode, i1, (Node)localObject1);
        }
        localObject1 = localNode1;
      }
      table = arrayOfNode;
      sizeCtl = (i - (i >>> 2));
      baseCount = l2;
    }
  }
  
  public V putIfAbsent(K paramK, V paramV)
  {
    return (V)putVal(paramK, paramV, true);
  }
  
  public boolean remove(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == null) {
      throw new NullPointerException();
    }
    return (paramObject2 != null) && (replaceNode(paramObject1, null, paramObject2) != null);
  }
  
  public boolean replace(K paramK, V paramV1, V paramV2)
  {
    if ((paramK == null) || (paramV1 == null) || (paramV2 == null)) {
      throw new NullPointerException();
    }
    return replaceNode(paramK, paramV2, paramV1) != null;
  }
  
  public V replace(K paramK, V paramV)
  {
    if ((paramK == null) || (paramV == null)) {
      throw new NullPointerException();
    }
    return (V)replaceNode(paramK, paramV, null);
  }
  
  public V getOrDefault(Object paramObject, V paramV)
  {
    Object localObject;
    return (localObject = get(paramObject)) == null ? paramV : localObject;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer)
  {
    if (paramBiConsumer == null) {
      throw new NullPointerException();
    }
    Node[] arrayOfNode;
    if ((arrayOfNode = table) != null)
    {
      Traverser localTraverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node localNode;
      while ((localNode = localTraverser.advance()) != null) {
        paramBiConsumer.accept(key, val);
      }
    }
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    Node[] arrayOfNode;
    if ((arrayOfNode = table) != null)
    {
      Traverser localTraverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node localNode;
      while ((localNode = localTraverser.advance()) != null)
      {
        Object localObject1 = val;
        Object localObject2 = key;
        for (;;)
        {
          Object localObject3 = paramBiFunction.apply(localObject2, localObject1);
          if (localObject3 == null) {
            throw new NullPointerException();
          }
          if ((replaceNode(localObject2, localObject3, localObject1) != null) || ((localObject1 = get(localObject2)) == null)) {
            break;
          }
        }
      }
    }
  }
  
  public V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction)
  {
    if ((paramK == null) || (paramFunction == null)) {
      throw new NullPointerException();
    }
    int i = spread(paramK.hashCode());
    Object localObject1 = null;
    int j = 0;
    Node[] arrayOfNode = table;
    for (;;)
    {
      int k;
      if ((arrayOfNode == null) || ((k = arrayOfNode.length) == 0))
      {
        arrayOfNode = initTable();
      }
      else
      {
        int m;
        Node localNode;
        Object localObject2;
        if ((localNode = tabAt(arrayOfNode, m = k - 1 & i)) == null)
        {
          ReservationNode localReservationNode = new ReservationNode();
          synchronized (localReservationNode)
          {
            if (casTabAt(arrayOfNode, m, null, localReservationNode))
            {
              j = 1;
              localObject2 = null;
              try
              {
                if ((localObject1 = paramFunction.apply(paramK)) != null) {
                  localObject2 = new Node(i, paramK, localObject1, null);
                }
              }
              finally
              {
                setTabAt(arrayOfNode, m, (Node)localObject2);
              }
            }
          }
          if (j != 0) {
            break;
          }
        }
        else
        {
          int n;
          if ((n = hash) == -1)
          {
            arrayOfNode = helpTransfer(arrayOfNode, localNode);
          }
          else
          {
            int i1 = 0;
            synchronized (localNode)
            {
              if (tabAt(arrayOfNode, m) == localNode)
              {
                Object localObject4;
                if (n >= 0)
                {
                  j = 1;
                  localObject2 = localNode;
                  for (;;)
                  {
                    if ((hash == i) && (((localObject4 = key) == paramK) || ((localObject4 != null) && (paramK.equals(localObject4)))))
                    {
                      localObject1 = val;
                      break;
                    }
                    Object localObject6 = localObject2;
                    if ((localObject2 = next) == null)
                    {
                      if ((localObject1 = paramFunction.apply(paramK)) == null) {
                        break;
                      }
                      i1 = 1;
                      next = new Node(i, paramK, localObject1, null);
                      break;
                    }
                    j++;
                  }
                }
                else if ((localNode instanceof TreeBin))
                {
                  j = 2;
                  localObject2 = (TreeBin)localNode;
                  TreeNode localTreeNode;
                  if (((localObject4 = root) != null) && ((localTreeNode = ((TreeNode)localObject4).findTreeNode(i, paramK, null)) != null))
                  {
                    localObject1 = val;
                  }
                  else if ((localObject1 = paramFunction.apply(paramK)) != null)
                  {
                    i1 = 1;
                    ((TreeBin)localObject2).putTreeVal(i, paramK, localObject1);
                  }
                }
              }
            }
            if (j != 0)
            {
              if (j >= 8) {
                treeifyBin(arrayOfNode, m);
              }
              if (i1 != 0) {
                break;
              }
              return (V)localObject1;
            }
          }
        }
      }
    }
    if (localObject1 != null) {
      addCount(1L, j);
    }
    return (V)localObject1;
  }
  
  public V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    if ((paramK == null) || (paramBiFunction == null)) {
      throw new NullPointerException();
    }
    int i = spread(paramK.hashCode());
    Object localObject1 = null;
    int j = 0;
    int k = 0;
    Node[] arrayOfNode = table;
    for (;;)
    {
      int m;
      if ((arrayOfNode == null) || ((m = arrayOfNode.length) == 0))
      {
        arrayOfNode = initTable();
      }
      else
      {
        int n;
        Node localNode1;
        if ((localNode1 = tabAt(arrayOfNode, n = m - 1 & i)) == null) {
          break;
        }
        int i1;
        if ((i1 = hash) == -1)
        {
          arrayOfNode = helpTransfer(arrayOfNode, localNode1);
        }
        else
        {
          synchronized (localNode1)
          {
            if (tabAt(arrayOfNode, n) == localNode1)
            {
              Object localObject2;
              Object localObject3;
              Object localObject4;
              if (i1 >= 0)
              {
                k = 1;
                localObject2 = localNode1;
                localObject3 = null;
                for (;;)
                {
                  if ((hash == i) && (((localObject4 = key) == paramK) || ((localObject4 != null) && (paramK.equals(localObject4)))))
                  {
                    localObject1 = paramBiFunction.apply(paramK, val);
                    if (localObject1 != null)
                    {
                      val = localObject1;
                      break;
                    }
                    j = -1;
                    Node localNode2 = next;
                    if (localObject3 != null) {
                      next = localNode2;
                    } else {
                      setTabAt(arrayOfNode, n, localNode2);
                    }
                    break;
                  }
                  localObject3 = localObject2;
                  if ((localObject2 = next) == null) {
                    break;
                  }
                  k++;
                }
              }
              else if ((localNode1 instanceof TreeBin))
              {
                k = 2;
                localObject2 = (TreeBin)localNode1;
                if (((localObject3 = root) != null) && ((localObject4 = ((TreeNode)localObject3).findTreeNode(i, paramK, null)) != null))
                {
                  localObject1 = paramBiFunction.apply(paramK, val);
                  if (localObject1 != null)
                  {
                    val = localObject1;
                  }
                  else
                  {
                    j = -1;
                    if (((TreeBin)localObject2).removeTreeNode((TreeNode)localObject4)) {
                      setTabAt(arrayOfNode, n, untreeify(first));
                    }
                  }
                }
              }
            }
          }
          if (k != 0) {
            break;
          }
        }
      }
    }
    if (j != 0) {
      addCount(j, k);
    }
    return (V)localObject1;
  }
  
  public V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    if ((paramK == null) || (paramBiFunction == null)) {
      throw new NullPointerException();
    }
    int i = spread(paramK.hashCode());
    Object localObject1 = null;
    int j = 0;
    int k = 0;
    Node[] arrayOfNode = table;
    for (;;)
    {
      int m;
      if ((arrayOfNode == null) || ((m = arrayOfNode.length) == 0))
      {
        arrayOfNode = initTable();
      }
      else
      {
        int n;
        Node localNode;
        Object localObject2;
        if ((localNode = tabAt(arrayOfNode, n = m - 1 & i)) == null)
        {
          ReservationNode localReservationNode = new ReservationNode();
          synchronized (localReservationNode)
          {
            if (casTabAt(arrayOfNode, n, null, localReservationNode))
            {
              k = 1;
              localObject2 = null;
              try
              {
                if ((localObject1 = paramBiFunction.apply(paramK, null)) != null)
                {
                  j = 1;
                  localObject2 = new Node(i, paramK, localObject1, null);
                }
              }
              finally
              {
                setTabAt(arrayOfNode, n, (Node)localObject2);
              }
            }
          }
          if (k != 0) {
            break;
          }
        }
        else
        {
          int i1;
          if ((i1 = hash) == -1)
          {
            arrayOfNode = helpTransfer(arrayOfNode, localNode);
          }
          else
          {
            synchronized (localNode)
            {
              if (tabAt(arrayOfNode, n) == localNode)
              {
                Object localObject4;
                Object localObject6;
                if (i1 >= 0)
                {
                  k = 1;
                  ??? = localNode;
                  localObject2 = null;
                  for (;;)
                  {
                    if ((hash == i) && (((localObject4 = key) == paramK) || ((localObject4 != null) && (paramK.equals(localObject4)))))
                    {
                      localObject1 = paramBiFunction.apply(paramK, val);
                      if (localObject1 != null)
                      {
                        val = localObject1;
                        break;
                      }
                      j = -1;
                      localObject6 = next;
                      if (localObject2 != null) {
                        next = ((Node)localObject6);
                      } else {
                        setTabAt(arrayOfNode, n, (Node)localObject6);
                      }
                      break;
                    }
                    localObject2 = ???;
                    if ((??? = next) == null)
                    {
                      localObject1 = paramBiFunction.apply(paramK, null);
                      if (localObject1 == null) {
                        break;
                      }
                      j = 1;
                      next = new Node(i, paramK, localObject1, null);
                      break;
                    }
                    k++;
                  }
                }
                else if ((localNode instanceof TreeBin))
                {
                  k = 1;
                  ??? = (TreeBin)localNode;
                  if ((localObject2 = root) != null) {
                    localObject4 = ((TreeNode)localObject2).findTreeNode(i, paramK, null);
                  } else {
                    localObject4 = null;
                  }
                  localObject6 = localObject4 == null ? null : val;
                  localObject1 = paramBiFunction.apply(paramK, localObject6);
                  if (localObject1 != null)
                  {
                    if (localObject4 != null)
                    {
                      val = localObject1;
                    }
                    else
                    {
                      j = 1;
                      ((TreeBin)???).putTreeVal(i, paramK, localObject1);
                    }
                  }
                  else if (localObject4 != null)
                  {
                    j = -1;
                    if (((TreeBin)???).removeTreeNode((TreeNode)localObject4)) {
                      setTabAt(arrayOfNode, n, untreeify(first));
                    }
                  }
                }
              }
            }
            if (k != 0)
            {
              if (k < 8) {
                break;
              }
              treeifyBin(arrayOfNode, n);
              break;
            }
          }
        }
      }
    }
    if (j != 0) {
      addCount(j, k);
    }
    return (V)localObject1;
  }
  
  public V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction)
  {
    if ((paramK == null) || (paramV == null) || (paramBiFunction == null)) {
      throw new NullPointerException();
    }
    int i = spread(paramK.hashCode());
    Object localObject1 = null;
    int j = 0;
    int k = 0;
    Node[] arrayOfNode = table;
    for (;;)
    {
      int m;
      if ((arrayOfNode == null) || ((m = arrayOfNode.length) == 0))
      {
        arrayOfNode = initTable();
      }
      else
      {
        int n;
        Node localNode1;
        if ((localNode1 = tabAt(arrayOfNode, n = m - 1 & i)) == null)
        {
          if (casTabAt(arrayOfNode, n, null, new Node(i, paramK, paramV, null)))
          {
            j = 1;
            localObject1 = paramV;
            break;
          }
        }
        else
        {
          int i1;
          if ((i1 = hash) == -1)
          {
            arrayOfNode = helpTransfer(arrayOfNode, localNode1);
          }
          else
          {
            synchronized (localNode1)
            {
              if (tabAt(arrayOfNode, n) == localNode1)
              {
                Object localObject2;
                Object localObject3;
                Object localObject4;
                if (i1 >= 0)
                {
                  k = 1;
                  localObject2 = localNode1;
                  localObject3 = null;
                  for (;;)
                  {
                    if ((hash == i) && (((localObject4 = key) == paramK) || ((localObject4 != null) && (paramK.equals(localObject4)))))
                    {
                      localObject1 = paramBiFunction.apply(val, paramV);
                      if (localObject1 != null)
                      {
                        val = localObject1;
                        break;
                      }
                      j = -1;
                      Node localNode2 = next;
                      if (localObject3 != null) {
                        next = localNode2;
                      } else {
                        setTabAt(arrayOfNode, n, localNode2);
                      }
                      break;
                    }
                    localObject3 = localObject2;
                    if ((localObject2 = next) == null)
                    {
                      j = 1;
                      localObject1 = paramV;
                      next = new Node(i, paramK, localObject1, null);
                      break;
                    }
                    k++;
                  }
                }
                else if ((localNode1 instanceof TreeBin))
                {
                  k = 2;
                  localObject2 = (TreeBin)localNode1;
                  localObject3 = root;
                  localObject4 = localObject3 == null ? null : ((TreeNode)localObject3).findTreeNode(i, paramK, null);
                  localObject1 = localObject4 == null ? paramV : paramBiFunction.apply(val, paramV);
                  if (localObject1 != null)
                  {
                    if (localObject4 != null)
                    {
                      val = localObject1;
                    }
                    else
                    {
                      j = 1;
                      ((TreeBin)localObject2).putTreeVal(i, paramK, localObject1);
                    }
                  }
                  else if (localObject4 != null)
                  {
                    j = -1;
                    if (((TreeBin)localObject2).removeTreeNode((TreeNode)localObject4)) {
                      setTabAt(arrayOfNode, n, untreeify(first));
                    }
                  }
                }
              }
            }
            if (k != 0)
            {
              if (k < 8) {
                break;
              }
              treeifyBin(arrayOfNode, n);
              break;
            }
          }
        }
      }
    }
    if (j != 0) {
      addCount(j, k);
    }
    return (V)localObject1;
  }
  
  public boolean contains(Object paramObject)
  {
    return containsValue(paramObject);
  }
  
  public Enumeration<K> keys()
  {
    Node[] arrayOfNode;
    int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
    return new KeyIterator(arrayOfNode, i, 0, i, this);
  }
  
  public Enumeration<V> elements()
  {
    Node[] arrayOfNode;
    int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
    return new ValueIterator(arrayOfNode, i, 0, i, this);
  }
  
  public long mappingCount()
  {
    long l = sumCount();
    return l < 0L ? 0L : l;
  }
  
  public static <K> KeySetView<K, Boolean> newKeySet()
  {
    return new KeySetView(new ConcurrentHashMap(), Boolean.TRUE);
  }
  
  public static <K> KeySetView<K, Boolean> newKeySet(int paramInt)
  {
    return new KeySetView(new ConcurrentHashMap(paramInt), Boolean.TRUE);
  }
  
  public KeySetView<K, V> keySet(V paramV)
  {
    if (paramV == null) {
      throw new NullPointerException();
    }
    return new KeySetView(this, paramV);
  }
  
  static final int resizeStamp(int paramInt)
  {
    return Integer.numberOfLeadingZeros(paramInt) | 1 << RESIZE_STAMP_BITS - 1;
  }
  
  private final Node<K, V>[] initTable()
  {
    Object localObject1;
    while (((localObject1 = table) == null) || (localObject1.length == 0))
    {
      int i;
      if ((i = sizeCtl) < 0) {
        Thread.yield();
      } else if (U.compareAndSwapInt(this, SIZECTL, i, -1)) {
        try
        {
          if (((localObject1 = table) == null) || (localObject1.length == 0))
          {
            int j = i > 0 ? i : 16;
            Node[] arrayOfNode = (Node[])new Node[j];
            table = (localObject1 = arrayOfNode);
            i = j - (j >>> 2);
          }
        }
        finally
        {
          sizeCtl = i;
        }
      }
    }
    return (Node<K, V>[])localObject1;
  }
  
  private final void addCount(long paramLong, int paramInt)
  {
    CounterCell[] arrayOfCounterCell;
    long l1;
    long l2;
    int j;
    Object localObject;
    if (((arrayOfCounterCell = counterCells) != null) || (!U.compareAndSwapLong(this, BASECOUNT, l1 = baseCount, l2 = l1 + paramLong)))
    {
      boolean bool = true;
      long l3;
      if ((arrayOfCounterCell == null) || ((j = arrayOfCounterCell.length - 1) < 0) || ((localObject = arrayOfCounterCell[(ThreadLocalRandom.getProbe() & j)]) == null) || (!(bool = U.compareAndSwapLong(localObject, CELLVALUE, l3 = value, l3 + paramLong))))
      {
        fullAddCount(paramLong, bool);
        return;
      }
      if (paramInt <= 1) {
        return;
      }
      l2 = sumCount();
    }
    if (paramInt >= 0)
    {
      int i;
      while ((l2 >= (j = sizeCtl)) && ((localObject = table) != null) && ((i = localObject.length) < 1073741824))
      {
        int k = resizeStamp(i);
        if (j < 0)
        {
          Node[] arrayOfNode;
          if ((j >>> RESIZE_STAMP_SHIFT != k) || (j == k + 1) || (j == k + MAX_RESIZERS) || ((arrayOfNode = nextTable) == null) || (transferIndex <= 0)) {
            break;
          }
          if (U.compareAndSwapInt(this, SIZECTL, j, j + 1)) {
            transfer((Node[])localObject, arrayOfNode);
          }
        }
        else if (U.compareAndSwapInt(this, SIZECTL, j, (k << RESIZE_STAMP_SHIFT) + 2))
        {
          transfer((Node[])localObject, null);
        }
        l2 = sumCount();
      }
    }
  }
  
  final Node<K, V>[] helpTransfer(Node<K, V>[] paramArrayOfNode, Node<K, V> paramNode)
  {
    Node[] arrayOfNode;
    if ((paramArrayOfNode != null) && ((paramNode instanceof ForwardingNode)) && ((arrayOfNode = nextTable) != null))
    {
      int j = resizeStamp(paramArrayOfNode.length);
      int i;
      while ((arrayOfNode == nextTable) && (table == paramArrayOfNode) && ((i = sizeCtl) < 0) && (i >>> RESIZE_STAMP_SHIFT == j) && (i != j + 1) && (i != j + MAX_RESIZERS) && (transferIndex > 0)) {
        if (U.compareAndSwapInt(this, SIZECTL, i, i + 1)) {
          transfer(paramArrayOfNode, arrayOfNode);
        }
      }
      return arrayOfNode;
    }
    return table;
  }
  
  private final void tryPresize(int paramInt)
  {
    int i = paramInt >= 536870912 ? 1073741824 : tableSizeFor(paramInt + (paramInt >>> 1) + 1);
    int j;
    while ((j = sizeCtl) >= 0)
    {
      Node[] arrayOfNode1 = table;
      int k;
      if ((arrayOfNode1 == null) || ((k = arrayOfNode1.length) == 0))
      {
        k = j > i ? j : i;
        if (U.compareAndSwapInt(this, SIZECTL, j, -1)) {
          try
          {
            if (table == arrayOfNode1)
            {
              Node[] arrayOfNode2 = (Node[])new Node[k];
              table = arrayOfNode2;
              j = k - (k >>> 2);
            }
          }
          finally
          {
            sizeCtl = j;
          }
        }
      }
      else
      {
        if ((i <= j) || (k >= 1073741824)) {
          break;
        }
        if (arrayOfNode1 == table)
        {
          int m = resizeStamp(k);
          if (j < 0)
          {
            Node[] arrayOfNode3;
            if ((j >>> RESIZE_STAMP_SHIFT != m) || (j == m + 1) || (j == m + MAX_RESIZERS) || ((arrayOfNode3 = nextTable) == null) || (transferIndex <= 0)) {
              break;
            }
            if (U.compareAndSwapInt(this, SIZECTL, j, j + 1)) {
              transfer(arrayOfNode1, arrayOfNode3);
            }
          }
          else if (U.compareAndSwapInt(this, SIZECTL, j, (m << RESIZE_STAMP_SHIFT) + 2))
          {
            transfer(arrayOfNode1, null);
          }
        }
      }
    }
  }
  
  private final void transfer(Node<K, V>[] paramArrayOfNode1, Node<K, V>[] paramArrayOfNode2)
  {
    int i = paramArrayOfNode1.length;
    int j;
    if ((j = NCPU > 1 ? (i >>> 3) / NCPU : i) < 16) {
      j = 16;
    }
    if (paramArrayOfNode2 == null)
    {
      try
      {
        Node[] arrayOfNode = (Node[])new Node[i << 1];
        paramArrayOfNode2 = arrayOfNode;
      }
      catch (Throwable localThrowable)
      {
        sizeCtl = Integer.MAX_VALUE;
        return;
      }
      nextTable = paramArrayOfNode2;
      transferIndex = i;
    }
    int k = paramArrayOfNode2.length;
    ForwardingNode localForwardingNode = new ForwardingNode(paramArrayOfNode2);
    boolean bool = true;
    int m = 0;
    int n = 0;
    int i1 = 0;
    for (;;)
    {
      int i3;
      if (bool)
      {
        n--;
        if ((n >= i1) || (m != 0))
        {
          bool = false;
        }
        else if ((i3 = transferIndex) <= 0)
        {
          n = -1;
          bool = false;
        }
        else
        {
          int i4;
          if (U.compareAndSwapInt(this, TRANSFERINDEX, i3, i4 = i3 > j ? i3 - j : 0))
          {
            i1 = i4;
            n = i3 - 1;
            bool = false;
          }
        }
      }
      else if ((n < 0) || (n >= i) || (n + i >= k))
      {
        if (m != 0)
        {
          nextTable = null;
          table = paramArrayOfNode2;
          sizeCtl = ((i << 1) - (i >>> 1));
          return;
        }
        if (U.compareAndSwapInt(this, SIZECTL, i3 = sizeCtl, i3 - 1))
        {
          if (i3 - 2 != resizeStamp(i) << RESIZE_STAMP_SHIFT) {
            return;
          }
          m = bool = 1;
          n = i;
        }
      }
      else
      {
        Node localNode;
        if ((localNode = tabAt(paramArrayOfNode1, n)) == null)
        {
          bool = casTabAt(paramArrayOfNode1, n, null, localForwardingNode);
        }
        else
        {
          int i2;
          if ((i2 = hash) == -1) {
            bool = true;
          } else {
            synchronized (localNode)
            {
              if (tabAt(paramArrayOfNode1, n) == localNode)
              {
                Object localObject3;
                Object localObject4;
                Object localObject1;
                Object localObject2;
                Object localObject6;
                if (i2 >= 0)
                {
                  int i5 = i2 & i;
                  localObject3 = localNode;
                  int i6;
                  for (localObject4 = next; localObject4 != null; localObject4 = next)
                  {
                    i6 = hash & i;
                    if (i6 != i5)
                    {
                      i5 = i6;
                      localObject3 = localObject4;
                    }
                  }
                  if (i5 == 0)
                  {
                    localObject1 = localObject3;
                    localObject2 = null;
                  }
                  else
                  {
                    localObject2 = localObject3;
                    localObject1 = null;
                  }
                  for (localObject4 = localNode; localObject4 != localObject3; localObject4 = next)
                  {
                    i6 = hash;
                    localObject6 = key;
                    Object localObject7 = val;
                    if ((i6 & i) == 0) {
                      localObject1 = new Node(i6, localObject6, localObject7, (Node)localObject1);
                    } else {
                      localObject2 = new Node(i6, localObject6, localObject7, (Node)localObject2);
                    }
                  }
                  setTabAt(paramArrayOfNode2, n, (Node)localObject1);
                  setTabAt(paramArrayOfNode2, n + i, (Node)localObject2);
                  setTabAt(paramArrayOfNode1, n, localForwardingNode);
                  bool = true;
                }
                else if ((localNode instanceof TreeBin))
                {
                  TreeBin localTreeBin = (TreeBin)localNode;
                  localObject3 = null;
                  localObject4 = null;
                  Object localObject5 = null;
                  localObject6 = null;
                  int i7 = 0;
                  int i8 = 0;
                  for (Object localObject8 = first; localObject8 != null; localObject8 = next)
                  {
                    int i9 = hash;
                    TreeNode localTreeNode = new TreeNode(i9, key, val, null, null);
                    if ((i9 & i) == 0)
                    {
                      if ((prev = localObject4) == null) {
                        localObject3 = localTreeNode;
                      } else {
                        next = localTreeNode;
                      }
                      localObject4 = localTreeNode;
                      i7++;
                    }
                    else
                    {
                      if ((prev = localObject6) == null) {
                        localObject5 = localTreeNode;
                      } else {
                        next = localTreeNode;
                      }
                      localObject6 = localTreeNode;
                      i8++;
                    }
                  }
                  localObject1 = i8 != 0 ? new TreeBin((TreeNode)localObject3) : i7 <= 6 ? untreeify((Node)localObject3) : localTreeBin;
                  localObject2 = i7 != 0 ? new TreeBin((TreeNode)localObject5) : i8 <= 6 ? untreeify((Node)localObject5) : localTreeBin;
                  setTabAt(paramArrayOfNode2, n, (Node)localObject1);
                  setTabAt(paramArrayOfNode2, n + i, (Node)localObject2);
                  setTabAt(paramArrayOfNode1, n, localForwardingNode);
                  bool = true;
                }
              }
            }
          }
        }
      }
    }
  }
  
  final long sumCount()
  {
    CounterCell[] arrayOfCounterCell = counterCells;
    long l = baseCount;
    if (arrayOfCounterCell != null) {
      for (int i = 0; i < arrayOfCounterCell.length; i++)
      {
        CounterCell localCounterCell;
        if ((localCounterCell = arrayOfCounterCell[i]) != null) {
          l += value;
        }
      }
    }
    return l;
  }
  
  private final void fullAddCount(long paramLong, boolean paramBoolean)
  {
    int i;
    if ((i = ThreadLocalRandom.getProbe()) == 0)
    {
      ThreadLocalRandom.localInit();
      i = ThreadLocalRandom.getProbe();
      paramBoolean = true;
    }
    int j = 0;
    for (;;)
    {
      CounterCell[] arrayOfCounterCell1;
      int k;
      long l;
      if (((arrayOfCounterCell1 = counterCells) != null) && ((k = arrayOfCounterCell1.length) > 0))
      {
        CounterCell localCounterCell;
        Object localObject1;
        int n;
        if ((localCounterCell = arrayOfCounterCell1[(k - 1 & i)]) == null)
        {
          if (cellsBusy == 0)
          {
            localObject1 = new CounterCell(paramLong);
            if ((cellsBusy == 0) && (U.compareAndSwapInt(this, CELLSBUSY, 0, 1)))
            {
              n = 0;
              try
              {
                CounterCell[] arrayOfCounterCell3;
                int i1;
                int i2;
                if (((arrayOfCounterCell3 = counterCells) != null) && ((i1 = arrayOfCounterCell3.length) > 0) && (arrayOfCounterCell3[(i2 = i1 - 1 & i)] == null))
                {
                  arrayOfCounterCell3[i2] = localObject1;
                  n = 1;
                }
              }
              finally
              {
                cellsBusy = 0;
              }
              if (n == 0) {
                continue;
              }
              break;
            }
          }
          j = 0;
        }
        else if (!paramBoolean)
        {
          paramBoolean = true;
        }
        else
        {
          if (U.compareAndSwapLong(localCounterCell, CELLVALUE, l = value, l + paramLong)) {
            break;
          }
          if ((counterCells != arrayOfCounterCell1) || (k >= NCPU))
          {
            j = 0;
          }
          else if (j == 0)
          {
            j = 1;
          }
          else if ((cellsBusy == 0) && (U.compareAndSwapInt(this, CELLSBUSY, 0, 1)))
          {
            try
            {
              if (counterCells == arrayOfCounterCell1)
              {
                localObject1 = new CounterCell[k << 1];
                for (n = 0; n < k; n++) {
                  localObject1[n] = arrayOfCounterCell1[n];
                }
                counterCells = ((CounterCell[])localObject1);
              }
            }
            finally
            {
              cellsBusy = 0;
            }
            j = 0;
            continue;
          }
        }
        i = ThreadLocalRandom.advanceProbe(i);
      }
      else if ((cellsBusy == 0) && (counterCells == arrayOfCounterCell1) && (U.compareAndSwapInt(this, CELLSBUSY, 0, 1)))
      {
        int m = 0;
        try
        {
          if (counterCells == arrayOfCounterCell1)
          {
            CounterCell[] arrayOfCounterCell2 = new CounterCell[2];
            arrayOfCounterCell2[(i & 0x1)] = new CounterCell(paramLong);
            counterCells = arrayOfCounterCell2;
            m = 1;
          }
        }
        finally
        {
          cellsBusy = 0;
        }
        if (m != 0) {
          break;
        }
      }
      else
      {
        if (U.compareAndSwapLong(this, BASECOUNT, l = baseCount, l + paramLong)) {
          break;
        }
      }
    }
  }
  
  private final void treeifyBin(Node<K, V>[] paramArrayOfNode, int paramInt)
  {
    if (paramArrayOfNode != null)
    {
      int i;
      if ((i = paramArrayOfNode.length) < 64)
      {
        tryPresize(i << 1);
      }
      else
      {
        Node localNode1;
        if (((localNode1 = tabAt(paramArrayOfNode, paramInt)) != null) && (hash >= 0)) {
          synchronized (localNode1)
          {
            if (tabAt(paramArrayOfNode, paramInt) == localNode1)
            {
              Object localObject1 = null;
              Object localObject2 = null;
              for (Node localNode2 = localNode1; localNode2 != null; localNode2 = next)
              {
                TreeNode localTreeNode = new TreeNode(hash, key, val, null, null);
                if ((prev = localObject2) == null) {
                  localObject1 = localTreeNode;
                } else {
                  next = localTreeNode;
                }
                localObject2 = localTreeNode;
              }
              setTabAt(paramArrayOfNode, paramInt, new TreeBin((TreeNode)localObject1));
            }
          }
        }
      }
    }
  }
  
  static <K, V> Node<K, V> untreeify(Node<K, V> paramNode)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    for (Object localObject3 = paramNode; localObject3 != null; localObject3 = next)
    {
      Node localNode = new Node(hash, key, val, null);
      if (localObject2 == null) {
        localObject1 = localNode;
      } else {
        next = localNode;
      }
      localObject2 = localNode;
    }
    return (Node<K, V>)localObject1;
  }
  
  final int batchFor(long paramLong)
  {
    long l;
    if ((paramLong == Long.MAX_VALUE) || ((l = sumCount()) <= 1L) || (l < paramLong)) {
      return 0;
    }
    int i = ForkJoinPool.getCommonPoolParallelism() << 2;
    return (paramLong <= 0L) || (l /= paramLong >= i) ? i : (int)l;
  }
  
  public void forEach(long paramLong, BiConsumer<? super K, ? super V> paramBiConsumer)
  {
    if (paramBiConsumer == null) {
      throw new NullPointerException();
    }
    new ForEachMappingTask(null, batchFor(paramLong), 0, 0, table, paramBiConsumer).invoke();
  }
  
  public <U> void forEach(long paramLong, BiFunction<? super K, ? super V, ? extends U> paramBiFunction, Consumer<? super U> paramConsumer)
  {
    if ((paramBiFunction == null) || (paramConsumer == null)) {
      throw new NullPointerException();
    }
    new ForEachTransformedMappingTask(null, batchFor(paramLong), 0, 0, table, paramBiFunction, paramConsumer).invoke();
  }
  
  public <U> U search(long paramLong, BiFunction<? super K, ? super V, ? extends U> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    return (U)new SearchMappingsTask(null, batchFor(paramLong), 0, 0, table, paramBiFunction, new AtomicReference()).invoke();
  }
  
  public <U> U reduce(long paramLong, BiFunction<? super K, ? super V, ? extends U> paramBiFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction1)
  {
    if ((paramBiFunction == null) || (paramBiFunction1 == null)) {
      throw new NullPointerException();
    }
    return (U)new MapReduceMappingsTask(null, batchFor(paramLong), 0, 0, table, null, paramBiFunction, paramBiFunction1).invoke();
  }
  
  public double reduceToDouble(long paramLong, ToDoubleBiFunction<? super K, ? super V> paramToDoubleBiFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator)
  {
    if ((paramToDoubleBiFunction == null) || (paramDoubleBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Double)new MapReduceMappingsToDoubleTask(null, batchFor(paramLong), 0, 0, table, null, paramToDoubleBiFunction, paramDouble, paramDoubleBinaryOperator).invoke()).doubleValue();
  }
  
  public long reduceToLong(long paramLong1, ToLongBiFunction<? super K, ? super V> paramToLongBiFunction, long paramLong2, LongBinaryOperator paramLongBinaryOperator)
  {
    if ((paramToLongBiFunction == null) || (paramLongBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Long)new MapReduceMappingsToLongTask(null, batchFor(paramLong1), 0, 0, table, null, paramToLongBiFunction, paramLong2, paramLongBinaryOperator).invoke()).longValue();
  }
  
  public int reduceToInt(long paramLong, ToIntBiFunction<? super K, ? super V> paramToIntBiFunction, int paramInt, IntBinaryOperator paramIntBinaryOperator)
  {
    if ((paramToIntBiFunction == null) || (paramIntBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Integer)new MapReduceMappingsToIntTask(null, batchFor(paramLong), 0, 0, table, null, paramToIntBiFunction, paramInt, paramIntBinaryOperator).invoke()).intValue();
  }
  
  public void forEachKey(long paramLong, Consumer<? super K> paramConsumer)
  {
    if (paramConsumer == null) {
      throw new NullPointerException();
    }
    new ForEachKeyTask(null, batchFor(paramLong), 0, 0, table, paramConsumer).invoke();
  }
  
  public <U> void forEachKey(long paramLong, Function<? super K, ? extends U> paramFunction, Consumer<? super U> paramConsumer)
  {
    if ((paramFunction == null) || (paramConsumer == null)) {
      throw new NullPointerException();
    }
    new ForEachTransformedKeyTask(null, batchFor(paramLong), 0, 0, table, paramFunction, paramConsumer).invoke();
  }
  
  public <U> U searchKeys(long paramLong, Function<? super K, ? extends U> paramFunction)
  {
    if (paramFunction == null) {
      throw new NullPointerException();
    }
    return (U)new SearchKeysTask(null, batchFor(paramLong), 0, 0, table, paramFunction, new AtomicReference()).invoke();
  }
  
  public K reduceKeys(long paramLong, BiFunction<? super K, ? super K, ? extends K> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    return (K)new ReduceKeysTask(null, batchFor(paramLong), 0, 0, table, null, paramBiFunction).invoke();
  }
  
  public <U> U reduceKeys(long paramLong, Function<? super K, ? extends U> paramFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction)
  {
    if ((paramFunction == null) || (paramBiFunction == null)) {
      throw new NullPointerException();
    }
    return (U)new MapReduceKeysTask(null, batchFor(paramLong), 0, 0, table, null, paramFunction, paramBiFunction).invoke();
  }
  
  public double reduceKeysToDouble(long paramLong, ToDoubleFunction<? super K> paramToDoubleFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator)
  {
    if ((paramToDoubleFunction == null) || (paramDoubleBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Double)new MapReduceKeysToDoubleTask(null, batchFor(paramLong), 0, 0, table, null, paramToDoubleFunction, paramDouble, paramDoubleBinaryOperator).invoke()).doubleValue();
  }
  
  public long reduceKeysToLong(long paramLong1, ToLongFunction<? super K> paramToLongFunction, long paramLong2, LongBinaryOperator paramLongBinaryOperator)
  {
    if ((paramToLongFunction == null) || (paramLongBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Long)new MapReduceKeysToLongTask(null, batchFor(paramLong1), 0, 0, table, null, paramToLongFunction, paramLong2, paramLongBinaryOperator).invoke()).longValue();
  }
  
  public int reduceKeysToInt(long paramLong, ToIntFunction<? super K> paramToIntFunction, int paramInt, IntBinaryOperator paramIntBinaryOperator)
  {
    if ((paramToIntFunction == null) || (paramIntBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Integer)new MapReduceKeysToIntTask(null, batchFor(paramLong), 0, 0, table, null, paramToIntFunction, paramInt, paramIntBinaryOperator).invoke()).intValue();
  }
  
  public void forEachValue(long paramLong, Consumer<? super V> paramConsumer)
  {
    if (paramConsumer == null) {
      throw new NullPointerException();
    }
    new ForEachValueTask(null, batchFor(paramLong), 0, 0, table, paramConsumer).invoke();
  }
  
  public <U> void forEachValue(long paramLong, Function<? super V, ? extends U> paramFunction, Consumer<? super U> paramConsumer)
  {
    if ((paramFunction == null) || (paramConsumer == null)) {
      throw new NullPointerException();
    }
    new ForEachTransformedValueTask(null, batchFor(paramLong), 0, 0, table, paramFunction, paramConsumer).invoke();
  }
  
  public <U> U searchValues(long paramLong, Function<? super V, ? extends U> paramFunction)
  {
    if (paramFunction == null) {
      throw new NullPointerException();
    }
    return (U)new SearchValuesTask(null, batchFor(paramLong), 0, 0, table, paramFunction, new AtomicReference()).invoke();
  }
  
  public V reduceValues(long paramLong, BiFunction<? super V, ? super V, ? extends V> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    return (V)new ReduceValuesTask(null, batchFor(paramLong), 0, 0, table, null, paramBiFunction).invoke();
  }
  
  public <U> U reduceValues(long paramLong, Function<? super V, ? extends U> paramFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction)
  {
    if ((paramFunction == null) || (paramBiFunction == null)) {
      throw new NullPointerException();
    }
    return (U)new MapReduceValuesTask(null, batchFor(paramLong), 0, 0, table, null, paramFunction, paramBiFunction).invoke();
  }
  
  public double reduceValuesToDouble(long paramLong, ToDoubleFunction<? super V> paramToDoubleFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator)
  {
    if ((paramToDoubleFunction == null) || (paramDoubleBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Double)new MapReduceValuesToDoubleTask(null, batchFor(paramLong), 0, 0, table, null, paramToDoubleFunction, paramDouble, paramDoubleBinaryOperator).invoke()).doubleValue();
  }
  
  public long reduceValuesToLong(long paramLong1, ToLongFunction<? super V> paramToLongFunction, long paramLong2, LongBinaryOperator paramLongBinaryOperator)
  {
    if ((paramToLongFunction == null) || (paramLongBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Long)new MapReduceValuesToLongTask(null, batchFor(paramLong1), 0, 0, table, null, paramToLongFunction, paramLong2, paramLongBinaryOperator).invoke()).longValue();
  }
  
  public int reduceValuesToInt(long paramLong, ToIntFunction<? super V> paramToIntFunction, int paramInt, IntBinaryOperator paramIntBinaryOperator)
  {
    if ((paramToIntFunction == null) || (paramIntBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Integer)new MapReduceValuesToIntTask(null, batchFor(paramLong), 0, 0, table, null, paramToIntFunction, paramInt, paramIntBinaryOperator).invoke()).intValue();
  }
  
  public void forEachEntry(long paramLong, Consumer<? super Map.Entry<K, V>> paramConsumer)
  {
    if (paramConsumer == null) {
      throw new NullPointerException();
    }
    new ForEachEntryTask(null, batchFor(paramLong), 0, 0, table, paramConsumer).invoke();
  }
  
  public <U> void forEachEntry(long paramLong, Function<Map.Entry<K, V>, ? extends U> paramFunction, Consumer<? super U> paramConsumer)
  {
    if ((paramFunction == null) || (paramConsumer == null)) {
      throw new NullPointerException();
    }
    new ForEachTransformedEntryTask(null, batchFor(paramLong), 0, 0, table, paramFunction, paramConsumer).invoke();
  }
  
  public <U> U searchEntries(long paramLong, Function<Map.Entry<K, V>, ? extends U> paramFunction)
  {
    if (paramFunction == null) {
      throw new NullPointerException();
    }
    return (U)new SearchEntriesTask(null, batchFor(paramLong), 0, 0, table, paramFunction, new AtomicReference()).invoke();
  }
  
  public Map.Entry<K, V> reduceEntries(long paramLong, BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    return (Map.Entry)new ReduceEntriesTask(null, batchFor(paramLong), 0, 0, table, null, paramBiFunction).invoke();
  }
  
  public <U> U reduceEntries(long paramLong, Function<Map.Entry<K, V>, ? extends U> paramFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction)
  {
    if ((paramFunction == null) || (paramBiFunction == null)) {
      throw new NullPointerException();
    }
    return (U)new MapReduceEntriesTask(null, batchFor(paramLong), 0, 0, table, null, paramFunction, paramBiFunction).invoke();
  }
  
  public double reduceEntriesToDouble(long paramLong, ToDoubleFunction<Map.Entry<K, V>> paramToDoubleFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator)
  {
    if ((paramToDoubleFunction == null) || (paramDoubleBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Double)new MapReduceEntriesToDoubleTask(null, batchFor(paramLong), 0, 0, table, null, paramToDoubleFunction, paramDouble, paramDoubleBinaryOperator).invoke()).doubleValue();
  }
  
  public long reduceEntriesToLong(long paramLong1, ToLongFunction<Map.Entry<K, V>> paramToLongFunction, long paramLong2, LongBinaryOperator paramLongBinaryOperator)
  {
    if ((paramToLongFunction == null) || (paramLongBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Long)new MapReduceEntriesToLongTask(null, batchFor(paramLong1), 0, 0, table, null, paramToLongFunction, paramLong2, paramLongBinaryOperator).invoke()).longValue();
  }
  
  public int reduceEntriesToInt(long paramLong, ToIntFunction<Map.Entry<K, V>> paramToIntFunction, int paramInt, IntBinaryOperator paramIntBinaryOperator)
  {
    if ((paramToIntFunction == null) || (paramIntBinaryOperator == null)) {
      throw new NullPointerException();
    }
    return ((Integer)new MapReduceEntriesToIntTask(null, batchFor(paramLong), 0, 0, table, null, paramToIntFunction, paramInt, paramIntBinaryOperator).invoke()).intValue();
  }
  
  static
  {
    try
    {
      U = Unsafe.getUnsafe();
      Class localClass1 = ConcurrentHashMap.class;
      SIZECTL = U.objectFieldOffset(localClass1.getDeclaredField("sizeCtl"));
      TRANSFERINDEX = U.objectFieldOffset(localClass1.getDeclaredField("transferIndex"));
      BASECOUNT = U.objectFieldOffset(localClass1.getDeclaredField("baseCount"));
      CELLSBUSY = U.objectFieldOffset(localClass1.getDeclaredField("cellsBusy"));
      Class localClass2 = CounterCell.class;
      CELLVALUE = U.objectFieldOffset(localClass2.getDeclaredField("value"));
      Class localClass3 = Node[].class;
      ABASE = U.arrayBaseOffset(localClass3);
      int i = U.arrayIndexScale(localClass3);
      if ((i & i - 1) != 0) {
        throw new Error("data type scale not a power of two");
      }
      ASHIFT = 31 - Integer.numberOfLeadingZeros(i);
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static class BaseIterator<K, V>
    extends ConcurrentHashMap.Traverser<K, V>
  {
    final ConcurrentHashMap<K, V> map;
    ConcurrentHashMap.Node<K, V> lastReturned;
    
    BaseIterator(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap<K, V> paramConcurrentHashMap)
    {
      super(paramInt1, paramInt2, paramInt3);
      map = paramConcurrentHashMap;
      advance();
    }
    
    public final boolean hasNext()
    {
      return next != null;
    }
    
    public final boolean hasMoreElements()
    {
      return next != null;
    }
    
    public final void remove()
    {
      ConcurrentHashMap.Node localNode;
      if ((localNode = lastReturned) == null) {
        throw new IllegalStateException();
      }
      lastReturned = null;
      map.replaceNode(key, null, null);
    }
  }
  
  static abstract class BulkTask<K, V, R>
    extends CountedCompleter<R>
  {
    ConcurrentHashMap.Node<K, V>[] tab;
    ConcurrentHashMap.Node<K, V> next;
    ConcurrentHashMap.TableStack<K, V> stack;
    ConcurrentHashMap.TableStack<K, V> spare;
    int index;
    int baseIndex;
    int baseLimit;
    final int baseSize;
    int batch;
    
    BulkTask(BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode)
    {
      super();
      batch = paramInt1;
      index = (baseIndex = paramInt2);
      if ((tab = paramArrayOfNode) == null)
      {
        baseSize = (baseLimit = 0);
      }
      else if (paramBulkTask == null)
      {
        baseSize = (baseLimit = paramArrayOfNode.length);
      }
      else
      {
        baseLimit = paramInt3;
        baseSize = baseSize;
      }
    }
    
    final ConcurrentHashMap.Node<K, V> advance()
    {
      Object localObject;
      if ((localObject = next) != null) {
        localObject = next;
      }
      for (;;)
      {
        if (localObject != null) {
          return (ConcurrentHashMap.Node<K, V>)(next = localObject);
        }
        ConcurrentHashMap.Node[] arrayOfNode;
        int j;
        int i;
        if ((baseIndex >= baseLimit) || ((arrayOfNode = tab) == null) || ((j = arrayOfNode.length) <= (i = index)) || (i < 0)) {
          return next = null;
        }
        if (((localObject = ConcurrentHashMap.tabAt(arrayOfNode, i)) != null) && (hash < 0))
        {
          if ((localObject instanceof ConcurrentHashMap.ForwardingNode))
          {
            tab = nextTable;
            localObject = null;
            pushState(arrayOfNode, i, j);
            continue;
          }
          if ((localObject instanceof ConcurrentHashMap.TreeBin)) {
            localObject = first;
          } else {
            localObject = null;
          }
        }
        if (stack != null) {
          recoverState(j);
        } else if ((index = i + baseSize) >= j) {
          index = (++baseIndex);
        }
      }
    }
    
    private void pushState(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2)
    {
      ConcurrentHashMap.TableStack localTableStack = spare;
      if (localTableStack != null) {
        spare = next;
      } else {
        localTableStack = new ConcurrentHashMap.TableStack();
      }
      tab = paramArrayOfNode;
      length = paramInt2;
      index = paramInt1;
      next = stack;
      stack = localTableStack;
    }
    
    private void recoverState(int paramInt)
    {
      ConcurrentHashMap.TableStack localTableStack1;
      int i;
      while (((localTableStack1 = stack) != null) && (index += (i = length) >= paramInt))
      {
        paramInt = i;
        index = index;
        tab = tab;
        tab = null;
        ConcurrentHashMap.TableStack localTableStack2 = next;
        next = spare;
        stack = localTableStack2;
        spare = localTableStack1;
      }
      if ((localTableStack1 == null) && (index += baseSize >= paramInt)) {
        index = (++baseIndex);
      }
    }
  }
  
  static abstract class CollectionView<K, V, E>
    implements Collection<E>, Serializable
  {
    private static final long serialVersionUID = 7249069246763182397L;
    final ConcurrentHashMap<K, V> map;
    private static final String oomeMsg = "Required array size too large";
    
    CollectionView(ConcurrentHashMap<K, V> paramConcurrentHashMap)
    {
      map = paramConcurrentHashMap;
    }
    
    public ConcurrentHashMap<K, V> getMap()
    {
      return map;
    }
    
    public final void clear()
    {
      map.clear();
    }
    
    public final int size()
    {
      return map.size();
    }
    
    public final boolean isEmpty()
    {
      return map.isEmpty();
    }
    
    public abstract Iterator<E> iterator();
    
    public abstract boolean contains(Object paramObject);
    
    public abstract boolean remove(Object paramObject);
    
    public final Object[] toArray()
    {
      long l = map.mappingCount();
      if (l > 2147483639L) {
        throw new OutOfMemoryError("Required array size too large");
      }
      int i = (int)l;
      Object[] arrayOfObject = new Object[i];
      int j = 0;
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (j == i)
        {
          if (i >= 2147483639) {
            throw new OutOfMemoryError("Required array size too large");
          }
          if (i >= 1073741819) {
            i = 2147483639;
          } else {
            i += (i >>> 1) + 1;
          }
          arrayOfObject = Arrays.copyOf(arrayOfObject, i);
        }
        arrayOfObject[(j++)] = localObject;
      }
      return j == i ? arrayOfObject : Arrays.copyOf(arrayOfObject, j);
    }
    
    public final <T> T[] toArray(T[] paramArrayOfT)
    {
      long l = map.mappingCount();
      if (l > 2147483639L) {
        throw new OutOfMemoryError("Required array size too large");
      }
      int i = (int)l;
      Object[] arrayOfObject = paramArrayOfT.length >= i ? paramArrayOfT : (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
      int j = arrayOfObject.length;
      int k = 0;
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (k == j)
        {
          if (j >= 2147483639) {
            throw new OutOfMemoryError("Required array size too large");
          }
          if (j >= 1073741819) {
            j = 2147483639;
          } else {
            j += (j >>> 1) + 1;
          }
          arrayOfObject = Arrays.copyOf(arrayOfObject, j);
        }
        arrayOfObject[(k++)] = localObject;
      }
      if ((paramArrayOfT == arrayOfObject) && (k < j))
      {
        arrayOfObject[k] = null;
        return arrayOfObject;
      }
      return k == j ? arrayOfObject : Arrays.copyOf(arrayOfObject, k);
    }
    
    public final String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append('[');
      Iterator localIterator = iterator();
      if (localIterator.hasNext()) {
        for (;;)
        {
          Object localObject = localIterator.next();
          localStringBuilder.append(localObject == this ? "(this Collection)" : localObject);
          if (!localIterator.hasNext()) {
            break;
          }
          localStringBuilder.append(',').append(' ');
        }
      }
      return ']';
    }
    
    public final boolean containsAll(Collection<?> paramCollection)
    {
      if (paramCollection != this)
      {
        Iterator localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          if ((localObject == null) || (!contains(localObject))) {
            return false;
          }
        }
      }
      return true;
    }
    
    public final boolean removeAll(Collection<?> paramCollection)
    {
      if (paramCollection == null) {
        throw new NullPointerException();
      }
      boolean bool = false;
      Iterator localIterator = iterator();
      while (localIterator.hasNext()) {
        if (paramCollection.contains(localIterator.next()))
        {
          localIterator.remove();
          bool = true;
        }
      }
      return bool;
    }
    
    public final boolean retainAll(Collection<?> paramCollection)
    {
      if (paramCollection == null) {
        throw new NullPointerException();
      }
      boolean bool = false;
      Iterator localIterator = iterator();
      while (localIterator.hasNext()) {
        if (!paramCollection.contains(localIterator.next()))
        {
          localIterator.remove();
          bool = true;
        }
      }
      return bool;
    }
  }
  
  @Contended
  static final class CounterCell
  {
    volatile long value;
    
    CounterCell(long paramLong)
    {
      value = paramLong;
    }
  }
  
  static final class EntryIterator<K, V>
    extends ConcurrentHashMap.BaseIterator<K, V>
    implements Iterator<Map.Entry<K, V>>
  {
    EntryIterator(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap<K, V> paramConcurrentHashMap)
    {
      super(paramInt1, paramInt2, paramInt3, paramConcurrentHashMap);
    }
    
    public final Map.Entry<K, V> next()
    {
      ConcurrentHashMap.Node localNode;
      if ((localNode = next) == null) {
        throw new NoSuchElementException();
      }
      Object localObject1 = key;
      Object localObject2 = val;
      lastReturned = localNode;
      advance();
      return new ConcurrentHashMap.MapEntry(localObject1, localObject2, map);
    }
  }
  
  static final class EntrySetView<K, V>
    extends ConcurrentHashMap.CollectionView<K, V, Map.Entry<K, V>>
    implements Set<Map.Entry<K, V>>, Serializable
  {
    private static final long serialVersionUID = 2249069246763182397L;
    
    EntrySetView(ConcurrentHashMap<K, V> paramConcurrentHashMap)
    {
      super();
    }
    
    public boolean contains(Object paramObject)
    {
      Map.Entry localEntry;
      Object localObject1;
      Object localObject3;
      Object localObject2;
      return ((paramObject instanceof Map.Entry)) && ((localObject1 = (localEntry = (Map.Entry)paramObject).getKey()) != null) && ((localObject3 = map.get(localObject1)) != null) && ((localObject2 = localEntry.getValue()) != null) && ((localObject2 == localObject3) || (localObject2.equals(localObject3)));
    }
    
    public boolean remove(Object paramObject)
    {
      Map.Entry localEntry;
      Object localObject1;
      Object localObject2;
      return ((paramObject instanceof Map.Entry)) && ((localObject1 = (localEntry = (Map.Entry)paramObject).getKey()) != null) && ((localObject2 = localEntry.getValue()) != null) && (map.remove(localObject1, localObject2));
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      ConcurrentHashMap localConcurrentHashMap = map;
      ConcurrentHashMap.Node[] arrayOfNode;
      int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.EntryIterator(arrayOfNode, i, 0, i, localConcurrentHashMap);
    }
    
    public boolean add(Map.Entry<K, V> paramEntry)
    {
      return map.putVal(paramEntry.getKey(), paramEntry.getValue(), false) == null;
    }
    
    public boolean addAll(Collection<? extends Map.Entry<K, V>> paramCollection)
    {
      boolean bool = false;
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (add(localEntry)) {
          bool = true;
        }
      }
      return bool;
    }
    
    public final int hashCode()
    {
      int i = 0;
      ConcurrentHashMap.Node[] arrayOfNode;
      if ((arrayOfNode = map.table) != null)
      {
        ConcurrentHashMap.Traverser localTraverser = new ConcurrentHashMap.Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
        ConcurrentHashMap.Node localNode;
        while ((localNode = localTraverser.advance()) != null) {
          i += localNode.hashCode();
        }
      }
      return i;
    }
    
    public final boolean equals(Object paramObject)
    {
      Set localSet;
      return ((paramObject instanceof Set)) && (((localSet = (Set)paramObject) == this) || ((containsAll(localSet)) && (localSet.containsAll(this))));
    }
    
    public Spliterator<Map.Entry<K, V>> spliterator()
    {
      ConcurrentHashMap localConcurrentHashMap = map;
      long l = localConcurrentHashMap.sumCount();
      ConcurrentHashMap.Node[] arrayOfNode;
      int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.EntrySpliterator(arrayOfNode, i, 0, i, l < 0L ? 0L : l, localConcurrentHashMap);
    }
    
    public void forEach(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentHashMap.Node[] arrayOfNode;
      if ((arrayOfNode = map.table) != null)
      {
        ConcurrentHashMap.Traverser localTraverser = new ConcurrentHashMap.Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
        ConcurrentHashMap.Node localNode;
        while ((localNode = localTraverser.advance()) != null) {
          paramConsumer.accept(new ConcurrentHashMap.MapEntry(key, val, map));
        }
      }
    }
  }
  
  static final class EntrySpliterator<K, V>
    extends ConcurrentHashMap.Traverser<K, V>
    implements Spliterator<Map.Entry<K, V>>
  {
    final ConcurrentHashMap<K, V> map;
    long est;
    
    EntrySpliterator(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2, int paramInt3, long paramLong, ConcurrentHashMap<K, V> paramConcurrentHashMap)
    {
      super(paramInt1, paramInt2, paramInt3);
      map = paramConcurrentHashMap;
      est = paramLong;
    }
    
    public Spliterator<Map.Entry<K, V>> trySplit()
    {
      int i;
      int j;
      int k;
      return (k = (i = baseIndex) + (j = baseLimit) >>> 1) <= i ? null : new EntrySpliterator(tab, baseSize, baseLimit = k, j, est >>>= 1, map);
    }
    
    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentHashMap.Node localNode;
      while ((localNode = advance()) != null) {
        paramConsumer.accept(new ConcurrentHashMap.MapEntry(key, val, map));
      }
    }
    
    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentHashMap.Node localNode;
      if ((localNode = advance()) == null) {
        return false;
      }
      paramConsumer.accept(new ConcurrentHashMap.MapEntry(key, val, map));
      return true;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return 4353;
    }
  }
  
  static final class ForEachEntryTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Void>
  {
    final Consumer<? super Map.Entry<K, V>> action;
    
    ForEachEntryTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      action = paramConsumer;
    }
    
    public final void compute()
    {
      Consumer localConsumer;
      if ((localConsumer = action) != null)
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          new ForEachEntryTask(this, batch >>>= 1, baseLimit = k, j, tab, localConsumer).fork();
        }
        ConcurrentHashMap.Node localNode;
        while ((localNode = advance()) != null) {
          localConsumer.accept(localNode);
        }
        propagateCompletion();
      }
    }
  }
  
  static final class ForEachKeyTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Void>
  {
    final Consumer<? super K> action;
    
    ForEachKeyTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, Consumer<? super K> paramConsumer)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      action = paramConsumer;
    }
    
    public final void compute()
    {
      Consumer localConsumer;
      if ((localConsumer = action) != null)
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          new ForEachKeyTask(this, batch >>>= 1, baseLimit = k, j, tab, localConsumer).fork();
        }
        ConcurrentHashMap.Node localNode;
        while ((localNode = advance()) != null) {
          localConsumer.accept(key);
        }
        propagateCompletion();
      }
    }
  }
  
  static final class ForEachMappingTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Void>
  {
    final BiConsumer<? super K, ? super V> action;
    
    ForEachMappingTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, BiConsumer<? super K, ? super V> paramBiConsumer)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      action = paramBiConsumer;
    }
    
    public final void compute()
    {
      BiConsumer localBiConsumer;
      if ((localBiConsumer = action) != null)
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          new ForEachMappingTask(this, batch >>>= 1, baseLimit = k, j, tab, localBiConsumer).fork();
        }
        ConcurrentHashMap.Node localNode;
        while ((localNode = advance()) != null) {
          localBiConsumer.accept(key, val);
        }
        propagateCompletion();
      }
    }
  }
  
  static final class ForEachTransformedEntryTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, Void>
  {
    final Function<Map.Entry<K, V>, ? extends U> transformer;
    final Consumer<? super U> action;
    
    ForEachTransformedEntryTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, Function<Map.Entry<K, V>, ? extends U> paramFunction, Consumer<? super U> paramConsumer)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      transformer = paramFunction;
      action = paramConsumer;
    }
    
    public final void compute()
    {
      Function localFunction;
      Consumer localConsumer;
      if (((localFunction = transformer) != null) && ((localConsumer = action) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          new ForEachTransformedEntryTask(this, batch >>>= 1, baseLimit = k, j, tab, localFunction, localConsumer).fork();
        }
        ConcurrentHashMap.Node localNode;
        while ((localNode = advance()) != null)
        {
          Object localObject;
          if ((localObject = localFunction.apply(localNode)) != null) {
            localConsumer.accept(localObject);
          }
        }
        propagateCompletion();
      }
    }
  }
  
  static final class ForEachTransformedKeyTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, Void>
  {
    final Function<? super K, ? extends U> transformer;
    final Consumer<? super U> action;
    
    ForEachTransformedKeyTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, Function<? super K, ? extends U> paramFunction, Consumer<? super U> paramConsumer)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      transformer = paramFunction;
      action = paramConsumer;
    }
    
    public final void compute()
    {
      Function localFunction;
      Consumer localConsumer;
      if (((localFunction = transformer) != null) && ((localConsumer = action) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          new ForEachTransformedKeyTask(this, batch >>>= 1, baseLimit = k, j, tab, localFunction, localConsumer).fork();
        }
        ConcurrentHashMap.Node localNode;
        while ((localNode = advance()) != null)
        {
          Object localObject;
          if ((localObject = localFunction.apply(key)) != null) {
            localConsumer.accept(localObject);
          }
        }
        propagateCompletion();
      }
    }
  }
  
  static final class ForEachTransformedMappingTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, Void>
  {
    final BiFunction<? super K, ? super V, ? extends U> transformer;
    final Consumer<? super U> action;
    
    ForEachTransformedMappingTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, BiFunction<? super K, ? super V, ? extends U> paramBiFunction, Consumer<? super U> paramConsumer)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      transformer = paramBiFunction;
      action = paramConsumer;
    }
    
    public final void compute()
    {
      BiFunction localBiFunction;
      Consumer localConsumer;
      if (((localBiFunction = transformer) != null) && ((localConsumer = action) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          new ForEachTransformedMappingTask(this, batch >>>= 1, baseLimit = k, j, tab, localBiFunction, localConsumer).fork();
        }
        ConcurrentHashMap.Node localNode;
        while ((localNode = advance()) != null)
        {
          Object localObject;
          if ((localObject = localBiFunction.apply(key, val)) != null) {
            localConsumer.accept(localObject);
          }
        }
        propagateCompletion();
      }
    }
  }
  
  static final class ForEachTransformedValueTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, Void>
  {
    final Function<? super V, ? extends U> transformer;
    final Consumer<? super U> action;
    
    ForEachTransformedValueTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, Function<? super V, ? extends U> paramFunction, Consumer<? super U> paramConsumer)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      transformer = paramFunction;
      action = paramConsumer;
    }
    
    public final void compute()
    {
      Function localFunction;
      Consumer localConsumer;
      if (((localFunction = transformer) != null) && ((localConsumer = action) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          new ForEachTransformedValueTask(this, batch >>>= 1, baseLimit = k, j, tab, localFunction, localConsumer).fork();
        }
        ConcurrentHashMap.Node localNode;
        while ((localNode = advance()) != null)
        {
          Object localObject;
          if ((localObject = localFunction.apply(val)) != null) {
            localConsumer.accept(localObject);
          }
        }
        propagateCompletion();
      }
    }
  }
  
  static final class ForEachValueTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Void>
  {
    final Consumer<? super V> action;
    
    ForEachValueTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, Consumer<? super V> paramConsumer)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      action = paramConsumer;
    }
    
    public final void compute()
    {
      Consumer localConsumer;
      if ((localConsumer = action) != null)
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          new ForEachValueTask(this, batch >>>= 1, baseLimit = k, j, tab, localConsumer).fork();
        }
        ConcurrentHashMap.Node localNode;
        while ((localNode = advance()) != null) {
          localConsumer.accept(val);
        }
        propagateCompletion();
      }
    }
  }
  
  static final class ForwardingNode<K, V>
    extends ConcurrentHashMap.Node<K, V>
  {
    final ConcurrentHashMap.Node<K, V>[] nextTable;
    
    ForwardingNode(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode)
    {
      super(null, null, null);
      nextTable = paramArrayOfNode;
    }
    
    ConcurrentHashMap.Node<K, V> find(int paramInt, Object paramObject)
    {
      ConcurrentHashMap.Node[] arrayOfNode = nextTable;
      int i;
      ConcurrentHashMap.Node localNode;
      if ((paramObject == null) || (arrayOfNode == null) || ((i = arrayOfNode.length) == 0) || ((localNode = ConcurrentHashMap.tabAt(arrayOfNode, i - 1 & paramInt)) == null)) {
        return null;
      }
      for (;;)
      {
        int j;
        Object localObject;
        if (((j = hash) == paramInt) && (((localObject = key) == paramObject) || ((localObject != null) && (paramObject.equals(localObject))))) {
          return localNode;
        }
        if (j < 0)
        {
          if ((localNode instanceof ForwardingNode))
          {
            arrayOfNode = nextTable;
            break;
          }
          return localNode.find(paramInt, paramObject);
        }
        if ((localNode = next) == null) {
          return null;
        }
      }
    }
  }
  
  static final class KeyIterator<K, V>
    extends ConcurrentHashMap.BaseIterator<K, V>
    implements Iterator<K>, Enumeration<K>
  {
    KeyIterator(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap<K, V> paramConcurrentHashMap)
    {
      super(paramInt1, paramInt2, paramInt3, paramConcurrentHashMap);
    }
    
    public final K next()
    {
      ConcurrentHashMap.Node localNode;
      if ((localNode = next) == null) {
        throw new NoSuchElementException();
      }
      Object localObject = key;
      lastReturned = localNode;
      advance();
      return (K)localObject;
    }
    
    public final K nextElement()
    {
      return (K)next();
    }
  }
  
  public static class KeySetView<K, V>
    extends ConcurrentHashMap.CollectionView<K, V, K>
    implements Set<K>, Serializable
  {
    private static final long serialVersionUID = 7249069246763182397L;
    private final V value;
    
    KeySetView(ConcurrentHashMap<K, V> paramConcurrentHashMap, V paramV)
    {
      super();
      value = paramV;
    }
    
    public V getMappedValue()
    {
      return (V)value;
    }
    
    public boolean contains(Object paramObject)
    {
      return map.containsKey(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      return map.remove(paramObject) != null;
    }
    
    public Iterator<K> iterator()
    {
      ConcurrentHashMap localConcurrentHashMap = map;
      ConcurrentHashMap.Node[] arrayOfNode;
      int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.KeyIterator(arrayOfNode, i, 0, i, localConcurrentHashMap);
    }
    
    public boolean add(K paramK)
    {
      Object localObject;
      if ((localObject = value) == null) {
        throw new UnsupportedOperationException();
      }
      return map.putVal(paramK, localObject, true) == null;
    }
    
    public boolean addAll(Collection<? extends K> paramCollection)
    {
      boolean bool = false;
      Object localObject1;
      if ((localObject1 = value) == null) {
        throw new UnsupportedOperationException();
      }
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = localIterator.next();
        if (map.putVal(localObject2, localObject1, true) == null) {
          bool = true;
        }
      }
      return bool;
    }
    
    public int hashCode()
    {
      int i = 0;
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        i += localObject.hashCode();
      }
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      Set localSet;
      return ((paramObject instanceof Set)) && (((localSet = (Set)paramObject) == this) || ((containsAll(localSet)) && (localSet.containsAll(this))));
    }
    
    public Spliterator<K> spliterator()
    {
      ConcurrentHashMap localConcurrentHashMap = map;
      long l = localConcurrentHashMap.sumCount();
      ConcurrentHashMap.Node[] arrayOfNode;
      int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.KeySpliterator(arrayOfNode, i, 0, i, l < 0L ? 0L : l);
    }
    
    public void forEach(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentHashMap.Node[] arrayOfNode;
      if ((arrayOfNode = map.table) != null)
      {
        ConcurrentHashMap.Traverser localTraverser = new ConcurrentHashMap.Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
        ConcurrentHashMap.Node localNode;
        while ((localNode = localTraverser.advance()) != null) {
          paramConsumer.accept(key);
        }
      }
    }
  }
  
  static final class KeySpliterator<K, V>
    extends ConcurrentHashMap.Traverser<K, V>
    implements Spliterator<K>
  {
    long est;
    
    KeySpliterator(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2, int paramInt3, long paramLong)
    {
      super(paramInt1, paramInt2, paramInt3);
      est = paramLong;
    }
    
    public Spliterator<K> trySplit()
    {
      int i;
      int j;
      int k;
      return (k = (i = baseIndex) + (j = baseLimit) >>> 1) <= i ? null : new KeySpliterator(tab, baseSize, baseLimit = k, j, est >>>= 1);
    }
    
    public void forEachRemaining(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentHashMap.Node localNode;
      while ((localNode = advance()) != null) {
        paramConsumer.accept(key);
      }
    }
    
    public boolean tryAdvance(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentHashMap.Node localNode;
      if ((localNode = advance()) == null) {
        return false;
      }
      paramConsumer.accept(key);
      return true;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return 4353;
    }
  }
  
  static final class MapEntry<K, V>
    implements Map.Entry<K, V>
  {
    final K key;
    V val;
    final ConcurrentHashMap<K, V> map;
    
    MapEntry(K paramK, V paramV, ConcurrentHashMap<K, V> paramConcurrentHashMap)
    {
      key = paramK;
      val = paramV;
      map = paramConcurrentHashMap;
    }
    
    public K getKey()
    {
      return (K)key;
    }
    
    public V getValue()
    {
      return (V)val;
    }
    
    public int hashCode()
    {
      return key.hashCode() ^ val.hashCode();
    }
    
    public String toString()
    {
      return key + "=" + val;
    }
    
    public boolean equals(Object paramObject)
    {
      Map.Entry localEntry;
      Object localObject1;
      Object localObject2;
      return ((paramObject instanceof Map.Entry)) && ((localObject1 = (localEntry = (Map.Entry)paramObject).getKey()) != null) && ((localObject2 = localEntry.getValue()) != null) && ((localObject1 == key) || (localObject1.equals(key))) && ((localObject2 == val) || (localObject2.equals(val)));
    }
    
    public V setValue(V paramV)
    {
      if (paramV == null) {
        throw new NullPointerException();
      }
      Object localObject = val;
      val = paramV;
      map.put(key, paramV);
      return (V)localObject;
    }
  }
  
  static final class MapReduceEntriesTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, U>
  {
    final Function<Map.Entry<K, V>, ? extends U> transformer;
    final BiFunction<? super U, ? super U, ? extends U> reducer;
    U result;
    MapReduceEntriesTask<K, V, U> rights;
    MapReduceEntriesTask<K, V, U> nextRight;
    
    MapReduceEntriesTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceEntriesTask<K, V, U> paramMapReduceEntriesTask, Function<Map.Entry<K, V>, ? extends U> paramFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceEntriesTask;
      transformer = paramFunction;
      reducer = paramBiFunction;
    }
    
    public final U getRawResult()
    {
      return (U)result;
    }
    
    public final void compute()
    {
      Function localFunction;
      BiFunction localBiFunction;
      if (((localFunction = transformer) != null) && ((localBiFunction = reducer) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceEntriesTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localFunction, localBiFunction)).fork();
        }
        Object localObject1 = null;
        Object localObject3;
        while ((localObject2 = advance()) != null) {
          if ((localObject3 = localFunction.apply(localObject2)) != null) {
            localObject1 = localObject1 == null ? localObject3 : localBiFunction.apply(localObject1, localObject3);
          }
        }
        result = localObject1;
        for (Object localObject2 = firstComplete(); localObject2 != null; localObject2 = ((CountedCompleter)localObject2).nextComplete())
        {
          localObject3 = (MapReduceEntriesTask)localObject2;
          for (MapReduceEntriesTask localMapReduceEntriesTask = rights; localMapReduceEntriesTask != null; localMapReduceEntriesTask = rights = nextRight)
          {
            Object localObject5;
            if ((localObject5 = result) != null)
            {
              Object localObject4;
              result = ((localObject4 = result) == null ? localObject5 : localBiFunction.apply(localObject4, localObject5));
            }
          }
        }
      }
    }
  }
  
  static final class MapReduceEntriesToDoubleTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Double>
  {
    final ToDoubleFunction<Map.Entry<K, V>> transformer;
    final DoubleBinaryOperator reducer;
    final double basis;
    double result;
    MapReduceEntriesToDoubleTask<K, V> rights;
    MapReduceEntriesToDoubleTask<K, V> nextRight;
    
    MapReduceEntriesToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceEntriesToDoubleTask<K, V> paramMapReduceEntriesToDoubleTask, ToDoubleFunction<Map.Entry<K, V>> paramToDoubleFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceEntriesToDoubleTask;
      transformer = paramToDoubleFunction;
      basis = paramDouble;
      reducer = paramDoubleBinaryOperator;
    }
    
    public final Double getRawResult()
    {
      return Double.valueOf(result);
    }
    
    public final void compute()
    {
      ToDoubleFunction localToDoubleFunction;
      DoubleBinaryOperator localDoubleBinaryOperator;
      if (((localToDoubleFunction = transformer) != null) && ((localDoubleBinaryOperator = reducer) != null))
      {
        double d = basis;
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceEntriesToDoubleTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localToDoubleFunction, d, localDoubleBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          d = localDoubleBinaryOperator.applyAsDouble(d, localToDoubleFunction.applyAsDouble(localObject));
        }
        result = d;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceEntriesToDoubleTask localMapReduceEntriesToDoubleTask1 = (MapReduceEntriesToDoubleTask)localObject;
          for (MapReduceEntriesToDoubleTask localMapReduceEntriesToDoubleTask2 = rights; localMapReduceEntriesToDoubleTask2 != null; localMapReduceEntriesToDoubleTask2 = rights = nextRight) {
            result = localDoubleBinaryOperator.applyAsDouble(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceEntriesToIntTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Integer>
  {
    final ToIntFunction<Map.Entry<K, V>> transformer;
    final IntBinaryOperator reducer;
    final int basis;
    int result;
    MapReduceEntriesToIntTask<K, V> rights;
    MapReduceEntriesToIntTask<K, V> nextRight;
    
    MapReduceEntriesToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceEntriesToIntTask<K, V> paramMapReduceEntriesToIntTask, ToIntFunction<Map.Entry<K, V>> paramToIntFunction, int paramInt4, IntBinaryOperator paramIntBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceEntriesToIntTask;
      transformer = paramToIntFunction;
      basis = paramInt4;
      reducer = paramIntBinaryOperator;
    }
    
    public final Integer getRawResult()
    {
      return Integer.valueOf(result);
    }
    
    public final void compute()
    {
      ToIntFunction localToIntFunction;
      IntBinaryOperator localIntBinaryOperator;
      if (((localToIntFunction = transformer) != null) && ((localIntBinaryOperator = reducer) != null))
      {
        int i = basis;
        int j = baseIndex;
        int k;
        int m;
        while ((batch > 0) && ((m = (k = baseLimit) + j >>> 1) > j))
        {
          addToPendingCount(1);
          (rights = new MapReduceEntriesToIntTask(this, batch >>>= 1, baseLimit = m, k, tab, rights, localToIntFunction, i, localIntBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          i = localIntBinaryOperator.applyAsInt(i, localToIntFunction.applyAsInt(localObject));
        }
        result = i;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceEntriesToIntTask localMapReduceEntriesToIntTask1 = (MapReduceEntriesToIntTask)localObject;
          for (MapReduceEntriesToIntTask localMapReduceEntriesToIntTask2 = rights; localMapReduceEntriesToIntTask2 != null; localMapReduceEntriesToIntTask2 = rights = nextRight) {
            result = localIntBinaryOperator.applyAsInt(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceEntriesToLongTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Long>
  {
    final ToLongFunction<Map.Entry<K, V>> transformer;
    final LongBinaryOperator reducer;
    final long basis;
    long result;
    MapReduceEntriesToLongTask<K, V> rights;
    MapReduceEntriesToLongTask<K, V> nextRight;
    
    MapReduceEntriesToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceEntriesToLongTask<K, V> paramMapReduceEntriesToLongTask, ToLongFunction<Map.Entry<K, V>> paramToLongFunction, long paramLong, LongBinaryOperator paramLongBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceEntriesToLongTask;
      transformer = paramToLongFunction;
      basis = paramLong;
      reducer = paramLongBinaryOperator;
    }
    
    public final Long getRawResult()
    {
      return Long.valueOf(result);
    }
    
    public final void compute()
    {
      ToLongFunction localToLongFunction;
      LongBinaryOperator localLongBinaryOperator;
      if (((localToLongFunction = transformer) != null) && ((localLongBinaryOperator = reducer) != null))
      {
        long l = basis;
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceEntriesToLongTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localToLongFunction, l, localLongBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          l = localLongBinaryOperator.applyAsLong(l, localToLongFunction.applyAsLong(localObject));
        }
        result = l;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceEntriesToLongTask localMapReduceEntriesToLongTask1 = (MapReduceEntriesToLongTask)localObject;
          for (MapReduceEntriesToLongTask localMapReduceEntriesToLongTask2 = rights; localMapReduceEntriesToLongTask2 != null; localMapReduceEntriesToLongTask2 = rights = nextRight) {
            result = localLongBinaryOperator.applyAsLong(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceKeysTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, U>
  {
    final Function<? super K, ? extends U> transformer;
    final BiFunction<? super U, ? super U, ? extends U> reducer;
    U result;
    MapReduceKeysTask<K, V, U> rights;
    MapReduceKeysTask<K, V, U> nextRight;
    
    MapReduceKeysTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceKeysTask<K, V, U> paramMapReduceKeysTask, Function<? super K, ? extends U> paramFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceKeysTask;
      transformer = paramFunction;
      reducer = paramBiFunction;
    }
    
    public final U getRawResult()
    {
      return (U)result;
    }
    
    public final void compute()
    {
      Function localFunction;
      BiFunction localBiFunction;
      if (((localFunction = transformer) != null) && ((localBiFunction = reducer) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceKeysTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localFunction, localBiFunction)).fork();
        }
        Object localObject1 = null;
        Object localObject3;
        while ((localObject2 = advance()) != null) {
          if ((localObject3 = localFunction.apply(key)) != null) {
            localObject1 = localObject1 == null ? localObject3 : localBiFunction.apply(localObject1, localObject3);
          }
        }
        result = localObject1;
        for (Object localObject2 = firstComplete(); localObject2 != null; localObject2 = ((CountedCompleter)localObject2).nextComplete())
        {
          localObject3 = (MapReduceKeysTask)localObject2;
          for (MapReduceKeysTask localMapReduceKeysTask = rights; localMapReduceKeysTask != null; localMapReduceKeysTask = rights = nextRight)
          {
            Object localObject5;
            if ((localObject5 = result) != null)
            {
              Object localObject4;
              result = ((localObject4 = result) == null ? localObject5 : localBiFunction.apply(localObject4, localObject5));
            }
          }
        }
      }
    }
  }
  
  static final class MapReduceKeysToDoubleTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Double>
  {
    final ToDoubleFunction<? super K> transformer;
    final DoubleBinaryOperator reducer;
    final double basis;
    double result;
    MapReduceKeysToDoubleTask<K, V> rights;
    MapReduceKeysToDoubleTask<K, V> nextRight;
    
    MapReduceKeysToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceKeysToDoubleTask<K, V> paramMapReduceKeysToDoubleTask, ToDoubleFunction<? super K> paramToDoubleFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceKeysToDoubleTask;
      transformer = paramToDoubleFunction;
      basis = paramDouble;
      reducer = paramDoubleBinaryOperator;
    }
    
    public final Double getRawResult()
    {
      return Double.valueOf(result);
    }
    
    public final void compute()
    {
      ToDoubleFunction localToDoubleFunction;
      DoubleBinaryOperator localDoubleBinaryOperator;
      if (((localToDoubleFunction = transformer) != null) && ((localDoubleBinaryOperator = reducer) != null))
      {
        double d = basis;
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceKeysToDoubleTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localToDoubleFunction, d, localDoubleBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          d = localDoubleBinaryOperator.applyAsDouble(d, localToDoubleFunction.applyAsDouble(key));
        }
        result = d;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceKeysToDoubleTask localMapReduceKeysToDoubleTask1 = (MapReduceKeysToDoubleTask)localObject;
          for (MapReduceKeysToDoubleTask localMapReduceKeysToDoubleTask2 = rights; localMapReduceKeysToDoubleTask2 != null; localMapReduceKeysToDoubleTask2 = rights = nextRight) {
            result = localDoubleBinaryOperator.applyAsDouble(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceKeysToIntTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Integer>
  {
    final ToIntFunction<? super K> transformer;
    final IntBinaryOperator reducer;
    final int basis;
    int result;
    MapReduceKeysToIntTask<K, V> rights;
    MapReduceKeysToIntTask<K, V> nextRight;
    
    MapReduceKeysToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceKeysToIntTask<K, V> paramMapReduceKeysToIntTask, ToIntFunction<? super K> paramToIntFunction, int paramInt4, IntBinaryOperator paramIntBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceKeysToIntTask;
      transformer = paramToIntFunction;
      basis = paramInt4;
      reducer = paramIntBinaryOperator;
    }
    
    public final Integer getRawResult()
    {
      return Integer.valueOf(result);
    }
    
    public final void compute()
    {
      ToIntFunction localToIntFunction;
      IntBinaryOperator localIntBinaryOperator;
      if (((localToIntFunction = transformer) != null) && ((localIntBinaryOperator = reducer) != null))
      {
        int i = basis;
        int j = baseIndex;
        int k;
        int m;
        while ((batch > 0) && ((m = (k = baseLimit) + j >>> 1) > j))
        {
          addToPendingCount(1);
          (rights = new MapReduceKeysToIntTask(this, batch >>>= 1, baseLimit = m, k, tab, rights, localToIntFunction, i, localIntBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          i = localIntBinaryOperator.applyAsInt(i, localToIntFunction.applyAsInt(key));
        }
        result = i;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceKeysToIntTask localMapReduceKeysToIntTask1 = (MapReduceKeysToIntTask)localObject;
          for (MapReduceKeysToIntTask localMapReduceKeysToIntTask2 = rights; localMapReduceKeysToIntTask2 != null; localMapReduceKeysToIntTask2 = rights = nextRight) {
            result = localIntBinaryOperator.applyAsInt(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceKeysToLongTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Long>
  {
    final ToLongFunction<? super K> transformer;
    final LongBinaryOperator reducer;
    final long basis;
    long result;
    MapReduceKeysToLongTask<K, V> rights;
    MapReduceKeysToLongTask<K, V> nextRight;
    
    MapReduceKeysToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceKeysToLongTask<K, V> paramMapReduceKeysToLongTask, ToLongFunction<? super K> paramToLongFunction, long paramLong, LongBinaryOperator paramLongBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceKeysToLongTask;
      transformer = paramToLongFunction;
      basis = paramLong;
      reducer = paramLongBinaryOperator;
    }
    
    public final Long getRawResult()
    {
      return Long.valueOf(result);
    }
    
    public final void compute()
    {
      ToLongFunction localToLongFunction;
      LongBinaryOperator localLongBinaryOperator;
      if (((localToLongFunction = transformer) != null) && ((localLongBinaryOperator = reducer) != null))
      {
        long l = basis;
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceKeysToLongTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localToLongFunction, l, localLongBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          l = localLongBinaryOperator.applyAsLong(l, localToLongFunction.applyAsLong(key));
        }
        result = l;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceKeysToLongTask localMapReduceKeysToLongTask1 = (MapReduceKeysToLongTask)localObject;
          for (MapReduceKeysToLongTask localMapReduceKeysToLongTask2 = rights; localMapReduceKeysToLongTask2 != null; localMapReduceKeysToLongTask2 = rights = nextRight) {
            result = localLongBinaryOperator.applyAsLong(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceMappingsTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, U>
  {
    final BiFunction<? super K, ? super V, ? extends U> transformer;
    final BiFunction<? super U, ? super U, ? extends U> reducer;
    U result;
    MapReduceMappingsTask<K, V, U> rights;
    MapReduceMappingsTask<K, V, U> nextRight;
    
    MapReduceMappingsTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceMappingsTask<K, V, U> paramMapReduceMappingsTask, BiFunction<? super K, ? super V, ? extends U> paramBiFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction1)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceMappingsTask;
      transformer = paramBiFunction;
      reducer = paramBiFunction1;
    }
    
    public final U getRawResult()
    {
      return (U)result;
    }
    
    public final void compute()
    {
      BiFunction localBiFunction1;
      BiFunction localBiFunction2;
      if (((localBiFunction1 = transformer) != null) && ((localBiFunction2 = reducer) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceMappingsTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localBiFunction1, localBiFunction2)).fork();
        }
        Object localObject1 = null;
        Object localObject3;
        while ((localObject2 = advance()) != null) {
          if ((localObject3 = localBiFunction1.apply(key, val)) != null) {
            localObject1 = localObject1 == null ? localObject3 : localBiFunction2.apply(localObject1, localObject3);
          }
        }
        result = localObject1;
        for (Object localObject2 = firstComplete(); localObject2 != null; localObject2 = ((CountedCompleter)localObject2).nextComplete())
        {
          localObject3 = (MapReduceMappingsTask)localObject2;
          for (MapReduceMappingsTask localMapReduceMappingsTask = rights; localMapReduceMappingsTask != null; localMapReduceMappingsTask = rights = nextRight)
          {
            Object localObject5;
            if ((localObject5 = result) != null)
            {
              Object localObject4;
              result = ((localObject4 = result) == null ? localObject5 : localBiFunction2.apply(localObject4, localObject5));
            }
          }
        }
      }
    }
  }
  
  static final class MapReduceMappingsToDoubleTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Double>
  {
    final ToDoubleBiFunction<? super K, ? super V> transformer;
    final DoubleBinaryOperator reducer;
    final double basis;
    double result;
    MapReduceMappingsToDoubleTask<K, V> rights;
    MapReduceMappingsToDoubleTask<K, V> nextRight;
    
    MapReduceMappingsToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceMappingsToDoubleTask<K, V> paramMapReduceMappingsToDoubleTask, ToDoubleBiFunction<? super K, ? super V> paramToDoubleBiFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceMappingsToDoubleTask;
      transformer = paramToDoubleBiFunction;
      basis = paramDouble;
      reducer = paramDoubleBinaryOperator;
    }
    
    public final Double getRawResult()
    {
      return Double.valueOf(result);
    }
    
    public final void compute()
    {
      ToDoubleBiFunction localToDoubleBiFunction;
      DoubleBinaryOperator localDoubleBinaryOperator;
      if (((localToDoubleBiFunction = transformer) != null) && ((localDoubleBinaryOperator = reducer) != null))
      {
        double d = basis;
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceMappingsToDoubleTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localToDoubleBiFunction, d, localDoubleBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          d = localDoubleBinaryOperator.applyAsDouble(d, localToDoubleBiFunction.applyAsDouble(key, val));
        }
        result = d;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceMappingsToDoubleTask localMapReduceMappingsToDoubleTask1 = (MapReduceMappingsToDoubleTask)localObject;
          for (MapReduceMappingsToDoubleTask localMapReduceMappingsToDoubleTask2 = rights; localMapReduceMappingsToDoubleTask2 != null; localMapReduceMappingsToDoubleTask2 = rights = nextRight) {
            result = localDoubleBinaryOperator.applyAsDouble(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceMappingsToIntTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Integer>
  {
    final ToIntBiFunction<? super K, ? super V> transformer;
    final IntBinaryOperator reducer;
    final int basis;
    int result;
    MapReduceMappingsToIntTask<K, V> rights;
    MapReduceMappingsToIntTask<K, V> nextRight;
    
    MapReduceMappingsToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceMappingsToIntTask<K, V> paramMapReduceMappingsToIntTask, ToIntBiFunction<? super K, ? super V> paramToIntBiFunction, int paramInt4, IntBinaryOperator paramIntBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceMappingsToIntTask;
      transformer = paramToIntBiFunction;
      basis = paramInt4;
      reducer = paramIntBinaryOperator;
    }
    
    public final Integer getRawResult()
    {
      return Integer.valueOf(result);
    }
    
    public final void compute()
    {
      ToIntBiFunction localToIntBiFunction;
      IntBinaryOperator localIntBinaryOperator;
      if (((localToIntBiFunction = transformer) != null) && ((localIntBinaryOperator = reducer) != null))
      {
        int i = basis;
        int j = baseIndex;
        int k;
        int m;
        while ((batch > 0) && ((m = (k = baseLimit) + j >>> 1) > j))
        {
          addToPendingCount(1);
          (rights = new MapReduceMappingsToIntTask(this, batch >>>= 1, baseLimit = m, k, tab, rights, localToIntBiFunction, i, localIntBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          i = localIntBinaryOperator.applyAsInt(i, localToIntBiFunction.applyAsInt(key, val));
        }
        result = i;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceMappingsToIntTask localMapReduceMappingsToIntTask1 = (MapReduceMappingsToIntTask)localObject;
          for (MapReduceMappingsToIntTask localMapReduceMappingsToIntTask2 = rights; localMapReduceMappingsToIntTask2 != null; localMapReduceMappingsToIntTask2 = rights = nextRight) {
            result = localIntBinaryOperator.applyAsInt(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceMappingsToLongTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Long>
  {
    final ToLongBiFunction<? super K, ? super V> transformer;
    final LongBinaryOperator reducer;
    final long basis;
    long result;
    MapReduceMappingsToLongTask<K, V> rights;
    MapReduceMappingsToLongTask<K, V> nextRight;
    
    MapReduceMappingsToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceMappingsToLongTask<K, V> paramMapReduceMappingsToLongTask, ToLongBiFunction<? super K, ? super V> paramToLongBiFunction, long paramLong, LongBinaryOperator paramLongBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceMappingsToLongTask;
      transformer = paramToLongBiFunction;
      basis = paramLong;
      reducer = paramLongBinaryOperator;
    }
    
    public final Long getRawResult()
    {
      return Long.valueOf(result);
    }
    
    public final void compute()
    {
      ToLongBiFunction localToLongBiFunction;
      LongBinaryOperator localLongBinaryOperator;
      if (((localToLongBiFunction = transformer) != null) && ((localLongBinaryOperator = reducer) != null))
      {
        long l = basis;
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceMappingsToLongTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localToLongBiFunction, l, localLongBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          l = localLongBinaryOperator.applyAsLong(l, localToLongBiFunction.applyAsLong(key, val));
        }
        result = l;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceMappingsToLongTask localMapReduceMappingsToLongTask1 = (MapReduceMappingsToLongTask)localObject;
          for (MapReduceMappingsToLongTask localMapReduceMappingsToLongTask2 = rights; localMapReduceMappingsToLongTask2 != null; localMapReduceMappingsToLongTask2 = rights = nextRight) {
            result = localLongBinaryOperator.applyAsLong(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceValuesTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, U>
  {
    final Function<? super V, ? extends U> transformer;
    final BiFunction<? super U, ? super U, ? extends U> reducer;
    U result;
    MapReduceValuesTask<K, V, U> rights;
    MapReduceValuesTask<K, V, U> nextRight;
    
    MapReduceValuesTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceValuesTask<K, V, U> paramMapReduceValuesTask, Function<? super V, ? extends U> paramFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceValuesTask;
      transformer = paramFunction;
      reducer = paramBiFunction;
    }
    
    public final U getRawResult()
    {
      return (U)result;
    }
    
    public final void compute()
    {
      Function localFunction;
      BiFunction localBiFunction;
      if (((localFunction = transformer) != null) && ((localBiFunction = reducer) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceValuesTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localFunction, localBiFunction)).fork();
        }
        Object localObject1 = null;
        Object localObject3;
        while ((localObject2 = advance()) != null) {
          if ((localObject3 = localFunction.apply(val)) != null) {
            localObject1 = localObject1 == null ? localObject3 : localBiFunction.apply(localObject1, localObject3);
          }
        }
        result = localObject1;
        for (Object localObject2 = firstComplete(); localObject2 != null; localObject2 = ((CountedCompleter)localObject2).nextComplete())
        {
          localObject3 = (MapReduceValuesTask)localObject2;
          for (MapReduceValuesTask localMapReduceValuesTask = rights; localMapReduceValuesTask != null; localMapReduceValuesTask = rights = nextRight)
          {
            Object localObject5;
            if ((localObject5 = result) != null)
            {
              Object localObject4;
              result = ((localObject4 = result) == null ? localObject5 : localBiFunction.apply(localObject4, localObject5));
            }
          }
        }
      }
    }
  }
  
  static final class MapReduceValuesToDoubleTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Double>
  {
    final ToDoubleFunction<? super V> transformer;
    final DoubleBinaryOperator reducer;
    final double basis;
    double result;
    MapReduceValuesToDoubleTask<K, V> rights;
    MapReduceValuesToDoubleTask<K, V> nextRight;
    
    MapReduceValuesToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceValuesToDoubleTask<K, V> paramMapReduceValuesToDoubleTask, ToDoubleFunction<? super V> paramToDoubleFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceValuesToDoubleTask;
      transformer = paramToDoubleFunction;
      basis = paramDouble;
      reducer = paramDoubleBinaryOperator;
    }
    
    public final Double getRawResult()
    {
      return Double.valueOf(result);
    }
    
    public final void compute()
    {
      ToDoubleFunction localToDoubleFunction;
      DoubleBinaryOperator localDoubleBinaryOperator;
      if (((localToDoubleFunction = transformer) != null) && ((localDoubleBinaryOperator = reducer) != null))
      {
        double d = basis;
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceValuesToDoubleTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localToDoubleFunction, d, localDoubleBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          d = localDoubleBinaryOperator.applyAsDouble(d, localToDoubleFunction.applyAsDouble(val));
        }
        result = d;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceValuesToDoubleTask localMapReduceValuesToDoubleTask1 = (MapReduceValuesToDoubleTask)localObject;
          for (MapReduceValuesToDoubleTask localMapReduceValuesToDoubleTask2 = rights; localMapReduceValuesToDoubleTask2 != null; localMapReduceValuesToDoubleTask2 = rights = nextRight) {
            result = localDoubleBinaryOperator.applyAsDouble(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceValuesToIntTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Integer>
  {
    final ToIntFunction<? super V> transformer;
    final IntBinaryOperator reducer;
    final int basis;
    int result;
    MapReduceValuesToIntTask<K, V> rights;
    MapReduceValuesToIntTask<K, V> nextRight;
    
    MapReduceValuesToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceValuesToIntTask<K, V> paramMapReduceValuesToIntTask, ToIntFunction<? super V> paramToIntFunction, int paramInt4, IntBinaryOperator paramIntBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceValuesToIntTask;
      transformer = paramToIntFunction;
      basis = paramInt4;
      reducer = paramIntBinaryOperator;
    }
    
    public final Integer getRawResult()
    {
      return Integer.valueOf(result);
    }
    
    public final void compute()
    {
      ToIntFunction localToIntFunction;
      IntBinaryOperator localIntBinaryOperator;
      if (((localToIntFunction = transformer) != null) && ((localIntBinaryOperator = reducer) != null))
      {
        int i = basis;
        int j = baseIndex;
        int k;
        int m;
        while ((batch > 0) && ((m = (k = baseLimit) + j >>> 1) > j))
        {
          addToPendingCount(1);
          (rights = new MapReduceValuesToIntTask(this, batch >>>= 1, baseLimit = m, k, tab, rights, localToIntFunction, i, localIntBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          i = localIntBinaryOperator.applyAsInt(i, localToIntFunction.applyAsInt(val));
        }
        result = i;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceValuesToIntTask localMapReduceValuesToIntTask1 = (MapReduceValuesToIntTask)localObject;
          for (MapReduceValuesToIntTask localMapReduceValuesToIntTask2 = rights; localMapReduceValuesToIntTask2 != null; localMapReduceValuesToIntTask2 = rights = nextRight) {
            result = localIntBinaryOperator.applyAsInt(result, result);
          }
        }
      }
    }
  }
  
  static final class MapReduceValuesToLongTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Long>
  {
    final ToLongFunction<? super V> transformer;
    final LongBinaryOperator reducer;
    final long basis;
    long result;
    MapReduceValuesToLongTask<K, V> rights;
    MapReduceValuesToLongTask<K, V> nextRight;
    
    MapReduceValuesToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, MapReduceValuesToLongTask<K, V> paramMapReduceValuesToLongTask, ToLongFunction<? super V> paramToLongFunction, long paramLong, LongBinaryOperator paramLongBinaryOperator)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramMapReduceValuesToLongTask;
      transformer = paramToLongFunction;
      basis = paramLong;
      reducer = paramLongBinaryOperator;
    }
    
    public final Long getRawResult()
    {
      return Long.valueOf(result);
    }
    
    public final void compute()
    {
      ToLongFunction localToLongFunction;
      LongBinaryOperator localLongBinaryOperator;
      if (((localToLongFunction = transformer) != null) && ((localLongBinaryOperator = reducer) != null))
      {
        long l = basis;
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new MapReduceValuesToLongTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localToLongFunction, l, localLongBinaryOperator)).fork();
        }
        while ((localObject = advance()) != null) {
          l = localLongBinaryOperator.applyAsLong(l, localToLongFunction.applyAsLong(val));
        }
        result = l;
        for (Object localObject = firstComplete(); localObject != null; localObject = ((CountedCompleter)localObject).nextComplete())
        {
          MapReduceValuesToLongTask localMapReduceValuesToLongTask1 = (MapReduceValuesToLongTask)localObject;
          for (MapReduceValuesToLongTask localMapReduceValuesToLongTask2 = rights; localMapReduceValuesToLongTask2 != null; localMapReduceValuesToLongTask2 = rights = nextRight) {
            result = localLongBinaryOperator.applyAsLong(result, result);
          }
        }
      }
    }
  }
  
  static class Node<K, V>
    implements Map.Entry<K, V>
  {
    final int hash;
    final K key;
    volatile V val;
    volatile Node<K, V> next;
    
    Node(int paramInt, K paramK, V paramV, Node<K, V> paramNode)
    {
      hash = paramInt;
      key = paramK;
      val = paramV;
      next = paramNode;
    }
    
    public final K getKey()
    {
      return (K)key;
    }
    
    public final V getValue()
    {
      return (V)val;
    }
    
    public final int hashCode()
    {
      return key.hashCode() ^ val.hashCode();
    }
    
    public final String toString()
    {
      return key + "=" + val;
    }
    
    public final V setValue(V paramV)
    {
      throw new UnsupportedOperationException();
    }
    
    public final boolean equals(Object paramObject)
    {
      Map.Entry localEntry;
      Object localObject1;
      Object localObject2;
      Object localObject3;
      return ((paramObject instanceof Map.Entry)) && ((localObject1 = (localEntry = (Map.Entry)paramObject).getKey()) != null) && ((localObject2 = localEntry.getValue()) != null) && ((localObject1 == key) || (localObject1.equals(key))) && ((localObject2 == (localObject3 = val)) || (localObject2.equals(localObject3)));
    }
    
    Node<K, V> find(int paramInt, Object paramObject)
    {
      Node localNode = this;
      if (paramObject != null) {
        do
        {
          Object localObject;
          if ((hash == paramInt) && (((localObject = key) == paramObject) || ((localObject != null) && (paramObject.equals(localObject))))) {
            return localNode;
          }
        } while ((localNode = next) != null);
      }
      return null;
    }
  }
  
  static final class ReduceEntriesTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, Map.Entry<K, V>>
  {
    final BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
    Map.Entry<K, V> result;
    ReduceEntriesTask<K, V> rights;
    ReduceEntriesTask<K, V> nextRight;
    
    ReduceEntriesTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, ReduceEntriesTask<K, V> paramReduceEntriesTask, BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> paramBiFunction)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramReduceEntriesTask;
      reducer = paramBiFunction;
    }
    
    public final Map.Entry<K, V> getRawResult()
    {
      return result;
    }
    
    public final void compute()
    {
      BiFunction localBiFunction;
      if ((localBiFunction = reducer) != null)
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new ReduceEntriesTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localBiFunction)).fork();
        }
        for (Object localObject1 = null; (localObject2 = advance()) != null; localObject1 = localObject1 == null ? localObject2 : (Map.Entry)localBiFunction.apply(localObject1, localObject2)) {}
        result = ((Map.Entry)localObject1);
        for (Object localObject2 = firstComplete(); localObject2 != null; localObject2 = ((CountedCompleter)localObject2).nextComplete())
        {
          ReduceEntriesTask localReduceEntriesTask1 = (ReduceEntriesTask)localObject2;
          for (ReduceEntriesTask localReduceEntriesTask2 = rights; localReduceEntriesTask2 != null; localReduceEntriesTask2 = rights = nextRight)
          {
            Map.Entry localEntry2;
            if ((localEntry2 = result) != null)
            {
              Map.Entry localEntry1;
              result = ((localEntry1 = result) == null ? localEntry2 : (Map.Entry)localBiFunction.apply(localEntry1, localEntry2));
            }
          }
        }
      }
    }
  }
  
  static final class ReduceKeysTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, K>
  {
    final BiFunction<? super K, ? super K, ? extends K> reducer;
    K result;
    ReduceKeysTask<K, V> rights;
    ReduceKeysTask<K, V> nextRight;
    
    ReduceKeysTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, ReduceKeysTask<K, V> paramReduceKeysTask, BiFunction<? super K, ? super K, ? extends K> paramBiFunction)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramReduceKeysTask;
      reducer = paramBiFunction;
    }
    
    public final K getRawResult()
    {
      return (K)result;
    }
    
    public final void compute()
    {
      BiFunction localBiFunction;
      if ((localBiFunction = reducer) != null)
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new ReduceKeysTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localBiFunction)).fork();
        }
        Object localObject3;
        for (Object localObject1 = null; (localObject2 = advance()) != null; localObject1 = localObject3 == null ? localObject1 : localObject1 == null ? localObject3 : localBiFunction.apply(localObject1, localObject3)) {
          localObject3 = key;
        }
        result = localObject1;
        for (Object localObject2 = firstComplete(); localObject2 != null; localObject2 = ((CountedCompleter)localObject2).nextComplete())
        {
          localObject3 = (ReduceKeysTask)localObject2;
          for (ReduceKeysTask localReduceKeysTask = rights; localReduceKeysTask != null; localReduceKeysTask = rights = nextRight)
          {
            Object localObject5;
            if ((localObject5 = result) != null)
            {
              Object localObject4;
              result = ((localObject4 = result) == null ? localObject5 : localBiFunction.apply(localObject4, localObject5));
            }
          }
        }
      }
    }
  }
  
  static final class ReduceValuesTask<K, V>
    extends ConcurrentHashMap.BulkTask<K, V, V>
  {
    final BiFunction<? super V, ? super V, ? extends V> reducer;
    V result;
    ReduceValuesTask<K, V> rights;
    ReduceValuesTask<K, V> nextRight;
    
    ReduceValuesTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, ReduceValuesTask<K, V> paramReduceValuesTask, BiFunction<? super V, ? super V, ? extends V> paramBiFunction)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      nextRight = paramReduceValuesTask;
      reducer = paramBiFunction;
    }
    
    public final V getRawResult()
    {
      return (V)result;
    }
    
    public final void compute()
    {
      BiFunction localBiFunction;
      if ((localBiFunction = reducer) != null)
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          addToPendingCount(1);
          (rights = new ReduceValuesTask(this, batch >>>= 1, baseLimit = k, j, tab, rights, localBiFunction)).fork();
        }
        Object localObject3;
        for (Object localObject1 = null; (localObject2 = advance()) != null; localObject1 = localObject1 == null ? localObject3 : localBiFunction.apply(localObject1, localObject3)) {
          localObject3 = val;
        }
        result = localObject1;
        for (Object localObject2 = firstComplete(); localObject2 != null; localObject2 = ((CountedCompleter)localObject2).nextComplete())
        {
          localObject3 = (ReduceValuesTask)localObject2;
          for (ReduceValuesTask localReduceValuesTask = rights; localReduceValuesTask != null; localReduceValuesTask = rights = nextRight)
          {
            Object localObject5;
            if ((localObject5 = result) != null)
            {
              Object localObject4;
              result = ((localObject4 = result) == null ? localObject5 : localBiFunction.apply(localObject4, localObject5));
            }
          }
        }
      }
    }
  }
  
  static final class ReservationNode<K, V>
    extends ConcurrentHashMap.Node<K, V>
  {
    ReservationNode()
    {
      super(null, null, null);
    }
    
    ConcurrentHashMap.Node<K, V> find(int paramInt, Object paramObject)
    {
      return null;
    }
  }
  
  static final class SearchEntriesTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, U>
  {
    final Function<Map.Entry<K, V>, ? extends U> searchFunction;
    final AtomicReference<U> result;
    
    SearchEntriesTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, Function<Map.Entry<K, V>, ? extends U> paramFunction, AtomicReference<U> paramAtomicReference)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      searchFunction = paramFunction;
      result = paramAtomicReference;
    }
    
    public final U getRawResult()
    {
      return (U)result.get();
    }
    
    public final void compute()
    {
      Function localFunction;
      AtomicReference localAtomicReference;
      if (((localFunction = searchFunction) != null) && ((localAtomicReference = result) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          if (localAtomicReference.get() != null) {
            return;
          }
          addToPendingCount(1);
          new SearchEntriesTask(this, batch >>>= 1, baseLimit = k, j, tab, localFunction, localAtomicReference).fork();
        }
        while (localAtomicReference.get() == null)
        {
          ConcurrentHashMap.Node localNode;
          if ((localNode = advance()) == null)
          {
            propagateCompletion();
            break;
          }
          Object localObject;
          if ((localObject = localFunction.apply(localNode)) != null)
          {
            if (localAtomicReference.compareAndSet(null, localObject)) {
              quietlyCompleteRoot();
            }
            return;
          }
        }
      }
    }
  }
  
  static final class SearchKeysTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, U>
  {
    final Function<? super K, ? extends U> searchFunction;
    final AtomicReference<U> result;
    
    SearchKeysTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, Function<? super K, ? extends U> paramFunction, AtomicReference<U> paramAtomicReference)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      searchFunction = paramFunction;
      result = paramAtomicReference;
    }
    
    public final U getRawResult()
    {
      return (U)result.get();
    }
    
    public final void compute()
    {
      Function localFunction;
      AtomicReference localAtomicReference;
      if (((localFunction = searchFunction) != null) && ((localAtomicReference = result) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          if (localAtomicReference.get() != null) {
            return;
          }
          addToPendingCount(1);
          new SearchKeysTask(this, batch >>>= 1, baseLimit = k, j, tab, localFunction, localAtomicReference).fork();
        }
        while (localAtomicReference.get() == null)
        {
          ConcurrentHashMap.Node localNode;
          if ((localNode = advance()) == null)
          {
            propagateCompletion();
            break;
          }
          Object localObject;
          if ((localObject = localFunction.apply(key)) != null)
          {
            if (!localAtomicReference.compareAndSet(null, localObject)) {
              break;
            }
            quietlyCompleteRoot();
            break;
          }
        }
      }
    }
  }
  
  static final class SearchMappingsTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, U>
  {
    final BiFunction<? super K, ? super V, ? extends U> searchFunction;
    final AtomicReference<U> result;
    
    SearchMappingsTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, BiFunction<? super K, ? super V, ? extends U> paramBiFunction, AtomicReference<U> paramAtomicReference)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      searchFunction = paramBiFunction;
      result = paramAtomicReference;
    }
    
    public final U getRawResult()
    {
      return (U)result.get();
    }
    
    public final void compute()
    {
      BiFunction localBiFunction;
      AtomicReference localAtomicReference;
      if (((localBiFunction = searchFunction) != null) && ((localAtomicReference = result) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          if (localAtomicReference.get() != null) {
            return;
          }
          addToPendingCount(1);
          new SearchMappingsTask(this, batch >>>= 1, baseLimit = k, j, tab, localBiFunction, localAtomicReference).fork();
        }
        while (localAtomicReference.get() == null)
        {
          ConcurrentHashMap.Node localNode;
          if ((localNode = advance()) == null)
          {
            propagateCompletion();
            break;
          }
          Object localObject;
          if ((localObject = localBiFunction.apply(key, val)) != null)
          {
            if (!localAtomicReference.compareAndSet(null, localObject)) {
              break;
            }
            quietlyCompleteRoot();
            break;
          }
        }
      }
    }
  }
  
  static final class SearchValuesTask<K, V, U>
    extends ConcurrentHashMap.BulkTask<K, V, U>
  {
    final Function<? super V, ? extends U> searchFunction;
    final AtomicReference<U> result;
    
    SearchValuesTask(ConcurrentHashMap.BulkTask<K, V, ?> paramBulkTask, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, Function<? super V, ? extends U> paramFunction, AtomicReference<U> paramAtomicReference)
    {
      super(paramInt1, paramInt2, paramInt3, paramArrayOfNode);
      searchFunction = paramFunction;
      result = paramAtomicReference;
    }
    
    public final U getRawResult()
    {
      return (U)result.get();
    }
    
    public final void compute()
    {
      Function localFunction;
      AtomicReference localAtomicReference;
      if (((localFunction = searchFunction) != null) && ((localAtomicReference = result) != null))
      {
        int i = baseIndex;
        int j;
        int k;
        while ((batch > 0) && ((k = (j = baseLimit) + i >>> 1) > i))
        {
          if (localAtomicReference.get() != null) {
            return;
          }
          addToPendingCount(1);
          new SearchValuesTask(this, batch >>>= 1, baseLimit = k, j, tab, localFunction, localAtomicReference).fork();
        }
        while (localAtomicReference.get() == null)
        {
          ConcurrentHashMap.Node localNode;
          if ((localNode = advance()) == null)
          {
            propagateCompletion();
            break;
          }
          Object localObject;
          if ((localObject = localFunction.apply(val)) != null)
          {
            if (!localAtomicReference.compareAndSet(null, localObject)) {
              break;
            }
            quietlyCompleteRoot();
            break;
          }
        }
      }
    }
  }
  
  static class Segment<K, V>
    extends ReentrantLock
    implements Serializable
  {
    private static final long serialVersionUID = 2249069246763182397L;
    final float loadFactor;
    
    Segment(float paramFloat)
    {
      loadFactor = paramFloat;
    }
  }
  
  static final class TableStack<K, V>
  {
    int length;
    int index;
    ConcurrentHashMap.Node<K, V>[] tab;
    TableStack<K, V> next;
    
    TableStack() {}
  }
  
  static class Traverser<K, V>
  {
    ConcurrentHashMap.Node<K, V>[] tab;
    ConcurrentHashMap.Node<K, V> next;
    ConcurrentHashMap.TableStack<K, V> stack;
    ConcurrentHashMap.TableStack<K, V> spare;
    int index;
    int baseIndex;
    int baseLimit;
    final int baseSize;
    
    Traverser(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2, int paramInt3)
    {
      tab = paramArrayOfNode;
      baseSize = paramInt1;
      baseIndex = (index = paramInt2);
      baseLimit = paramInt3;
      next = null;
    }
    
    final ConcurrentHashMap.Node<K, V> advance()
    {
      Object localObject;
      if ((localObject = next) != null) {
        localObject = next;
      }
      for (;;)
      {
        if (localObject != null) {
          return (ConcurrentHashMap.Node<K, V>)(next = localObject);
        }
        ConcurrentHashMap.Node[] arrayOfNode;
        int j;
        int i;
        if ((baseIndex >= baseLimit) || ((arrayOfNode = tab) == null) || ((j = arrayOfNode.length) <= (i = index)) || (i < 0)) {
          return next = null;
        }
        if (((localObject = ConcurrentHashMap.tabAt(arrayOfNode, i)) != null) && (hash < 0))
        {
          if ((localObject instanceof ConcurrentHashMap.ForwardingNode))
          {
            tab = nextTable;
            localObject = null;
            pushState(arrayOfNode, i, j);
            continue;
          }
          if ((localObject instanceof ConcurrentHashMap.TreeBin)) {
            localObject = first;
          } else {
            localObject = null;
          }
        }
        if (stack != null) {
          recoverState(j);
        } else if ((index = i + baseSize) >= j) {
          index = (++baseIndex);
        }
      }
    }
    
    private void pushState(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2)
    {
      ConcurrentHashMap.TableStack localTableStack = spare;
      if (localTableStack != null) {
        spare = next;
      } else {
        localTableStack = new ConcurrentHashMap.TableStack();
      }
      tab = paramArrayOfNode;
      length = paramInt2;
      index = paramInt1;
      next = stack;
      stack = localTableStack;
    }
    
    private void recoverState(int paramInt)
    {
      ConcurrentHashMap.TableStack localTableStack1;
      int i;
      while (((localTableStack1 = stack) != null) && (index += (i = length) >= paramInt))
      {
        paramInt = i;
        index = index;
        tab = tab;
        tab = null;
        ConcurrentHashMap.TableStack localTableStack2 = next;
        next = spare;
        stack = localTableStack2;
        spare = localTableStack1;
      }
      if ((localTableStack1 == null) && (index += baseSize >= paramInt)) {
        index = (++baseIndex);
      }
    }
  }
  
  static final class TreeBin<K, V>
    extends ConcurrentHashMap.Node<K, V>
  {
    ConcurrentHashMap.TreeNode<K, V> root;
    volatile ConcurrentHashMap.TreeNode<K, V> first;
    volatile Thread waiter;
    volatile int lockState;
    static final int WRITER = 1;
    static final int WAITER = 2;
    static final int READER = 4;
    private static final Unsafe U;
    private static final long LOCKSTATE;
    
    static int tieBreakOrder(Object paramObject1, Object paramObject2)
    {
      int i;
      if ((paramObject1 == null) || (paramObject2 == null) || ((i = paramObject1.getClass().getName().compareTo(paramObject2.getClass().getName())) == 0)) {
        i = System.identityHashCode(paramObject1) <= System.identityHashCode(paramObject2) ? -1 : 1;
      }
      return i;
    }
    
    TreeBin(ConcurrentHashMap.TreeNode<K, V> paramTreeNode)
    {
      super(null, null, null);
      first = paramTreeNode;
      Object localObject1 = null;
      ConcurrentHashMap.TreeNode localTreeNode;
      for (Object localObject2 = paramTreeNode; localObject2 != null; localObject2 = localTreeNode)
      {
        localTreeNode = (ConcurrentHashMap.TreeNode)next;
        left = (right = null);
        if (localObject1 == null)
        {
          parent = null;
          red = false;
          localObject1 = localObject2;
        }
        else
        {
          Object localObject3 = key;
          int i = hash;
          Class localClass = null;
          Object localObject4 = localObject1;
          for (;;)
          {
            Object localObject5 = key;
            int k;
            int j;
            if ((k = hash) > i) {
              j = -1;
            } else if (k < i) {
              j = 1;
            } else if (((localClass == null) && ((localClass = ConcurrentHashMap.comparableClassFor(localObject3)) == null)) || ((j = ConcurrentHashMap.compareComparables(localClass, localObject3, localObject5)) == 0)) {
              j = tieBreakOrder(localObject3, localObject5);
            }
            Object localObject6 = localObject4;
            if ((localObject4 = j <= 0 ? left : right) == null)
            {
              parent = ((ConcurrentHashMap.TreeNode)localObject6);
              if (j <= 0) {
                left = ((ConcurrentHashMap.TreeNode)localObject2);
              } else {
                right = ((ConcurrentHashMap.TreeNode)localObject2);
              }
              localObject1 = balanceInsertion((ConcurrentHashMap.TreeNode)localObject1, (ConcurrentHashMap.TreeNode)localObject2);
              break;
            }
          }
        }
      }
      root = ((ConcurrentHashMap.TreeNode)localObject1);
      assert (checkInvariants(root));
    }
    
    private final void lockRoot()
    {
      if (!U.compareAndSwapInt(this, LOCKSTATE, 0, 1)) {
        contendedLock();
      }
    }
    
    private final void unlockRoot()
    {
      lockState = 0;
    }
    
    private final void contendedLock()
    {
      int i = 0;
      for (;;)
      {
        int j;
        if (((j = lockState) & 0xFFFFFFFD) == 0)
        {
          if (U.compareAndSwapInt(this, LOCKSTATE, j, 1)) {
            if (i != 0) {
              waiter = null;
            }
          }
        }
        else if ((j & 0x2) == 0)
        {
          if (U.compareAndSwapInt(this, LOCKSTATE, j, j | 0x2))
          {
            i = 1;
            waiter = Thread.currentThread();
          }
        }
        else if (i != 0) {
          LockSupport.park(this);
        }
      }
    }
    
    final ConcurrentHashMap.Node<K, V> find(int paramInt, Object paramObject)
    {
      if (paramObject != null)
      {
        Object localObject1 = first;
        while (localObject1 != null)
        {
          int i;
          if (((i = lockState) & 0x3) != 0)
          {
            Object localObject2;
            if ((hash == paramInt) && (((localObject2 = key) == paramObject) || ((localObject2 != null) && (paramObject.equals(localObject2))))) {
              return (ConcurrentHashMap.Node<K, V>)localObject1;
            }
            localObject1 = next;
          }
          else if (U.compareAndSwapInt(this, LOCKSTATE, i, i + 4))
          {
            ConcurrentHashMap.TreeNode localTreeNode2;
            try
            {
              ConcurrentHashMap.TreeNode localTreeNode1;
              localTreeNode2 = (localTreeNode1 = root) == null ? null : localTreeNode1.findTreeNode(paramInt, paramObject, null);
            }
            finally
            {
              Thread localThread1;
              Thread localThread2;
              if ((U.getAndAddInt(this, LOCKSTATE, -4) == 6) && ((localThread2 = waiter) != null)) {
                LockSupport.unpark(localThread2);
              }
            }
            return localTreeNode2;
          }
        }
      }
      return null;
    }
    
    final ConcurrentHashMap.TreeNode<K, V> putTreeVal(int paramInt, K paramK, V paramV)
    {
      Class localClass = null;
      int i = 0;
      ConcurrentHashMap.TreeNode localTreeNode1 = root;
      for (;;)
      {
        if (localTreeNode1 == null)
        {
          first = (root = new ConcurrentHashMap.TreeNode(paramInt, paramK, paramV, null, null));
          break;
        }
        int k;
        int j;
        ConcurrentHashMap.TreeNode localTreeNode3;
        if ((k = hash) > paramInt)
        {
          j = -1;
        }
        else if (k < paramInt)
        {
          j = 1;
        }
        else
        {
          Object localObject1;
          if (((localObject1 = key) == paramK) || ((localObject1 != null) && (paramK.equals(localObject1)))) {
            return localTreeNode1;
          }
          if (((localClass == null) && ((localClass = ConcurrentHashMap.comparableClassFor(paramK)) == null)) || ((j = ConcurrentHashMap.compareComparables(localClass, paramK, localObject1)) == 0))
          {
            if (i == 0)
            {
              i = 1;
              if ((((localTreeNode3 = left) != null) && ((localTreeNode2 = localTreeNode3.findTreeNode(paramInt, paramK, localClass)) != null)) || (((localTreeNode3 = right) != null) && ((localTreeNode2 = localTreeNode3.findTreeNode(paramInt, paramK, localClass)) != null))) {
                return localTreeNode2;
              }
            }
            j = tieBreakOrder(paramK, localObject1);
          }
        }
        ConcurrentHashMap.TreeNode localTreeNode2 = localTreeNode1;
        if ((localTreeNode1 = j <= 0 ? left : right) == null)
        {
          ConcurrentHashMap.TreeNode localTreeNode4 = first;
          first = (localTreeNode3 = new ConcurrentHashMap.TreeNode(paramInt, paramK, paramV, localTreeNode4, localTreeNode2));
          if (localTreeNode4 != null) {
            prev = localTreeNode3;
          }
          if (j <= 0) {
            left = localTreeNode3;
          } else {
            right = localTreeNode3;
          }
          if (!red)
          {
            red = true;
            break;
          }
          lockRoot();
          try
          {
            root = balanceInsertion(root, localTreeNode3);
          }
          finally
          {
            unlockRoot();
          }
          break;
        }
      }
      assert (checkInvariants(root));
      return null;
    }
    
    final boolean removeTreeNode(ConcurrentHashMap.TreeNode<K, V> paramTreeNode)
    {
      ConcurrentHashMap.TreeNode localTreeNode1 = (ConcurrentHashMap.TreeNode)next;
      ConcurrentHashMap.TreeNode localTreeNode2 = prev;
      if (localTreeNode2 == null) {
        first = localTreeNode1;
      } else {
        next = localTreeNode1;
      }
      if (localTreeNode1 != null) {
        prev = localTreeNode2;
      }
      if (first == null)
      {
        root = null;
        return true;
      }
      Object localObject1;
      ConcurrentHashMap.TreeNode localTreeNode3;
      if (((localObject1 = root) == null) || (right == null) || ((localTreeNode3 = left) == null) || (left == null)) {
        return true;
      }
      lockRoot();
      try
      {
        ConcurrentHashMap.TreeNode localTreeNode4 = left;
        ConcurrentHashMap.TreeNode localTreeNode5 = right;
        Object localObject3;
        Object localObject2;
        if ((localTreeNode4 != null) && (localTreeNode5 != null))
        {
          ConcurrentHashMap.TreeNode localTreeNode6;
          for (localObject3 = localTreeNode5; (localTreeNode6 = left) != null; localObject3 = localTreeNode6) {}
          boolean bool = red;
          red = red;
          red = bool;
          ConcurrentHashMap.TreeNode localTreeNode7 = right;
          ConcurrentHashMap.TreeNode localTreeNode8 = parent;
          if (localObject3 == localTreeNode5)
          {
            parent = ((ConcurrentHashMap.TreeNode)localObject3);
            right = paramTreeNode;
          }
          else
          {
            ConcurrentHashMap.TreeNode localTreeNode9 = parent;
            if ((parent = localTreeNode9) != null) {
              if (localObject3 == left) {
                left = paramTreeNode;
              } else {
                right = paramTreeNode;
              }
            }
            if ((right = localTreeNode5) != null) {
              parent = ((ConcurrentHashMap.TreeNode)localObject3);
            }
          }
          left = null;
          if ((right = localTreeNode7) != null) {
            parent = paramTreeNode;
          }
          if ((left = localTreeNode4) != null) {
            parent = ((ConcurrentHashMap.TreeNode)localObject3);
          }
          if ((parent = localTreeNode8) == null) {
            localObject1 = localObject3;
          } else if (paramTreeNode == left) {
            left = ((ConcurrentHashMap.TreeNode)localObject3);
          } else {
            right = ((ConcurrentHashMap.TreeNode)localObject3);
          }
          if (localTreeNode7 != null) {
            localObject2 = localTreeNode7;
          } else {
            localObject2 = paramTreeNode;
          }
        }
        else if (localTreeNode4 != null)
        {
          localObject2 = localTreeNode4;
        }
        else if (localTreeNode5 != null)
        {
          localObject2 = localTreeNode5;
        }
        else
        {
          localObject2 = paramTreeNode;
        }
        if (localObject2 != paramTreeNode)
        {
          localObject3 = parent = parent;
          if (localObject3 == null) {
            localObject1 = localObject2;
          } else if (paramTreeNode == left) {
            left = ((ConcurrentHashMap.TreeNode)localObject2);
          } else {
            right = ((ConcurrentHashMap.TreeNode)localObject2);
          }
          left = (right = parent = null);
        }
        root = (red ? localObject1 : balanceDeletion((ConcurrentHashMap.TreeNode)localObject1, (ConcurrentHashMap.TreeNode)localObject2));
        if ((paramTreeNode == localObject2) && ((localObject3 = parent) != null))
        {
          if (paramTreeNode == left) {
            left = null;
          } else if (paramTreeNode == right) {
            right = null;
          }
          parent = null;
        }
      }
      finally
      {
        unlockRoot();
      }
      assert (checkInvariants(root));
      return false;
    }
    
    static <K, V> ConcurrentHashMap.TreeNode<K, V> rotateLeft(ConcurrentHashMap.TreeNode<K, V> paramTreeNode1, ConcurrentHashMap.TreeNode<K, V> paramTreeNode2)
    {
      ConcurrentHashMap.TreeNode localTreeNode1;
      if ((paramTreeNode2 != null) && ((localTreeNode1 = right) != null))
      {
        ConcurrentHashMap.TreeNode localTreeNode3;
        if ((localTreeNode3 = right = left) != null) {
          parent = paramTreeNode2;
        }
        ConcurrentHashMap.TreeNode localTreeNode2;
        if ((localTreeNode2 = parent = parent) == null) {
          red = false;
        } else if (left == paramTreeNode2) {
          left = localTreeNode1;
        } else {
          right = localTreeNode1;
        }
        left = paramTreeNode2;
        parent = localTreeNode1;
      }
      return paramTreeNode1;
    }
    
    static <K, V> ConcurrentHashMap.TreeNode<K, V> rotateRight(ConcurrentHashMap.TreeNode<K, V> paramTreeNode1, ConcurrentHashMap.TreeNode<K, V> paramTreeNode2)
    {
      ConcurrentHashMap.TreeNode localTreeNode1;
      if ((paramTreeNode2 != null) && ((localTreeNode1 = left) != null))
      {
        ConcurrentHashMap.TreeNode localTreeNode3;
        if ((localTreeNode3 = left = right) != null) {
          parent = paramTreeNode2;
        }
        ConcurrentHashMap.TreeNode localTreeNode2;
        if ((localTreeNode2 = parent = parent) == null) {
          red = false;
        } else if (right == paramTreeNode2) {
          right = localTreeNode1;
        } else {
          left = localTreeNode1;
        }
        right = paramTreeNode2;
        parent = localTreeNode1;
      }
      return paramTreeNode1;
    }
    
    static <K, V> ConcurrentHashMap.TreeNode<K, V> balanceInsertion(ConcurrentHashMap.TreeNode<K, V> paramTreeNode1, ConcurrentHashMap.TreeNode<K, V> paramTreeNode2)
    {
      red = true;
      for (;;)
      {
        ConcurrentHashMap.TreeNode localTreeNode1;
        if ((localTreeNode1 = parent) == null)
        {
          red = false;
          return paramTreeNode2;
        }
        ConcurrentHashMap.TreeNode localTreeNode2;
        if ((!red) || ((localTreeNode2 = parent) == null)) {
          return paramTreeNode1;
        }
        ConcurrentHashMap.TreeNode localTreeNode3;
        if (localTreeNode1 == (localTreeNode3 = left))
        {
          ConcurrentHashMap.TreeNode localTreeNode4;
          if (((localTreeNode4 = right) != null) && (red))
          {
            red = false;
            red = false;
            red = true;
            paramTreeNode2 = localTreeNode2;
          }
          else
          {
            if (paramTreeNode2 == right)
            {
              paramTreeNode1 = rotateLeft(paramTreeNode1, paramTreeNode2 = localTreeNode1);
              localTreeNode2 = (localTreeNode1 = parent) == null ? null : parent;
            }
            if (localTreeNode1 != null)
            {
              red = false;
              if (localTreeNode2 != null)
              {
                red = true;
                paramTreeNode1 = rotateRight(paramTreeNode1, localTreeNode2);
              }
            }
          }
        }
        else if ((localTreeNode3 != null) && (red))
        {
          red = false;
          red = false;
          red = true;
          paramTreeNode2 = localTreeNode2;
        }
        else
        {
          if (paramTreeNode2 == left)
          {
            paramTreeNode1 = rotateRight(paramTreeNode1, paramTreeNode2 = localTreeNode1);
            localTreeNode2 = (localTreeNode1 = parent) == null ? null : parent;
          }
          if (localTreeNode1 != null)
          {
            red = false;
            if (localTreeNode2 != null)
            {
              red = true;
              paramTreeNode1 = rotateLeft(paramTreeNode1, localTreeNode2);
            }
          }
        }
      }
    }
    
    static <K, V> ConcurrentHashMap.TreeNode<K, V> balanceDeletion(ConcurrentHashMap.TreeNode<K, V> paramTreeNode1, ConcurrentHashMap.TreeNode<K, V> paramTreeNode2)
    {
      for (;;)
      {
        if ((paramTreeNode2 == null) || (paramTreeNode2 == paramTreeNode1)) {
          return paramTreeNode1;
        }
        ConcurrentHashMap.TreeNode localTreeNode1;
        if ((localTreeNode1 = parent) == null)
        {
          red = false;
          return paramTreeNode2;
        }
        if (red)
        {
          red = false;
          return paramTreeNode1;
        }
        ConcurrentHashMap.TreeNode localTreeNode2;
        ConcurrentHashMap.TreeNode localTreeNode4;
        ConcurrentHashMap.TreeNode localTreeNode5;
        if ((localTreeNode2 = left) == paramTreeNode2)
        {
          ConcurrentHashMap.TreeNode localTreeNode3;
          if (((localTreeNode3 = right) != null) && (red))
          {
            red = false;
            red = true;
            paramTreeNode1 = rotateLeft(paramTreeNode1, localTreeNode1);
            localTreeNode3 = (localTreeNode1 = parent) == null ? null : right;
          }
          if (localTreeNode3 == null)
          {
            paramTreeNode2 = localTreeNode1;
          }
          else
          {
            localTreeNode4 = left;
            localTreeNode5 = right;
            if (((localTreeNode5 == null) || (!red)) && ((localTreeNode4 == null) || (!red)))
            {
              red = true;
              paramTreeNode2 = localTreeNode1;
            }
            else
            {
              if ((localTreeNode5 == null) || (!red))
              {
                if (localTreeNode4 != null) {
                  red = false;
                }
                red = true;
                paramTreeNode1 = rotateRight(paramTreeNode1, localTreeNode3);
                localTreeNode3 = (localTreeNode1 = parent) == null ? null : right;
              }
              if (localTreeNode3 != null)
              {
                red = (localTreeNode1 == null ? false : red);
                if ((localTreeNode5 = right) != null) {
                  red = false;
                }
              }
              if (localTreeNode1 != null)
              {
                red = false;
                paramTreeNode1 = rotateLeft(paramTreeNode1, localTreeNode1);
              }
              paramTreeNode2 = paramTreeNode1;
            }
          }
        }
        else
        {
          if ((localTreeNode2 != null) && (red))
          {
            red = false;
            red = true;
            paramTreeNode1 = rotateRight(paramTreeNode1, localTreeNode1);
            localTreeNode2 = (localTreeNode1 = parent) == null ? null : left;
          }
          if (localTreeNode2 == null)
          {
            paramTreeNode2 = localTreeNode1;
          }
          else
          {
            localTreeNode4 = left;
            localTreeNode5 = right;
            if (((localTreeNode4 == null) || (!red)) && ((localTreeNode5 == null) || (!red)))
            {
              red = true;
              paramTreeNode2 = localTreeNode1;
            }
            else
            {
              if ((localTreeNode4 == null) || (!red))
              {
                if (localTreeNode5 != null) {
                  red = false;
                }
                red = true;
                paramTreeNode1 = rotateLeft(paramTreeNode1, localTreeNode2);
                localTreeNode2 = (localTreeNode1 = parent) == null ? null : left;
              }
              if (localTreeNode2 != null)
              {
                red = (localTreeNode1 == null ? false : red);
                if ((localTreeNode4 = left) != null) {
                  red = false;
                }
              }
              if (localTreeNode1 != null)
              {
                red = false;
                paramTreeNode1 = rotateRight(paramTreeNode1, localTreeNode1);
              }
              paramTreeNode2 = paramTreeNode1;
            }
          }
        }
      }
    }
    
    static <K, V> boolean checkInvariants(ConcurrentHashMap.TreeNode<K, V> paramTreeNode)
    {
      ConcurrentHashMap.TreeNode localTreeNode1 = parent;
      ConcurrentHashMap.TreeNode localTreeNode2 = left;
      ConcurrentHashMap.TreeNode localTreeNode3 = right;
      ConcurrentHashMap.TreeNode localTreeNode4 = prev;
      ConcurrentHashMap.TreeNode localTreeNode5 = (ConcurrentHashMap.TreeNode)next;
      if ((localTreeNode4 != null) && (next != paramTreeNode)) {
        return false;
      }
      if ((localTreeNode5 != null) && (prev != paramTreeNode)) {
        return false;
      }
      if ((localTreeNode1 != null) && (paramTreeNode != left) && (paramTreeNode != right)) {
        return false;
      }
      if ((localTreeNode2 != null) && ((parent != paramTreeNode) || (hash > hash))) {
        return false;
      }
      if ((localTreeNode3 != null) && ((parent != paramTreeNode) || (hash < hash))) {
        return false;
      }
      if ((red) && (localTreeNode2 != null) && (red) && (localTreeNode3 != null) && (red)) {
        return false;
      }
      if ((localTreeNode2 != null) && (!checkInvariants(localTreeNode2))) {
        return false;
      }
      return (localTreeNode3 == null) || (checkInvariants(localTreeNode3));
    }
    
    static
    {
      try
      {
        U = Unsafe.getUnsafe();
        Class localClass = TreeBin.class;
        LOCKSTATE = U.objectFieldOffset(localClass.getDeclaredField("lockState"));
      }
      catch (Exception localException)
      {
        throw new Error(localException);
      }
    }
  }
  
  static final class TreeNode<K, V>
    extends ConcurrentHashMap.Node<K, V>
  {
    TreeNode<K, V> parent;
    TreeNode<K, V> left;
    TreeNode<K, V> right;
    TreeNode<K, V> prev;
    boolean red;
    
    TreeNode(int paramInt, K paramK, V paramV, ConcurrentHashMap.Node<K, V> paramNode, TreeNode<K, V> paramTreeNode)
    {
      super(paramK, paramV, paramNode);
      parent = paramTreeNode;
    }
    
    ConcurrentHashMap.Node<K, V> find(int paramInt, Object paramObject)
    {
      return findTreeNode(paramInt, paramObject, null);
    }
    
    final TreeNode<K, V> findTreeNode(int paramInt, Object paramObject, Class<?> paramClass)
    {
      if (paramObject != null)
      {
        Object localObject1 = this;
        do
        {
          TreeNode localTreeNode2 = left;
          TreeNode localTreeNode3 = right;
          int i;
          if ((i = hash) > paramInt)
          {
            localObject1 = localTreeNode2;
          }
          else if (i < paramInt)
          {
            localObject1 = localTreeNode3;
          }
          else
          {
            Object localObject2;
            if (((localObject2 = key) == paramObject) || ((localObject2 != null) && (paramObject.equals(localObject2)))) {
              return (TreeNode<K, V>)localObject1;
            }
            if (localTreeNode2 == null)
            {
              localObject1 = localTreeNode3;
            }
            else if (localTreeNode3 == null)
            {
              localObject1 = localTreeNode2;
            }
            else
            {
              int j;
              if (((paramClass != null) || ((paramClass = ConcurrentHashMap.comparableClassFor(paramObject)) != null)) && ((j = ConcurrentHashMap.compareComparables(paramClass, paramObject, localObject2)) != 0))
              {
                localObject1 = j < 0 ? localTreeNode2 : localTreeNode3;
              }
              else
              {
                TreeNode localTreeNode1;
                if ((localTreeNode1 = localTreeNode3.findTreeNode(paramInt, paramObject, paramClass)) != null) {
                  return localTreeNode1;
                }
                localObject1 = localTreeNode2;
              }
            }
          }
        } while (localObject1 != null);
      }
      return null;
    }
  }
  
  static final class ValueIterator<K, V>
    extends ConcurrentHashMap.BaseIterator<K, V>
    implements Iterator<V>, Enumeration<V>
  {
    ValueIterator(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2, int paramInt3, ConcurrentHashMap<K, V> paramConcurrentHashMap)
    {
      super(paramInt1, paramInt2, paramInt3, paramConcurrentHashMap);
    }
    
    public final V next()
    {
      ConcurrentHashMap.Node localNode;
      if ((localNode = next) == null) {
        throw new NoSuchElementException();
      }
      Object localObject = val;
      lastReturned = localNode;
      advance();
      return (V)localObject;
    }
    
    public final V nextElement()
    {
      return (V)next();
    }
  }
  
  static final class ValueSpliterator<K, V>
    extends ConcurrentHashMap.Traverser<K, V>
    implements Spliterator<V>
  {
    long est;
    
    ValueSpliterator(ConcurrentHashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2, int paramInt3, long paramLong)
    {
      super(paramInt1, paramInt2, paramInt3);
      est = paramLong;
    }
    
    public Spliterator<V> trySplit()
    {
      int i;
      int j;
      int k;
      return (k = (i = baseIndex) + (j = baseLimit) >>> 1) <= i ? null : new ValueSpliterator(tab, baseSize, baseLimit = k, j, est >>>= 1);
    }
    
    public void forEachRemaining(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentHashMap.Node localNode;
      while ((localNode = advance()) != null) {
        paramConsumer.accept(val);
      }
    }
    
    public boolean tryAdvance(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentHashMap.Node localNode;
      if ((localNode = advance()) == null) {
        return false;
      }
      paramConsumer.accept(val);
      return true;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return 4352;
    }
  }
  
  static final class ValuesView<K, V>
    extends ConcurrentHashMap.CollectionView<K, V, V>
    implements Collection<V>, Serializable
  {
    private static final long serialVersionUID = 2249069246763182397L;
    
    ValuesView(ConcurrentHashMap<K, V> paramConcurrentHashMap)
    {
      super();
    }
    
    public final boolean contains(Object paramObject)
    {
      return map.containsValue(paramObject);
    }
    
    public final boolean remove(Object paramObject)
    {
      if (paramObject != null)
      {
        Iterator localIterator = iterator();
        while (localIterator.hasNext()) {
          if (paramObject.equals(localIterator.next()))
          {
            localIterator.remove();
            return true;
          }
        }
      }
      return false;
    }
    
    public final Iterator<V> iterator()
    {
      ConcurrentHashMap localConcurrentHashMap = map;
      ConcurrentHashMap.Node[] arrayOfNode;
      int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.ValueIterator(arrayOfNode, i, 0, i, localConcurrentHashMap);
    }
    
    public final boolean add(V paramV)
    {
      throw new UnsupportedOperationException();
    }
    
    public final boolean addAll(Collection<? extends V> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public Spliterator<V> spliterator()
    {
      ConcurrentHashMap localConcurrentHashMap = map;
      long l = localConcurrentHashMap.sumCount();
      ConcurrentHashMap.Node[] arrayOfNode;
      int i = (arrayOfNode = table) == null ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.ValueSpliterator(arrayOfNode, i, 0, i, l < 0L ? 0L : l);
    }
    
    public void forEach(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentHashMap.Node[] arrayOfNode;
      if ((arrayOfNode = map.table) != null)
      {
        ConcurrentHashMap.Traverser localTraverser = new ConcurrentHashMap.Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
        ConcurrentHashMap.Node localNode;
        while ((localNode = localTraverser.advance()) != null) {
          paramConsumer.accept(val);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ConcurrentHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */