package com.heilaiq.communication.echonetlite;

import java.io.IOException;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heilaiq.communication.api.Channel;
import com.heilaiq.communication.api.ErrorStatus;
import com.heilaiq.communication.api.Read;
import com.heilaiq.communication.api.Write;
import com.heilaiq.lib.configuration.communication.channels.EchonetLiteChannelConfiguration;
import com.heilaiq.scheduler.api.Scheduler;

public class EchonetLiteChannel implements Channel {

	protected final static Logger	LOGGER	= LoggerFactory.getLogger(EchonetLiteChannel.class.getName());

	EchonetLiteChannelConfiguration	configuration;
	EchonetLiteConnection			connection;
	Scheduler						scheduler;

	public EchonetLiteChannel(EchonetLiteChannelConfiguration configuration, Scheduler scheduler) {
		this.configuration = configuration;
		this.scheduler = scheduler;
		this.connection = new EchonetLiteConnection(configuration);
		try {
			this.connection.setup();
			this.connection.discovery();
		} catch (IOException e) {
			LOGGER.error("CRITICAL ERROR", e);
		}

	}

	@Override
	public String getKey() {
		return configuration.getKey();
	}

	@Override
	public Promise<double[]> read(Read readAction) throws Exception {
		EchonetLiteRead read = (EchonetLiteRead) readAction;
		try {
			double[] results = read.execute(connection);
			return Promises.resolved(results);
		} catch (Exception e) {
			LOGGER.error("", e);
			throw e;
		}
	}

	@Override
	public Promise<ErrorStatus> write(Write writeAction) throws Exception {
		EchonetLiteWrite write = (EchonetLiteWrite) writeAction;
		try {
			write.execute(connection);
		} catch (Exception e) {
			LOGGER.error("", e);
			return Promises.failed(e);
		}
		return Promises.resolved(ErrorStatus.OK);
	}

	@Override
	public ErrorStatus getErrorStatus() {
		return ErrorStatus.OK;
	}

	@Override
	public void close() {

	}

}
