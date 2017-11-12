package org.snapscript.studio.ui.jnr.ffi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jnr.ffi.Pointer;
import jnr.ffi.Struct;
import jnr.ffi.mapper.ToNativeContext;
import jnr.ffi.mapper.ToNativeConverter;

public class InnerStructToNativeConverter implements ToNativeConverter<InnerStruct, Pointer> {

	private static final Field STRUCT_INFO;
	private static final Field INFO_ENCLOSING;
	private static final Method GET_OFFSET;
	
	static {
		try{
			STRUCT_INFO = Class.forName("jnr.ffi.Struct").getDeclaredField("__info");
			INFO_ENCLOSING = Class.forName("jnr.ffi.Struct$Info").getDeclaredField("enclosing");
			GET_OFFSET = Class.forName("jnr.ffi.Struct$Info").getDeclaredMethod("getOffset");
		}catch(Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
    public InnerStructToNativeConverter() {
    }

	public Class<Pointer> nativeType() {
        return Pointer.class;
    }

    public Pointer toNative(InnerStruct value, ToNativeContext ctx) {
    	if (value == null)
    		return null;
    	try {
	        Pointer memory = Struct.getMemory(value, 0);
	        Object _info = STRUCT_INFO.get(value);
	        Struct enclosing = (Struct)INFO_ENCLOSING.get(_info);
	        
	        if (enclosing != null) {
				int offset = (Integer)GET_OFFSET.invoke(_info);
	        	Pointer innerMemory = memory.slice(offset);
	        	return innerMemory;
	        }
			return memory;
    	} catch(Throwable e) {
    		throw new IllegalStateException("Internal error", e);
    	}
    }

}
