package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import java.util.Map;

public abstract class DTDDVFactory
{
  private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl";
  private static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
  
  public static final DTDDVFactory getInstance()
    throws DVFactoryException
  {
    return getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl");
  }
  
  public static final DTDDVFactory getInstance(String paramString)
    throws DVFactoryException
  {
    try
    {
      if ("com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl".equals(paramString)) {
        return new DTDDVFactoryImpl();
      }
      if ("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl".equals(paramString)) {
        return new XML11DTDDVFactoryImpl();
      }
      return (DTDDVFactory)ObjectFactory.newInstance(paramString, true);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new DVFactoryException("DTD factory class " + paramString + " does not extend from DTDDVFactory.");
    }
  }
  
  protected DTDDVFactory() {}
  
  public abstract DatatypeValidator getBuiltInDV(String paramString);
  
  public abstract Map<String, DatatypeValidator> getBuiltInTypes();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\DTDDVFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */