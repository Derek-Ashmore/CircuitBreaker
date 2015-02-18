/*
 * This software is licensed under the Apache License, Version 2.0
 * (the "License") agreement; you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.force66.circuit;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CircuitTest {
	
	Circuit<String> circuit;
	MyCallable callable;

	@Before
	public void setUp() throws Exception {
		circuit = new Circuit<String>();
		callable=new MyCallable();
	}

	@Test
	public void testConstructors() throws Exception {
		circuit = new Circuit<String>();
		DefaultCircuitBreakerAlgorithm algorithm = (DefaultCircuitBreakerAlgorithm)circuit.getCircuitBreakerAlgorithm();
		Assert.assertTrue(algorithm != null);
		Assert.assertTrue(DefaultCircuitBreakerAlgorithm.DEFAULT_NBR_FAILURES_TO_OPEN_CIRCUIT.equals(algorithm.getNbrFailuresToOpenCircuit()));
		Assert.assertTrue(DefaultCircuitBreakerAlgorithm.DEFAULT_OPEN_INTERVAL_MILLIS.equals(algorithm.getOpenIntervalInMillis()));
		Assert.assertTrue(CircuitState.CLOSED.equals(circuit.getCircuitState()));
		
		circuit = new Circuit<String>(algorithm);
		Assert.assertTrue(algorithm == circuit.getCircuitBreakerAlgorithm());
		
		try {
			circuit = new Circuit<String>(null);
			Assert.fail("Null constructor should have failed.");
		}
		catch (Exception e) {
			Assert.assertTrue(e.getMessage().contains("Null algorithm not allowed"));
		}
	}
	
	@Test
	public void testBasic() throws Exception {
		DefaultCircuitBreakerAlgorithm algorithm = new DefaultCircuitBreakerAlgorithm(10L, 2L); 
		circuit = new Circuit<String>(algorithm);
		
		Assert.assertTrue(CircuitState.CLOSED.equals(circuit.getCircuitState()));
		Assert.assertTrue("stuff".equals(circuit.invoke(callable)));
		
		callable.throwException = true;
		testException("crap");
		testException("crap");
		testException("Operation not available");
		
		Thread.sleep(11);
		callable.throwException = false;
		Assert.assertTrue("stuff".equals(circuit.invoke(callable)));
		Assert.assertTrue(algorithm.getNbrReportedConsecutiveFailures().equals(0L));
		Assert.assertTrue(algorithm.getTimeOpenedInMillis()==0);
	}
	
	private void testException(String testMessage) {
		Exception exceptionThrown=null;
		try {circuit.invoke(callable);}
		catch (Exception e) {
			exceptionThrown=e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage().contains(testMessage));
	}
	
	static class MyCallable implements Callable<String>{
		
		boolean throwException=false;

		public String call() throws Exception {
			if (throwException) {
				throw new Exception("crap");
			}
			return "stuff";
		}
		
	}

}
