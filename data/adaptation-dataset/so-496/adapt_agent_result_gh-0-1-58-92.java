private void typeNumPad(int digit) {
	// Numpad-only helper: accepts digits 0–9, ignores everything else
	if (digit < 0 || digit > 9) {
		// Optional debug: ignored non-numpad digit
		// Application.debug("Ignored numpad digit: " + digit);
		return;
	}

	switch (digit) {
	case 0:
		doType(java.awt.event.KeyEvent.VK_NUMPAD0);
		break;
	case 1:
		doType(java.awt.event.KeyEvent.VK_NUMPAD1);
		break;
	case 2:
		doType(java.awt.event.KeyEvent.VK_NUMPAD2);
		break;
	case 3:
		doType(java.awt.event.KeyEvent.VK_NUMPAD3);
		break;
	case 4:
		doType(java.awt.event.KeyEvent.VK_NUMPAD4);
		break;
	case 5:
		doType(java.awt.event.KeyEvent.VK_NUMPAD5);
		break;
	case 6:
		doType(java.awt.event.KeyEvent.VK_NUMPAD6);
		break;
	case 7:
		doType(java.awt.event.KeyEvent.VK_NUMPAD7);
		break;
	case 8:
		doType(java.awt.event.KeyEvent.VK_NUMPAD8);
		break;
	case 9:
		doType(java.awt.event.KeyEvent.VK_NUMPAD9);
		break;
	default:
		// no-op; guarded above
		break;
	}
}