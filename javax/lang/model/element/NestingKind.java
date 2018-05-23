package javax.lang.model.element;

public enum NestingKind
{
  TOP_LEVEL,  MEMBER,  LOCAL,  ANONYMOUS;
  
  private NestingKind() {}
  
  public boolean isNested()
  {
    return this != TOP_LEVEL;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\NestingKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */