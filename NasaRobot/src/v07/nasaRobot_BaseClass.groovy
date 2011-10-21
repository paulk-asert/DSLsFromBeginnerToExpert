package v07

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.*

enum Direction {
    left, right, forward, backward
}

class Robot {
    void move(Direction dir) {
        println "robot moved $dir"
    }
}

def binding = new Binding([robot: new Robot()])

def importCustomizer = new ImportCustomizer()
importCustomizer.addStaticStars Direction.class.name

def config = new CompilerConfiguration()
config.addCompilationCustomizers importCustomizer
config.scriptBaseClass = RobotBaseScriptClass.class.name

def shell = new GroovyShell(this.class.classLoader, binding, config)
shell.evaluate '''
    move left
'''

abstract class RobotBaseScriptClass extends Script {
    void move(Direction dir) {
        this.binding.robot.move dir
    }
}