package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class Wildcard
  implements TypeArgument
{
  private FieldTypeSignature[] upperBounds;
  private FieldTypeSignature[] lowerBounds;
  private static final FieldTypeSignature[] emptyBounds = new FieldTypeSignature[0];
  
  private Wildcard(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2)
  {
    upperBounds = paramArrayOfFieldTypeSignature1;
    lowerBounds = paramArrayOfFieldTypeSignature2;
  }
  
  public static Wildcard make(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2)
  {
    return new Wildcard(paramArrayOfFieldTypeSignature1, paramArrayOfFieldTypeSignature2);
  }
  
  public FieldTypeSignature[] getUpperBounds()
  {
    return upperBounds;
  }
  
  public FieldTypeSignature[] getLowerBounds()
  {
    if ((lowerBounds.length == 1) && (lowerBounds[0] == BottomSignature.make())) {
      return emptyBounds;
    }
    return lowerBounds;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitWildcard(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\Wildcard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */