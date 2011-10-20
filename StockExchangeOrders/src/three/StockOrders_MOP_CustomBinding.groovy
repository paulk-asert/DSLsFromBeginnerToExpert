package three

class Order {
    def security
    def quantity
    def limitPrice
    def allOrNone
    def valueCalculation
    def bs

    def buy(securityQuantity, closure) {
        bs = 'Bought'
        buySell(securityQuantity, closure)
    }

    def sell(securityQuantity, closure) {
        bs = 'Sold'
        buySell(securityQuantity, closure)
    }

    private buySell(securityQuantity, closure) {
        // multiple assignment
        (security, quantity) = [securityQuantity.security, securityQuantity.quantity]
        // better clone the closure to avoid multi-threading access issues
        def c = closure.clone()
        // delegate the method calls inside the closure to our methodMissing
        c.delegationStrategy = Closure.DELEGATE_ONLY
        c.delegate = this
        valueCalculation = c()
        // debug print of the resulting order
        println toString()
        return this
    }

    // methods inside the closure will assign the Order properties
    def methodMissing(String name, args) {
        this."$name" = args[0]
    }

    def getTo() { this }

    def valueAs(Closure wrapee) {
        // in order to be able to define closures like { qty * unitPrice }
        // without having to explicitly pass the parameters to the closure
        // we can wrap the closure inside another one
        // and that closure sets a delegate to the qty and unitPrice variables
        def wrapped = { qty, unitPrice ->
            def cloned = wrapee.clone()
            cloned.resolveStrategy = Closure.DELEGATE_ONLY
            cloned.delegate = [qty: qty, unitPrice: unitPrice]
            cloned()
        }
        return wrapped
    }

    String toString() {
        "$bs $quantity shares of $security.name and valuation of ${valueCalculation(quantity, limitPrice)}"
    }
}

class Security {
    String name
}

class Quantity {
    Security security
    Integer quantity
}

Integer.metaClass.getShares = { -> delegate }
Integer.metaClass.of = { new Quantity(security: it, quantity: delegate) }

class CustomBinding extends Binding {
    def getVariable(String symbol) {
        // create a new order each time
        // for when you pass several orders
        if (symbol == "newOrder")
            new Order()
        // otherwise, it's an instrument
        // trick to avoid using strings: use IBM instead of 'IBM'
        else
            new Security(name: symbol)
    }
}

// use the script binding for retrieving IBM, etc.
binding = new CustomBinding()

newOrder.to.sell(150.shares.of(IBM)) {
    limitPrice   300
    allOrNone    true
    valueAs      { qty * unitPrice - 600 }
}

newOrder.to.buy 500.shares.of(GOOG), {
    limitPrice   200
    allOrNone    false
    valueAs      { qty * unitPrice - 100 }
}
