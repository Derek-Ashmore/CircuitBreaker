# CircuitBreaker
Implementation of the circuit breaker pattern.  This circuit operates like an electrical circuit in your house.  If something goes wrong, the circuit "trips" and power is cut to outlets connected to that circuit.

This circuit is meant for calls to external resources of some type.  The goal is to conserve resources by failing early.  If a remote resource is down, the circuit will prevent repeated executions to a resource which is known to be down.  At some point, executions will be allowed automatically.

For more information, see http://martinfowler.com/bliki/CircuitBreaker.html

## System Requirements
* Java JDK 6.0 or above (it was compiled under JDK 7 using 1.6 as the target source).
* Apache Commons Lang version 3.0 or above

## Installation Instructions  
CircuitBreaker is easy to install whether you use maven or not.

### Maven Users  
Maven users can find dependency information [here](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22CircuitBreaker%22).

### Non-Maven Users  
Include the following jars in your class path:  
* Download the CircuitBreaker jar from [Github](https://github.com/Derek-Ashmore/CircuitBreaker/releases) and put it in your class path.  
* Insure Apache Commons Lang version 3.0 or above is in your class path.  

## Usage Instructions  
To use CircuitBreaker, follow these instructions:  
* Create a "callable" that calls your remote resource
* Use the circuit class to execute your callable.

This circuit has a pluggable circuit algorithm (just implement interface CircuitBreakerAlgorithm).  The default algorithm will
trip (e.g. prevent executions) after a configurable number of consecutive failures.  After that, the "resource" is deemed to be down and
any attempted calls will result in a CircuitException.  

After a configurable time period, a call will be retried.  If that call is successful, then the circuit will permit all calls from that point forward.

## Usage examples:

### A most basic example with the default algorithm
```  
Circuit<String> circuit = new Circuit<String>();  
MyCallable callable = new MyCallable();  

String callResult = circuit.invoke(callable);
```  

### An example configuring the circuit algorithm
```  
DefaultCircuitBreakerAlgorithm algorithm = new DefaultCircuitBreakerAlgorithm(10L, 2L); 

Circuit<String> circuit = new Circuit<String>(algorithm);  
MyCallable callable = new MyCallable();  

String callResult = circuit.invoke(callable);
```  

## Creating your own circuit algorithm
Implement interface CircuitBreakerAlgorithm.  See DefaultCircuitBreakerAlgorithm as an example.  

You are required to implement the following methods:

```  
public boolean isExecutionAllowed();  
public void reportExecutionFailure(Exception exceptionThrown);  
public CircuitState getCircuitState();  

```  

## Using a circuit as a Callable
CircuitBreaker provides a circuit implemented as a java.util.concurrent.Callable.  Example follows.
```  
DefaultCircuitBreakerAlgorithm algorithm = new DefaultCircuitBreakerAlgorithm(10L, 2L); 

Circuit<String> circuit = new Circuit<String>(algorithm);  
MyCallable myCallable = new MyCallable();  
CallableCircuit<String> circuitCallable = new CallableCircuit<String>(circuit,
				myCallable);

String callResult = circuitCallable.call();
```  
