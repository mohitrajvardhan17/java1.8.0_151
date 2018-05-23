package jdk.internal.org.objectweb.asm.signature;

public class SignatureWriter
  extends SignatureVisitor
{
  private final StringBuffer buf = new StringBuffer();
  private boolean hasFormals;
  private boolean hasParameters;
  private int argumentStack;
  
  public SignatureWriter()
  {
    super(327680);
  }
  
  public void visitFormalTypeParameter(String paramString)
  {
    if (!hasFormals)
    {
      hasFormals = true;
      buf.append('<');
    }
    buf.append(paramString);
    buf.append(':');
  }
  
  public SignatureVisitor visitClassBound()
  {
    return this;
  }
  
  public SignatureVisitor visitInterfaceBound()
  {
    buf.append(':');
    return this;
  }
  
  public SignatureVisitor visitSuperclass()
  {
    endFormals();
    return this;
  }
  
  public SignatureVisitor visitInterface()
  {
    return this;
  }
  
  public SignatureVisitor visitParameterType()
  {
    endFormals();
    if (!hasParameters)
    {
      hasParameters = true;
      buf.append('(');
    }
    return this;
  }
  
  public SignatureVisitor visitReturnType()
  {
    endFormals();
    if (!hasParameters) {
      buf.append('(');
    }
    buf.append(')');
    return this;
  }
  
  public SignatureVisitor visitExceptionType()
  {
    buf.append('^');
    return this;
  }
  
  public void visitBaseType(char paramChar)
  {
    buf.append(paramChar);
  }
  
  public void visitTypeVariable(String paramString)
  {
    buf.append('T');
    buf.append(paramString);
    buf.append(';');
  }
  
  public SignatureVisitor visitArrayType()
  {
    buf.append('[');
    return this;
  }
  
  public void visitClassType(String paramString)
  {
    buf.append('L');
    buf.append(paramString);
    argumentStack *= 2;
  }
  
  public void visitInnerClassType(String paramString)
  {
    endArguments();
    buf.append('.');
    buf.append(paramString);
    argumentStack *= 2;
  }
  
  public void visitTypeArgument()
  {
    if (argumentStack % 2 == 0)
    {
      argumentStack += 1;
      buf.append('<');
    }
    buf.append('*');
  }
  
  public SignatureVisitor visitTypeArgument(char paramChar)
  {
    if (argumentStack % 2 == 0)
    {
      argumentStack += 1;
      buf.append('<');
    }
    if (paramChar != '=') {
      buf.append(paramChar);
    }
    return this;
  }
  
  public void visitEnd()
  {
    endArguments();
    buf.append(';');
  }
  
  public String toString()
  {
    return buf.toString();
  }
  
  private void endFormals()
  {
    if (hasFormals)
    {
      hasFormals = false;
      buf.append('>');
    }
  }
  
  private void endArguments()
  {
    if (argumentStack % 2 != 0) {
      buf.append('>');
    }
    argumentStack /= 2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\signature\SignatureWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */