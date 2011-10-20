Einstein's Riddle
=================

Solves a logic puzzle using the following approaches:
 
* prolog directly
* prolog underneath a Groovy DSL
* a constraint solving library beneath a Groovy DSL

The [prolog4j](https://github.com/espakm/prolog4j) generic prolog interface api is used along with the [tuprolog](http://tuprolog.alice.unibo.it/) prolog engine but
you can try some of the other engines supported by prolog4j if you wish.

The [choco](http://www.emn.fr/z-info/choco-solver/) constraint solving library is used.