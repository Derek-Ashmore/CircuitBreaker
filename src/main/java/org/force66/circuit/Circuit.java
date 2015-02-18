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

import org.apache.commons.lang3.Validate;

/**
 * Implementation of the circuit breaker pattern.
 * @author D. Ashmore
 *
 * @param <T>
 */
public class Circuit<T> {
	
	private CircuitBreakerAlgorithm circuitBreakerAlgorithm;
	
	public Circuit()   {
		this(new DefaultCircuitBreakerAlgorithm());
	}
	public Circuit(CircuitBreakerAlgorithm algorithm)   {
		Validate.notNull(algorithm, "Null algorithm not allowed.");
		circuitBreakerAlgorithm = algorithm;
	}
	
	public T invoke(Callable<T> operation) {
		Validate.notNull(operation, "Null operation not allowed.");
		if ( !circuitBreakerAlgorithm.isExecutionAllowed()) {
			throw new CircuitException("Operation not available.")
				.addContextValue("callable class", operation.getClass().getName())
				.addContextValue("callable", operation.toString());
		}
		
		try {
			T output = operation.call();			
			circuitBreakerAlgorithm.reportExecutionSuccess();
			return output;
		}
		catch (Exception e) {
			circuitBreakerAlgorithm.reportExecutionFailure(e);
			throw new CircuitException(e)
				.addContextValue("callable class", operation.getClass().getName())
				.addContextValue("callable", operation.toString());
		}

	}
	
	public CircuitState getCircuitState() {
		return circuitBreakerAlgorithm.getCircuitState();
	}
	
	protected CircuitBreakerAlgorithm getCircuitBreakerAlgorithm() {
		return circuitBreakerAlgorithm;
	}

}
