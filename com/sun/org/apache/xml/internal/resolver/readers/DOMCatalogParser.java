package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import org.w3c.dom.Node;

public abstract interface DOMCatalogParser
{
  public abstract void parseCatalogEntry(Catalog paramCatalog, Node paramNode);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\DOMCatalogParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */