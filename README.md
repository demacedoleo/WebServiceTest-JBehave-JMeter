# WebServiceTest-JBehave-JMeter

[![Travis](https://secure.travis-ci.org/macedoleonardo/WebServiceTest-JBehave-JMeter.png)](http://travis-ci.org/macedoleonardo/WebServiceTest-JBehave-JMeter)

+ To Execute this project is needed download JMeter & configure path of JMeter in the properties:
   - src/test/resources/conf/webservice.properties
   - test.performance.jmeterPath=<set your jmeter path>

+ JBehave
+ JMeter

+ Prepare Environment:
   - mvn eclipse:clean eclipse eclipse
+ To Run: (Eclipse IDE)
   - clean integration-test
+ Debug Mode: (Eclipse IDE)
   - clean integration-test -Dmaven.failsafe.debug -X
