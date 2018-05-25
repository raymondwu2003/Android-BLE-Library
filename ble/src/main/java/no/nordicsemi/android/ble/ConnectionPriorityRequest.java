package no.nordicsemi.android.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import no.nordicsemi.android.ble.callback.ConnectionPriorityCallback;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.ble.callback.SuccessCallback;
import no.nordicsemi.android.ble.exception.DeviceDisconnectedException;
import no.nordicsemi.android.ble.exception.RequestFailedException;

public final class ConnectionPriorityRequest extends ValueRequest<ConnectionPriorityCallback> {
	private ConnectionPriorityCallback valueCallback;
	private final int value;

	ConnectionPriorityRequest(final @NonNull Type type, int priority) {
		super(type);
		if (priority < 0 || priority > 2)
			priority = 0; // Balanced
		this.value = priority;
	}

	@Override
	@NonNull
	public ConnectionPriorityRequest done(final @NonNull SuccessCallback callback) {
		this.successCallback = callback;
		return this;
	}

	@Override
	@NonNull
	public ConnectionPriorityRequest fail(final @NonNull FailCallback callback) {
		this.failCallback = callback;
		return this;
	}

	@RequiresApi(value = Build.VERSION_CODES.O)
	@Override
	@NonNull
	public ConnectionPriorityRequest with(final @NonNull ConnectionPriorityCallback callback) {
		// The BluetoothGattCallback#onConnectionUpdated callback was introduced in Android Oreo.
		this.valueCallback = callback;
		return this;
	}

	@RequiresApi(value = Build.VERSION_CODES.O)
	@NonNull
	@Override
	public <E extends ConnectionPriorityCallback> E await(final Class<E> responseClass)
			throws RequestFailedException, DeviceDisconnectedException {
		// The BluetoothGattCallback#onConnectionUpdated callback was introduced in Android Oreo.
		return super.await(responseClass);
	}

	@RequiresApi(value = Build.VERSION_CODES.O)
	@NonNull
	@Override
	public <E extends ConnectionPriorityCallback> E await(@NonNull final Class<E> responseClass, final int timeout)
			throws RequestFailedException, InterruptedException, DeviceDisconnectedException {
		// The BluetoothGattCallback#onConnectionUpdated callback was introduced in Android Oreo.
		return super.await(responseClass, timeout);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	void notifyConnectionPriorityChanged(final @NonNull BluetoothDevice device,
										 final int interval, final int latency, final int timeout) {
		if (valueCallback != null)
			valueCallback.onConnectionUpdated(device, interval, latency, timeout);
	}

	int getRequiredPriority() {
		return value;
	}
}