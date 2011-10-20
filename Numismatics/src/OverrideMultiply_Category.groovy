import static Coin.*

class CoinMath {
    static multiply(Integer self, Coin c) {
        self * c.value
    }
}

use (CoinMath) {
    assert 4 * quarter + 4 * nickel + 3 * penny == 123
}
