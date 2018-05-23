package com.sun.corba.se.impl.util;

import java.util.EmptyStackException;
import java.util.Stack;

class RepositoryIdPool
  extends Stack
{
  private static int MAX_CACHE_SIZE = 4;
  private RepositoryIdCache cache;
  
  RepositoryIdPool() {}
  
  public final synchronized RepositoryId popId()
  {
    try
    {
      return (RepositoryId)super.pop();
    }
    catch (EmptyStackException localEmptyStackException)
    {
      increasePool(5);
    }
    return (RepositoryId)super.pop();
  }
  
  final void increasePool(int paramInt)
  {
    for (int i = paramInt; i > 0; i--) {
      push(new RepositoryId());
    }
  }
  
  final void setCaches(RepositoryIdCache paramRepositoryIdCache)
  {
    cache = paramRepositoryIdCache;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\RepositoryIdPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */