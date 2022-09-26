package com.heilaiq.communication.echonetlite;

import com.heilaiq.communication.api.Write;
import com.heilaiq.lib.configuration.communication.writeactions.EchonetLiteWriteActionConfiguration;

public class EchonetLiteWrite implements Write {

	EchonetLiteWriteActionConfiguration config;
	private byte[]						values;

	public EchonetLiteWrite(EchonetLiteWriteActionConfiguration config) {
		this.config = config;
	}

	public void execute(EchonetLiteConnection connection) throws Exception {
		connection.write(config.ipAddress, config.classGroupCode, config.classCode, config.instanceCode, config.epc,
			this.values);
	}

	@Override
	public void setValues(double[] values) {
		byte[] byteValues = new byte[values.length];
		for (int i = 0; i < values.length; i++) {
			byteValues[i] = (byte) values[i];
		}
		this.values = byteValues;
	}

}
