package jacop

import JaCoP.core.*
import JaCoP.constraints.*
import JaCoP.search.*
import groovy.transform.Field

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

// define logic solver data structures
num = 5
center = 2
first = 0
println "Solving Einstein's Riddle:"

@Field s = new Store()

def makeEnumVar(String st, Object[] arr) { new IntVar(s, st, 0, arr.size()-1) }
pets    = new IntVar[num]
colors  = new IntVar[num]
plays   = new IntVar[num]
drinks  = new IntVar[num]
nations = new IntVar[num]

(0..<num).each { i ->
     pets[i] = makeEnumVar("pet$i",    pets)
   colors[i] = makeEnumVar("color$i",  colors)
    plays[i] = makeEnumVar("plays$i",  plays)
   drinks[i] = makeEnumVar("drink$i",  drinks)
  nations[i] = makeEnumVar("nation$i", nations)
}

def pretty(enumClass, selected) { enumClass.values().find{ it.ordinal().toString() == selected.toString() } }

// define DSL (simplistic non-refactored version)
def neighbours(var1, val1, var2, val2) {
  s.impose and(
    ifOnlyIf(eq(var1[0], val1), eq(var2[1], val2)),
    implies(eq(var1[1], val1), or(eq(var2[0], val2), eq(var2[2], val2))),
    implies(eq(var1[2], val1), or(eq(var2[1], val2), eq(var2[3], val2))),
    implies(eq(var1[3], val1), or(eq(var2[2], val2), eq(var2[4], val2))),
    ifOnlyIf(eq(var1[4], val1), eq(var2[3], val2))
  )
}
iff = { e1, c1, e2, c2 -> s.impose and(*(0..<num).collect{ ifOnlyIf(eq(e1[it], c1), eq(e2[it], c2)) }) }
isEq = { a, b -> s.impose eq(a, b) }

dogs = dog; birds = bird; cats = cat; horses = horse
a = owner = house = the = abode = person = man = to = is = side = next = who = different = 'ignored'

// define the DSL in terms of DSL implementation
def the(Nationality n) {
  def ctx = [nations, n]
  [
    drinks:iff.curry(*ctx, drinks),
    plays:iff.curry(*ctx, plays),
    keeps:iff.curry(*ctx, pets),
    rears:iff.curry(*ctx, pets),
    owns:{ _the -> [first:{ house -> isEq(nations[first], n)}] },
    has:{ _a ->
      [pet:iff.curry(*ctx, pets)] + Color.values().collectEntries{ c ->
        [c.toString(), { _dummy -> iff(*ctx, colors, c) } ]
      }
    },
    lives: { _next -> [to: { _the ->
      Color.values().collectEntries{ c ->
        [c.toString(), { _dummy -> neighbours(*ctx, colors, c) } ]
      }
    }]}
  ]
}

def the(Color c1) {[
  house: { _is -> [on: { _the -> [left: { _side -> [of: { __the ->
    Color.values().collectEntries{ c2 -> [c2.toString(), { _dummy ->
        s.impose and(*(1..<num).collect{ ifOnlyIf(eq(colors[it-1], c1), eq(colors[it], c2)) })
    }]}
  }]}]}]}
]}

def the(String _dummy) {[
  of:{ _the ->
    Color.values().collectEntries{ c -> [c.toString(), { _house -> [
      drinks:iff.curry(colors, c, drinks),
      plays:iff.curry(colors, c, plays)
    ] } ] }
  },
  known: { _to -> [
    play: { sport ->
      def ctx = [plays, sport]
      [
        rears: iff.curry(*ctx, pets),
        keeps: iff.curry(*ctx, pets),
        drinks: iff.curry(*ctx, drinks),
        lives: { _next -> [to: { _the -> [one: { _who -> [
          keeps: { pet -> neighbours(pets, pet, *ctx) },
          drinks: { beverage -> neighbours(drinks, beverage, *ctx) }
        ]}]}]}
      ]
    },
    keep : { pet -> [
      lives: { _next -> [to: { _the -> [man: { _who -> [
        plays: { sport -> neighbours(pets, pet, plays, sport) }
      ]}]}]}
    ]}
  ]},
  from: { _the -> [center: { house ->
    [drinks: { d -> isEq(drinks[center], d)}]
  }]}
]}

def all(IntVar[] var) {
  [are: { _different -> s.impose new Alldifferent(var) } ]
}

// define rules
all pets are different
all colors are different
all plays are different
all drinks are different
all nations are different
the man from the center house drinks milk
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

// invoke logic solver
def vars = [pets, plays, drinks, colors, nations]
def search = new DepthFirstSearch()
def select = new SimpleMatrixSelect(vars as Var[][], new IndomainMin())
search.solutionListener.searchAll(true)
def result = search.labeling(s, select)
println "Solutions found: " + result
search.solutionListener.solutionsNo().times { i ->
  println "Solution ${i + 1}:"
  def sol = search.getSolution(i + 1)
  def ps = sol.take(5); sol = sol.drop(5)
  def ss = sol.take(5); sol = sol.drop(5)
  def ds = sol.take(5); sol = sol.drop(5)
  def cs = sol.take(5); sol = sol.drop(5)
  def ns = sol.take(5)
  5.times {
    printSol(ns[it], ps[it], ss[it], ds[it], cs[it])
  }
}

def printSol(n, p, s, d, c) {
  print   'The ' + pretty(Nationality, n)
  print   ' has a pet ' + pretty(Pet, p)
  print   ' plays ' + pretty(Sport, s)
  print   ' drinks ' + pretty(Drink, d)
  println ' and lives in a ' + pretty(Color, c) + ' house'
}

def eq(IntVar x, IntVar y) { new XeqY(x, y) }
def eq(IntVar x, enumVar) { new XeqC(x, enumVar.ordinal()) }
def or(PrimitiveConstraint c1, PrimitiveConstraint c2) { new Or(c1, c2) }
def and(PrimitiveConstraint[] cs) { new And(cs) }
def ifOnlyIf(PrimitiveConstraint c1, PrimitiveConstraint c2) { new Eq(c1, c2) }
def implies(PrimitiveConstraint c1, PrimitiveConstraint c2) { new IfThen(c1, c2) }
