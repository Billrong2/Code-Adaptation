@SuppressWarnings({"unchecked", "rawtypes"})
	DroidLED() throws Exception {
		try {
			// Reflectively obtain the hardware service binder via ServiceManager
			final Class smClass = Class.forName("android.os.ServiceManager");
			final Object hwBinder = smClass.getMethod("getService", String.class)
					.invoke(null, "hardware");
			if (hwBinder == null) {
				throw new Exception("Hardware service binder was null");
			}

			// Resolve the IHardwareService stub and obtain the proxy interface
			final Class hwsStubClass = Class.forName("android.os.IHardwareService$Stub");
			final Method asInterfaceMethod = hwsStubClass.getMethod(
					"asInterface", android.os.IBinder.class);
			this.svc = asInterfaceMethod.invoke(null, (IBinder) hwBinder);

			// Cache proxy methods for later invocation
			final Class proxyClass = this.svc.getClass();
			this.getFlashlightEnabled = proxyClass.getMethod("getFlashlightEnabled");
			this.setFlashlightEnabled = proxyClass.getMethod(
					"setFlashlightEnabled", boolean.class);
		} catch (Exception e) {
			// Preserve the original cause while providing a clear, high-level message
			throw new Exception("LED could not be initialized", e);
		}
	}