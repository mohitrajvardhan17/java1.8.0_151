package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeReader;
import com.sun.corba.se.impl.encoding.WrapperInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.omg.CORBA.Any;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import sun.corba.OutputStreamFactory;

public final class TypeCodeImpl
  extends TypeCode
{
  protected static final int tk_indirect = -1;
  private static final int EMPTY = 0;
  private static final int SIMPLE = 1;
  private static final int COMPLEX = 2;
  private static final int[] typeTable = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 1, 2, 2, 2, 2, 0, 0, 0, 0, 1, 1, 2, 2, 2, 2 };
  static final String[] kindNames = { "null", "void", "short", "long", "ushort", "ulong", "float", "double", "boolean", "char", "octet", "any", "typecode", "principal", "objref", "struct", "union", "enum", "string", "sequence", "array", "alias", "exception", "longlong", "ulonglong", "longdouble", "wchar", "wstring", "fixed", "value", "valueBox", "native", "abstractInterface" };
  private int _kind = 0;
  private String _id = "";
  private String _name = "";
  private int _memberCount = 0;
  private String[] _memberNames = null;
  private TypeCodeImpl[] _memberTypes = null;
  private AnyImpl[] _unionLabels = null;
  private TypeCodeImpl _discriminator = null;
  private int _defaultIndex = -1;
  private int _length = 0;
  private TypeCodeImpl _contentType = null;
  private short _digits = 0;
  private short _scale = 0;
  private short _type_modifier = -1;
  private TypeCodeImpl _concrete_base = null;
  private short[] _memberAccess = null;
  private TypeCodeImpl _parent = null;
  private int _parentOffset = 0;
  private TypeCodeImpl _indirectType = null;
  private byte[] outBuffer = null;
  private boolean cachingEnabled = false;
  private com.sun.corba.se.spi.orb.ORB _orb;
  private ORBUtilSystemException wrapper;
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB)
  {
    _orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, TypeCode paramTypeCode)
  {
    this(paramORB);
    Object localObject;
    if ((paramTypeCode instanceof TypeCodeImpl))
    {
      localObject = (TypeCodeImpl)paramTypeCode;
      if (_kind == -1) {
        throw wrapper.badRemoteTypecode();
      }
      if ((_kind == 19) && (_contentType == null)) {
        throw wrapper.badRemoteTypecode();
      }
    }
    try
    {
      int j;
      switch (_kind)
      {
      case 29: 
        _type_modifier = paramTypeCode.type_modifier();
        localObject = paramTypeCode.concrete_base_type();
        if (localObject != null) {
          _concrete_base = convertToNative(_orb, (TypeCode)localObject);
        } else {
          _concrete_base = null;
        }
        _memberAccess = new short[paramTypeCode.member_count()];
        for (j = 0; j < paramTypeCode.member_count(); j++) {
          _memberAccess[j] = paramTypeCode.member_visibility(j);
        }
      case 15: 
      case 16: 
      case 22: 
        _memberTypes = new TypeCodeImpl[paramTypeCode.member_count()];
        for (j = 0; j < paramTypeCode.member_count(); j++)
        {
          _memberTypes[j] = convertToNative(_orb, paramTypeCode.member_type(j));
          _memberTypes[j].setParent(this);
        }
      case 17: 
        _memberNames = new String[paramTypeCode.member_count()];
        for (j = 0; j < paramTypeCode.member_count(); j++) {
          _memberNames[j] = paramTypeCode.member_name(j);
        }
        _memberCount = paramTypeCode.member_count();
      case 14: 
      case 21: 
      case 30: 
      case 31: 
      case 32: 
        setId(paramTypeCode.id());
        _name = paramTypeCode.name();
      }
      switch (_kind)
      {
      case 16: 
        _discriminator = convertToNative(_orb, paramTypeCode.discriminator_type());
        _defaultIndex = paramTypeCode.default_index();
        _unionLabels = new AnyImpl[_memberCount];
        for (int i = 0; i < _memberCount; i++) {
          _unionLabels[i] = new AnyImpl(_orb, paramTypeCode.member_label(i));
        }
      }
      switch (_kind)
      {
      case 18: 
      case 19: 
      case 20: 
      case 27: 
        _length = paramTypeCode.length();
      }
      switch (_kind)
      {
      case 19: 
      case 20: 
      case 21: 
      case 30: 
        _contentType = convertToNative(_orb, paramTypeCode.content_type());
      }
    }
    catch (Bounds localBounds) {}catch (BadKind localBadKind) {}
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt)
  {
    this(paramORB);
    switch (_kind)
    {
    case 14: 
      setId("IDL:omg.org/CORBA/Object:1.0");
      _name = "Object";
      break;
    case 18: 
    case 27: 
      _length = 0;
      break;
    case 29: 
      _concrete_base = null;
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, StructMember[] paramArrayOfStructMember)
  {
    this(paramORB);
    if ((paramInt == 15) || (paramInt == 22))
    {
      _kind = paramInt;
      setId(paramString1);
      _name = paramString2;
      _memberCount = paramArrayOfStructMember.length;
      _memberNames = new String[_memberCount];
      _memberTypes = new TypeCodeImpl[_memberCount];
      for (int i = 0; i < _memberCount; i++)
      {
        _memberNames[i] = name;
        _memberTypes[i] = convertToNative(_orb, type);
        _memberTypes[i].setParent(this);
      }
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, TypeCode paramTypeCode, UnionMember[] paramArrayOfUnionMember)
  {
    this(paramORB);
    if (paramInt == 16)
    {
      _kind = paramInt;
      setId(paramString1);
      _name = paramString2;
      _memberCount = paramArrayOfUnionMember.length;
      _discriminator = convertToNative(_orb, paramTypeCode);
      _memberNames = new String[_memberCount];
      _memberTypes = new TypeCodeImpl[_memberCount];
      _unionLabels = new AnyImpl[_memberCount];
      for (int i = 0; i < _memberCount; i++)
      {
        _memberNames[i] = name;
        _memberTypes[i] = convertToNative(_orb, type);
        _memberTypes[i].setParent(this);
        _unionLabels[i] = new AnyImpl(_orb, label);
        if ((_unionLabels[i].type().kind() == TCKind.tk_octet) && (_unionLabels[i].extract_octet() == 0)) {
          _defaultIndex = i;
        }
      }
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, short paramShort, TypeCode paramTypeCode, ValueMember[] paramArrayOfValueMember)
  {
    this(paramORB);
    if (paramInt == 29)
    {
      _kind = paramInt;
      setId(paramString1);
      _name = paramString2;
      _type_modifier = paramShort;
      if (paramTypeCode != null) {
        _concrete_base = convertToNative(_orb, paramTypeCode);
      }
      _memberCount = paramArrayOfValueMember.length;
      _memberNames = new String[_memberCount];
      _memberTypes = new TypeCodeImpl[_memberCount];
      _memberAccess = new short[_memberCount];
      for (int i = 0; i < _memberCount; i++)
      {
        _memberNames[i] = name;
        _memberTypes[i] = convertToNative(_orb, type);
        _memberTypes[i].setParent(this);
        _memberAccess[i] = access;
      }
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, String[] paramArrayOfString)
  {
    this(paramORB);
    if (paramInt == 17)
    {
      _kind = paramInt;
      setId(paramString1);
      _name = paramString2;
      _memberCount = paramArrayOfString.length;
      _memberNames = new String[_memberCount];
      for (int i = 0; i < _memberCount; i++) {
        _memberNames[i] = paramArrayOfString[i];
      }
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, TypeCode paramTypeCode)
  {
    this(paramORB);
    if ((paramInt == 21) || (paramInt == 30))
    {
      _kind = paramInt;
      setId(paramString1);
      _name = paramString2;
      _contentType = convertToNative(_orb, paramTypeCode);
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2)
  {
    this(paramORB);
    if ((paramInt == 14) || (paramInt == 31) || (paramInt == 32))
    {
      _kind = paramInt;
      setId(paramString1);
      _name = paramString2;
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt1, int paramInt2)
  {
    this(paramORB);
    if (paramInt2 < 0) {
      throw wrapper.negativeBounds();
    }
    if ((paramInt1 == 18) || (paramInt1 == 27))
    {
      _kind = paramInt1;
      _length = paramInt2;
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt1, int paramInt2, TypeCode paramTypeCode)
  {
    this(paramORB);
    if ((paramInt1 == 19) || (paramInt1 == 20))
    {
      _kind = paramInt1;
      _length = paramInt2;
      _contentType = convertToNative(_orb, paramTypeCode);
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramORB);
    if (paramInt1 == 19)
    {
      _kind = paramInt1;
      _length = paramInt2;
      _parentOffset = paramInt3;
    }
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, String paramString)
  {
    this(paramORB);
    tryIndirectType();
  }
  
  public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, short paramShort1, short paramShort2)
  {
    this(paramORB);
    if (paramInt == 28)
    {
      _kind = paramInt;
      _digits = paramShort1;
      _scale = paramShort2;
    }
  }
  
  protected static TypeCodeImpl convertToNative(com.sun.corba.se.spi.orb.ORB paramORB, TypeCode paramTypeCode)
  {
    if ((paramTypeCode instanceof TypeCodeImpl)) {
      return (TypeCodeImpl)paramTypeCode;
    }
    return new TypeCodeImpl(paramORB, paramTypeCode);
  }
  
  public static CDROutputStream newOutputStream(com.sun.corba.se.spi.orb.ORB paramORB)
  {
    TypeCodeOutputStream localTypeCodeOutputStream = OutputStreamFactory.newTypeCodeOutputStream(paramORB);
    return localTypeCodeOutputStream;
  }
  
  private TypeCodeImpl indirectType()
  {
    _indirectType = tryIndirectType();
    if (_indirectType == null) {
      throw wrapper.unresolvedRecursiveTypecode();
    }
    return _indirectType;
  }
  
  private TypeCodeImpl tryIndirectType()
  {
    if (_indirectType != null) {
      return _indirectType;
    }
    setIndirectType(_orb.getTypeCode(_id));
    return _indirectType;
  }
  
  private void setIndirectType(TypeCodeImpl paramTypeCodeImpl)
  {
    _indirectType = paramTypeCodeImpl;
    if (_indirectType != null) {
      try
      {
        _id = _indirectType.id();
      }
      catch (BadKind localBadKind)
      {
        throw wrapper.badkindCannotOccur();
      }
    }
  }
  
  private void setId(String paramString)
  {
    _id = paramString;
    if ((_orb instanceof TypeCodeFactory)) {
      _orb.setTypeCode(_id, this);
    }
  }
  
  private void setParent(TypeCodeImpl paramTypeCodeImpl)
  {
    _parent = paramTypeCodeImpl;
  }
  
  private TypeCodeImpl getParentAtLevel(int paramInt)
  {
    if (paramInt == 0) {
      return this;
    }
    if (_parent == null) {
      throw wrapper.unresolvedRecursiveTypecode();
    }
    return _parent.getParentAtLevel(paramInt - 1);
  }
  
  private TypeCodeImpl lazy_content_type()
  {
    if ((_contentType == null) && (_kind == 19) && (_parentOffset > 0) && (_parent != null))
    {
      TypeCodeImpl localTypeCodeImpl = getParentAtLevel(_parentOffset);
      if ((localTypeCodeImpl != null) && (_id != null)) {
        _contentType = new TypeCodeImpl(_orb, _id);
      }
    }
    return _contentType;
  }
  
  private TypeCode realType(TypeCode paramTypeCode)
  {
    TypeCode localTypeCode = paramTypeCode;
    try
    {
      while (localTypeCode.kind().value() == 21) {
        localTypeCode = localTypeCode.content_type();
      }
    }
    catch (BadKind localBadKind)
    {
      throw wrapper.badkindCannotOccur();
    }
    return localTypeCode;
  }
  
  public final boolean equal(TypeCode paramTypeCode)
  {
    if (paramTypeCode == this) {
      return true;
    }
    try
    {
      if (_kind == -1)
      {
        if ((_id != null) && (paramTypeCode.id() != null)) {
          return _id.equals(paramTypeCode.id());
        }
        return (_id == null) && (paramTypeCode.id() == null);
      }
      if (_kind != paramTypeCode.kind().value()) {
        return false;
      }
      switch (typeTable[_kind])
      {
      case 0: 
        return true;
      case 1: 
        switch (_kind)
        {
        case 18: 
        case 27: 
          return _length == paramTypeCode.length();
        case 28: 
          return (_digits == paramTypeCode.fixed_digits()) && (_scale == paramTypeCode.fixed_scale());
        }
        return false;
      case 2: 
        int i;
        switch (_kind)
        {
        case 14: 
          if (_id.compareTo(paramTypeCode.id()) == 0) {
            return true;
          }
          if (_id.compareTo(_orb.get_primitive_tc(_kind).id()) == 0) {
            return true;
          }
          return paramTypeCode.id().compareTo(_orb.get_primitive_tc(_kind).id()) == 0;
        case 31: 
        case 32: 
          return _id.compareTo(paramTypeCode.id()) == 0;
        case 15: 
        case 22: 
          if (_memberCount != paramTypeCode.member_count()) {
            return false;
          }
          if (_id.compareTo(paramTypeCode.id()) != 0) {
            return false;
          }
          for (i = 0; i < _memberCount; i++) {
            if (!_memberTypes[i].equal(paramTypeCode.member_type(i))) {
              return false;
            }
          }
          return true;
        case 16: 
          if (_memberCount != paramTypeCode.member_count()) {
            return false;
          }
          if (_id.compareTo(paramTypeCode.id()) != 0) {
            return false;
          }
          if (_defaultIndex != paramTypeCode.default_index()) {
            return false;
          }
          if (!_discriminator.equal(paramTypeCode.discriminator_type())) {
            return false;
          }
          for (i = 0; i < _memberCount; i++) {
            if (!_unionLabels[i].equal(paramTypeCode.member_label(i))) {
              return false;
            }
          }
          for (i = 0; i < _memberCount; i++) {
            if (!_memberTypes[i].equal(paramTypeCode.member_type(i))) {
              return false;
            }
          }
          return true;
        case 17: 
          if (_id.compareTo(paramTypeCode.id()) != 0) {
            return false;
          }
          return _memberCount == paramTypeCode.member_count();
        case 19: 
        case 20: 
          if (_length != paramTypeCode.length()) {
            return false;
          }
          return lazy_content_type().equal(paramTypeCode.content_type());
        case 29: 
          if (_memberCount != paramTypeCode.member_count()) {
            return false;
          }
          if (_id.compareTo(paramTypeCode.id()) != 0) {
            return false;
          }
          for (i = 0; i < _memberCount; i++) {
            if ((_memberAccess[i] != paramTypeCode.member_visibility(i)) || (!_memberTypes[i].equal(paramTypeCode.member_type(i)))) {
              return false;
            }
          }
          if (_type_modifier == paramTypeCode.type_modifier()) {
            return false;
          }
          TypeCode localTypeCode = paramTypeCode.concrete_base_type();
          return ((_concrete_base != null) || (localTypeCode == null)) && ((_concrete_base == null) || (localTypeCode != null)) && (_concrete_base.equal(localTypeCode));
        case 21: 
        case 30: 
          if (_id.compareTo(paramTypeCode.id()) != 0) {
            return false;
          }
          return _contentType.equal(paramTypeCode.content_type());
        }
        break;
      }
    }
    catch (Bounds localBounds) {}catch (BadKind localBadKind) {}
    return false;
  }
  
  public boolean equivalent(TypeCode paramTypeCode)
  {
    if (paramTypeCode == this) {
      return true;
    }
    Object localObject = _kind == -1 ? indirectType() : this;
    localObject = realType((TypeCode)localObject);
    TypeCode localTypeCode = realType(paramTypeCode);
    if (((TypeCode)localObject).kind().value() != localTypeCode.kind().value()) {
      return false;
    }
    String str1 = null;
    String str2 = null;
    try
    {
      str1 = id();
      str2 = paramTypeCode.id();
      if ((str1 != null) && (str2 != null)) {
        return str1.equals(str2);
      }
    }
    catch (BadKind localBadKind1) {}
    int i = ((TypeCode)localObject).kind().value();
    try
    {
      if (((i == 15) || (i == 16) || (i == 17) || (i == 22) || (i == 29)) && (((TypeCode)localObject).member_count() != localTypeCode.member_count())) {
        return false;
      }
      if ((i == 16) && (((TypeCode)localObject).default_index() != localTypeCode.default_index())) {
        return false;
      }
      if (((i == 18) || (i == 27) || (i == 19) || (i == 20)) && (((TypeCode)localObject).length() != localTypeCode.length())) {
        return false;
      }
      if ((i == 28) && ((((TypeCode)localObject).fixed_digits() != localTypeCode.fixed_digits()) || (((TypeCode)localObject).fixed_scale() != localTypeCode.fixed_scale()))) {
        return false;
      }
      int j;
      if (i == 16)
      {
        for (j = 0; j < ((TypeCode)localObject).member_count(); j++) {
          if (((TypeCode)localObject).member_label(j) != localTypeCode.member_label(j)) {
            return false;
          }
        }
        if (!((TypeCode)localObject).discriminator_type().equivalent(localTypeCode.discriminator_type())) {
          return false;
        }
      }
      if (((i == 21) || (i == 30) || (i == 19) || (i == 20)) && (!((TypeCode)localObject).content_type().equivalent(localTypeCode.content_type()))) {
        return false;
      }
      if ((i == 15) || (i == 16) || (i == 22) || (i == 29)) {
        for (j = 0; j < ((TypeCode)localObject).member_count(); j++) {
          if (!((TypeCode)localObject).member_type(j).equivalent(localTypeCode.member_type(j))) {
            return false;
          }
        }
      }
    }
    catch (BadKind localBadKind2)
    {
      throw wrapper.badkindCannotOccur();
    }
    catch (Bounds localBounds)
    {
      throw wrapper.boundsCannotOccur();
    }
    return true;
  }
  
  public TypeCode get_compact_typecode()
  {
    return this;
  }
  
  public TCKind kind()
  {
    if (_kind == -1) {
      return indirectType().kind();
    }
    return TCKind.from_int(_kind);
  }
  
  public boolean is_recursive()
  {
    return _kind == -1;
  }
  
  public String id()
    throws BadKind
  {
    switch (_kind)
    {
    case -1: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 21: 
    case 22: 
    case 29: 
    case 30: 
    case 31: 
    case 32: 
      return _id;
    }
    throw new BadKind();
  }
  
  public String name()
    throws BadKind
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().name();
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 21: 
    case 22: 
    case 29: 
    case 30: 
    case 31: 
    case 32: 
      return _name;
    }
    throw new BadKind();
  }
  
  public int member_count()
    throws BadKind
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().member_count();
    case 15: 
    case 16: 
    case 17: 
    case 22: 
    case 29: 
      return _memberCount;
    }
    throw new BadKind();
  }
  
  public String member_name(int paramInt)
    throws BadKind, Bounds
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().member_name(paramInt);
    case 15: 
    case 16: 
    case 17: 
    case 22: 
    case 29: 
      try
      {
        return _memberNames[paramInt];
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new Bounds();
      }
    }
    throw new BadKind();
  }
  
  public TypeCode member_type(int paramInt)
    throws BadKind, Bounds
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().member_type(paramInt);
    case 15: 
    case 16: 
    case 22: 
    case 29: 
      try
      {
        return _memberTypes[paramInt];
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new Bounds();
      }
    }
    throw new BadKind();
  }
  
  public Any member_label(int paramInt)
    throws BadKind, Bounds
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().member_label(paramInt);
    case 16: 
      try
      {
        return new AnyImpl(_orb, _unionLabels[paramInt]);
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new Bounds();
      }
    }
    throw new BadKind();
  }
  
  public TypeCode discriminator_type()
    throws BadKind
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().discriminator_type();
    case 16: 
      return _discriminator;
    }
    throw new BadKind();
  }
  
  public int default_index()
    throws BadKind
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().default_index();
    case 16: 
      return _defaultIndex;
    }
    throw new BadKind();
  }
  
  public int length()
    throws BadKind
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().length();
    case 18: 
    case 19: 
    case 20: 
    case 27: 
      return _length;
    }
    throw new BadKind();
  }
  
  public TypeCode content_type()
    throws BadKind
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().content_type();
    case 19: 
      return lazy_content_type();
    case 20: 
    case 21: 
    case 30: 
      return _contentType;
    }
    throw new BadKind();
  }
  
  public short fixed_digits()
    throws BadKind
  {
    switch (_kind)
    {
    case 28: 
      return _digits;
    }
    throw new BadKind();
  }
  
  public short fixed_scale()
    throws BadKind
  {
    switch (_kind)
    {
    case 28: 
      return _scale;
    }
    throw new BadKind();
  }
  
  public short member_visibility(int paramInt)
    throws BadKind, Bounds
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().member_visibility(paramInt);
    case 29: 
      try
      {
        return _memberAccess[paramInt];
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new Bounds();
      }
    }
    throw new BadKind();
  }
  
  public short type_modifier()
    throws BadKind
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().type_modifier();
    case 29: 
      return _type_modifier;
    }
    throw new BadKind();
  }
  
  public TypeCode concrete_base_type()
    throws BadKind
  {
    switch (_kind)
    {
    case -1: 
      return indirectType().concrete_base_type();
    case 29: 
      return _concrete_base;
    }
    throw new BadKind();
  }
  
  public void read_value(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
  {
    if ((paramInputStream instanceof TypeCodeReader))
    {
      if (read_value_kind((TypeCodeReader)paramInputStream)) {
        read_value_body(paramInputStream);
      }
    }
    else if ((paramInputStream instanceof CDRInputStream))
    {
      WrapperInputStream localWrapperInputStream = new WrapperInputStream((CDRInputStream)paramInputStream);
      if (read_value_kind(localWrapperInputStream)) {
        read_value_body(localWrapperInputStream);
      }
    }
    else
    {
      read_value_kind(paramInputStream);
      read_value_body(paramInputStream);
    }
  }
  
  private void read_value_recursive(TypeCodeInputStream paramTypeCodeInputStream)
  {
    if ((paramTypeCodeInputStream instanceof TypeCodeReader))
    {
      if (read_value_kind(paramTypeCodeInputStream)) {
        read_value_body(paramTypeCodeInputStream);
      }
    }
    else
    {
      read_value_kind(paramTypeCodeInputStream);
      read_value_body(paramTypeCodeInputStream);
    }
  }
  
  boolean read_value_kind(TypeCodeReader paramTypeCodeReader)
  {
    _kind = paramTypeCodeReader.read_long();
    int i = paramTypeCodeReader.getTopLevelPosition() - 4;
    if (((_kind < 0) || (_kind > typeTable.length)) && (_kind != -1)) {
      throw wrapper.cannotMarshalBadTckind();
    }
    if (_kind == 31) {
      throw wrapper.cannotMarshalNative();
    }
    TypeCodeReader localTypeCodeReader = paramTypeCodeReader.getTopLevelStream();
    if (_kind == -1)
    {
      int j = paramTypeCodeReader.read_long();
      if (j > -4) {
        throw wrapper.invalidIndirection(new Integer(j));
      }
      int k = paramTypeCodeReader.getTopLevelPosition();
      int m = k - 4 + j;
      TypeCodeImpl localTypeCodeImpl = localTypeCodeReader.getTypeCodeAtPosition(m);
      if (localTypeCodeImpl == null) {
        throw wrapper.indirectionNotFound(new Integer(m));
      }
      setIndirectType(localTypeCodeImpl);
      return false;
    }
    localTypeCodeReader.addTypeCodeAtPosition(this, i);
    return true;
  }
  
  void read_value_kind(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
  {
    _kind = paramInputStream.read_long();
    if (((_kind < 0) || (_kind > typeTable.length)) && (_kind != -1)) {
      throw wrapper.cannotMarshalBadTckind();
    }
    if (_kind == 31) {
      throw wrapper.cannotMarshalNative();
    }
    if (_kind == -1) {
      throw wrapper.recursiveTypecodeError();
    }
  }
  
  void read_value_body(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
  {
    switch (typeTable[_kind])
    {
    case 0: 
      break;
    case 1: 
      switch (_kind)
      {
      case 18: 
      case 27: 
        _length = paramInputStream.read_long();
        break;
      case 28: 
        _digits = paramInputStream.read_ushort();
        _scale = paramInputStream.read_short();
        break;
      default: 
        throw wrapper.invalidSimpleTypecode();
      }
      break;
    case 2: 
      TypeCodeInputStream localTypeCodeInputStream = TypeCodeInputStream.readEncapsulation(paramInputStream, paramInputStream.orb());
      int i;
      switch (_kind)
      {
      case 14: 
      case 32: 
        setId(localTypeCodeInputStream.read_string());
        _name = localTypeCodeInputStream.read_string();
        break;
      case 16: 
        setId(localTypeCodeInputStream.read_string());
        _name = localTypeCodeInputStream.read_string();
        _discriminator = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
        _discriminator.read_value_recursive(localTypeCodeInputStream);
        _defaultIndex = localTypeCodeInputStream.read_long();
        _memberCount = localTypeCodeInputStream.read_long();
        _unionLabels = new AnyImpl[_memberCount];
        _memberNames = new String[_memberCount];
        _memberTypes = new TypeCodeImpl[_memberCount];
        for (i = 0; i < _memberCount; i++)
        {
          _unionLabels[i] = new AnyImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
          if (i == _defaultIndex) {
            _unionLabels[i].insert_octet(localTypeCodeInputStream.read_octet());
          } else {
            switch (realType(_discriminator).kind().value())
            {
            case 2: 
              _unionLabels[i].insert_short(localTypeCodeInputStream.read_short());
              break;
            case 3: 
              _unionLabels[i].insert_long(localTypeCodeInputStream.read_long());
              break;
            case 4: 
              _unionLabels[i].insert_ushort(localTypeCodeInputStream.read_short());
              break;
            case 5: 
              _unionLabels[i].insert_ulong(localTypeCodeInputStream.read_long());
              break;
            case 6: 
              _unionLabels[i].insert_float(localTypeCodeInputStream.read_float());
              break;
            case 7: 
              _unionLabels[i].insert_double(localTypeCodeInputStream.read_double());
              break;
            case 8: 
              _unionLabels[i].insert_boolean(localTypeCodeInputStream.read_boolean());
              break;
            case 9: 
              _unionLabels[i].insert_char(localTypeCodeInputStream.read_char());
              break;
            case 17: 
              _unionLabels[i].type(_discriminator);
              _unionLabels[i].insert_long(localTypeCodeInputStream.read_long());
              break;
            case 23: 
              _unionLabels[i].insert_longlong(localTypeCodeInputStream.read_longlong());
              break;
            case 24: 
              _unionLabels[i].insert_ulonglong(localTypeCodeInputStream.read_longlong());
              break;
            case 26: 
              _unionLabels[i].insert_wchar(localTypeCodeInputStream.read_wchar());
              break;
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 25: 
            default: 
              throw wrapper.invalidComplexTypecode();
            }
          }
          _memberNames[i] = localTypeCodeInputStream.read_string();
          _memberTypes[i] = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
          _memberTypes[i].read_value_recursive(localTypeCodeInputStream);
          _memberTypes[i].setParent(this);
        }
        break;
      case 17: 
        setId(localTypeCodeInputStream.read_string());
        _name = localTypeCodeInputStream.read_string();
        _memberCount = localTypeCodeInputStream.read_long();
        _memberNames = new String[_memberCount];
        for (i = 0; i < _memberCount; i++) {
          _memberNames[i] = localTypeCodeInputStream.read_string();
        }
        break;
      case 19: 
        _contentType = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
        _contentType.read_value_recursive(localTypeCodeInputStream);
        _length = localTypeCodeInputStream.read_long();
        break;
      case 20: 
        _contentType = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
        _contentType.read_value_recursive(localTypeCodeInputStream);
        _length = localTypeCodeInputStream.read_long();
        break;
      case 21: 
      case 30: 
        setId(localTypeCodeInputStream.read_string());
        _name = localTypeCodeInputStream.read_string();
        _contentType = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
        _contentType.read_value_recursive(localTypeCodeInputStream);
        break;
      case 15: 
      case 22: 
        setId(localTypeCodeInputStream.read_string());
        _name = localTypeCodeInputStream.read_string();
        _memberCount = localTypeCodeInputStream.read_long();
        _memberNames = new String[_memberCount];
        _memberTypes = new TypeCodeImpl[_memberCount];
        for (i = 0; i < _memberCount; i++)
        {
          _memberNames[i] = localTypeCodeInputStream.read_string();
          _memberTypes[i] = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
          _memberTypes[i].read_value_recursive(localTypeCodeInputStream);
          _memberTypes[i].setParent(this);
        }
        break;
      case 29: 
        setId(localTypeCodeInputStream.read_string());
        _name = localTypeCodeInputStream.read_string();
        _type_modifier = localTypeCodeInputStream.read_short();
        _concrete_base = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
        _concrete_base.read_value_recursive(localTypeCodeInputStream);
        if (_concrete_base.kind().value() == 0) {
          _concrete_base = null;
        }
        _memberCount = localTypeCodeInputStream.read_long();
        _memberNames = new String[_memberCount];
        _memberTypes = new TypeCodeImpl[_memberCount];
        _memberAccess = new short[_memberCount];
        for (i = 0; i < _memberCount; i++)
        {
          _memberNames[i] = localTypeCodeInputStream.read_string();
          _memberTypes[i] = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
          _memberTypes[i].read_value_recursive(localTypeCodeInputStream);
          _memberTypes[i].setParent(this);
          _memberAccess[i] = localTypeCodeInputStream.read_short();
        }
        break;
      case 18: 
      case 23: 
      case 24: 
      case 25: 
      case 26: 
      case 27: 
      case 28: 
      case 31: 
      default: 
        throw wrapper.invalidTypecodeKindMarshal();
      }
      break;
    }
  }
  
  public void write_value(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream)
  {
    if ((paramOutputStream instanceof TypeCodeOutputStream))
    {
      write_value((TypeCodeOutputStream)paramOutputStream);
    }
    else
    {
      TypeCodeOutputStream localTypeCodeOutputStream = null;
      if (outBuffer == null)
      {
        localTypeCodeOutputStream = TypeCodeOutputStream.wrapOutputStream(paramOutputStream);
        write_value(localTypeCodeOutputStream);
        if (cachingEnabled) {
          outBuffer = localTypeCodeOutputStream.getTypeCodeBuffer();
        }
      }
      if ((cachingEnabled) && (outBuffer != null))
      {
        paramOutputStream.write_long(_kind);
        paramOutputStream.write_octet_array(outBuffer, 0, outBuffer.length);
      }
      else
      {
        localTypeCodeOutputStream.writeRawBuffer(paramOutputStream, _kind);
      }
    }
  }
  
  public void write_value(TypeCodeOutputStream paramTypeCodeOutputStream)
  {
    if (_kind == 31) {
      throw wrapper.cannotMarshalNative();
    }
    TypeCodeOutputStream localTypeCodeOutputStream1 = paramTypeCodeOutputStream.getTopLevelStream();
    int j;
    if (_kind == -1)
    {
      int i = localTypeCodeOutputStream1.getPositionForID(_id);
      j = paramTypeCodeOutputStream.getTopLevelPosition();
      paramTypeCodeOutputStream.writeIndirection(-1, i);
      return;
    }
    paramTypeCodeOutputStream.write_long(_kind);
    localTypeCodeOutputStream1.addIDAtPosition(_id, paramTypeCodeOutputStream.getTopLevelPosition() - 4);
    switch (typeTable[_kind])
    {
    case 0: 
      break;
    case 1: 
      switch (_kind)
      {
      case 18: 
      case 27: 
        paramTypeCodeOutputStream.write_long(_length);
        break;
      case 28: 
        paramTypeCodeOutputStream.write_ushort(_digits);
        paramTypeCodeOutputStream.write_short(_scale);
        break;
      default: 
        throw wrapper.invalidSimpleTypecode();
      }
      break;
    case 2: 
      TypeCodeOutputStream localTypeCodeOutputStream2 = paramTypeCodeOutputStream.createEncapsulation(paramTypeCodeOutputStream.orb());
      switch (_kind)
      {
      case 14: 
      case 32: 
        localTypeCodeOutputStream2.write_string(_id);
        localTypeCodeOutputStream2.write_string(_name);
        break;
      case 16: 
        localTypeCodeOutputStream2.write_string(_id);
        localTypeCodeOutputStream2.write_string(_name);
        _discriminator.write_value(localTypeCodeOutputStream2);
        localTypeCodeOutputStream2.write_long(_defaultIndex);
        localTypeCodeOutputStream2.write_long(_memberCount);
        for (j = 0; j < _memberCount; j++)
        {
          if (j == _defaultIndex) {
            localTypeCodeOutputStream2.write_octet(_unionLabels[j].extract_octet());
          } else {
            switch (realType(_discriminator).kind().value())
            {
            case 2: 
              localTypeCodeOutputStream2.write_short(_unionLabels[j].extract_short());
              break;
            case 3: 
              localTypeCodeOutputStream2.write_long(_unionLabels[j].extract_long());
              break;
            case 4: 
              localTypeCodeOutputStream2.write_short(_unionLabels[j].extract_ushort());
              break;
            case 5: 
              localTypeCodeOutputStream2.write_long(_unionLabels[j].extract_ulong());
              break;
            case 6: 
              localTypeCodeOutputStream2.write_float(_unionLabels[j].extract_float());
              break;
            case 7: 
              localTypeCodeOutputStream2.write_double(_unionLabels[j].extract_double());
              break;
            case 8: 
              localTypeCodeOutputStream2.write_boolean(_unionLabels[j].extract_boolean());
              break;
            case 9: 
              localTypeCodeOutputStream2.write_char(_unionLabels[j].extract_char());
              break;
            case 17: 
              localTypeCodeOutputStream2.write_long(_unionLabels[j].extract_long());
              break;
            case 23: 
              localTypeCodeOutputStream2.write_longlong(_unionLabels[j].extract_longlong());
              break;
            case 24: 
              localTypeCodeOutputStream2.write_longlong(_unionLabels[j].extract_ulonglong());
              break;
            case 26: 
              localTypeCodeOutputStream2.write_wchar(_unionLabels[j].extract_wchar());
              break;
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 25: 
            default: 
              throw wrapper.invalidComplexTypecode();
            }
          }
          localTypeCodeOutputStream2.write_string(_memberNames[j]);
          _memberTypes[j].write_value(localTypeCodeOutputStream2);
        }
        break;
      case 17: 
        localTypeCodeOutputStream2.write_string(_id);
        localTypeCodeOutputStream2.write_string(_name);
        localTypeCodeOutputStream2.write_long(_memberCount);
        for (j = 0; j < _memberCount; j++) {
          localTypeCodeOutputStream2.write_string(_memberNames[j]);
        }
        break;
      case 19: 
        lazy_content_type().write_value(localTypeCodeOutputStream2);
        localTypeCodeOutputStream2.write_long(_length);
        break;
      case 20: 
        _contentType.write_value(localTypeCodeOutputStream2);
        localTypeCodeOutputStream2.write_long(_length);
        break;
      case 21: 
      case 30: 
        localTypeCodeOutputStream2.write_string(_id);
        localTypeCodeOutputStream2.write_string(_name);
        _contentType.write_value(localTypeCodeOutputStream2);
        break;
      case 15: 
      case 22: 
        localTypeCodeOutputStream2.write_string(_id);
        localTypeCodeOutputStream2.write_string(_name);
        localTypeCodeOutputStream2.write_long(_memberCount);
        for (j = 0; j < _memberCount; j++)
        {
          localTypeCodeOutputStream2.write_string(_memberNames[j]);
          _memberTypes[j].write_value(localTypeCodeOutputStream2);
        }
        break;
      case 29: 
        localTypeCodeOutputStream2.write_string(_id);
        localTypeCodeOutputStream2.write_string(_name);
        localTypeCodeOutputStream2.write_short(_type_modifier);
        if (_concrete_base == null) {
          _orb.get_primitive_tc(0).write_value(localTypeCodeOutputStream2);
        } else {
          _concrete_base.write_value(localTypeCodeOutputStream2);
        }
        localTypeCodeOutputStream2.write_long(_memberCount);
        for (j = 0; j < _memberCount; j++)
        {
          localTypeCodeOutputStream2.write_string(_memberNames[j]);
          _memberTypes[j].write_value(localTypeCodeOutputStream2);
          localTypeCodeOutputStream2.write_short(_memberAccess[j]);
        }
        break;
      case 18: 
      case 23: 
      case 24: 
      case 25: 
      case 26: 
      case 27: 
      case 28: 
      case 31: 
      default: 
        throw wrapper.invalidTypecodeKindMarshal();
      }
      localTypeCodeOutputStream2.writeOctetSequenceTo(paramTypeCodeOutputStream);
      break;
    }
  }
  
  protected void copy(org.omg.CORBA.portable.InputStream paramInputStream, org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    Object localObject;
    int i2;
    switch (_kind)
    {
    case 0: 
    case 1: 
    case 31: 
    case 32: 
      break;
    case 2: 
    case 4: 
      paramOutputStream.write_short(paramInputStream.read_short());
      break;
    case 3: 
    case 5: 
      paramOutputStream.write_long(paramInputStream.read_long());
      break;
    case 6: 
      paramOutputStream.write_float(paramInputStream.read_float());
      break;
    case 7: 
      paramOutputStream.write_double(paramInputStream.read_double());
      break;
    case 23: 
    case 24: 
      paramOutputStream.write_longlong(paramInputStream.read_longlong());
      break;
    case 25: 
      throw wrapper.tkLongDoubleNotSupported();
    case 8: 
      paramOutputStream.write_boolean(paramInputStream.read_boolean());
      break;
    case 9: 
      paramOutputStream.write_char(paramInputStream.read_char());
      break;
    case 26: 
      paramOutputStream.write_wchar(paramInputStream.read_wchar());
      break;
    case 10: 
      paramOutputStream.write_octet(paramInputStream.read_octet());
      break;
    case 18: 
      localObject = paramInputStream.read_string();
      if ((_length != 0) && (((String)localObject).length() > _length)) {
        throw wrapper.badStringBounds(new Integer(((String)localObject).length()), new Integer(_length));
      }
      paramOutputStream.write_string((String)localObject);
      break;
    case 27: 
      localObject = paramInputStream.read_wstring();
      if ((_length != 0) && (((String)localObject).length() > _length)) {
        throw wrapper.badStringBounds(new Integer(((String)localObject).length()), new Integer(_length));
      }
      paramOutputStream.write_wstring((String)localObject);
      break;
    case 28: 
      paramOutputStream.write_ushort(paramInputStream.read_ushort());
      paramOutputStream.write_short(paramInputStream.read_short());
      break;
    case 11: 
      localObject = ((CDRInputStream)paramInputStream).orb().create_any();
      TypeCodeImpl localTypeCodeImpl = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramOutputStream.orb());
      localTypeCodeImpl.read_value((org.omg.CORBA_2_3.portable.InputStream)paramInputStream);
      localTypeCodeImpl.write_value((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream);
      ((Any)localObject).read_value(paramInputStream, localTypeCodeImpl);
      ((Any)localObject).write_value(paramOutputStream);
      break;
    case 12: 
      paramOutputStream.write_TypeCode(paramInputStream.read_TypeCode());
      break;
    case 13: 
      paramOutputStream.write_Principal(paramInputStream.read_Principal());
      break;
    case 14: 
      paramOutputStream.write_Object(paramInputStream.read_Object());
      break;
    case 22: 
      paramOutputStream.write_string(paramInputStream.read_string());
    case 15: 
    case 29: 
      for (int i = 0; i < _memberTypes.length; i++) {
        _memberTypes[i].copy(paramInputStream, paramOutputStream);
      }
      break;
    case 16: 
      AnyImpl localAnyImpl = new AnyImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
      int k;
      long l;
      switch (realType(_discriminator).kind().value())
      {
      case 2: 
        short s = paramInputStream.read_short();
        localAnyImpl.insert_short(s);
        paramOutputStream.write_short(s);
        break;
      case 3: 
        k = paramInputStream.read_long();
        localAnyImpl.insert_long(k);
        paramOutputStream.write_long(k);
        break;
      case 4: 
        k = paramInputStream.read_short();
        localAnyImpl.insert_ushort(k);
        paramOutputStream.write_short(k);
        break;
      case 5: 
        int m = paramInputStream.read_long();
        localAnyImpl.insert_ulong(m);
        paramOutputStream.write_long(m);
        break;
      case 6: 
        float f = paramInputStream.read_float();
        localAnyImpl.insert_float(f);
        paramOutputStream.write_float(f);
        break;
      case 7: 
        double d = paramInputStream.read_double();
        localAnyImpl.insert_double(d);
        paramOutputStream.write_double(d);
        break;
      case 8: 
        boolean bool = paramInputStream.read_boolean();
        localAnyImpl.insert_boolean(bool);
        paramOutputStream.write_boolean(bool);
        break;
      case 9: 
        char c = paramInputStream.read_char();
        localAnyImpl.insert_char(c);
        paramOutputStream.write_char(c);
        break;
      case 17: 
        int n = paramInputStream.read_long();
        localAnyImpl.type(_discriminator);
        localAnyImpl.insert_long(n);
        paramOutputStream.write_long(n);
        break;
      case 23: 
        l = paramInputStream.read_longlong();
        localAnyImpl.insert_longlong(l);
        paramOutputStream.write_longlong(l);
        break;
      case 24: 
        l = paramInputStream.read_longlong();
        localAnyImpl.insert_ulonglong(l);
        paramOutputStream.write_longlong(l);
        break;
      case 26: 
        i1 = paramInputStream.read_wchar();
        localAnyImpl.insert_wchar(i1);
        paramOutputStream.write_wchar(i1);
        break;
      case 10: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      case 16: 
      case 18: 
      case 19: 
      case 20: 
      case 21: 
      case 22: 
      case 25: 
      default: 
        throw wrapper.illegalUnionDiscriminatorType();
      }
      for (int i1 = 0; i1 < _unionLabels.length; i1++) {
        if (localAnyImpl.equal(_unionLabels[i1]))
        {
          _memberTypes[i1].copy(paramInputStream, paramOutputStream);
          break;
        }
      }
      if ((i1 == _unionLabels.length) && (_defaultIndex != -1)) {
        _memberTypes[_defaultIndex].copy(paramInputStream, paramOutputStream);
      }
      break;
    case 17: 
      paramOutputStream.write_long(paramInputStream.read_long());
      break;
    case 19: 
      int j = paramInputStream.read_long();
      if ((_length != 0) && (j > _length)) {
        throw wrapper.badSequenceBounds(new Integer(j), new Integer(_length));
      }
      paramOutputStream.write_long(j);
      lazy_content_type();
      for (i2 = 0; i2 < j; i2++) {
        _contentType.copy(paramInputStream, paramOutputStream);
      }
      break;
    case 20: 
      for (i2 = 0; i2 < _length; i2++) {
        _contentType.copy(paramInputStream, paramOutputStream);
      }
      break;
    case 21: 
    case 30: 
      _contentType.copy(paramInputStream, paramOutputStream);
      break;
    case -1: 
      indirectType().copy(paramInputStream, paramOutputStream);
      break;
    default: 
      throw wrapper.invalidTypecodeKindMarshal();
    }
  }
  
  protected static short digits(BigDecimal paramBigDecimal)
  {
    if (paramBigDecimal == null) {
      return 0;
    }
    short s = (short)paramBigDecimal.unscaledValue().toString().length();
    if (paramBigDecimal.signum() == -1) {
      s = (short)(s - 1);
    }
    return s;
  }
  
  protected static short scale(BigDecimal paramBigDecimal)
  {
    if (paramBigDecimal == null) {
      return 0;
    }
    return (short)paramBigDecimal.scale();
  }
  
  int currentUnionMemberIndex(Any paramAny)
    throws BadKind
  {
    if (_kind != 16) {
      throw new BadKind();
    }
    try
    {
      for (int i = 0; i < member_count(); i++) {
        if (member_label(i).equal(paramAny)) {
          return i;
        }
      }
      if (_defaultIndex != -1) {
        return _defaultIndex;
      }
    }
    catch (BadKind localBadKind) {}catch (Bounds localBounds) {}
    return -1;
  }
  
  public String description()
  {
    return "TypeCodeImpl with kind " + _kind + " and id " + _id;
  }
  
  public String toString()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1024);
    PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream, true);
    printStream(localPrintStream);
    return super.toString() + " =\n" + localByteArrayOutputStream.toString();
  }
  
  public void printStream(PrintStream paramPrintStream)
  {
    printStream(paramPrintStream, 0);
  }
  
  private void printStream(PrintStream paramPrintStream, int paramInt)
  {
    if (_kind == -1)
    {
      paramPrintStream.print("indirect " + _id);
      return;
    }
    switch (_kind)
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 23: 
    case 24: 
    case 25: 
    case 26: 
    case 31: 
      paramPrintStream.print(kindNames[_kind] + " " + _name);
      break;
    case 15: 
    case 22: 
    case 29: 
      paramPrintStream.println(kindNames[_kind] + " " + _name + " = {");
      for (int i = 0; i < _memberCount; i++)
      {
        paramPrintStream.print(indent(paramInt + 1));
        if (_memberTypes[i] != null) {
          _memberTypes[i].printStream(paramPrintStream, paramInt + 1);
        } else {
          paramPrintStream.print("<unknown type>");
        }
        paramPrintStream.println(" " + _memberNames[i] + ";");
      }
      paramPrintStream.print(indent(paramInt) + "}");
      break;
    case 16: 
      paramPrintStream.print("union " + _name + "...");
      break;
    case 17: 
      paramPrintStream.print("enum " + _name + "...");
      break;
    case 18: 
      if (_length == 0) {
        paramPrintStream.print("unbounded string " + _name);
      } else {
        paramPrintStream.print("bounded string(" + _length + ") " + _name);
      }
      break;
    case 19: 
    case 20: 
      paramPrintStream.println(kindNames[_kind] + "[" + _length + "] " + _name + " = {");
      paramPrintStream.print(indent(paramInt + 1));
      if (lazy_content_type() != null) {
        lazy_content_type().printStream(paramPrintStream, paramInt + 1);
      }
      paramPrintStream.println(indent(paramInt) + "}");
      break;
    case 21: 
      paramPrintStream.print("alias " + _name + " = " + (_contentType != null ? _contentType._name : "<unresolved>"));
      break;
    case 27: 
      paramPrintStream.print("wstring[" + _length + "] " + _name);
      break;
    case 28: 
      paramPrintStream.print("fixed(" + _digits + ", " + _scale + ") " + _name);
      break;
    case 30: 
      paramPrintStream.print("valueBox " + _name + "...");
      break;
    case 32: 
      paramPrintStream.print("abstractInterface " + _name + "...");
      break;
    default: 
      paramPrintStream.print("<unknown type>");
    }
  }
  
  private String indent(int paramInt)
  {
    String str = "";
    for (int i = 0; i < paramInt; i++) {
      str = str + "  ";
    }
    return str;
  }
  
  protected void setCaching(boolean paramBoolean)
  {
    cachingEnabled = paramBoolean;
    if (!paramBoolean) {
      outBuffer = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\TypeCodeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */