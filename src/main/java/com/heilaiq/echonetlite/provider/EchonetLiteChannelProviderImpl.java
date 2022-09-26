package com.heilaiq.echonetlite.provider;

import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.heilaiq.communication.api.Channel;
import com.heilaiq.communication.api.ChannelConfiguration;
import com.heilaiq.communication.api.ChannelProvider;
import com.heilaiq.communication.api.Read;
import com.heilaiq.communication.api.ReadActionConfiguration;
import com.heilaiq.communication.api.Write;
import com.heilaiq.communication.api.WriteActionConfiguration;
import com.heilaiq.communication.echonetlite.EchonetLiteChannel;
import com.heilaiq.communication.echonetlite.EchonetLiteRead;
import com.heilaiq.communication.echonetlite.EchonetLiteWrite;
import com.heilaiq.echonetlite.api.EchonetLiteChannelProvider;
import com.heilaiq.lib.configuration.communication.channels.EchonetLiteChannelConfiguration;
import com.heilaiq.lib.configuration.communication.readactions.EchonetLiteReadActionConfiguration;
import com.heilaiq.lib.configuration.communication.writeactions.EchonetLiteWriteActionConfiguration;
import com.heilaiq.scheduler.api.Scheduler;

@Component
public class EchonetLiteChannelProviderImpl implements EchonetLiteChannelProvider, ChannelProvider {

	private Scheduler scheduler;

	@Override
	public String getType() {
		return TYPE;
	}

	@Activate
	public EchonetLiteChannelProviderImpl(@Reference
	Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public Optional<Channel> createChannel(ChannelConfiguration channelConfiguration, boolean mock) {
		if (channelConfiguration instanceof EchonetLiteChannelConfiguration) {
			EchonetLiteChannelConfiguration configuration = (EchonetLiteChannelConfiguration) channelConfiguration;
			return Optional.of(new EchonetLiteChannel(configuration, this.scheduler));

		}
		return Optional.empty();
	}

	@Override
	public Optional<Read> createReadAction(ReadActionConfiguration actionConfiguration, boolean mock) {
		if (actionConfiguration instanceof EchonetLiteReadActionConfiguration) {
			return Optional.of(new EchonetLiteRead((EchonetLiteReadActionConfiguration) actionConfiguration));
		}
		return Optional.empty();
	}

	@Override
	public Optional<Write> createWriteAction(WriteActionConfiguration actionConfiguration, boolean mock) {
		if (actionConfiguration instanceof EchonetLiteWriteActionConfiguration) {
			return Optional.of(new EchonetLiteWrite((EchonetLiteWriteActionConfiguration) actionConfiguration));
		}
		return Optional.empty();
	}

}
