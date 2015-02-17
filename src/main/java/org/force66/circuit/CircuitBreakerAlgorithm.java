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

/**
 * Provides breaker algorithm for circuit.  Implementations must to be thread-safe.
 * @author D. Ashmore
 *
 */
public interface CircuitBreakerAlgorithm {
	
	/**
	 * Implementors report if operation execution should proceed.
	 */
	public boolean isExecutionAllowed();
	
	/**
	 * Will be invoked when an execution failure is encountered.
	 * @param exceptionThrown
	 */
	public void reportExecutionFailure(Exception exceptionThrown);
	
	/**
	 * Will be invoked when an execution success is encountered.
	 */
	public void reportExecutionSuccess();
	
	public CircuitState getCircuitState();

}
