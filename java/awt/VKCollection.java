package java.awt;

import java.util.HashMap;
import java.util.Map;

class VKCollection
{
  Map<Integer, String> code2name = new HashMap();
  Map<String, Integer> name2code = new HashMap();
  
  public VKCollection() {}
  
  public synchronized void put(String paramString, Integer paramInteger)
  {
    assert ((paramString != null) && (paramInteger != null));
    assert (findName(paramInteger) == null);
    assert (findCode(paramString) == null);
    code2name.put(paramInteger, paramString);
    name2code.put(paramString, paramInteger);
  }
  
  public synchronized Integer findCode(String paramString)
  {
    assert (paramString != null);
    return (Integer)name2code.get(paramString);
  }
  
  public synchronized String findName(Integer paramInteger)
  {
    assert (paramInteger != null);
    return (String)code2name.get(paramInteger);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\VKCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */