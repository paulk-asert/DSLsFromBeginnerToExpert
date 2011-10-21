package v09

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

def robot = new Robot()
def binding = new Binding([
    robot: robot,
    move: robot.&move
])

def importCustomizer = new ImportCustomizer()
importCustomizer.addStaticStars Direction.class.name

def config = new CompilerConfiguration()
config.addCompilationCustomizers importCustomizer

def shell = new GroovyShell(this.class.classLoader, binding, config)
shell.evaluate '''
    move left
'''
