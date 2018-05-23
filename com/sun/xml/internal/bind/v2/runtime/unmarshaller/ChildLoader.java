package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

public final class ChildLoader
{
  public final Loader loader;
  public final Receiver receiver;
  
  public ChildLoader(Loader paramLoader, Receiver paramReceiver)
  {
    assert (paramLoader != null);
    loader = paramLoader;
    receiver = paramReceiver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\ChildLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */