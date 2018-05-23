package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;

class TagAndType
{
  final Name tagName;
  final JaxBeanInfo beanInfo;
  
  TagAndType(Name paramName, JaxBeanInfo paramJaxBeanInfo)
  {
    tagName = paramName;
    beanInfo = paramJaxBeanInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\TagAndType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */