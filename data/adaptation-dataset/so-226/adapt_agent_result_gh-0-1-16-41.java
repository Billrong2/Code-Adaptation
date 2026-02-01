/**
     * Creates a Bluetooth-backed ListPreference populated with bonded devices when available.
     *
     * @param context the application context
     * @param attrs attribute set from XML
     */
    public BluetoothDevicePreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        CharSequence[] entries = new CharSequence[pairedDevices.size()];
        CharSequence[] entryValues = new CharSequence[pairedDevices.size()];

        int index = 0;
        for (BluetoothDevice device : pairedDevices)
        {
            entries[index] = device.getName();
            entryValues[index] = device.getAddress();
            index++;
        }

        setEntries(entries);
        setEntryValues(entryValues);
    }