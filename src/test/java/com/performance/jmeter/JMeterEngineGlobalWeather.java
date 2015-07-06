package com.performance.jmeter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
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
	
	public StandardJMeterEngine globalWeatherEngine() throws FileNotFoundException, IOException { 
		
		jMeterEngine = new StandardJMeterEngine();
		
		JMeterUtils.setJMeterHome("/Users/admin/Work/apache-jmeter-2.13");
		JMeterUtils.loadJMeterProperties(jmeterProperties);
		
		JMeterUtils.initLogging();
        JMeterUtils.initLocale();

        HashTree hashTree = new HashTree();     

        // HTTP Sampler
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setDomain("www.google.com");
        httpSampler.setPort(80);
        httpSampler.setPath("/");
        httpSampler.setMethod("GET");
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
        
        // save generated test plan to JMeter's .jmx file format
        SaveService.saveTree(hashTree, new FileOutputStream("report\\jmeter_api_sample.jmx"));
        
        //add Summarizer output to get test progress in stdout like:
        // summary = 2 in 1.3s = 1.5/s  Avg: 631  Min: 290  Max: 973  Err: 0 (0.00%)
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }
        System.out.println(">>> " + summer.getComment());
        // Store execution results into a .jtl file, we can save file as csv also
        String reportFile = "report\\report.jtl";
        String csvFile = "report\\report.csv";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(reportFile);
        ResultCollector csvlogger = new ResultCollector(summer);
        csvlogger.setFilename(csvFile);
        hashTree.add(hashTree.getArray()[0], logger);
        hashTree.add(hashTree.getArray()[0], csvlogger); 
		
		return jMeterEngine;
	}
}
