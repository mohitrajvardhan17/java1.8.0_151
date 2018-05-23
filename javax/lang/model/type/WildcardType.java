package javax.lang.model.type;

public abstract interface WildcardType
  extends TypeMirror
{
  public abstract TypeMirror getExtendsBound();
  
  public abstract TypeMirror getSuperBound();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\WildcardType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */