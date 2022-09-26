package com.heilaiq.communication.echonetlite;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.Battery;

public class GenericReceiver extends Battery.Receiver {

	// private String eojID;
	Map<Byte, byte[]> localMap;

	public GenericReceiver(String eojID, Map<String, Map<Byte, byte[]>> bufferValues) {
		super();
		// this.eojID = eojID;
		localMap = new LinkedHashMap<>();
		bufferValues.put(eojID, localMap);
		System.out.println("Device found: " + eojID);
	}

	@Override
	protected boolean onGetProperty(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
		super.onGetProperty(eoj, tid, esv, property, success);
		// TODO: logger
		// System.out.println("EPC: " + property.epc);
		localMap.put(property.epc, property.edt);
		return true;
	}
}