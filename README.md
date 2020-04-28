# Code Controller
  
   
Code Controller (aka CC) is an immediate plug & play GUI library that is mainly aimed for the development of creative and visual programs. The main focus of the project are ease of use and overhead zeroing while trying to (1) provide powerful tools to debug and interact with a live program, (2) minimizing software iterations and gui controls setup time, (3) be portable between different platforms and frameworks.

The project is still in development and will include additional features in the future. The core functionality will probably remain, although some major design changes and few bug fixes are still required.
Any direct or indirect support and contribution will be appreciated. 

An example of a fully equipped control panel. 


**Core features overview:**
1. The controllers:
   1. Slider (ccDouble)
   1. Boolean toggle (ccBool)
   1. XY Controller (ccVec2)
1. Information pane - displays a dynamically updated strings (ccInfo). 
1. Real time plotter (ccPlot).
1. Local configuration - Save/Load values to/from a automatically created config file. Controllers are initialized with the previously saved value on startup. 
1. Remove and Refactor - after you finish to use a control normally you would go back to the source code to remove the gui elelemnts declarations manually. Instead of doing so this feature will remove a controller from the gui panel and will replace the source code declaration of that controller automatically with the current controller value. Use with care.


Watch a 2 minutes show case video that explains most of what you need to know about CC:



## Installation via gradle:

**Using gradle with kotlin script (build.gradle.kts file):**
```
repositories {
	// …
  jcenter()	// <- needed due to internal dependencies
  maven(url = "https://dl.bintray.com/yokiano/my-tools")
}

dependencies {
	// ...
  implementation("yokiano","code-controller", "0.0.1")
}
```
 
**Using gradle with groovy script (build.gradle file):**
```
repositories {
	// …
  jcenter()	// <- needed due to internal dependencies
  maven url = ‘https://dl.bintray.com/yokiano/my-tools’
}

dependencies {
	// ...
  implementation ‘yokiano:code-controller:0.0.1’
}
```  


# Basic Usage:
```
import yokiano.codecontroller.domain.*

// EXAMPLE 1 - not in a class context
fun main() {
  val someDoubleValue = CodeController.ccDouble("my slider") // will add a slider controller with “my slider” as id and default range of 0 to 1.
}

// EXAMPLE 2 - inside a class.
class MyClass : CCAware {
	
	fun someFunction() {
		val someDoubleValue = ccDouble("my slider") // will add a slider controller with “my slider” as id and default range of 0 to 1.

		val xyPairValue = ccVec2("my 2D vector") // will add a 2D vector controller with “my 2D vector” as id and default range of 0 to 1 (both in x and y).
	}
}
```  

There are two ways of invoking code controller functions, (1) referring to the global object CodeController as done in EXAMPLE 1 above, (2) extending CCAware class and using the controller methods directly as done in EXAMPLE 2 above. 
  
The above examples present **only** the usage of a single and default slider controller, for other controller types and the full documentation refer to the [wiki page](https://github.com/yokiano/code_controller/wiki).  



