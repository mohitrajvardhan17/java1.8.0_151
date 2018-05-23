package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

public final class Discarder
  extends Loader
{
  public static final Loader INSTANCE = new Discarder();
  
  private Discarder()
  {
    super(false);
  }
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
  {
    paramState.setTarget(null);
    paramState.setLoader(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\Discarder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */