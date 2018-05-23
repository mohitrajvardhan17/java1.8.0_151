package sun.reflect.generics.parser;

import java.lang.reflect.GenericSignatureFormatError;
import java.util.ArrayList;
import java.util.List;
import sun.reflect.generics.tree.ArrayTypeSignature;
import sun.reflect.generics.tree.BaseType;
import sun.reflect.generics.tree.BooleanSignature;
import sun.reflect.generics.tree.BottomSignature;
import sun.reflect.generics.tree.ByteSignature;
import sun.reflect.generics.tree.CharSignature;
import sun.reflect.generics.tree.ClassSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.tree.FloatSignature;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.IntSignature;
import sun.reflect.generics.tree.LongSignature;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.ReturnType;
import sun.reflect.generics.tree.ShortSignature;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.TypeArgument;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.Wildcard;

public class SignatureParser
{
  private char[] input;
  private int index = 0;
  private static final char EOI = ':';
  private static final boolean DEBUG = false;
  
  private SignatureParser() {}
  
  private char getNext()
  {
    assert (index <= input.length);
    try
    {
      return input[(index++)];
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return ':';
  }
  
  private char current()
  {
    assert (index <= input.length);
    try
    {
      return input[index];
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return ':';
  }
  
  private void advance()
  {
    assert (index <= input.length);
    index += 1;
  }
  
  private String remainder()
  {
    return new String(input, index, input.length - index);
  }
  
  private boolean matches(char paramChar, char... paramVarArgs)
  {
    for (char c : paramVarArgs) {
      if (paramChar == c) {
        return true;
      }
    }
    return false;
  }
  
  private Error error(String paramString)
  {
    return new GenericSignatureFormatError("Signature Parse error: " + paramString + "\n\tRemaining input: " + remainder());
  }
  
  private void progress(int paramInt)
  {
    if (index <= paramInt) {
      throw error("Failure to make progress!");
    }
  }
  
  public static SignatureParser make()
  {
    return new SignatureParser();
  }
  
  public ClassSignature parseClassSig(String paramString)
  {
    input = paramString.toCharArray();
    return parseClassSignature();
  }
  
  public MethodTypeSignature parseMethodSig(String paramString)
  {
    input = paramString.toCharArray();
    return parseMethodTypeSignature();
  }
  
  public TypeSignature parseTypeSig(String paramString)
  {
    input = paramString.toCharArray();
    return parseTypeSignature();
  }
  
  private ClassSignature parseClassSignature()
  {
    assert (index == 0);
    return ClassSignature.make(parseZeroOrMoreFormalTypeParameters(), parseClassTypeSignature(), parseSuperInterfaces());
  }
  
  private FormalTypeParameter[] parseZeroOrMoreFormalTypeParameters()
  {
    if (current() == '<') {
      return parseFormalTypeParameters();
    }
    return new FormalTypeParameter[0];
  }
  
  private FormalTypeParameter[] parseFormalTypeParameters()
  {
    ArrayList localArrayList = new ArrayList(3);
    assert (current() == '<');
    if (current() != '<') {
      throw error("expected '<'");
    }
    advance();
    localArrayList.add(parseFormalTypeParameter());
    while (current() != '>')
    {
      int i = index;
      localArrayList.add(parseFormalTypeParameter());
      progress(i);
    }
    advance();
    return (FormalTypeParameter[])localArrayList.toArray(new FormalTypeParameter[localArrayList.size()]);
  }
  
  private FormalTypeParameter parseFormalTypeParameter()
  {
    String str = parseIdentifier();
    FieldTypeSignature[] arrayOfFieldTypeSignature = parseBounds();
    return FormalTypeParameter.make(str, arrayOfFieldTypeSignature);
  }
  
  private String parseIdentifier()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    while (!Character.isWhitespace(current()))
    {
      char c = current();
      switch (c)
      {
      case '.': 
      case '/': 
      case ':': 
      case ';': 
      case '<': 
      case '>': 
      case '[': 
        return localStringBuilder.toString();
      }
      localStringBuilder.append(c);
      advance();
    }
    return localStringBuilder.toString();
  }
  
  private FieldTypeSignature parseFieldTypeSignature()
  {
    return parseFieldTypeSignature(true);
  }
  
  private FieldTypeSignature parseFieldTypeSignature(boolean paramBoolean)
  {
    switch (current())
    {
    case 'L': 
      return parseClassTypeSignature();
    case 'T': 
      return parseTypeVariableSignature();
    case '[': 
      if (paramBoolean) {
        return parseArrayTypeSignature();
      }
      throw error("Array signature not allowed here.");
    }
    throw error("Expected Field Type Signature");
  }
  
  private ClassTypeSignature parseClassTypeSignature()
  {
    assert (current() == 'L');
    if (current() != 'L') {
      throw error("expected a class type");
    }
    advance();
    ArrayList localArrayList = new ArrayList(5);
    localArrayList.add(parsePackageNameAndSimpleClassTypeSignature());
    parseClassTypeSignatureSuffix(localArrayList);
    if (current() != ';') {
      throw error("expected ';' got '" + current() + "'");
    }
    advance();
    return ClassTypeSignature.make(localArrayList);
  }
  
  private SimpleClassTypeSignature parsePackageNameAndSimpleClassTypeSignature()
  {
    String str = parseIdentifier();
    if (current() == '/')
    {
      StringBuilder localStringBuilder = new StringBuilder(str);
      while (current() == '/')
      {
        advance();
        localStringBuilder.append(".");
        localStringBuilder.append(parseIdentifier());
      }
      str = localStringBuilder.toString();
    }
    switch (current())
    {
    case ';': 
      return SimpleClassTypeSignature.make(str, false, new TypeArgument[0]);
    case '<': 
      return SimpleClassTypeSignature.make(str, false, parseTypeArguments());
    }
    throw error("expected '<' or ';' but got " + current());
  }
  
  private SimpleClassTypeSignature parseSimpleClassTypeSignature(boolean paramBoolean)
  {
    String str = parseIdentifier();
    char c = current();
    switch (c)
    {
    case '.': 
    case ';': 
      return SimpleClassTypeSignature.make(str, paramBoolean, new TypeArgument[0]);
    case '<': 
      return SimpleClassTypeSignature.make(str, paramBoolean, parseTypeArguments());
    }
    throw error("expected '<' or ';' or '.', got '" + c + "'.");
  }
  
  private void parseClassTypeSignatureSuffix(List<SimpleClassTypeSignature> paramList)
  {
    while (current() == '.')
    {
      advance();
      paramList.add(parseSimpleClassTypeSignature(true));
    }
  }
  
  private TypeArgument[] parseTypeArgumentsOpt()
  {
    if (current() == '<') {
      return parseTypeArguments();
    }
    return new TypeArgument[0];
  }
  
  private TypeArgument[] parseTypeArguments()
  {
    ArrayList localArrayList = new ArrayList(3);
    assert (current() == '<');
    if (current() != '<') {
      throw error("expected '<'");
    }
    advance();
    localArrayList.add(parseTypeArgument());
    while (current() != '>') {
      localArrayList.add(parseTypeArgument());
    }
    advance();
    return (TypeArgument[])localArrayList.toArray(new TypeArgument[localArrayList.size()]);
  }
  
  private TypeArgument parseTypeArgument()
  {
    FieldTypeSignature[] arrayOfFieldTypeSignature1 = new FieldTypeSignature[1];
    FieldTypeSignature[] arrayOfFieldTypeSignature2 = new FieldTypeSignature[1];
    TypeArgument[] arrayOfTypeArgument = new TypeArgument[0];
    int i = current();
    switch (i)
    {
    case 43: 
      advance();
      arrayOfFieldTypeSignature1[0] = parseFieldTypeSignature();
      arrayOfFieldTypeSignature2[0] = BottomSignature.make();
      return Wildcard.make(arrayOfFieldTypeSignature1, arrayOfFieldTypeSignature2);
    case 42: 
      advance();
      arrayOfFieldTypeSignature1[0] = SimpleClassTypeSignature.make("java.lang.Object", false, arrayOfTypeArgument);
      arrayOfFieldTypeSignature2[0] = BottomSignature.make();
      return Wildcard.make(arrayOfFieldTypeSignature1, arrayOfFieldTypeSignature2);
    case 45: 
      advance();
      arrayOfFieldTypeSignature2[0] = parseFieldTypeSignature();
      arrayOfFieldTypeSignature1[0] = SimpleClassTypeSignature.make("java.lang.Object", false, arrayOfTypeArgument);
      return Wildcard.make(arrayOfFieldTypeSignature1, arrayOfFieldTypeSignature2);
    }
    return parseFieldTypeSignature();
  }
  
  private TypeVariableSignature parseTypeVariableSignature()
  {
    assert (current() == 'T');
    if (current() != 'T') {
      throw error("expected a type variable usage");
    }
    advance();
    TypeVariableSignature localTypeVariableSignature = TypeVariableSignature.make(parseIdentifier());
    if (current() != ';') {
      throw error("; expected in signature of type variable named" + localTypeVariableSignature.getIdentifier());
    }
    advance();
    return localTypeVariableSignature;
  }
  
  private ArrayTypeSignature parseArrayTypeSignature()
  {
    if (current() != '[') {
      throw error("expected array type signature");
    }
    advance();
    return ArrayTypeSignature.make(parseTypeSignature());
  }
  
  private TypeSignature parseTypeSignature()
  {
    switch (current())
    {
    case 'B': 
    case 'C': 
    case 'D': 
    case 'F': 
    case 'I': 
    case 'J': 
    case 'S': 
    case 'Z': 
      return parseBaseType();
    }
    return parseFieldTypeSignature();
  }
  
  private BaseType parseBaseType()
  {
    switch (current())
    {
    case 'B': 
      advance();
      return ByteSignature.make();
    case 'C': 
      advance();
      return CharSignature.make();
    case 'D': 
      advance();
      return DoubleSignature.make();
    case 'F': 
      advance();
      return FloatSignature.make();
    case 'I': 
      advance();
      return IntSignature.make();
    case 'J': 
      advance();
      return LongSignature.make();
    case 'S': 
      advance();
      return ShortSignature.make();
    case 'Z': 
      advance();
      return BooleanSignature.make();
    }
    if (!$assertionsDisabled) {
      throw new AssertionError();
    }
    throw error("expected primitive type");
  }
  
  private FieldTypeSignature[] parseBounds()
  {
    ArrayList localArrayList = new ArrayList(3);
    if (current() == ':')
    {
      advance();
      switch (current())
      {
      case ':': 
        break;
      default: 
        localArrayList.add(parseFieldTypeSignature());
      }
      while (current() == ':')
      {
        advance();
        localArrayList.add(parseFieldTypeSignature());
      }
    }
    error("Bound expected");
    return (FieldTypeSignature[])localArrayList.toArray(new FieldTypeSignature[localArrayList.size()]);
  }
  
  private ClassTypeSignature[] parseSuperInterfaces()
  {
    ArrayList localArrayList = new ArrayList(5);
    while (current() == 'L') {
      localArrayList.add(parseClassTypeSignature());
    }
    return (ClassTypeSignature[])localArrayList.toArray(new ClassTypeSignature[localArrayList.size()]);
  }
  
  private MethodTypeSignature parseMethodTypeSignature()
  {
    assert (index == 0);
    return MethodTypeSignature.make(parseZeroOrMoreFormalTypeParameters(), parseFormalParameters(), parseReturnType(), parseZeroOrMoreThrowsSignatures());
  }
  
  private TypeSignature[] parseFormalParameters()
  {
    if (current() != '(') {
      throw error("expected '('");
    }
    advance();
    TypeSignature[] arrayOfTypeSignature = parseZeroOrMoreTypeSignatures();
    if (current() != ')') {
      throw error("expected ')'");
    }
    advance();
    return arrayOfTypeSignature;
  }
  
  private TypeSignature[] parseZeroOrMoreTypeSignatures()
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i == 0) {
      switch (current())
      {
      case 'B': 
      case 'C': 
      case 'D': 
      case 'F': 
      case 'I': 
      case 'J': 
      case 'L': 
      case 'S': 
      case 'T': 
      case 'Z': 
      case '[': 
        localArrayList.add(parseTypeSignature());
        break;
      case 'E': 
      case 'G': 
      case 'H': 
      case 'K': 
      case 'M': 
      case 'N': 
      case 'O': 
      case 'P': 
      case 'Q': 
      case 'R': 
      case 'U': 
      case 'V': 
      case 'W': 
      case 'X': 
      case 'Y': 
      default: 
        i = 1;
      }
    }
    return (TypeSignature[])localArrayList.toArray(new TypeSignature[localArrayList.size()]);
  }
  
  private ReturnType parseReturnType()
  {
    if (current() == 'V')
    {
      advance();
      return VoidDescriptor.make();
    }
    return parseTypeSignature();
  }
  
  private FieldTypeSignature[] parseZeroOrMoreThrowsSignatures()
  {
    ArrayList localArrayList = new ArrayList(3);
    while (current() == '^') {
      localArrayList.add(parseThrowsSignature());
    }
    return (FieldTypeSignature[])localArrayList.toArray(new FieldTypeSignature[localArrayList.size()]);
  }
  
  private FieldTypeSignature parseThrowsSignature()
  {
    assert (current() == '^');
    if (current() != '^') {
      throw error("expected throws signature");
    }
    advance();
    return parseFieldTypeSignature(false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\parser\SignatureParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */