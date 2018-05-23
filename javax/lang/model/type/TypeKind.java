package javax.lang.model.type;

public enum TypeKind
{
  BOOLEAN,  BYTE,  SHORT,  INT,  LONG,  CHAR,  FLOAT,  DOUBLE,  VOID,  NONE,  NULL,  ARRAY,  DECLARED,  ERROR,  TYPEVAR,  WILDCARD,  PACKAGE,  EXECUTABLE,  OTHER,  UNION,  INTERSECTION;
  
  private TypeKind() {}
  
  public boolean isPrimitive()
  {
    switch (this)
    {
    case BOOLEAN: 
    case BYTE: 
    case SHORT: 
    case INT: 
    case LONG: 
    case CHAR: 
    case FLOAT: 
    case DOUBLE: 
      return true;
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\TypeKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */