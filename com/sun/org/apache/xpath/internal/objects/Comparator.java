package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

abstract class Comparator
{
  Comparator() {}
  
  abstract boolean compareStrings(XMLString paramXMLString1, XMLString paramXMLString2);
  
  abstract boolean compareNumbers(double paramDouble1, double paramDouble2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\Comparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */