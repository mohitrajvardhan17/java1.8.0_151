package com.sun.corba.se.impl.util;

import java.util.Hashtable;

public class RepositoryIdCache
  extends Hashtable
{
  private RepositoryIdPool pool = new RepositoryIdPool();
  
  public RepositoryIdCache()
  {
    pool.setCaches(this);
  }
  
  public final synchronized RepositoryId getId(String paramString)
  {
    RepositoryId localRepositoryId = (RepositoryId)super.get(paramString);
    if (localRepositoryId != null) {
      return localRepositoryId;
    }
    localRepositoryId = new RepositoryId(paramString);
    put(paramString, localRepositoryId);
    return localRepositoryId;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\RepositoryIdCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */