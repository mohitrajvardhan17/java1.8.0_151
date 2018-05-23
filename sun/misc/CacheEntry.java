package sun.misc;

class CacheEntry
  extends Ref
{
  int hash;
  Object key;
  CacheEntry next;
  
  CacheEntry() {}
  
  public Object reconstitute()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\CacheEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */