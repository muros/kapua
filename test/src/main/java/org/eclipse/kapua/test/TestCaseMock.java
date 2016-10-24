package org.eclipse.kapua.test;

import java.lang.reflect.Method;

import org.eclipse.kapua.test.annotations.TestCase;

public abstract class TestCaseMock {

	public String getTestCaseId() {
		boolean isTestCase = false;
		String caseId = null;

		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

		endit: for (StackTraceElement stackTraceElement : stackTraceElements) {
			String className = stackTraceElement.getClassName();
			String methodName = stackTraceElement.getMethodName();
			try {
				Class<?> clazz = Class.forName(className);
				Method[] clazzMethodz = clazz.getMethods();
				for (Method method : clazzMethodz) {
					if (methodName.equals(method.getName())) {
						if (method.isAnnotationPresent(TestCase.class)) {
							isTestCase = true;
							TestCase testCaseAnnotation = method.getAnnotation(TestCase.class);
							caseId = testCaseAnnotation.caseId();
							break endit;
						}
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return caseId;
	}
}
