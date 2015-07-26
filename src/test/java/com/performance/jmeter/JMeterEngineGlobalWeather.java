package com.performance.jmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JMeterEngineGlobalWeather {
	private StandardJMeterEngine jMeterEngine;
	
	@Value("${test.performance.jmeterProperties}")
	private String jmeterProperties;
	
	@Value("${test.performance.threads}")
	private String threads;
	
	@Value("${test.performance.rampUp}")
	private String rampUp;
	
	@Value("${test.jmeter.report.projects}")
	private String projectJMeter;
	
	@Value("${test.jmeter.report.jtl}")
	private String reportJTL;
	
	@Value("${test.jmeter.report.csv}")
	private String reportCSV;
	
	@Value("${test.jmeter.report.dirReport}")
	private String dirReport;
	
	public StandardJMeterEngine globalWeatherEngine() throws FileNotFoundException, IOException { 
		
		jMeterEngine = new StandardJMeterEngine();
		
		JMeterUtils.setJMeterHome("/Users/admin/Work/apache-jmeter-2.13");
		JMeterUtils.loadJMeterProperties(jmeterProperties);
		
		JMeterUtils.initLogging();
        JMeterUtils.initLocale();

        HashTree hashTree = new HashTree(); 
        
        // HTTP Sampler
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setDomain("api.openweathermap.org");
        httpSampler.setPort(80);
        httpSampler.setPath("/data/2.5/weather");
        httpSampler.setMethod("GET");
        httpSampler.addArgument("q", "London");
        httpSampler.setName("openweathermap");
        
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        // Loop Controller
        TestElement loopCtrl = new LoopController();
        ((LoopController)  loopCtrl).setLoops(1);
        ((LoopController) loopCtrl).addTestElement(httpSampler);
        ((LoopController) loopCtrl).setFirst(true);

        // Thread Group
        SetupThreadGroup threadGroup = new SetupThreadGroup();
        threadGroup.setNumThreads(Integer.valueOf(threads));
        threadGroup.setRampUp(Integer.valueOf(rampUp));
        threadGroup.setSamplerController((LoopController) loopCtrl);

        // Test plan
        TestPlan testPlan = new TestPlan("Global Weather - Get Weather");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        hashTree.add("testPlan", testPlan);
        hashTree.add("loopCtrl", loopCtrl);
        hashTree.add("threadGroup", threadGroup);
        hashTree.add("httpSampler", httpSampler);
       
        jMeterEngine.configure(hashTree);
      
        createReport(hashTree); 
        
		return jMeterEngine;
	}

	/**
	 * Generate JMeter Report
	 * @param hashTree
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void createReport(HashTree hashTree) throws FileNotFoundException, IOException { 
		
		// save generated test plan to JMeter's .jmx file format
		if(new File(dirReport).exists()) {
			FileUtils.cleanDirectory(new File(dirReport));  
		} else {
			FileUtils.forceMkdir(new File(dirReport));
		}
		
        SaveService.saveTree(hashTree, new FileOutputStream(new File(dirReport + "/" + projectJMeter)));
        
        //add Summarizer output to get test progress in stdout like:
        // summary = (Max + Min = --> 1.3~) 2 in 1.3s = 1.5/s  
        //Avg([Max+Min]/2): 631 milis  Min: 290 milis  Max: 973 milis  (Response) Err: 0 (0.00%)
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }
        
        // Store execution
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(dirReport + "/" + reportJTL);
        ResultCollector csvlogger = new ResultCollector(summer);
        csvlogger.setFilename(dirReport + "/" + reportCSV);
        hashTree.add(hashTree.getArray()[0], logger);
        hashTree.add(hashTree.getArray()[0], csvlogger);
		
	}
	
	/**
	 * Map that contains a jmeter log
	 * @return Map
	 * @throws FileNotFoundException
	 */
	public Map<String, String> getSummary() throws FileNotFoundException { 
		int positionAverage = 4;
		Map<String, String> summary = new HashMap<String, String>();
		
		try {
			List<String> lines = FileUtils.readLines(new File("jmeter.log"));
			String[] results = lines.get(lines.size()-1).toString().split(":");
			summary.put("average", results[positionAverage].trim().substring(0, 3));  
		} catch(IOException io) {
			throw new FileNotFoundException();
		}
		
		return summary;
	}
}
