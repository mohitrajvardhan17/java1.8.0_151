package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PSVIAttrNSImpl
  extends AttrNSImpl
  implements AttributePSVI
{
  static final long serialVersionUID = -3241738699421018889L;
  protected XSAttributeDeclaration fDeclaration = null;
  protected XSTypeDefinition fTypeDecl = null;
  protected boolean fSpecified = true;
  protected String fNormalizedValue = null;
  protected Object fActualValue = null;
  protected short fActualValueType = 45;
  protected ShortList fItemValueTypes = null;
  protected XSSimpleTypeDefinition fMemberType = null;
  protected short fValidationAttempted = 0;
  protected short fValidity = 0;
  protected StringList fErrorCodes = null;
  protected String fValidationContext = null;
  
  public PSVIAttrNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2, String paramString3)
  {
    super(paramCoreDocumentImpl, paramString1, paramString2, paramString3);
  }
  
  public PSVIAttrNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2)
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
  
  public XSTypeDefinition getTypeDefinition()
  {
    return fTypeDecl;
  }
  
  public XSSimpleTypeDefinition getMemberTypeDefinition()
  {
    return fMemberType;
  }
  
  public XSAttributeDeclaration getAttributeDeclaration()
  {
    return fDeclaration;
  }
  
  public void setPSVI(AttributePSVI paramAttributePSVI)
  {
    fDeclaration = paramAttributePSVI.getAttributeDeclaration();
    fValidationContext = paramAttributePSVI.getValidationContext();
    fValidity = paramAttributePSVI.getValidity();
    fValidationAttempted = paramAttributePSVI.getValidationAttempted();
    fErrorCodes = paramAttributePSVI.getErrorCodes();
    fNormalizedValue = paramAttributePSVI.getSchemaNormalizedValue();
    fActualValue = paramAttributePSVI.getActualNormalizedValue();
    fActualValueType = paramAttributePSVI.getActualNormalizedValueType();
    fItemValueTypes = paramAttributePSVI.getItemValueTypes();
    fTypeDecl = paramAttributePSVI.getTypeDefinition();
    fMemberType = paramAttributePSVI.getMemberTypeDefinition();
    fSpecified = paramAttributePSVI.getIsSchemaSpecified();
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\PSVIAttrNSImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */