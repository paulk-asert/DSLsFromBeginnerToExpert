package prolog

@GrabResolver('https://oss.sonatype.org/content/repositories/snapshots/')
@Grab('org.prolog4j:prolog4j-api:0.2.1-SNAPSHOT')
// uncomment one of next three
@Grab('org.prolog4j:prolog4j-tuprolog:0.2.1-SNAPSHOT')
//@Grab('org.prolog4j:prolog4j-jtrolog:0.2.1-SNAPSHOT')
//@Grab('org.prolog4j:prolog4j-jlog:0.2.1-SNAPSHOT')
// sometimes useful with above
//@Grab('jlog:jlogic-debug:1.3.6')
import org.prolog4j.ProverFactory

def p = ProverFactory.prover
p.addTheory(new File(/einstein.pl/).text)
def sol = p.solve("solution(Persons).")
//println sol.dump()
//println sol.class.methods.join('\n')
//println sol.solution.get('Persons')//jlog to avoid converter
println sol.get('Persons') //jtrolog/tuProlog
