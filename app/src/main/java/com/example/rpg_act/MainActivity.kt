package com.example.rpg_act

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var playerHealthTextView: TextView
    private lateinit var enemyHealthTextView: TextView
    private lateinit var playerImageView: ImageView
    private lateinit var enemyImageView: ImageView
    private lateinit var attackButton: Button
    private lateinit var defendButton: Button
    private lateinit var healButton: Button
    private lateinit var rollDiceButton: Button
    private lateinit var resetButton: Button
    private lateinit var gameStatusTextView: TextView
    private var playerHealth = 100
    private var enemyHealth = 100
    private var isPlayerDefending = false
    private var currentTurn: String? = null
    private var gameOver = false
    private var isFirstTurn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerHealthTextView = findViewById(R.id.playerHealth)
        enemyHealthTextView = findViewById(R.id.enemyHealth)
        playerImageView = findViewById(R.id.playerImageView)
        enemyImageView = findViewById(R.id.enemyImageView)
        attackButton = findViewById(R.id.attackButton)
        defendButton = findViewById(R.id.defendButton)
        healButton = findViewById(R.id.healButton)
        rollDiceButton = findViewById(R.id.rollDiceButton)
        resetButton = findViewById(R.id.resetButton)
        gameStatusTextView = findViewById(R.id.gameStatus)

        updateHealthViews()
        checkGameStatus()

        attackButton.setOnClickListener {
            playerTurn("Attack")
        }

        defendButton.setOnClickListener {
            playerTurn("Defend")
        }

        healButton.setOnClickListener {
            playerTurn("Heal")
        }

        resetButton.setOnClickListener {
            resetGame()
        }

        rollDiceButton.setOnClickListener {
            rollDice()
        }

        disableButtons()

        rollDiceToDetermineFirstTurn()
    }

    private fun disableButtonsExcept(rollDiceButton: Button?) {
        attackButton.isEnabled = false
        defendButton.isEnabled = false
        healButton.isEnabled = false
        resetButton.isEnabled = false
        rollDiceButton?.isEnabled = true
    }

    private fun rollDice() {

        val result = (1..6).random()
        val rollDiceButton = findViewById<TextView>(R.id.rollDiceButton)
        rollDiceButton.text = "You rolled: $result"
    }

    private fun rollDice(sides: Int): Int {
        return Random().nextInt(sides) + 1
    }

    private fun rollDiceToDetermineFirstTurn() {
        val diceResult = rollDice(2)
        if (diceResult == 1) {
            currentTurn = "Player"
        } else {
            currentTurn = "Enemy"
        }
        displayCurrentTurn()
        enableButtons()
        if (currentTurn == "Enemy") {
            enemyTurn()
        }
    }

    private fun displayCurrentTurn() {
        gameStatusTextView.text = "Current Turn: $currentTurn"
    }

    private fun playerTurn(action: String) {
        if (!gameOver && currentTurn == "Player") {
            disableButtons() // Disable buttons at the start of the player's turn
            displayPlayerAction("It's your turn.")
            when (action) {
                "Attack" -> {
                    displayPlayerAction("You use a normal attack.")
                    val damageDealt = rollDice(20) + 10
                    enemyHealth -= damageDealt
                    if (enemyHealth < 0) {
                        enemyHealth = 0
                    }
                }
                "Defend" -> {
                    isPlayerDefending = true
                    displayPlayerAction("You defend against the enemy's attack.")
                }
                "Heal" -> {
                    if (playerHealth < 100) {
                        val healingAmount = rollDice(15) + 10
                        playerHealth += healingAmount
                        displayPlayerAction("You heal for $healingAmount health.")
                    }
                }
            }
            updateHealthViews()
            checkGameStatus()
            currentTurn = "Enemy"
            displayCurrentTurn()
            enemyTurn()
        }
    }

    private fun enemyTurn() {
        if (!gameOver && currentTurn == "Enemy") {
            disableButtons()
            val action = rollDice(3)
            when (action) {
                1 -> {
                    val damageDealt = if (isPlayerDefending) {
                        rollDice(10) + 5
                    } else {
                        rollDice(15) + 10
                    }
                    playerHealth -= damageDealt
                    if (playerHealth < 0) {
                        playerHealth = 0
                    }
                    isPlayerDefending = false
                    displayEnemyAction("Enemy attacks for $damageDealt damage.")
                }
                2 -> {
                    displayEnemyAction("Enemy defends.")
                }
                3 -> {
                    val healing = rollDice(15) + 5
                    enemyHealth += healing
                    displayEnemyAction("Enemy heals for $healing health.")
                }
            }
            updateHealthViews()
            checkGameStatus()
            currentTurn = "Player"
            displayCurrentTurn()
            enableButtons()
        }
    }

    private fun updateHealthViews() {
        playerHealthTextView.text = "Player Health: ${this.playerHealth}"
        enemyHealthTextView.text = "Enemy Health: ${this.enemyHealth}"
    }

    private fun checkGameStatus() {
        if (playerHealth <= 0 || enemyHealth <= 0) {
            gameOver = true
            disableAllButtonsExceptReset()
            if (playerHealth <= 0) {
                "Game over. You lose.".also { gameStatusTextView.text = it }
            } else {
                "Congratulations! You win.".also { gameStatusTextView.text = it }
            }
        }
    }

    private fun disableButtons() {
        attackButton.isEnabled = false
        defendButton.isEnabled = false
        healButton.isEnabled = false
    }

    private fun enableButtons() {
        attackButton.isEnabled = true
        defendButton.isEnabled = true
        healButton.isEnabled = true
        rollDiceButton.isEnabled = true
        if (currentTurn == "Player") {
            attackButton.isEnabled = !gameOver
        }
    }

    private fun disableAllButtonsExceptReset() {
        attackButton.isEnabled = false
        defendButton.isEnabled = false
        healButton.isEnabled = false
        rollDiceButton.isEnabled = false
        resetButton.isEnabled = true
    }

    private fun displayEnemyAction(action: String) {
        this.gameStatusTextView.text = action
    }

    private fun displayPlayerAction(action: String) {
        this.gameStatusTextView.text = action
    }

    private fun resetGame() {
        gameOver = false
        playerHealth = 100
        enemyHealth = 100
        updateHealthViews()
        enableButtons()
        gameStatusTextView.text = ""
        currentTurn = "Player"
        displayCurrentTurn()


        val rollDiceButton = findViewById<TextView>(R.id.rollDiceButton)
        rollDiceButton.text = "Roll Dice"
    }

}