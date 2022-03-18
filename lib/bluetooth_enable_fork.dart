import 'dart:async';

import 'package:flutter/services.dart';

class BluetoothEnable {
  static const MethodChannel _channel = const MethodChannel('bluetooth_enable');

  /// Main method of this package.
  ///
  /// This will check if Bluetooth is enabled on the smartphone; if it's not the
  /// case, it will ask for its activation.
  ///
  /// This is done differently regarding the hosting platform:
  ///     * on Android, an intent will be fired, displaying the user a dialog to
  ///     activate Bluetooth; the method will return once user clicked a dialog
  ///     option button.
  ///     * on iOS, a dialog will be displayed asking user to activate Bluetooth
  ///     in the application settings; the method will immediately return false,
  ///     and needs to be called a second time (after user supposedly activated
  ///     Bluetooth).
  static Future<String> get enableBluetooth async {
    final String bluetoothState =
        await _channel.invokeMethod('enableBluetooth');
    return bluetoothState;
  }
}
