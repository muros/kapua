Cucumber testing
================

How to run tests with 

'''
mvn clean install -Dlocator.class.impl=org.eclipse.kapua.test.MockedLocator
'''

Jacoco Coverage report
======================

'''
mvn jacoco:report
'''

Report is created in target/site/jacoco folder.  

