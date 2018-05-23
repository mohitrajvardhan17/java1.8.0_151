package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PSVIElementNSImpl
  extends ElementNSImpl
  implements ElementPSVI
{
  static final long serialVersionUID = 6815489624636016068L;
  protected XSElementDeclaration fDeclaration = null;
  protected XSTypeDefinition fTypeDecl = null;
  protected boolean fNil = false;
  protected boolean fSpecified = true;
  protected String fNormalizedValue = null;
  protected Object fActualValue = null;
  protected short fActualValueType = 45;
  protected ShortList fItemValueTypes = null;
  protected XSNotationDeclaration fNotation = null;
  protected XSSimpleTypeDefinition fMemberType = null;
  protected short fValidationAttempted = 0;
  protected short fValidity = 0;
  protected StringList fErrorCodes = null;
  protected String fValidationContext = null;
  protected XSModel fSchemaInformation = null;
  
  public PSVIElementNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2, String paramString3)
  {
    super(paramCoreDocumentImpl, paramString1, paramString2, paramString3);
  }
  
  public PSVIElementNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2)
  {
    super(paramCoreDocumentImpl, paramString1, paramString2);
  }
  
  public String getSchemaDefault()
  {
    return fDeclaration == null ? null : fDeclaration.getConstraintValue();
  }
  
  public String getSchemaNormalizedValue()
  {
    return fNormalizedValue;
  }
  
  public boolean getIsSchemaSpecified()
  {
    return fSpecified;
  }
  
  public short getValidationAttempted()
  {
    return fValidationAttempted;
  }
  
  public short getValidity()
  {
    return fValidity;
  }
  
  public StringList getErrorCodes()
  {
    return fErrorCodes;
  }
  
  public String getValidationContext()
  {
    return fValidationContext;
  }
  
  public boolean getNil()
  {
    return fNil;
  }
  
  public XSNotationDeclaration getNotation()
  {
    return fNotation;
  }
  
  public XSTypeDefinition getTypeDefinition()
  {
    return fTypeDecl;
  }
  
  public XSSimpleTypeDefinition getMemberTypeDefinition()
  {
    return fMemberType;
  }
  
  public XSElementDeclaration getElementDeclaration()
  {
    return fDeclaration;
  }
  
  public XSModel getSchemaInformation()
  {
    return fSchemaInformation;
  }
  
  public void setPSVI(ElementPSVI paramElementPSVI)
  {
    fDeclaration = paramElementPSVI.getElementDeclaration();
    fNotation = paramElementPSVI.getNotation();
    fValidationContext = paramElementPSVI.getValidationContext();
    fTypeDecl = paramElementPSVI.getTypeDefinition();
    fSchemaInformation = paramElementPSVI.getSchemaInformation();
    fValidity = paramElementPSVI.getValidity();
    fValidationAttempted = paramElementPSVI.getValidationAttempted();
    fErrorCodes = paramElementPSVI.getErrorCodes();
    fNormalizedValue = paramElementPSVI.getSchemaNormalizedValue();
    fActualValue = paramElementPSVI.getActualNormalizedValue();
    fActualValueType = paramElementPSVI.getActualNormalizedValueType();
    fItemValueTypes = paramElementPSVI.getItemValueTypes();
    fMemberType = paramElementPSVI.getMemberTypeDefinition();
    fSpecified = paramElementPSVI.getIsSchemaSpecified();
    fNil = paramElementPSVI.getNil();
  }
  
  public Object getActualNormalizedValue()
  {
    return fActualValue;
  }
  
  public short getActualNormalizedValueType()
  {
    return fActualValueType;
  }
  
  public ShortList getItemValueTypes()
  {
    return fItemValueTypes;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    throw new NotSerializableException(getClass().getName());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    throw new NotSerializableException(getClass().getName());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\PSVIElementNSImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */