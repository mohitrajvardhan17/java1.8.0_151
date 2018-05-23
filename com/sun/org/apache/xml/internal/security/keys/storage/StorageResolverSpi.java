package com.sun.org.apache.xml.internal.security.keys.storage;

import java.security.cert.Certificate;
import java.util.Iterator;

public abstract class StorageResolverSpi
{
  public StorageResolverSpi() {}
  
  public abstract Iterator<Certificate> getIterator();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\storage\StorageResolverSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */