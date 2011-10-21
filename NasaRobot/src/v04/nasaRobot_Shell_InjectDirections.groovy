package v04

enum Direction {
    left, right, forward, backward
}

class Robot {
    void move(Direction dir) {
        println "robot moved $dir"
    }
}
/*
def binding = new Binding([
    robot: new Robot(),
    left: Direction.left,
    right: Direction.right,
    forward: Direction.forward,
    backward: Direction.backward
])
*/
def binding = new Binding([
    robot: new Robot(),
    *:v04.Direction.values().collectEntries { [(it.name()): it] }
])

def shell = new GroovyShell(this.class.classLoader, binding)
shell.evaluate '''
    robot.move left
'''