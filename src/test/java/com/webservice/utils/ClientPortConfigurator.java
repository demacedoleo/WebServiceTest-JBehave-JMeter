package com.webservice.utils;

import javax.xml.ws.BindingProvider;

/**
 * Configure Port Type
 * @author leonardo.pereira
 *
 */
public final class ClientPortConfigurator {
	
	private ClientPortConfigurator() {}

	/**
	 * Configure Port
	 * @param bindingProvider
	 * @param balancerAddress
	 * @param balancerPort
	 * @return BindingProvider
	 */
	public static BindingProvider configurePort(BindingProvider bindingProvider, String balancerAddress, String balancerPort){
		String endpoint = balancerAddress;
		if (!balancerPort.isEmpty() && !("80").equals(balancerPort)){
			endpoint += ":" + balancerPort;
		}
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endpoint);
		return bindingProvider;
	}

}
