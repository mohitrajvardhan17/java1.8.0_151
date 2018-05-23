package jdk.internal.org.objectweb.asm.signature;

public class SignatureReader
{
  private final String signature;
  
  public SignatureReader(String paramString)
  {
    signature = paramString;
  }
  
  public void accept(SignatureVisitor paramSignatureVisitor)
  {
    String str = signature;
    int i = str.length();
    if (str.charAt(0) == '<')
    {
      j = 2;
      int k;
      do
      {
        int m = str.indexOf(':', j);
        paramSignatureVisitor.visitFormalTypeParameter(str.substring(j - 1, m));
        j = m + 1;
        k = str.charAt(j);
        if ((k == 76) || (k == 91) || (k == 84)) {}
        for (j = parseType(str, j, paramSignatureVisitor.visitClassBound()); (k = str.charAt(j++)) == ':'; j = parseType(str, j, paramSignatureVisitor.visitInterfaceBound())) {}
      } while (k != 62);
    }
    else
    {
      j = 0;
    }
    if (str.charAt(j) == '(')
    {
      j++;
      while (str.charAt(j) != ')') {
        j = parseType(str, j, paramSignatureVisitor.visitParameterType());
      }
      for (j = parseType(str, j + 1, paramSignatureVisitor.visitReturnType()); j < i; j = parseType(str, j + 1, paramSignatureVisitor.visitExceptionType())) {}
    }
    for (int j = parseType(str, j, paramSignatureVisitor.visitSuperclass()); j < i; j = parseType(str, j, paramSignatureVisitor.visitInterface())) {}
  }
  
  public void acceptType(SignatureVisitor paramSignatureVisitor)
  {
    parseType(signature, 0, paramSignatureVisitor);
  }
  
  private static int parseType(String paramString, int paramInt, SignatureVisitor paramSignatureVisitor)
  {
    char c;
    switch (c = paramString.charAt(paramInt++))
    {
    case 'B': 
    case 'C': 
    case 'D': 
    case 'F': 
    case 'I': 
    case 'J': 
    case 'S': 
    case 'V': 
    case 'Z': 
      paramSignatureVisitor.visitBaseType(c);
      return paramInt;
    case '[': 
      return parseType(paramString, paramInt, paramSignatureVisitor.visitArrayType());
    case 'T': 
      int j = paramString.indexOf(';', paramInt);
      paramSignatureVisitor.visitTypeVariable(paramString.substring(paramInt, j));
      return j + 1;
    }
    int i = paramInt;
    int k = 0;
    int m = 0;
    for (;;)
    {
      String str;
      switch (c = paramString.charAt(paramInt++))
      {
      case '.': 
      case ';': 
        if (k == 0)
        {
          str = paramString.substring(i, paramInt - 1);
          if (m != 0) {
            paramSignatureVisitor.visitInnerClassType(str);
          } else {
            paramSignatureVisitor.visitClassType(str);
          }
        }
        if (c == ';')
        {
          paramSignatureVisitor.visitEnd();
          return paramInt;
        }
        i = paramInt;
        k = 0;
        m = 1;
        break;
      case '<': 
        str = paramString.substring(i, paramInt - 1);
        if (m != 0) {
          paramSignatureVisitor.visitInnerClassType(str);
        } else {
          paramSignatureVisitor.visitClassType(str);
        }
        k = 1;
        for (;;)
        {
          switch (c = paramString.charAt(paramInt))
          {
          case '>': 
            break;
          case '*': 
            paramInt++;
            paramSignatureVisitor.visitTypeArgument();
            break;
          case '+': 
          case '-': 
            paramInt = parseType(paramString, paramInt + 1, paramSignatureVisitor.visitTypeArgument(c));
            break;
          default: 
            paramInt = parseType(paramString, paramInt, paramSignatureVisitor.visitTypeArgument('='));
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\signature\SignatureReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */