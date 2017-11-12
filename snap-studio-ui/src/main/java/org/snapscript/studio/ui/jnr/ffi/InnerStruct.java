package org.snapscript.studio.ui.jnr.ffi;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public abstract class InnerStruct extends Struct {

	public InnerStruct(Runtime runtime) {
		super(runtime);
	}
	
	protected InnerStruct(Runtime runtime, Struct enclosing) {
		super(runtime, enclosing);
	}

}
