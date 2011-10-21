package v03

enum Direction {
    left, right, forward, backward
}

class Robot {
    void move(Direction dir) {
        println "robot moved $dir"
    }
}

def binding = new Binding([robot: new v02.Robot()])

def shell = new GroovyShell(this.class.classLoader, binding)
shell.evaluate '''
    import static v03.Direction.*
    import v03.Robot
    
    def robot = new Robot()
    
    robot.move left
'''

