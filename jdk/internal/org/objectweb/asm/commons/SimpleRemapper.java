package jdk.internal.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Map;

public class SimpleRemapper
  extends Remapper
{
  private final Map<String, String> mapping;
  
  public SimpleRemapper(Map<String, String> paramMap)
  {
    mapping = paramMap;
  }
  
  public SimpleRemapper(String paramString1, String paramString2)
  {
    mapping = Collections.singletonMap(paramString1, paramString2);
  }
  
  public String mapMethodName(String paramString1, String paramString2, String paramString3)
  {
    String str = map(paramString1 + '.' + paramString2 + paramString3);
    return str == null ? paramString2 : str;
  }
  
  public String mapFieldName(String paramString1, String paramString2, String paramString3)
  {
    String str = map(paramString1 + '.' + paramString2);
    return str == null ? paramString2 : str;
  }
  
  public String map(String paramString)
  {
    return (String)mapping.get(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\SimpleRemapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */