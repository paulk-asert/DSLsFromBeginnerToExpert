package one

println new Order()
    .sell(150, "IBM")
    .limitPrice(300)
    .allOrNone(true)
    .valueAs{ qty, unitPrice -> qty * unitPrice - 100 }

println new Order()
    .buy(200, "GOOG")
    .limitPrice(200)
    .allOrNone(true)
    .valueAs{ qty, unitPrice -> qty * unitPrice - 500 }

// ----- Implementation of the Fluent API ---------------

enum Action { Buy, Sell }

class Order {
    def security
    def quantity, limitPrice
    boolean allOrNone
    def valueCalculation
    Action action

    def buy(Integer quantity, String security) {
        this.quantity = quantity
        this.security = security
        this.action = Action.Buy
        return this
    }

    def sell(Integer quantity, String security) {
        this.quantity = quantity
        this.security = security
        this.action = Action.Sell
        return this
    }

    def limitPrice(Integer limit) {
        this.limitPrice = limit
        return this
    }

    def allOrNone(boolean allOrNone) {
        this.allOrNone = allOrNone
        return this
    }

    def valueAs(Closure valueCalculation) {
        this.valueCalculation = valueCalculation
        return this
    }

    String toString() {
        "$action $quantity shares of $security at valuation of ${valueCalculation(quantity, limitPrice)}"
    }
}
