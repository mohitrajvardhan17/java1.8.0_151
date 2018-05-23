package jdk.internal.org.objectweb.asm.tree;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class InsnList
{
  private int size;
  private AbstractInsnNode first;
  private AbstractInsnNode last;
  AbstractInsnNode[] cache;
  
  public InsnList() {}
  
  public int size()
  {
    return size;
  }
  
  public AbstractInsnNode getFirst()
  {
    return first;
  }
  
  public AbstractInsnNode getLast()
  {
    return last;
  }
  
  public AbstractInsnNode get(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= size)) {
      throw new IndexOutOfBoundsException();
    }
    if (cache == null) {
      cache = toArray();
    }
    return cache[paramInt];
  }
  
  public boolean contains(AbstractInsnNode paramAbstractInsnNode)
  {
    for (AbstractInsnNode localAbstractInsnNode = first; (localAbstractInsnNode != null) && (localAbstractInsnNode != paramAbstractInsnNode); localAbstractInsnNode = next) {}
    return localAbstractInsnNode != null;
  }
  
  public int indexOf(AbstractInsnNode paramAbstractInsnNode)
  {
    if (cache == null) {
      cache = toArray();
    }
    return index;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    for (AbstractInsnNode localAbstractInsnNode = first; localAbstractInsnNode != null; localAbstractInsnNode = next) {
      localAbstractInsnNode.accept(paramMethodVisitor);
    }
  }
  
  public ListIterator<AbstractInsnNode> iterator()
  {
    return iterator(0);
  }
  
  public ListIterator<AbstractInsnNode> iterator(int paramInt)
  {
    return new InsnListIterator(paramInt);
  }
  
  public AbstractInsnNode[] toArray()
  {
    int i = 0;
    AbstractInsnNode localAbstractInsnNode = first;
    AbstractInsnNode[] arrayOfAbstractInsnNode = new AbstractInsnNode[size];
    while (localAbstractInsnNode != null)
    {
      arrayOfAbstractInsnNode[i] = localAbstractInsnNode;
      index = (i++);
      localAbstractInsnNode = next;
    }
    return arrayOfAbstractInsnNode;
  }
  
  public void set(AbstractInsnNode paramAbstractInsnNode1, AbstractInsnNode paramAbstractInsnNode2)
  {
    AbstractInsnNode localAbstractInsnNode1 = next;
    next = localAbstractInsnNode1;
    if (localAbstractInsnNode1 != null) {
      prev = paramAbstractInsnNode2;
    } else {
      last = paramAbstractInsnNode2;
    }
    AbstractInsnNode localAbstractInsnNode2 = prev;
    prev = localAbstractInsnNode2;
    if (localAbstractInsnNode2 != null) {
      next = paramAbstractInsnNode2;
    } else {
      first = paramAbstractInsnNode2;
    }
    if (cache != null)
    {
      int i = index;
      cache[i] = paramAbstractInsnNode2;
      index = i;
    }
    else
    {
      index = 0;
    }
    index = -1;
    prev = null;
    next = null;
  }
  
  public void add(AbstractInsnNode paramAbstractInsnNode)
  {
    size += 1;
    if (last == null)
    {
      first = paramAbstractInsnNode;
      last = paramAbstractInsnNode;
    }
    else
    {
      last.next = paramAbstractInsnNode;
      prev = last;
    }
    last = paramAbstractInsnNode;
    cache = null;
    index = 0;
  }
  
  public void add(InsnList paramInsnList)
  {
    if (size == 0) {
      return;
    }
    size += size;
    if (last == null)
    {
      first = first;
      last = last;
    }
    else
    {
      AbstractInsnNode localAbstractInsnNode = first;
      last.next = localAbstractInsnNode;
      prev = last;
      last = last;
    }
    cache = null;
    paramInsnList.removeAll(false);
  }
  
  public void insert(AbstractInsnNode paramAbstractInsnNode)
  {
    size += 1;
    if (first == null)
    {
      first = paramAbstractInsnNode;
      last = paramAbstractInsnNode;
    }
    else
    {
      first.prev = paramAbstractInsnNode;
      next = first;
    }
    first = paramAbstractInsnNode;
    cache = null;
    index = 0;
  }
  
  public void insert(InsnList paramInsnList)
  {
    if (size == 0) {
      return;
    }
    size += size;
    if (first == null)
    {
      first = first;
      last = last;
    }
    else
    {
      AbstractInsnNode localAbstractInsnNode = last;
      first.prev = localAbstractInsnNode;
      next = first;
      first = first;
    }
    cache = null;
    paramInsnList.removeAll(false);
  }
  
  public void insert(AbstractInsnNode paramAbstractInsnNode1, AbstractInsnNode paramAbstractInsnNode2)
  {
    size += 1;
    AbstractInsnNode localAbstractInsnNode = next;
    if (localAbstractInsnNode == null) {
      last = paramAbstractInsnNode2;
    } else {
      prev = paramAbstractInsnNode2;
    }
    next = paramAbstractInsnNode2;
    next = localAbstractInsnNode;
    prev = paramAbstractInsnNode1;
    cache = null;
    index = 0;
  }
  
  public void insert(AbstractInsnNode paramAbstractInsnNode, InsnList paramInsnList)
  {
    if (size == 0) {
      return;
    }
    size += size;
    AbstractInsnNode localAbstractInsnNode1 = first;
    AbstractInsnNode localAbstractInsnNode2 = last;
    AbstractInsnNode localAbstractInsnNode3 = next;
    if (localAbstractInsnNode3 == null) {
      last = localAbstractInsnNode2;
    } else {
      prev = localAbstractInsnNode2;
    }
    next = localAbstractInsnNode1;
    next = localAbstractInsnNode3;
    prev = paramAbstractInsnNode;
    cache = null;
    paramInsnList.removeAll(false);
  }
  
  public void insertBefore(AbstractInsnNode paramAbstractInsnNode1, AbstractInsnNode paramAbstractInsnNode2)
  {
    size += 1;
    AbstractInsnNode localAbstractInsnNode = prev;
    if (localAbstractInsnNode == null) {
      first = paramAbstractInsnNode2;
    } else {
      next = paramAbstractInsnNode2;
    }
    prev = paramAbstractInsnNode2;
    next = paramAbstractInsnNode1;
    prev = localAbstractInsnNode;
    cache = null;
    index = 0;
  }
  
  public void insertBefore(AbstractInsnNode paramAbstractInsnNode, InsnList paramInsnList)
  {
    if (size == 0) {
      return;
    }
    size += size;
    AbstractInsnNode localAbstractInsnNode1 = first;
    AbstractInsnNode localAbstractInsnNode2 = last;
    AbstractInsnNode localAbstractInsnNode3 = prev;
    if (localAbstractInsnNode3 == null) {
      first = localAbstractInsnNode1;
    } else {
      next = localAbstractInsnNode1;
    }
    prev = localAbstractInsnNode2;
    next = paramAbstractInsnNode;
    prev = localAbstractInsnNode3;
    cache = null;
    paramInsnList.removeAll(false);
  }
  
  public void remove(AbstractInsnNode paramAbstractInsnNode)
  {
    size -= 1;
    AbstractInsnNode localAbstractInsnNode1 = next;
    AbstractInsnNode localAbstractInsnNode2 = prev;
    if (localAbstractInsnNode1 == null)
    {
      if (localAbstractInsnNode2 == null)
      {
        first = null;
        last = null;
      }
      else
      {
        next = null;
        last = localAbstractInsnNode2;
      }
    }
    else if (localAbstractInsnNode2 == null)
    {
      first = localAbstractInsnNode1;
      prev = null;
    }
    else
    {
      next = localAbstractInsnNode1;
      prev = localAbstractInsnNode2;
    }
    cache = null;
    index = -1;
    prev = null;
    next = null;
  }
  
  void removeAll(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      AbstractInsnNode localAbstractInsnNode;
      for (Object localObject = first; localObject != null; localObject = localAbstractInsnNode)
      {
        localAbstractInsnNode = next;
        index = -1;
        prev = null;
        next = null;
      }
    }
    size = 0;
    first = null;
    last = null;
    cache = null;
  }
  
  public void clear()
  {
    removeAll(false);
  }
  
  public void resetLabels()
  {
    for (AbstractInsnNode localAbstractInsnNode = first; localAbstractInsnNode != null; localAbstractInsnNode = next) {
      if ((localAbstractInsnNode instanceof LabelNode)) {
        ((LabelNode)localAbstractInsnNode).resetLabel();
      }
    }
  }
  
  private final class InsnListIterator
    implements ListIterator
  {
    AbstractInsnNode next;
    AbstractInsnNode prev;
    AbstractInsnNode remove;
    
    InsnListIterator(int paramInt)
    {
      if (paramInt == size())
      {
        next = null;
        prev = getLast();
      }
      else
      {
        next = get(paramInt);
        prev = next.prev;
      }
    }
    
    public boolean hasNext()
    {
      return next != null;
    }
    
    public Object next()
    {
      if (next == null) {
        throw new NoSuchElementException();
      }
      AbstractInsnNode localAbstractInsnNode = next;
      prev = localAbstractInsnNode;
      next = next;
      remove = localAbstractInsnNode;
      return localAbstractInsnNode;
    }
    
    public void remove()
    {
      if (remove != null)
      {
        if (remove == next) {
          next = next.next;
        } else {
          prev = prev.prev;
        }
        remove(remove);
        remove = null;
      }
      else
      {
        throw new IllegalStateException();
      }
    }
    
    public boolean hasPrevious()
    {
      return prev != null;
    }
    
    public Object previous()
    {
      AbstractInsnNode localAbstractInsnNode = prev;
      next = localAbstractInsnNode;
      prev = prev;
      remove = localAbstractInsnNode;
      return localAbstractInsnNode;
    }
    
    public int nextIndex()
    {
      if (next == null) {
        return size();
      }
      if (cache == null) {
        cache = toArray();
      }
      return next.index;
    }
    
    public int previousIndex()
    {
      if (prev == null) {
        return -1;
      }
      if (cache == null) {
        cache = toArray();
      }
      return prev.index;
    }
    
    public void add(Object paramObject)
    {
      insertBefore(next, (AbstractInsnNode)paramObject);
      prev = ((AbstractInsnNode)paramObject);
      remove = null;
    }
    
    public void set(Object paramObject)
    {
      set(next.prev, (AbstractInsnNode)paramObject);
      prev = ((AbstractInsnNode)paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\InsnList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */