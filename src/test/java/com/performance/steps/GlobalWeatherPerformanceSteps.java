package com.performance.steps;

import java.io.FileNotFoundException;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.performance.jmeter.JMeterEngineGlobalWeather;

/**
 * @since 25-05-2015
 * @author leonardo.pereira
 * Global Weather Service Steps
 */

@Slf4j
@Component
public class GlobalWeatherPerformanceSteps {   
	
	@Autowired
	private JMeterEngineGlobalWeather engineGlobalWeather;
	
	/**
	 * Prepare data between steps
	 * @param countryName
	 * @param cityName
	 */
	@Given("A country $countryName and city $cityName")
	public void doPrepareData(@Named("countryName") String countryName, @Named("cityName") String cityName) {
	}

	/**
	 * Call service
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@When("I consume GetWeather service")
	public void doCallService() throws FileNotFoundException, IOException {
		engineGlobalWeather.globalWeatherEngine().run(); 
	}

	/**
	 * Verify elapsed time
	 * @param temperture
	 * @throws FileNotFoundException 
	 */
	@Then("Verify that response is less than $time milis")
	public void verifyTemperature(@Named("time") int timeExpected) throws FileNotFoundException {	
		int actualResult = Integer.parseInt(engineGlobalWeather.getSummary().get("average"));
		log.info("[Verify Average Response]: --> " + timeExpected + " > " + actualResult); 
		Assert.assertTrue("Verify Average Response", timeExpected > actualResult);
	}
}
