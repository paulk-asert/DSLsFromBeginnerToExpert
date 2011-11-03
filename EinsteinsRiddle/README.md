Einstein's Riddle
=================

Solves a logic puzzle using the following approaches:
 
* prolog directly (for comparative purposes)
* prolog underneath a Groovy DSL
* the __choco__ constraint solving library beneath a Groovy DSL
* the __jacop__ constraint solving library beneath a Groovy DSL

The interesting thing to note is that the "business rules" are the same in all cases. The "DSL helper code" would typically be hidden from the user.

The [prolog4j](https://github.com/espakm/prolog4j) generic prolog interface api is used along with the [tuprolog](http://tuprolog.alice.unibo.it/) prolog engine but
you can try some of the other engines supported by prolog4j if you wish.

The [choco](http://www.emn.fr/z-info/choco-solver/) and [JaCoP](http://jacop.osolpro.com/) constraint solving libraries are used.
These libraries offer similar features as far as this problem is concerned. Given that the JaCoP package isn't available in a public
Maven repository and has a restrictive GPL license, we have a preference for Choco for this example; but see the respective
documentation of the two packages to see which better suits your needs.

To install the library needed for JaCoP you will need to run the following command (windows command shown):

    ..\gradlew downloadJaCoP
