package sun.reflect.generics.visitor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.ArrayTypeSignature;
import sun.reflect.generics.tree.BooleanSignature;
import sun.reflect.generics.tree.BottomSignature;
import sun.reflect.generics.tree.ByteSignature;
import sun.reflect.generics.tree.CharSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.FloatSignature;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.IntSignature;
import sun.reflect.generics.tree.LongSignature;
import sun.reflect.generics.tree.ShortSignature;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.TypeArgument;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.Wildcard;

public class Reifier
  implements TypeTreeVisitor<Type>
{
  private Type resultType;
  private GenericsFactory factory;
  
  private Reifier(GenericsFactory paramGenericsFactory)
  {
    factory = paramGenericsFactory;
  }
  
  private GenericsFactory getFactory()
  {
    return factory;
  }
  
  public static Reifier make(GenericsFactory paramGenericsFactory)
  {
    return new Reifier(paramGenericsFactory);
  }
  
  private Type[] reifyTypeArguments(TypeArgument[] paramArrayOfTypeArgument)
  {
    Type[] arrayOfType = new Type[paramArrayOfTypeArgument.length];
    for (int i = 0; i < paramArrayOfTypeArgument.length; i++)
    {
      paramArrayOfTypeArgument[i].accept(this);
      arrayOfType[i] = resultType;
    }
    return arrayOfType;
  }
  
  public Type getResult()
  {
    assert (resultType != null);
    return resultType;
  }
  
  public void visitFormalTypeParameter(FormalTypeParameter paramFormalTypeParameter)
  {
    resultType = getFactory().makeTypeVariable(paramFormalTypeParameter.getName(), paramFormalTypeParameter.getBounds());
  }
  
  public void visitClassTypeSignature(ClassTypeSignature paramClassTypeSignature)
  {
    List localList = paramClassTypeSignature.getPath();
    assert (!localList.isEmpty());
    Iterator localIterator = localList.iterator();
    SimpleClassTypeSignature localSimpleClassTypeSignature = (SimpleClassTypeSignature)localIterator.next();
    StringBuilder localStringBuilder = new StringBuilder(localSimpleClassTypeSignature.getName());
    boolean bool = localSimpleClassTypeSignature.getDollar();
    while ((localIterator.hasNext()) && (localSimpleClassTypeSignature.getTypeArguments().length == 0))
    {
      localSimpleClassTypeSignature = (SimpleClassTypeSignature)localIterator.next();
      bool = localSimpleClassTypeSignature.getDollar();
      localStringBuilder.append(bool ? "$" : ".").append(localSimpleClassTypeSignature.getName());
    }
    assert ((!localIterator.hasNext()) || (localSimpleClassTypeSignature.getTypeArguments().length > 0));
    Type localType = getFactory().makeNamedType(localStringBuilder.toString());
    if (localSimpleClassTypeSignature.getTypeArguments().length == 0)
    {
      assert (!localIterator.hasNext());
      resultType = localType;
    }
    else
    {
      assert (localSimpleClassTypeSignature.getTypeArguments().length > 0);
      Type[] arrayOfType = reifyTypeArguments(localSimpleClassTypeSignature.getTypeArguments());
      ParameterizedType localParameterizedType = getFactory().makeParameterizedType(localType, arrayOfType, null);
      bool = false;
      while (localIterator.hasNext())
      {
        localSimpleClassTypeSignature = (SimpleClassTypeSignature)localIterator.next();
        bool = localSimpleClassTypeSignature.getDollar();
        localStringBuilder.append(bool ? "$" : ".").append(localSimpleClassTypeSignature.getName());
        localType = getFactory().makeNamedType(localStringBuilder.toString());
        arrayOfType = reifyTypeArguments(localSimpleClassTypeSignature.getTypeArguments());
        localParameterizedType = getFactory().makeParameterizedType(localType, arrayOfType, localParameterizedType);
      }
      resultType = localParameterizedType;
    }
  }
  
  public void visitArrayTypeSignature(ArrayTypeSignature paramArrayTypeSignature)
  {
    paramArrayTypeSignature.getComponentType().accept(this);
    Type localType = resultType;
    resultType = getFactory().makeArrayType(localType);
  }
  
  public void visitTypeVariableSignature(TypeVariableSignature paramTypeVariableSignature)
  {
    resultType = getFactory().findTypeVariable(paramTypeVariableSignature.getIdentifier());
  }
  
  public void visitWildcard(Wildcard paramWildcard)
  {
    resultType = getFactory().makeWildcard(paramWildcard.getUpperBounds(), paramWildcard.getLowerBounds());
  }
  
  public void visitSimpleClassTypeSignature(SimpleClassTypeSignature paramSimpleClassTypeSignature)
  {
    resultType = getFactory().makeNamedType(paramSimpleClassTypeSignature.getName());
  }
  
  public void visitBottomSignature(BottomSignature paramBottomSignature) {}
  
  public void visitByteSignature(ByteSignature paramByteSignature)
  {
    resultType = getFactory().makeByte();
  }
  
  public void visitBooleanSignature(BooleanSignature paramBooleanSignature)
  {
    resultType = getFactory().makeBool();
  }
  
  public void visitShortSignature(ShortSignature paramShortSignature)
  {
    resultType = getFactory().makeShort();
  }
  
  public void visitCharSignature(CharSignature paramCharSignature)
  {
    resultType = getFactory().makeChar();
  }
  
  public void visitIntSignature(IntSignature paramIntSignature)
  {
    resultType = getFactory().makeInt();
  }
  
  public void visitLongSignature(LongSignature paramLongSignature)
  {
    resultType = getFactory().makeLong();
  }
  
  public void visitFloatSignature(FloatSignature paramFloatSignature)
  {
    resultType = getFactory().makeFloat();
  }
  
  public void visitDoubleSignature(DoubleSignature paramDoubleSignature)
  {
    resultType = getFactory().makeDouble();
  }
  
  public void visitVoidDescriptor(VoidDescriptor paramVoidDescriptor)
  {
    resultType = getFactory().makeVoid();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\visitor\Reifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */