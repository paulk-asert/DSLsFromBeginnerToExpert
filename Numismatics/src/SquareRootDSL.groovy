// English
def the, root
def show(t) {
  [square: { r -> [of: { n -> println Math.sqrt(n) }]},
   cube_root_of: { n -> println Math.cbrt(n) }]
}

show the square root of 64
show the cube_root_of 64

// Spanish
def la, cuadrada = 'sqrt', cúbica = 'cbrt'
def muestra(t) {
  [raíz: { op -> [de: { n -> println Math."$op"(n) }]}]
}

muestra la raíz cuadrada de 64
muestra la raíz cúbica de 64

// Japanese
Object.metaClass.を = Object.metaClass.の = { c -> c(delegate) }
まず = 次に = { it }
表示する = { println it }
平方根 = { Math.sqrt(it) }
立方根 = { Math.cbrt(it) }

まず 64 の 平方根 を 表示する
次に 64 の 立方根 を 表示する

// Chinese
Integer.metaClass.propertyMissing = { op ->
  def d = delegate
  switch(op) {
    case '的平方根' -> println Math.sqrt(d)
    case '的立方根' -> println Math.cbrt(d)
  }
}
def '显示'(t) { t }

显示 64 的平方根
显示 64 的立方根
