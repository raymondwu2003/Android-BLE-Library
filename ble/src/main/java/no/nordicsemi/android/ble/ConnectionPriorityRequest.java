/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import no.nordicsemi.android.ble.callback.ConnectionPriorityCallback;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.ble.callback.SuccessCallback;
import no.nordicsemi.android.ble.exception.BluetoothDisabledException;
import no.nordicsemi.android.ble.exception.DeviceDisconnectedException;
import no.nordicsemi.android.ble.exception.RequestFailedException;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class ConnectionPriorityRequest extends ValueRequest<ConnectionPriorityCallback> {
	/**
	 * Connection parameter update - Use the connection parameters recommended by the
	 * Bluetooth SIG. This is the default value if no connection parameter update
	 * is requested.
	 * <p>
	 * Interval: 30 - 50 ms, latency: 0, supervision timeout: 20 sec.
	 */
	public static final int CONNECTION_PRIORITY_BALANCED = 0;

	/**
	 * Connection parameter update - Request a high priority, low latency connection.
	 * An application should only request high priority connection parameters to transfer
	 * large amounts of data over LE quickly. Once the transfer is complete, the application
	 * should request {@link #CONNECTION_PRIORITY_BALANCED} connection parameters
	 * to reduce energy use.
	 * <p>
	 * Interval: 11.25 - 15 ms (Android 6+) or 7.5 - 10 ms (Android 4.3 - 5.1),
	 * latency: 0, supervision timeout: 20 sec.
	 */
	public static final int CONNECTION_PRIORITY_HIGH = 1;

	/**
	 * Connection parameter update - Request low power, reduced data rate connection parameters.
	 * <p>
	 * Interval: 100 - 125 ms, latency: 2, supervision timeout: 20 sec.
	 */
	public static final int CONNECTION_PRIORITY_LOW_POWER = 2;

	private final int value;

	ConnectionPriorityRequest(@NonNull final Type type, int priority) {
		super(type);
		if (priority < 0 || priority > 2)
			priority = CONNECTION_PRIORITY_BALANCED;
		this.value = priority;
	}

	@Override
	@NonNull
	public ConnectionPriorityRequest done(@NonNull final SuccessCallback callback) {
		this.successCallback = callback;
		return this;
	}

	@Override
	@NonNull
	public ConnectionPriorityRequest fail(@NonNull final FailCallback callback) {
		this.failCallback = callback;
		return this;
	}

	@RequiresApi(value = Build.VERSION_CODES.O)
	@Override
	@NonNull
	public ConnectionPriorityRequest with(@NonNull final ConnectionPriorityCallback callback) {
		// The BluetoothGattCallback#onConnectionUpdated callback was introduced in Android Oreo.
		this.valueCallback = callback;
		return this;
	}

	@RequiresApi(value = Build.VERSION_CODES.O)
	@NonNull
	@Override
	public <E extends ConnectionPriorityCallback> E await(final Class<E> responseClass)
			throws RequestFailedException, DeviceDisconnectedException, BluetoothDisabledException {
		// The BluetoothGattCallback#onConnectionUpdated callback was introduced in Android Oreo.
		return super.await(responseClass);
	}

	@RequiresApi(value = Build.VERSION_CODES.O)
	@NonNull
	@Override
	public <E extends ConnectionPriorityCallback> E await(@NonNull final Class<E> responseClass,
														  final int timeout)
			throws RequestFailedException, InterruptedException, DeviceDisconnectedException,
			BluetoothDisabledException {
		// The BluetoothGattCallback#onConnectionUpdated callback was introduced in Android Oreo.
		return super.await(responseClass, timeout);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	void notifyConnectionPriorityChanged(@NonNull final BluetoothDevice device,
										 final int interval, final int latency, final int timeout) {
		if (valueCallback != null)
			valueCallback.onConnectionUpdated(device, interval, latency, timeout);
	}

	int getRequiredPriority() {
		return value;
	}
}