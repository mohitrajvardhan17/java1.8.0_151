package javax.xml.bind;

/**
 * @deprecated
 */
public abstract interface Validator
{
  /**
   * @deprecated
   */
  public abstract void setEventHandler(ValidationEventHandler paramValidationEventHandler)
    throws JAXBException;
  
  /**
   * @deprecated
   */
  public abstract ValidationEventHandler getEventHandler()
    throws JAXBException;
  
  /**
   * @deprecated
   */
  public abstract boolean validate(Object paramObject)
    throws JAXBException;
  
  /**
   * @deprecated
   */
  public abstract boolean validateRoot(Object paramObject)
    throws JAXBException;
  
  /**
   * @deprecated
   */
  public abstract void setProperty(String paramString, Object paramObject)
    throws PropertyException;
  
  /**
   * @deprecated
   */
  public abstract Object getProperty(String paramString)
    throws PropertyException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\Validator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */