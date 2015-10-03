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
 * Implements the circuit breaker pattern as a Callable so that it can be
 * combined easily with other utilities.
 *
 * @author D. Ashmore
 * @since 1.0.0-rc2
 *
 * @param <T>
 */
public class CallableCircuit<T> implements Callable<T> {

	private final Circuit<T> localCircuit;
	private final Callable<T> localOperation;

	public CallableCircuit(Circuit<T> circuit, Callable<T> operation) {
		Validate.notNull(circuit, "Null circuit not allowed.");
		Validate.notNull(operation, "Null operation not allowed.");
		localCircuit = circuit;
		localOperation = operation;
	}

	@Override
	public T call() throws Exception {
		return localCircuit.invoke(localOperation);
	}

}
