package javax.xml.validation;

public abstract class Schema
{
  protected Schema() {}
  
  public abstract Validator newValidator();
  
  public abstract ValidatorHandler newValidatorHandler();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\validation\Schema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */