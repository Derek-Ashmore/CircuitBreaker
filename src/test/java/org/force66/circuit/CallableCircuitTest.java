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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CallableCircuitTest {

	Circuit<String> circuit;
	MyCallable myCallable;

	@Before
	public void setUp() throws Exception {
		circuit = new Circuit<String>();
		myCallable = new MyCallable();
	}

	@Test
	public void testCall() throws Exception {
		CallableCircuit<String> callable = new CallableCircuit<String>(circuit,
				myCallable);
		Assert.assertEquals("stuff", callable.call());
	}

	@Test
	public void testCallableCircuit() throws Exception {
		CallableCircuit<String> callable = new CallableCircuit<String>(circuit,
				myCallable);
		Assert.assertEquals(circuit,
				FieldUtils.readDeclaredField(callable, "localCircuit", true));
		Assert.assertEquals(myCallable,
				FieldUtils.readDeclaredField(callable, "localOperation", true));

		try {
			callable = new CallableCircuit<String>(null, myCallable);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().contains("not allowed"));
		}

		try {
			callable = new CallableCircuit<String>(circuit, null);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().contains("not allowed"));
		}
	}

}
