# Project was developed as part of a mobile game framework
# Shows some of the interesting things that can be done with embedded scripting

1) Developed to be used on Android but can be used on any VM
2) Comparable frameworks for the Java platform what work across platforms
	a) LUA (JLua)
	b) Rhino (Javascript)
	c) Groid (Groovy)
	
3) Features of the language
   a) Compliant with JSR233 for integration in to existing frameworks
   b) Creates proxies which make it accessible from within the host application
   c) Runs on any Java platform variant including SDK, Dalvik, ART, and GAE

4) Debugger can be used as a service and contains
	- Step In/Out/Over
	- Evaluations
	- Heap Walking
	- Fault Capture
	
- Introduction to the language and some examples
- Example using it with embedded camunda
- Example bootstrapping a game in to android
- Example debugging an actual device
- Example using evaulations
- Dependency injection language and extending it
- Internals of the framework?


A) Introduction 
	- What problem is it solving
	- Alternatives that exist
	- Standards compliant

B) Overview of tools
	- Debugger (Assist in development, Syntax Check, Debugging, Heap Walking, Evaluations)
	- Example applications

c) Example source
	- Keywords
	- Types
	- Functions

D) Example Android apps
	- Poor tools that exist on IntelliJ/Eclipse
	- Bootstrap an application
	- Debug an application

1) Leverage some of the existing tools on the Java platform such as Proxies and Reflection
2) Compliant with JSR233 for integration in to existing frameworks
3) Runs on any Java platform variant including SDK, Dalvik, ART, and GAE
4) Its embeddable in to any existing Java application for evaluations
5) Creates proxies which make it accessible from within the host application
6) Comparable frameworks for the Java platform what work across platforms
	- LUA (JLua)
	- Rhino (Javascript)
	- Groid (Groovy)
7) Most offer poor compile performance when used as scripts, no tools
8) Can be used to bootstap functionality in to existing host applications
9) Tools can perform the following 
	- Step In/Out/Over
	- Evaluations
	- Heap Walking
	- Fault Capture
10) Source is organised in to class files and package files
	- <package>.snap
	- <package>/<Class>.snap
