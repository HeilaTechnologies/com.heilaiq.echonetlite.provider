package com.heilaiq.communication.echonetlite;

import java.util.stream.Stream;

import com.heilaiq.communication.api.Read;
import com.heilaiq.lib.configuration.communication.readactions.EchonetLiteReadActionConfiguration;

public class EchonetLiteRead implements Read {

	EchonetLiteReadActionConfiguration config;

	public EchonetLiteRead(EchonetLiteReadActionConfiguration actionConfiguration) {
		this.config = actionConfiguration;
	}

	public double[] execute(EchonetLiteConnection connection) throws Exception {

		Double[] results = connection.execute(config.ipAddress, config.classGroupCode, config.classCode,
			config.instanceCode, config.epc);
		return Stream.of(results)
			.mapToDouble(Double::doubleValue)
			.toArray();
	}

	@Override
	public int getNumberOfValues() {
		return 1;
	}

}
