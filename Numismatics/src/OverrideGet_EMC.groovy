import static Coin.*

Integer.metaClass.getProperty = { String name ->
    def mp = Integer.metaClass.getMetaProperty(name)
    if (mp) return mp.getProperty(delegate)
    def singular = name.endsWith('ies') ? name[0..-4] + 'y' :
            name.endsWith('s') ? name[0..-2] : name
    delegate * Coin."$singular".value
}

assert 2.quarters + 1.nickel + 2.pennies == 57
