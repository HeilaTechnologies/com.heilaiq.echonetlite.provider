package com.heilaiq.communication.echonetlite;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heilaiq.lib.configuration.communication.channels.EchonetLiteChannelConfiguration;
import com.sonycsl.echo.Echo;
import com.sonycsl.echo.eoj.device.DeviceObject;
import com.sonycsl.echo.eoj.device.DeviceObject.Getter;
import com.sonycsl.echo.eoj.profile.NodeProfile;
import com.sonycsl.echo.node.EchoNode;
import com.sonycsl.echo.processing.defaults.DefaultController;
import com.sonycsl.echo.processing.defaults.DefaultNodeProfile;

public class EchonetLiteConnection {

	protected final static Logger	LOGGER		= LoggerFactory.getLogger(EchonetLiteConnection.class.getName());

	EchoNode[]						nodes;
	EchoNode						local;

	Map<String, DeviceObject>		deviceObjects;
	Map<String, Map<Byte, byte[]>>	bufferValues;

	EchonetLiteChannelConfiguration	configuration;
	private boolean					initialized	= false;

	public EchonetLiteConnection(EchonetLiteChannelConfiguration configuration) {
		this.configuration = configuration;
		this.deviceObjects = new HashMap<>();
		this.bufferValues = new HashMap<>();
	}

	public void setup() throws IOException {
		// Start Controller Node
		NodeProfile profile = new DefaultNodeProfile(); // EchoNode
		NetworkInterface networkInterface = NetworkInterface.getByName(this.configuration.networkInterface);
		Echo.start(profile, new DeviceObject[] {
			new DefaultController()
		}, networkInterface);
		this.initialized = true;
	}

	public void discovery() {

		// Node Discovery
		try {
			if (!initialized)
				setup();
			NodeProfile.getG()
				.reqGetSelfNodeInstanceListS()
				.send();
			nodes = Echo.getNodes();
			local = Echo.getSelfNode();
			String printString;
			for (EchoNode node : nodes) {
				if (node != local) {
					printString = "Node id = " + node.getAddress()
						.getHostAddress();
					// System.out.println(printString);
					DeviceObject[] dos = node.getDevices();
					for (DeviceObject d : dos) {
						// Unique ID of the device object
						String eojID = node.getAddress()
							.getHostAddress() + ":" + d.getClassGroupCode() + ":" + d.getClassCode() + ":"
							+ d.getInstanceCode();

						if (!deviceObjects.containsKey(eojID)) {
							// New Device discovered
							// TODO: what happens if the device goes offline?
							DeviceObject.Receiver receiver = new GenericReceiver(eojID, bufferValues);
							d.setReceiver(receiver);
							deviceObjects.put(eojID, d);
						}
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("{}", e);
		}
	}

	public Double[] execute(String ipAddress, int classGroupCode, byte classCode, int instanceCode, int[] epc)
		throws Exception {
		String eojID = ipAddress + ":" + classGroupCode + ":" + classCode + ":" + instanceCode;
		DeviceObject deviceObject = deviceObjects.get(eojID);
		if (deviceObject == null) {
			// TODO: send a discovery request and wait?
			discovery();
			Thread.sleep(5000);
			throw new Exception("Device object " + eojID + " not found");
		}
		// Prepare the request based on epc
		Getter getter = deviceObject.get();
		for (int i = 0; i < epc.length; i++) {
			getter.reqGetProperty((byte) epc[i]);
			// System.out.println(eojID + ": " + (byte) epc[i]);
		}
		getter.send();
		Thread.sleep(1000); // wait 1 second to receive response. TODO:
							// configure wait time by request? device?

		// TODO: handle expiration of values
		Map<Byte, byte[]> bufferEOJ = bufferValues.get(eojID);
		LinkedList<Double> results = new LinkedList<>();
		for (int i = 0; i < epc.length; i++) {
			Byte b = new Byte((byte) epc[i]);

			// TODO: what happens if it fails? we don't have the size of the
			// resulting value to say we can handle a failed value
			try {
				byte[] cs = bufferEOJ.get(b);
				for (int j = 0; j < cs.length; j++) {
					results.add((double) cs[j]);
				}
			} catch (Exception e) {
				LOGGER.error(eojID + "/n" + epc[i]);
				LOGGER.error("{}", e);

			}
		}
		Double[] array = results.toArray(new Double[0]);
		return array;
	}

	public void write(String ipAddress, int classGroupCode, byte classCode, int instanceCode, int epc, byte[] values)
		throws Exception {
		String eojID = ipAddress + ":" + classGroupCode + ":" + classCode + ":" + instanceCode;
		DeviceObject deviceObject = deviceObjects.get(eojID);
		if (deviceObject == null) {
			// TODO: send a discovery request and wait?
			discovery();
			Thread.sleep(5000);
			throw new Exception("Device object " + eojID + " not found");
		}
		deviceObject.set()
			.reqSetProperty((byte) epc, values)
			.send();
	}

}
