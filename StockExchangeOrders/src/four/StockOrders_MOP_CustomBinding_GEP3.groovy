package four
// ---- A bit of script initialization ----------------------------

// use the script binding for silent sentence words like "to", "the"
binding = new CustomBinding()
// syntax for 200.shares
Integer.metaClass.getShares = { -> delegate }

// ---- Stock exchange orders DSL ---------------------------------

order to sell 150.shares of IBM {
    limitPrice       300
    allOrNone        false
    at the value of  { qty * unitPrice - 600 }
}

order to buy 500.shares of GOOG {
    limitPrice       200
    allOrNone        true
    at the value of  { qty * unitPrice - 100 }
}

// ----- Implementation of the DSL --------------------------------

enum Action { Buy, Sell }

class Order {
    Security security
    Integer quantity, limitPrice
    boolean allOrNone
    Closure valueCalculation
    Action action

    def buy(Integer quantity) {
        this.quantity = quantity
        this.action = Action.Buy
        return this
    }

    def sell(Integer quantity) {
        this.quantity = quantity
        this.action = Action.Sell
        return this
    }

    def limitPrice(Integer limit) {
        this.limitPrice = limit
    }

    def allOrNone(boolean allOrNone) {
        this.allOrNone = allOrNone
    }

    def at(Closure characteristicsClosure) {
        return this
    }

    def value(Closure valueCalculation) {
        this.valueCalculation = valueCalculation
    }

    // Characteristics of the order:
    // "of GOOG {...}"
    def of(SecurityAndCharacteristics secAndCharact) {
        security = secAndCharact.security
        def c = secAndCharact.characteristics.clone()
        c.delegationStrategy = Closure.DELEGATE_ONLY
        c.delegate = this
        c()
        // debug print of the resulting order
        println toString()
        return this
    }

    // Valuation closure:
    // "of { qty, unitPrice -> ... }"
    def of(Closure valueCalculation) {
        // in order to be able to define closures like { qty * unitPrice }
        // without having to explicitly pass the parameters to the closure
        // we can wrap the closure inside another one
        // and that closure sets a delegate to the qty and unitPrice variables
        def wrapped = { qty, unitPrice ->
            def cloned = valueCalculation.clone()
            cloned.resolveStrategy = Closure.DELEGATE_ONLY
            cloned.delegate = [qty: qty, unitPrice: unitPrice]
            cloned()
        }
        return wrapped
    }

    String toString() {
        "$action $quantity shares of $security.name with valuation of ${valueCalculation(quantity, limitPrice)}"
    }
}

class Security {
    String name
}

class SecurityAndCharacteristics {
    Security security
    Closure characteristics
}

class CustomBinding extends Binding {
    def getVariable(String word) {
        // return System.out when the script requests to write to 'out'
        if (word == "out") System.out

        // don't thrown an exception and return null
        // when a silent sentence word is used,
        // like "to" and "the" in our DSL
        null
    }
}

// Script helper method for "GOOG {}", "VMW {}", etc.
def methodMissing(String name, args) {
    new SecurityAndCharacteristics(
        security: new Security(name: name),
        characteristics: args[0]
    )
}

// Script helper method to make "order to" silent
// by just creating our current order
def order(to) { new Order() }


