package sun.awt;

final class MostRecentKeyValue
{
  Object key;
  Object value;
  
  MostRecentKeyValue(Object paramObject1, Object paramObject2)
  {
    key = paramObject1;
    value = paramObject2;
  }
  
  void setPair(Object paramObject1, Object paramObject2)
  {
    key = paramObject1;
    value = paramObject2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\MostRecentKeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */