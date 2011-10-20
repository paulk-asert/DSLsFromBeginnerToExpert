import static Coin.*

class CoinValues {
    static get(Integer self, String name) {
        self * Coin."${singular(name)}".value
    }
    static singular(String val) {
        val.endsWith('ies') ? val[0..-4] + 'y' :
            val.endsWith('s') ? val[0..-2] : val
    }
}

use (CoinValues) {
    assert 2.quarters + 1.nickel + 2.pennies == 57
}
