package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;

public final class TraceSignatureVisitor
  extends SignatureVisitor
{
  private final StringBuffer declaration;
  private boolean isInterface;
  private boolean seenFormalParameter;
  private boolean seenInterfaceBound;
  private boolean seenParameter;
  private boolean seenInterface;
  private StringBuffer returnType;
  private StringBuffer exceptions;
  private int argumentStack;
  private int arrayStack;
  private String separator = "";
  
  public TraceSignatureVisitor(int paramInt)
  {
    super(327680);
    isInterface = ((paramInt & 0x200) != 0);
    declaration = new StringBuffer();
  }
  
  private TraceSignatureVisitor(StringBuffer paramStringBuffer)
  {
    super(327680);
    declaration = paramStringBuffer;
  }
  
  public void visitFormalTypeParameter(String paramString)
  {
    declaration.append(seenFormalParameter ? ", " : "<").append(paramString);
    seenFormalParameter = true;
    seenInterfaceBound = false;
  }
  
  public SignatureVisitor visitClassBound()
  {
    separator = " extends ";
    startType();
    return this;
  }
  
  public SignatureVisitor visitInterfaceBound()
  {
    separator = (seenInterfaceBound ? ", " : " extends ");
    seenInterfaceBound = true;
    startType();
    return this;
  }
  
  public SignatureVisitor visitSuperclass()
  {
    endFormals();
    separator = " extends ";
    startType();
    return this;
  }
  
  public SignatureVisitor visitInterface()
  {
    separator = (isInterface ? " extends " : seenInterface ? ", " : " implements ");
    seenInterface = true;
    startType();
    return this;
  }
  
  public SignatureVisitor visitParameterType()
  {
    endFormals();
    if (seenParameter)
    {
      declaration.append(", ");
    }
    else
    {
      seenParameter = true;
      declaration.append('(');
    }
    startType();
    return this;
  }
  
  public SignatureVisitor visitReturnType()
  {
    endFormals();
    if (seenParameter) {
      seenParameter = false;
    } else {
      declaration.append('(');
    }
    declaration.append(')');
    returnType = new StringBuffer();
    return new TraceSignatureVisitor(returnType);
  }
  
  public SignatureVisitor visitExceptionType()
  {
    if (exceptions == null) {
      exceptions = new StringBuffer();
    } else {
      exceptions.append(", ");
    }
    return new TraceSignatureVisitor(exceptions);
  }
  
  public void visitBaseType(char paramChar)
  {
    switch (paramChar)
    {
    case 'V': 
      declaration.append("void");
      break;
    case 'B': 
      declaration.append("byte");
      break;
    case 'J': 
      declaration.append("long");
      break;
    case 'Z': 
      declaration.append("boolean");
      break;
    case 'I': 
      declaration.append("int");
      break;
    case 'S': 
      declaration.append("short");
      break;
    case 'C': 
      declaration.append("char");
      break;
    case 'F': 
      declaration.append("float");
      break;
    case 'D': 
    case 'E': 
    case 'G': 
    case 'H': 
    case 'K': 
    case 'L': 
    case 'M': 
    case 'N': 
    case 'O': 
    case 'P': 
    case 'Q': 
    case 'R': 
    case 'T': 
    case 'U': 
    case 'W': 
    case 'X': 
    case 'Y': 
    default: 
      declaration.append("double");
    }
    endType();
  }
  
  public void visitTypeVariable(String paramString)
  {
    declaration.append(paramString);
    endType();
  }
  
  public SignatureVisitor visitArrayType()
  {
    startType();
    arrayStack |= 0x1;
    return this;
  }
  
  public void visitClassType(String paramString)
  {
    if ("java/lang/Object".equals(paramString))
    {
      int i = (argumentStack % 2 != 0) || (seenParameter) ? 1 : 0;
      if (i != 0) {
        declaration.append(separator).append(paramString.replace('/', '.'));
      }
    }
    else
    {
      declaration.append(separator).append(paramString.replace('/', '.'));
    }
    separator = "";
    argumentStack *= 2;
  }
  
  public void visitInnerClassType(String paramString)
  {
    if (argumentStack % 2 != 0) {
      declaration.append('>');
    }
    argumentStack /= 2;
    declaration.append('.');
    declaration.append(separator).append(paramString.replace('/', '.'));
    separator = "";
    argumentStack *= 2;
  }
  
  public void visitTypeArgument()
  {
    if (argumentStack % 2 == 0)
    {
      argumentStack += 1;
      declaration.append('<');
    }
    else
    {
      declaration.append(", ");
    }
    declaration.append('?');
  }
  
  public SignatureVisitor visitTypeArgument(char paramChar)
  {
    if (argumentStack % 2 == 0)
    {
      argumentStack += 1;
      declaration.append('<');
    }
    else
    {
      declaration.append(", ");
    }
    if (paramChar == '+') {
      declaration.append("? extends ");
    } else if (paramChar == '-') {
      declaration.append("? super ");
    }
    startType();
    return this;
  }
  
  public void visitEnd()
  {
    if (argumentStack % 2 != 0) {
      declaration.append('>');
    }
    argumentStack /= 2;
    endType();
  }
  
  public String getDeclaration()
  {
    return declaration.toString();
  }
  
  public String getReturnType()
  {
    return returnType == null ? null : returnType.toString();
  }
  
  public String getExceptions()
  {
    return exceptions == null ? null : exceptions.toString();
  }
  
  private void endFormals()
  {
    if (seenFormalParameter)
    {
      declaration.append('>');
      seenFormalParameter = false;
    }
  }
  
  private void startType()
  {
    arrayStack *= 2;
  }
  
  private void endType()
  {
    if (arrayStack % 2 == 0) {
      arrayStack /= 2;
    } else {
      while (arrayStack % 2 != 0)
      {
        arrayStack /= 2;
        declaration.append("[]");
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\TraceSignatureVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */