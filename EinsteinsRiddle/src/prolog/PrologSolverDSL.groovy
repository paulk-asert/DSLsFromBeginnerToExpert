// inspired by http://www.baptiste-wicht.com/2010/09/solve-einsteins-riddle-using-prolog/
package prolog

@GrabResolver('http://dev.inf.unideb.hu:8090/archiva/repository/internal')
@Grab('org.prolog4j:prolog4j-api:0.2.0')
// uncomment one of next three
//@Grab('org.prolog4j:prolog4j-tuprolog:0.2.0')
//@Grab('org.prolog4j:prolog4j-jtrolog:0.2.0')
//@Grab('org.prolog4j:prolog4j-jlog:0.2.0')
// maybe needed with above
//@Grab('jlog:jlogic-debug:1.3.6')
import org.prolog4j.*

enum Pet { dog, cat, bird, fish, horse }
enum Color { green, white, red, blue, yellow }
enum Sport { baseball, volleyball, football, hockey, tennis }
enum Drink { water, tea, milk, coffee, beer }
enum Nationality { Norwegian, Dane, Briton, German, Swede }

import static Pet.*
import static Color.*
import static Sport.*
import static Drink.*
import static Nationality.*

dogs = dog; birds = bird; cats = cat; horses = horse
a = owner = house = the = abode = person = man = is = to = side = next = who = different = 'ignored'

p = ProverFactory.prover
hintNum = 1

p.addTheory('''
persons(0, []) :- !.
persons(N, [(_Men,_Color,_Drink,_Sport,_Animal)|T]) :- N1 is N-1, persons(N1,T).
person(1, [H|_], H) :- !.
person(N, [_|T], R) :- N1 is N-1, person(N1, T, R).
''')

def the(choco.Nationality n) {
  def ctx = [from:n]
  [
    drinks: { d -> addPairHint(ctx + [drink:d]) },
    plays: { s -> addPairHint(ctx + [play:s]) },
    keeps: { p -> addPairHint(ctx + [pet:p]) },
    rears: { p -> addPairHint(ctx + [pet:p]) },
    owns:{ _the -> [first:{ house -> addPositionHint(ctx, 1) }] },
    has:{ _a ->
      [pet: { a -> addPairHint(ctx + [pet:a]) }] +
        choco.Color.values().collectEntries{ c ->
        [c.toString(), { _dummy -> addPairHint(ctx + [color:c]) } ]
      }
    },
    lives: { _next -> [to: { _the ->
      choco.Color.values().collectEntries{ c ->
        [c.toString(), { _dummy -> addNeighbourHint(ctx, [color:c]) } ]
      }
    }]}
  ]
}

class HousePlaceHolder {
    def c1, script
    def house(_is) {
        [on: { _the -> [left: { _side -> [of: { __the ->
            choco.Color.values().collectEntries { c2 ->
                [c2.toString(), { _dummy ->
                    script.addToLeftHint([color: c1], [color: c2])
                }]
            }
        }]}]}]
    }
}

def the(choco.Color c1) {[
  house: { _is -> [on: { _the -> [left: { _side -> [of: { __the ->
    choco.Color.values().collectEntries{ c2 -> [c2.toString(), { _dummy ->
      addToLeftHint([color:c1], [color:c2])
    }]}
  }]}]}]}
]}

//def the(Color c1) { new HousePlaceHolder(c1:c1, script:this) }

def the(String _dummy) {[
  of:{ _the ->
    choco.Color.values().collectEntries{ c -> [c.toString(), { _house -> [
      drinks: { d -> addPairHint([color: c, drink: d])},
      plays: { s -> addPairHint([color: c, play: s])}
    ] } ] }
  },
  known: { _to -> [
          play: { s ->
      def ctx = [play: s]
      [
        rears: { a -> addPairHint(ctx + [pet:a])},
        keeps: { a -> addPairHint(ctx + [pet:a])},
        plays: { d -> addPairHint(ctx + [play:d])},
        lives: { _next -> [to: { _the -> [one: { _who -> [
          keeps: { a -> addNeighbourHint(ctx, [pet:a]) },
          drinks: { beverage -> addNeighbourHint(ctx, [drink:beverage]) }
        ]}]}]}
      ]
    },
    keep : { pet -> [
      lives: { _next -> [to: { _the -> [man: { _who -> [
        plays: { brand -> addNeighbourHint([pet:pet], [play:brand]) }
      ]}]}]}
    ]}
  ]},
  from: { _the -> [centre: { house ->
    [drinks: { d -> addPositionHint([drink:d], 3)}]
  }]}
]}

def addPairHint(Map m) {
    def from = m.from?.toString()?.toLowerCase()
    p.addTheory("""
    hint$hintNum([(${from ?: '_'},${m.color ?: '_'},${m.drink ?: '_'},${m.play ?: '_'},${m.pet ?: '_'})|_]).
    hint$hintNum([_|T]) :- hint$hintNum(T).
    """)
    hintNum++
}

def addPositionHint(Map m, int pos) {
    def from = m.from?.toString()?.toLowerCase()
    p.addTheory("""
    hint$hintNum(Persons) :- person($pos, Persons, (${from ?: '_'},${m.color ?: '_'},${m.drink ?: '_'},${m.play ?: '_'},${m.pet ?: '_'})).
    """)
    hintNum++
}

def addToLeftHint(Map left, Map right) {
    p.addTheory("""
    hint$hintNum([(_,$left.color,_,_,_),(_,$right.color,_,_,_)|_]).
    hint$hintNum([_|T]) :- hint$hintNum(T).
    """)
    hintNum++
}

def addNeighbourHint(Map m1, Map m2) {
    def from1 = m1.from?.toString()?.toLowerCase()
    def from2 = m2.from?.toString()?.toLowerCase()
    def term1 = "${from1 ?: '_'},${m1.color ?: '_'},${m1.drink ?: '_'},${m1.play ?: '_'},${m1.pet ?: '_'}"
    def term2 = "${from2 ?: '_'},${m2.color ?: '_'},${m2.drink ?: '_'},${m2.play ?: '_'},${m2.pet ?: '_'}"
    p.addTheory("""
    hint$hintNum([($term1),($term2)|_]).
    hint$hintNum([($term2),($term1)|_]).
    hint$hintNum([_|T]) :- hint$hintNum(T).
    """)
    hintNum++
}

the man from the centre house drinks milk
the Norwegian owns the first house
the Dane drinks tea
the German plays hockey
the Swede keeps dogs // alternate ending: has a pet dog
the Briton has a red house  // alternate ending: red abode
the owner of the green house drinks coffee
the owner of the yellow house plays baseball
the person known to play football rears birds // alternate ending: keeps birds
the man known to play tennis drinks beer
the green house is on the left side of the white house
the man known to play volleyball lives next to the one who keeps cats
the man known to keep horses lives next to the man who plays baseball
the man known to play volleyball lives next to the one who drinks water
the Norwegian lives next to the blue house

p.addTheory('''
% The question : Who owns the fish ?
question([(_,_,_,_,fish)|_]).
question([_|T]) :- question(T).
''')

p.addTheory('''solution(Persons) :- persons(5, Persons),''' +
(1..<hintNum).collect{ "  hint$it(Persons)," }.join('\n') + 'question(Persons).')

def sol = p.solve("solution(Persons).")
//println sol.dump()
//println sol.class.methods.join('\n')
//println sol.solution.get('Persons')//jlog to avoid converter
println sol.get('Persons') //jtrolog/tuProlog
