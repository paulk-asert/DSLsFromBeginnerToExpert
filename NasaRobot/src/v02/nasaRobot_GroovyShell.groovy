package v02

enum Direction {
    left, right, forward, backward
}

class Robot {
    void move(Direction dir) {
        println "robot moved $dir"
    }
}

def shell = new GroovyShell(this.class.classLoader)
shell.evaluate '''
    import v02.Robot
    import static v02.Direction.*
    
    def robot = new Robot()
    
    robot.move left
'''

