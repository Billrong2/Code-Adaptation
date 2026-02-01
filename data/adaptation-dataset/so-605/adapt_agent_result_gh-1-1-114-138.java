@Override
public void onReceive(final android.content.Context context, final android.content.Intent intent) {
	final android.bluetooth.BluetoothDevice device = intent != null ? intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE) : null;
	if (device == null)
		return;

	final android.bluetooth.BluetoothGatt gatt = mBluetoothGatt;
	if (gatt == null)
		return;

	final android.bluetooth.BluetoothDevice gattDevice = gatt.getDevice();
	if (gattDevice == null)
		return;

	final java.lang.String address = device.getAddress();
	if (address == null || !address.equals(gattDevice.getAddress()))
		return;

	final int bondState = intent.getIntExtra(android.bluetooth.BluetoothDevice.EXTRA_BOND_STATE, android.bluetooth.BluetoothDevice.BOND_NONE);

	final java.lang.String deviceName = device.getName() != null ? device.getName() : "unknown";
	no.nordicsemi.android.nrftoolbox.utility.DebugLogger.i(TAG, "Bond state changed for device: " + deviceName + ", state=" + bondState);

	switch (bondState) {
		case android.bluetooth.BluetoothDevice.BOND_BONDING:
			// Notify that bonding is required
			mCallbacks.onBondingRequired(device);
			break;
		case android.bluetooth.BluetoothDevice.BOND_BONDED:
			// Notify bonded and restart initialization by discovering services again
			mCallbacks.onBonded(device);
			gatt.discoverServices();
			break;
		default:
			// no-op
			break;
	}
}