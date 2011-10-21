package v08

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.*

enum Direction {
    left, right, forward, backward
}

class Distance {}

class Robot {
    void move(Direction dir) {
        println "robot moved $dir"
    }
    
    void move(Direction dir, Distance d) {
    }
}

def binding = new CustomBinding([robot: new Robot()])

def importCustomizer = new ImportCustomizer()
importCustomizer.addStaticStars Direction.class.name

def config = new CompilerConfiguration()
config.addCompilationCustomizers importCustomizer
config.scriptBaseClass = RobotBaseScriptClass.class.name

def shell = new GroovyShell(this.class.classLoader, binding, config)
shell.evaluate '''
    mOvE lefT
'''

abstract class RobotBaseScriptClass extends Script {
    @Delegate @Lazy Robot robot = this.binding.robot
    
    def invokeMethod(String name, args) {
        robot."${name.toLowerCase()}"(*args)
    }
}

class CustomBinding extends Binding {
    private Map variables
    
    CustomBinding(Map vars) {
        this.variables = [
            *:vars, 
            *:Direction.values().collectEntries { [(it.name()): it] }
        ]
    }
    
    def getVariable(String name) {
        variables[name.toLowerCase()]
    }
}