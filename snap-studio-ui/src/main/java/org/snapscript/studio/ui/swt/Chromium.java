package org.snapscript.studio.ui.swt;


import java.awt.Window;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.annotations.Direct;
import jnr.ffi.provider.ClosureManager;
import jnr.ffi.provider.jffi.NativeRuntime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.snapscript.studio.ui.cef.ChromiumBridge;
import org.snapscript.studio.ui.cef.ChromiumBridge.App;
import org.snapscript.studio.ui.cef.ChromiumBridge.Browser;
import org.snapscript.studio.ui.cef.ChromiumBridge.BrowserProcessHandler;
import org.snapscript.studio.ui.cef.ChromiumBridge.Client;
import org.snapscript.studio.ui.swt.internal.NativeExpander;

public class Chromium extends Composite {

	private static String OS = System.getProperty("os.name").toLowerCase();
    
	private static Lib lib;
	private static String cefrustPath;
	private static AtomicInteger browsers = new AtomicInteger(0);
	private static CompletableFuture<Boolean> cefInitilized;
	private static App app;
	private static BrowserProcessHandler browserProcessHandler;

	private long hwnd;
	private Pointer browser;
	private ChromiumBridge.Client clientHandler;
	private ChromiumBridge.FocusHandler focusHandler;
	private ChromiumBridge.LifeSpanHandler lifeSpanHandler;
	private FocusListener focusListener;
	private String url;
//private ScheduledFuture<?> pumpTask;

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

	static {
		lib = loadLib();
	}

	//static final String NO_INPUT_METHOD = "org.eclipse.swt.internal.gtk.noInputMethod"; //$NON-NLS-1$
	
	/*static Composite checkParent(Composite parent) {
		if (parent != null && !parent.isDisposed ()) {
			Display display = parent.getDisplay ();
			if (display != null) {
				if (display.getThread () == Thread.currentThread ()) {
					display.setData (NO_INPUT_METHOD, "true"); //$NON-NLS-1$
				}
			}
		}
		return parent;
	}*/
	
	public Chromium(Composite parent, int style) {
//		super(/*checkParent(*/parent/*)*/, SWT.NONE);
		super(parent, SWT.NO_BACKGROUND | SWT. NO_FOCUS | SWT.NO_MERGE_PAINTS | SWT.NO_REDRAW_RESIZE);
//		super(checkParent(parent), SWT.EMBEDDED);
		
		
		parent.addListener(SWT.Close, new Listener() {
	      public void handleEvent(Event event) {
	           DEBUG_CALLBACK("CLOSE EVENT shutdown");
	         shutdown();
	         DEBUG_CALLBACK("Closing window");
	         System.exit(0);
	      }
	    });
		
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//		   @Override
//		   public void run(){
//            DEBUG_CALLBACK("SHUTDOWN HOOK");
//            shutdown();
//            DEBUG_CALLBACK("Terminating application");
//		   }
//		});
		
//		parent.getDisplay ().setData (NO_INPUT_METHOD, null);
		// Field field;
		// try {
		// field = Widget.class.getDeclaredField("handle");
		// Object handleObj = field.get(this);
		// if (handleObj instanceof Long)
		// hwnd = (Long) handleObj;
		// else
		// hwnd = (Integer) handleObj;
		// } catch (NoSuchFieldException | SecurityException |
		// IllegalArgumentException | IllegalAccessException e) {
		// throw new RuntimeException(e);
		// }

		// Runtime runtime = jnr.ffi.Runtime.getRuntime(lib);
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				DEBUG_CALLBACK("paintControl");
				removePaintListener(this);
//				getDisplay().timerExec(2000, () -> {
					initCEF(getDisplay());
					DEBUG_CALLBACK("initCef Done");
					cefInitilized.thenRun(() -> { 
						DEBUG_CALLBACK("cefInitilized Future CALLBACK");
						getDisplay().asyncExec(() -> createBrowser());
					});
					DEBUG_CALLBACK("paintControl Done");
//				});
			}
		});
//				initCEF(getDisplay());
//				cefInitilized.thenRun(() -> createBrowser());
	}
//	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private void initCEF(Display display) {
		synchronized (lib) {
			if (app == null) {
				app = new ChromiumBridge.App(ChromiumBridge.RUNTIME);
				browserProcessHandler = new ChromiumBridge.BrowserProcessHandler(ChromiumBridge.RUNTIME);
				cefInitilized = new CompletableFuture<>();
				browserProcessHandler.setOnContextInitialized(browserProcessHandler -> {
					DEBUG_CALLBACK("OnContextInitialized");
					
					cefInitilized.complete(true);
				});
				Runnable runnable = () -> { 
					if (display.isDisposed() || isDisposed() || display.getActiveShell() != getShell()) {
						//System.err.println("Ignore do_message_loop_work due inactive shell");
						return;
					}
					if (browsers.get() > 0) {
						lib.cefswt_do_message_loop_work();
					}
				};
				browserProcessHandler.setOnScheduleMessagePumpWork((pbrowserProcessHandler, delay) -> {
//					synchronized (browserProcessHandler) {
//					DEBUG_CALLBACK("OnScheduleMessagePumpWork " + delay);
					if (display.isDisposed())
						return;
					if (delay <= 0) {
//						if (pumpTask != null) {
//							if (pumpTask.cancel(false) )
//								DEBUG_CALLBACK("Canceled");
//						}
						display.asyncExec(runnable);
					} else {
						display.timerExec((int)delay, runnable);
//						if (pumpTask != null) {
//							if (pumpTask.cancel(false) )
//								DEBUG_CALLBACK("Canceled");
//						}
//						pumpTask = executor.schedule(() -> {
//							DEBUG_CALLBACK("SCHEDULED DO_WORK");
//							if (browsers > 0) display.asyncExec(() -> lib.do_message_loop_work());
//						}, delay, TimeUnit.MILLISECONDS);
					}
//					}
				});
				
				app.setGetBrowserProcessHandler(appPtr -> {
					DEBUG_CALLBACK("GetBrowserProcessHandler");
					return browserProcessHandler;
				});
				System.out.println("cefrust.path: " + cefrustPath);
				//DEBUG_CALLBACK("INIT FROM thread " + Thread.currentThread().getName());
				lib.cefswt_init(app, cefrustPath);
			}
			//browsers++;
		}
	}

	private long getHandle(Composite control) {
		long hwnd = 0;
		if (isMac()) {
			try {
				Field field = Control.class.getDeclaredField("view");
				field.setAccessible(true);
				Object nsview = field.get(control);
				
				Class<?> idClass = Class.forName("org.eclipse.swt.internal.cocoa.id");
				Field idField = idClass.getField("id");

				hwnd = idField.getLong(nsview);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (isWindows()) {
			try {
				Field field = Control.class.getDeclaredField("handle");
				field.setAccessible(true);
				hwnd = (long) field.get(control);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Field field = Widget.class.getDeclaredField("handle");
				field.setAccessible(true);
				hwnd = (long) field.get(control);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return hwnd;
	}
	
	private void createBrowser() {
		hwnd = getHandle(this);
//		hwnd = embeddedHandle;
		DEBUG_CALLBACK("HWND1: " + hwnd);
		// String url = "http://www.lanacion.com.ar";
//		String url = "http://www.google.com";
		String url = (this.url != null) ? this.url : "about:blank";
		// String url = "http://www.keyboardtester.com/tester.html";

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				DEBUG_CALLBACK("disposing chromium");
				dispose();
			}
		});
		focusListener = new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				removeFocusListener(focusListener);
				//DEBUG_CALLBACK("focusLost");
				browserFocus(false);
				// System.out.println(Display.getDefault().getFocusControl());
				addFocusListener(focusListener);
			}

			@Override
			public void focusGained(FocusEvent e) {
				//DEBUG_CALLBACK("focusGained");
				browserFocus(true);
			}
		};
		addFocusListener(focusListener);

		clientHandler = new ChromiumBridge.Client(ChromiumBridge.RUNTIME);
		initializeClientHandler(clientHandler);
		focusHandler = new ChromiumBridge.FocusHandler(ChromiumBridge.RUNTIME);
		focusHandler.setOnGotFocus((focusHandler, browser_1) -> {
			//DEBUG_CALLBACK("CALLBACK OnGotFocus");
			if (!isFocusControl()) {
				removeFocusListener(focusListener);
				boolean r = forceFocus();
				browserFocus(true);
				addFocusListener(focusListener);
				DEBUG_CALLBACK("Forcing focus to SWT canvas: " + r);
			}
		});
		focusHandler.setOnSetFocus((focusHandler, browser_1, focusSource_2) -> {
			//DEBUG_CALLBACK("CALLBACK OnSetFocus " + focusSource_2);
			if (!isFocusControl()) {
				DEBUG_CALLBACK("Disallowing focus to SWT canvas");
				removeFocusListener(focusListener);
				setFocus();
				addFocusListener(focusListener);
				return 1;
			}
			//System.out.println("Allowing focus to SWT canvas");
			return 0;
		});
		focusHandler.setOnTakeFocus((focusHandler, browser_1, next) -> {
			//DEBUG_CALLBACK("CALLBACK OnTakeFocus " + next);
			Control[] tabOrder = getParent().getTabList();
			if (tabOrder.length == 0)
				tabOrder = getParent().getChildren();
			int indexOf = Arrays.asList(tabOrder).indexOf(Chromium.this);
			if (indexOf != -1) {
				int newIndex = (next == 1) ? indexOf + 1 : indexOf - 1;
				if (newIndex > 0 && newIndex < tabOrder.length && !tabOrder[newIndex].isDisposed()) {
					tabOrder[newIndex].setFocus();
					return;
				}
			}
			if (!getParent().isDisposed()) {
				getParent().setFocus();
			}
		});
		clientHandler.setGetFocusHandler(client -> {
			//DEBUG_CALLBACK("GetFocusHandler");
			return focusHandler;
		});
		
		lifeSpanHandler = new ChromiumBridge.LifeSpanHandler(ChromiumBridge.RUNTIME);
		lifeSpanHandler.setOnBeforeClose((plifeSpanHandler, browser) -> {
			//lifeSpanHandler.base.ref++;
			DEBUG_CALLBACK("OnBeforeClose");
			Browser bs = new ChromiumBridge.Browser(ChromiumBridge.RUNTIME);
			bs.useMemory(browser);
			lib.cefswt_free(bs);
			Chromium.this.clientHandler = null;
			Chromium.this.browser = null;
			Chromium.this.focusHandler = null;
			Chromium.this.lifeSpanHandler = null;
		});
		lifeSpanHandler.setDoClose((plifeSpanHandler, browser) -> {
			//lifeSpanHandler.base.ref++;
			DEBUG_CALLBACK("DoClose");
			// do not send close notification to top level window
			return 0;
		});
		clientHandler.setGetLifeSpanHandler(client -> {
			//DEBUG_CALLBACK("GetLifeSpanHandler");
			return lifeSpanHandler;
		});

		addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				if (!isDisposed() && browser != null) {
					if (getDisplay().getActiveShell() != getShell()) {
//						System.err.println("Ignore do_message_loop_work due inactive shell");
						return;
					}
					lib.cefswt_resized(browser, getSize().x, getSize().y);
//					lib.do_message_loop_work();
				}
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
		
		browser = lib.cefswt_create_browser(hwnd, url, clientHandler);
		if (browser != null) {
			browsers.incrementAndGet();
			lib.cefswt_resized(browser, getSize().x, getSize().y);
//			lib.do_message_loop_work();
		}

		final Display display = this.getDisplay();
//		lib.do_message_loop_work();
		if (browsers.get() == 1) {
			DEBUG_CALLBACK("STARTING MSG LOOP");
			doMessageLoop(display);
		}
	}

	public void setUrl(String url) {
		if (!isDisposed() && browser != null) {
			DEBUG_CALLBACK("setUrl: " + url);
			lib.cefswt_load_url(browser, url);
		}
		this.url = url;
	}

	// single loop for all browsers
	private static void doMessageLoop(final Display display) {
		final int loop = 5;
		display.timerExec(loop, new Runnable() {
			public void run() {
				if (lib != null && browsers.get() > 0) {
					 //System.out.println("loop");
					lib.cefswt_do_message_loop_work();
					display.timerExec(loop, this);
				} else {
					DEBUG_CALLBACK("STOPPING MSG LOOP");
				}
			}
		});
	}

	private static jnr.ffi.Pointer DEBUG_CALLBACK(String log) {
		System.out.println("J:" + log);
		return null;
	}

	protected static void initializeClientHandler(Client client) {
		// callbacks
		client.setGetContextMenuHandler((c) -> DEBUG_CALLBACK("get_context_menu_handler"));
		client.setGetDialogHandler((c) -> DEBUG_CALLBACK("get_dialog_handler"));
		client.setGetDisplayHandler((c) -> null);
		client.setGetDownloadHandler((c) -> DEBUG_CALLBACK("get_download_handler"));
		client.setGetDragHandler((c) -> DEBUG_CALLBACK("get_drag_handler"));
		client.setGetFocusHandler((c) -> null);
		client.setGetGeolocationHandler((c) -> DEBUG_CALLBACK("get_geolocation_handler"));
		client.setGetJsdialogHandler((c) -> DEBUG_CALLBACK("get_jsdialog_handler"));
		client.setGetKeyboardHandler((c) -> null);
		client.setGetLifeSpanHandler((c) -> null);
		client.setGetLoadHandler((c) -> null);
		client.setGetRenderHandler((c) -> null);
		client.setGetRequestHandler((c) -> null);
		client.setOnProcessMessageReceived((c, browser_1, processId_2, processMessage_3) -> {
			DEBUG_CALLBACK("on_process_message_received");
			return 0;
		});
		client.setGetFindHandler(c -> DEBUG_CALLBACK("setGetFindHandler"));
	}

	// @Override
	// public boolean forceFocus() {
	// System.out.println("focus");
	// boolean forceFocus = super.forceFocus();
	// if (forceFocus) {
	// browserFocus(true);
	// }
	// return forceFocus;
	// }
	//
	// @Override
	// public boolean setFocus() {
	// // TODO Auto-generated method stub
	// return super.setFocus();
	// }

	protected void browserFocus(boolean set) {
//		DEBUG_CALLBACK("cef focus: " + set);
		if (!isDisposed() && browser != null) {
			long parent = (Display.getDefault().getActiveShell() == null) ? 0 : getHandle(getParent());
			if (getDisplay().getActiveShell() != getShell()) {
//				System.err.println("Ignore do_message_loop_work due inactive shell");
				return;
			}
			lib.cefswt_set_focus(browser, set, parent);
		}
	}

	@Override
	public void dispose() {
		if (focusListener != null)
			removeFocusListener(focusListener);
		focusListener = null;
		if (browser != null) {
			browsers.decrementAndGet();
			DEBUG_CALLBACK("call close_browser");
			lib.cefswt_close_browser(browser);
		}
		super.dispose();
	}

	/**
	 * Re-initializing CEF3 is not supported due to the use of globals. This must be called on app exit. 
	 */
	private void shutdown() {
		DEBUG_CALLBACK("shutting down CEF on exit from thread " + Thread.currentThread().getName());
		lib.cefswt_shutdown();
		//MemoryIO.getInstance().freeMemory(Struct.getMemory(app).address());
		app = null;
		DEBUG_CALLBACK("after shutting down CEF");
	}

	private static Lib loadLib() {
		fixJNRClosureClassLoader();

		cefrustPath = System.getProperty("cefswt.path", "");
		if (cefrustPath.trim().isEmpty()) {
		   File installPath = new File(System.getProperty("user.home"), ".snap");
			cefrustPath = NativeExpander.expand(installPath.getAbsolutePath());
			System.setProperty("cefswt.path", cefrustPath);
		}

		//System.out.println("LOADCEF: " + cefrustPath + "/" + "libcef.so");
		//System.setProperty("java.library.path", cefrustPath + File.pathSeparator + System.getProperty("java.library.path", ""));
		//System.out.println("JAVA_LIBRARY_PATH: " + System.getProperty("java.library.path", ""));
		
		LibraryLoader<Lib> loader = LibraryLoader.create(Lib.class);
		if (isUnix() && !isMac())
			loader.library("cef");
		Lib libc = loader
			.failImmediately()
			.search(cefrustPath)
			.load("cefrustlib");
		return libc;
	}

	private static void fixJNRClosureClassLoader() {
		try {
			ClosureManager closureManager = NativeRuntime.getInstance().getClosureManager();
			Field classLoader = findField(closureManager.getClass(), "classLoader");
			classLoader.setAccessible(true);
			Object asmClassLoader = classLoader.get(closureManager);

			Field parent = findField(ClassLoader.class, "parent");
			parent.setAccessible(true);
			parent.set(asmClassLoader, Chromium.class.getClassLoader());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Field findField(Class<?> class1, String name) throws Exception {
		return class1.getDeclaredField(name);
	}

	public static interface Lib {
		void cefswt_init(@Direct ChromiumBridge.App app, String cefrustPath);

		Pointer cefswt_create_browser(long hwnd, String url, @Direct ChromiumBridge.Client clientHandler);

		void cefswt_do_message_loop_work();

		void cefswt_load_url(Pointer browser, String url);

		void cefswt_resized(Pointer browser, int width, int height);

		void cefswt_set_focus(Pointer browser, boolean focus, long shell_hwnd);

		void cefswt_close_browser(Pointer browser);

		void cefswt_shutdown();
		
		void cefswt_free(@Direct ChromiumBridge.Browser bs);
	}
}
