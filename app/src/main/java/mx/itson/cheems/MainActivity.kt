package mx.itson.cheems

    import android.content.Context
    import android.os.Build
    import android.os.Bundle
    import android.os.VibrationEffect
    import android.os.Vibrator
    import android.os.VibratorManager
    import android.util.Log
    import android.view.View
    import android.widget.ImageButton
    import android.widget.Toast
    import android.widget.Toast.makeText
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat

    class MainActivity : AppCompatActivity(), View.OnClickListener {

        var gameOverCard = 0
        var aciertos = 0 // Aquí guardaremos cuántas cartas buenas llevas

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
                val btnReiniciar = findViewById<View>(R.id.btn_reiniciar)
                btnReiniciar.setOnClickListener {
                    start()
                }

                start()
            }

            fun start() {
                aciertos = 0

                for (i in 1..9) {
                    val resID = resources.getIdentifier("card$i", "id", packageName)
                    val btnCard = findViewById<ImageButton>(resID)
                    btnCard.setOnClickListener(this)
                    btnCard.setBackgroundResource(R.drawable.cheems_question)
                    // Habilitamos el botón de nuevo (porque los deshabilitamos al jugar)
                    btnCard.isEnabled = true
                }

                gameOverCard = (1..9).random()
                Log.d("El valor de la carta", "La carta perdedora es $gameOverCard")
            }

            fun fli(cart: Int) {
                // checamos si la carta que tocaste es la perdedora
                if (cart == gameOverCard) {

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                        val vibratorAdmin = applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        val vibrator = vibratorAdmin.defaultVibrator
                        vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
                        Log.d("VIBRACION", "Vibrando con VibratorManager (Android 12+)") // <-- Agrega esto
                    } else {
                        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(1500)
                        Log.d("VIBRACION", "Vibrando con Vibrator clásico (Android 11 o menor)") // <-- Agrega esto
                    }
                                makeText(this, "Perdiste", Toast.LENGTH_LONG).show()

                    // Ciclo para destapar todas las cartas si pierdes
                    for (i in 1..9) {
                        val btn = findViewById<ImageButton>(
                            resources.getIdentifier("card$i", "id", this.packageName)
                        )
                        if (i == cart) {
                            btn.setBackgroundResource(R.drawable.cheems_bad)
                        } else {
                            btn.setBackgroundResource(R.drawable.cheems_ok)
                        }
                        // Se bloquean todas las cartas para que no se siga jugando
                        btn.isEnabled = false
                    }

                } else {
                    // Si NO es la perdedora (es un acierto)
                    val btnCard = findViewById<ImageButton>(
                        resources.getIdentifier("card$cart", "id", packageName)
                    )

                    btnCard.setBackgroundResource(R.drawable.cheems_ok)
                    btnCard.isEnabled = false // Bloqueamos la que acabas de tocar

                    aciertos++ // Sumamos al contador

                    // Verificamos si ya ganaste (8 aciertos)
                    if (aciertos == 8) {
                        makeText(this, "¡Ganaste! Has esquivado a Cheems", Toast.LENGTH_LONG).show()

                        // Destapamos la carta mala que sobró
                        val idBad = resources.getIdentifier("card$gameOverCard", "id", packageName)
                        val btnBad = findViewById<ImageButton>(idBad)
                        btnBad.setBackgroundResource(R.drawable.cheems_bad)

                        // Se bloquea la carta para que no sea seleccionable
                        btnBad.isEnabled = false
                    }
                }
            }

            override fun onClick(v: View) {
                when (v.id) {
                    R.id.card1 -> fli(1)
                    R.id.card2 -> fli(2)
                    R.id.card3 -> fli(3)
                    R.id.card4 -> fli(4)
                    R.id.card5 -> fli(5)
                    R.id.card6 -> fli(6)
                    R.id.card7 -> fli(7)
                    R.id.card8 -> fli(8)
                    R.id.card9 -> fli(9)
                }
            }
        }