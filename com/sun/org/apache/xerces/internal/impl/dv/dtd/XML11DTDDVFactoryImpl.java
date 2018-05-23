package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class XML11DTDDVFactoryImpl
  extends DTDDVFactoryImpl
{
  static Map<String, DatatypeValidator> XML11BUILTINTYPES;
  
  public XML11DTDDVFactoryImpl() {}
  
  public DatatypeValidator getBuiltInDV(String paramString)
  {
    if (XML11BUILTINTYPES.get(paramString) != null) {
      return (DatatypeValidator)XML11BUILTINTYPES.get(paramString);
    }
    return (DatatypeValidator)fBuiltInTypes.get(paramString);
  }
  
  public Map<String, DatatypeValidator> getBuiltInTypes()
  {
    HashMap localHashMap = new HashMap(fBuiltInTypes);
    localHashMap.putAll(XML11BUILTINTYPES);
    return localHashMap;
  }
  
  static
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("XML11ID", new XML11IDDatatypeValidator());
    Object localObject = new XML11IDREFDatatypeValidator();
    localHashMap.put("XML11IDREF", localObject);
    localHashMap.put("XML11IDREFS", new ListDatatypeValidator((DatatypeValidator)localObject));
    localObject = new XML11NMTOKENDatatypeValidator();
    localHashMap.put("XML11NMTOKEN", localObject);
    localHashMap.put("XML11NMTOKENS", new ListDatatypeValidator((DatatypeValidator)localObject));
    XML11BUILTINTYPES = Collections.unmodifiableMap(localHashMap);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\dtd\XML11DTDDVFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */