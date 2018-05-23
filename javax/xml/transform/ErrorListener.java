package javax.xml.transform;

public abstract interface ErrorListener
{
  public abstract void warning(TransformerException paramTransformerException)
    throws TransformerException;
  
  public abstract void error(TransformerException paramTransformerException)
    throws TransformerException;
  
  public abstract void fatalError(TransformerException paramTransformerException)
    throws TransformerException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\ErrorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */