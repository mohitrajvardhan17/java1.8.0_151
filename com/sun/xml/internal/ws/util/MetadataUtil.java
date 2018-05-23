package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MetadataUtil
{
  public MetadataUtil() {}
  
  public static Map<String, SDDocument> getMetadataClosure(@NotNull String paramString, @NotNull SDDocumentResolver paramSDDocumentResolver, boolean paramBoolean)
  {
    HashMap localHashMap = new HashMap();
    HashSet localHashSet = new HashSet();
    localHashSet.add(paramString);
    while (!localHashSet.isEmpty())
    {
      Iterator localIterator1 = localHashSet.iterator();
      String str1 = (String)localIterator1.next();
      localHashSet.remove(str1);
      SDDocument localSDDocument1 = paramSDDocumentResolver.resolve(str1);
      SDDocument localSDDocument2 = (SDDocument)localHashMap.put(localSDDocument1.getURL().toExternalForm(), localSDDocument1);
      assert (localSDDocument2 == null);
      Set localSet = localSDDocument1.getImports();
      if ((!localSDDocument1.isSchema()) || (!paramBoolean))
      {
        Iterator localIterator2 = localSet.iterator();
        while (localIterator2.hasNext())
        {
          String str2 = (String)localIterator2.next();
          if (localHashMap.get(str2) == null) {
            localHashSet.add(str2);
          }
        }
      }
    }
    return localHashMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\MetadataUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */