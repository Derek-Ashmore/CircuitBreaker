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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultCircuitBreakerAlgorithmTest {
	
	DefaultCircuitBreakerAlgorithm algorithm;

	@Before
	public void setUp() throws Exception {
		algorithm = new DefaultCircuitBreakerAlgorithm();
	}
	
	@Test
	public void testConstructors() throws Exception {
		Assert.assertTrue(algorithm.getOpenIntervalInMillis() != null);
		Assert.assertTrue(algorithm.getOpenIntervalInMillis().equals(DefaultCircuitBreakerAlgorithm.DEFAULT_OPEN_INTERVAL_MILLIS));
		Assert.assertTrue(algorithm.getNbrFailuresToOpenCircuit() != null);
		Assert.assertTrue(algorithm.getNbrFailuresToOpenCircuit().equals(DefaultCircuitBreakerAlgorithm.DEFAULT_NBR_FAILURES_TO_OPEN_CIRCUIT));
		
		testConstructorValues(null, null, "Null openIntervalInMillis");
		testConstructorValues(1L, null, "Null nbrFailuresToOpenCircuit");
		testConstructorValues(-1L, -1L, "-1");
		testConstructorValues(1L, -1L, "-1");
	}
	
	private void testConstructorValues(Long openInterval, Long nbrFailures, String testMessage) {
		try {new DefaultCircuitBreakerAlgorithm(openInterval, nbrFailures);}
		catch (Exception e) {
			Assert.assertTrue(e.getMessage() != null);
			Assert.assertTrue(e.getMessage().contains(testMessage));
			return;
		}
		Assert.fail("No exception thrown.  openInterval="+openInterval+"  nbrFailures="+nbrFailures);
	}

	@Test
	public void testBasic() throws Exception {
		algorithm = new DefaultCircuitBreakerAlgorithm(10L, 2L);
		Assert.assertTrue(algorithm.isExecutionAllowed());
		
		for (int i = 0; i < algorithm.getNbrFailuresToOpenCircuit(); i++) {
			algorithm.reportExecutionFailure(new Exception("break"));
		}
		Assert.assertTrue(!algorithm.isExecutionAllowed());
		Thread.sleep(algorithm.getOpenIntervalInMillis() +1);
		Assert.assertTrue(algorithm.isExecutionAllowed());
		Assert.assertTrue(CircuitState.HALFOPEN.equals(algorithm.getCircuitState()));
		
		algorithm.reportExecutionFailure(new Exception("break"));
		Assert.assertTrue(CircuitState.OPEN.equals(algorithm.getCircuitState()));
		
		Thread.sleep(algorithm.getOpenIntervalInMillis() +1);
		Assert.assertTrue(algorithm.isExecutionAllowed());
		algorithm.reportExecutionSuccess();
		
		Assert.assertTrue(CircuitState.CLOSED.equals(algorithm.getCircuitState()));
		Assert.assertTrue(algorithm.getNbrReportedConsecutiveFailures().equals(0L));
		Assert.assertTrue(algorithm.getTimeOpenedInMillis() == 0);
				
	}

}
