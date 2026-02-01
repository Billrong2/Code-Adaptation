protected void initDevices() {
	// initialize shared transmitter list
	allTransmitters = new ArrayList<Transmitter>();
	
	javax.sound.midi.MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
	for (int i = 0; i < infos.length; i++) {
		try {
			MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
			
			// open device before accessing transmitters
			device.open();
			System.out.println(device.getDeviceInfo() + " was opened");
			
			// configure receivers only on existing transmitters
			List<Transmitter> transmitters = device.getTransmitters();
			if (transmitters != null && transmitters.size() > 0) {
				for (int j = 0; j < transmitters.size(); j++) {
					transmitters.get(j).setReceiver(
						new MidiInputReceiver(device.getDeviceInfo().toString())
					);
				}
			}
			
			// store default transmitter for later management (no receiver assigned here)
			Transmitter defaultTransmitter = device.getTransmitter();
			allTransmitters.add(defaultTransmitter);
			
		} catch (MidiUnavailableException e) {
			// silent handling, intentionally ignored
		}
	}
}