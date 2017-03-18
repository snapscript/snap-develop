
package org.snapscript.develop.common;

import java.io.File;

public interface FileAction<T> {
   T execute(String reference, File file) throws Exception;
}
