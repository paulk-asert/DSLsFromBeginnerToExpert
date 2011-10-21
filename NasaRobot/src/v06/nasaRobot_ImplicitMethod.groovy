package v06

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
importCustomizer.addStaticStars 'v06.Direction'

def config = new CompilerConfiguration()
config.addCompilationCustomizers importCustomizer

def shell = new GroovyShell(this.class.classLoader, binding, config)
shell.evaluate '''
    move left
''' + '''
    def move(dir) {
        robot.move dir
    }
'''