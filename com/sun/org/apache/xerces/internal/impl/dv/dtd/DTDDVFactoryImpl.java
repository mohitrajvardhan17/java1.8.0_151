package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DTDDVFactoryImpl
  extends DTDDVFactory
{
  static final Map<String, DatatypeValidator> fBuiltInTypes;
  
  public DTDDVFactoryImpl() {}
  
  public DatatypeValidator getBuiltInDV(String paramString)
  {
    return (DatatypeValidator)fBuiltInTypes.get(paramString);
  }
  
  public Map<String, DatatypeValidator> getBuiltInTypes()
  {
    return new HashMap(fBuiltInTypes);
  }
  
  static
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("string", new StringDatatypeValidator());
    localHashMap.put("ID", new IDDatatypeValidator());
    Object localObject = new IDREFDatatypeValidator();
    localHashMap.put("IDREF", localObject);
    localHashMap.put("IDREFS", new ListDatatypeValidator((DatatypeValidator)localObject));
    localObject = new ENTITYDatatypeValidator();
    localHashMap.put("ENTITY", new ENTITYDatatypeValidator());
    localHashMap.put("ENTITIES", new ListDatatypeValidator((DatatypeValidator)localObject));
    localHashMap.put("NOTATION", new NOTATIONDatatypeValidator());
    localObject = new NMTOKENDatatypeValidator();
    localHashMap.put("NMTOKEN", localObject);
    localHashMap.put("NMTOKENS", new ListDatatypeValidator((DatatypeValidator)localObject));
    fBuiltInTypes = Collections.unmodifiableMap(localHashMap);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\dtd\DTDDVFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */