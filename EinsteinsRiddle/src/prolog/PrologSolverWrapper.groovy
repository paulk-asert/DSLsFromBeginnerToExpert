package prolog

//@GrabResolver('http://dev.inf.unideb.hu:8090/archiva/repository/internal')
//@Grab('jlog:jlogic-debug:1.3.6')
//@Grab('org.prolog4j:prolog4j-api:0.2.0')
//one of next three
//@Grab('org.prolog4j:prolog4j-jlog:0.2.0')
//@Grab('org.prolog4j:prolog4j-tuprolog:0.2.0')
//@Grab('org.prolog4j:prolog4j-jtrolog:0.2.0')
//import org.prolog4j.*

//@Grab('it.unibo.alice.tuprolog:tuprolog:2.1.1')
//import alice.tuprolog.*

def p = ProverFactory.prover
p.addTheory(new File(/einstein.pl/).text)
def sol = p.solve("solution(Persons).")
//println sol.dump()
//println sol.class.methods.join('\n')
//println sol.solution.get('Persons')//jlog to avoid converter
println sol.get('Persons') //jtrolog/tuProlog
