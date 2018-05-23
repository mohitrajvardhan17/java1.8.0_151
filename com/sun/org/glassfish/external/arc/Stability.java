package com.sun.org.glassfish.external.arc;

public enum Stability
{
  COMMITTED("Committed"),  UNCOMMITTED("Uncommitted"),  VOLATILE("Volatile"),  NOT_AN_INTERFACE("Not-An-Interface"),  PRIVATE("Private"),  EXPERIMENTAL("Experimental"),  UNSPECIFIED("Unspecified");
  
  private final String mName;
  
  private Stability(String paramString)
  {
    mName = paramString;
  }
  
  public String toString()
  {
    return mName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\arc\Stability.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */