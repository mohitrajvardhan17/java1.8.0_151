package javax.xml.stream;

public abstract interface Location
{
  public abstract int getLineNumber();
  
  public abstract int getColumnNumber();
  
  public abstract int getCharacterOffset();
  
  public abstract String getPublicId();
  
  public abstract String getSystemId();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\Location.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */