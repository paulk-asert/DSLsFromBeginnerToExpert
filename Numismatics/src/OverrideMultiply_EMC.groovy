import static Coin.*

Integer.metaClass.multiply = { Coin c -> delegate * c.value }
assert 4 * quarter + 4 * nickel + 3 * penny == 123
