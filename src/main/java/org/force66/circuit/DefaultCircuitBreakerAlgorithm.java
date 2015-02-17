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

import org.apache.commons.lang3.Validate;

public class DefaultCircuitBreakerAlgorithm implements CircuitBreakerAlgorithm {
	
	/**
	 * Default time that circuit will remain open in millis.
	 */
	public static final Long DEFAULT_OPEN_INTERVAL_MILLIS=120000L;
	
	/**
	 * Default number of failures required to "open" the circuit. 
	 */
	public static final Long DEFAULT_NBR_FAILURES_TO_OPEN_CIRCUIT=5L;
	
	private CircuitState circuitState = CircuitState.CLOSED;
	private Long openIntervalInMillis;
	private Long nbrFailuresToOpenCircuit;
	private Long nbrReportedConsecutiveFailures = 0L;
	private long timeOpenedInMillis=0;
	
	public DefaultCircuitBreakerAlgorithm() {
		this(DEFAULT_OPEN_INTERVAL_MILLIS, DEFAULT_NBR_FAILURES_TO_OPEN_CIRCUIT);
	}
	
	public DefaultCircuitBreakerAlgorithm(Long openIntervalInMillis, Long nbrFailuresToOpenCircuit) {
		Validate.notNull(openIntervalInMillis, "Null openIntervalInMillis not allowed.");
		Validate.notNull(nbrFailuresToOpenCircuit, "Null nbrFailuresToOpenCircuit not allowed.");
		Validate.isTrue(openIntervalInMillis>0L, "openIntervalInMillis must be larger than 0.  value=%s", openIntervalInMillis);
		Validate.isTrue(nbrFailuresToOpenCircuit>0L, "nbrFailuresToOpenCircuit must be larger than 0.  value=%s", nbrFailuresToOpenCircuit);
		
		this.openIntervalInMillis = openIntervalInMillis;
		this.nbrFailuresToOpenCircuit = nbrFailuresToOpenCircuit;
	}

	public synchronized boolean isExecutionAllowed() {
		if (CircuitState.OPEN.equals(circuitState)) {
			if (System.currentTimeMillis() - timeOpenedInMillis > openIntervalInMillis) {
				circuitState = CircuitState.HALFOPEN;
			}
			else {
				return false;
			}
		}
		return true;
	}

	public synchronized void reportExecutionFailure(Exception exceptionThrown) {
		nbrReportedConsecutiveFailures++;
		if (CircuitState.HALFOPEN.equals(circuitState) || nbrReportedConsecutiveFailures >= nbrFailuresToOpenCircuit) {
			circuitState = CircuitState.OPEN;
			timeOpenedInMillis=System.currentTimeMillis();
		}

	}

	public synchronized void reportExecutionSuccess() {
		if ( !CircuitState.CLOSED.equals(circuitState)) {
			circuitState = CircuitState.CLOSED;
			nbrReportedConsecutiveFailures = 0L;
			timeOpenedInMillis=0;
		}
	}

	public synchronized CircuitState getCircuitState() {
		return circuitState;
	}

	public synchronized Long getOpenIntervalInMillis() {
		return openIntervalInMillis;
	}

	public synchronized Long getNbrFailuresToOpenCircuit() {
		return nbrFailuresToOpenCircuit;
	}

	public synchronized Long getNbrReportedConsecutiveFailures() {
		return nbrReportedConsecutiveFailures;
	}

	public synchronized long getTimeOpenedInMillis() {
		return timeOpenedInMillis;
	}

}
