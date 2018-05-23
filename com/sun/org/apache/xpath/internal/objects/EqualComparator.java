package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

class EqualComparator
  extends Comparator
{
  EqualComparator() {}
  
  boolean compareStrings(XMLString paramXMLString1, XMLString paramXMLString2)
  {
    return paramXMLString1.equals(paramXMLString2);
  }
  
  boolean compareNumbers(double paramDouble1, double paramDouble2)
  {
    return paramDouble1 == paramDouble2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\EqualComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */