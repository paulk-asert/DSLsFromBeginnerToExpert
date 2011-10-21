package v11

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.*

import groovy.transform.TupleConstructor

enum Direction {
    left, right, forward, backward
}

enum Unit {
    centimeter ('cm', 0.01),
    meter      ( 'm',    1),
    kilometer  ('km', 1000)    

    String abbreviation
    double multiplier
    
    Unit(String abbr, double mult) {
        this.abbreviation = abbr
        this.multiplier = mult
    }
    
    String toString() { abbreviation }
}

enum Duration {
    hour
}

@TupleConstructor
class Distance {
    double amount
    Unit unit
    
    Speed div(Duration dur) {
        new Speed(amount, unit)
    }
    
    String toString() { "$amount$unit" }
}

@TupleConstructor
class Speed {
    double amount
    Unit unit
    
    String toString() { "$amount $unit/h" }
}

class DistanceCategory {
    static Distance getCentimeters(Number num) {
        new Distance(num, Unit.centimeter)
    }

    static Distance getMeters(Number num) {
        new Distance(num, Unit.meter)
    }

    static Distance getKilometers(Number num) {
        new Distance(num, Unit.kilometer)
    }
    
    static Distance getCm(Number num) { getCentimeters(num) }
    static Distance getM(Number num)  { getMeters(num) }
    static Distance getKm(Number num) { getKilometers(num) }
}

class Robot {
    def move(Direction dir) {
        [by: { Distance dist -> 
            [at: { Speed s ->
                println "robot moved $dir by $dist at $s"
            }]
        }]
    }
    
    void move(Direction dir, Distance d) {
        println "robot moved $dir by $d"
    }

    void move(Map m, Direction dir) {
        println "robot moved $dir by $m.by at ${m.at ?: '1 km/h'}"
    }
    
    def deploy(Direction dir) {
        [arm: {-> println "deploy $dir arm" }()]
    }
}

def binding = new Binding([
    robot: new Robot(),
    h: Duration.hour
])

def importCustomizer = new ImportCustomizer()
importCustomizer.addStaticStars Direction.class.name

def config = new CompilerConfiguration()
config.addCompilationCustomizers importCustomizer
config.scriptBaseClass = RobotBaseScriptClass.class.name

def shell = new GroovyShell(this.class.classLoader, binding, config)
use(DistanceCategory) {
shell.evaluate '''
    move left
    move right, 3.meters
    
    move right, by: 3.meters
    move right, by: 3.meters, at: 5.km/h
    
    move right by 3.meters at 5.km/h   
    
    deploy left arm 
'''
}

abstract class RobotBaseScriptClass extends Script {
    @Delegate @Lazy Robot robot = this.binding.robot
}
