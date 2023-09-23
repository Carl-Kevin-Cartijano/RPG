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
    private lateinit var resetButton: Button
    private lateinit var rollDiceButton: Button
    private lateinit var gameStatusTextView: TextView
    private var playerHealth = 100
    private var enemyHealth = 100
    private var isPlayerDefending = false
    private var currentTurn: String? = null
    private var gameOver = false

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
        resetButton = findViewById(R.id.resetButton)
        rollDiceButton = findViewById(R.id.rollDiceButton)
        gameStatusTextView = findViewById(R.id.gameStatus)

        updateHealthViews()
        checkGameStatus()

        attackButton.setOnClickListener {
            playerTurn()
        }

        defendButton.setOnClickListener {
            playerDefend()
        }

        healButton.setOnClickListener {
            playerHeal()
        }

        resetButton.setOnClickListener {
            resetGame()
        }

        rollDiceButton.setOnClickListener {
            rollDiceToDetermineFirstTurn()
        }

        // Disable buttons initially
        disableButtons()

        // Roll the dice to determine the first turn when the app starts.
        rollDiceToDetermineFirstTurn()
    }

    private fun rollDice(sides: Int): Int {
        return Random().nextInt(sides)
    }

    private fun rollDiceToDetermineFirstTurn() {
        val diceResult = rollDice(2) // Roll a 2-sided dice (0 or 1)
        if (diceResult == 0) {
            currentTurn = "Player"
        } else {
            currentTurn = "Enemy"
        }
        displayCurrentTurn()
        enableButtons() // Enable buttons after rolling the dice
    }

    private fun displayCurrentTurn() {
        gameStatusTextView.text = "Current Turn: $currentTurn"
    }

    private fun playerTurn() {
        // Check if it's the player's turn before allowing actions.
        if (!gameOver && currentTurn == "Player") {
            disableButtons() // Disable buttons at the start of the player's turn
            displayPlayerAction("It's your turn.")
            displayPlayerAction("You use a normal attack.")
            val damageDealt = rollDice(20) + 10 // Roll a 20-sided dice and add 10 for damage
            enemyHealth -= damageDealt
            if (enemyHealth < 0) {
                enemyHealth = 0
            }
            updateHealthViews()
            checkGameStatus()
            currentTurn = "Enemy" // Switch to the enemy's turn
            displayCurrentTurn()
            enemyTurn()
        }
    }

    private fun playerAttack() {
        val damageDealt = rollDice(20) + 10 // Roll a 20-sided dice and add 10 for damage
        enemyHealth -= damageDealt
        if (enemyHealth < 0) {
            enemyHealth = 0
        }
        updateHealthViews()
        checkGameStatus()
        displayPlayerAction("You attack for $damageDealt damage.")
    }

    private fun playerDefend() {
        isPlayerDefending = true
        displayPlayerAction("You defend against the enemy's attack.")
        currentTurn = "Enemy" // Switch to the enemy's turn
        displayCurrentTurn()
        enemyTurn()
    }

    private fun playerHeal() {
        if (!gameOver && playerHealth < 100) { // Limit healing to not exceed maximum health.
            val healingAmount = rollDice(15) + 10 // Roll a 15-sided dice and add 10 for healing
            playerHealth += healingAmount
            displayPlayerAction("You heal for $healingAmount health.")
            currentTurn = "Enemy" // Switch to the enemy's turn
            displayCurrentTurn()
            enemyTurn()
        }
    }

    private fun enemyTurn() {
        // Check if it's the enemy's turn before allowing actions.
        if (!gameOver && currentTurn == "Enemy") {
            disableButtons() // Disable buttons at the start of the enemy's turn
            val action = rollDice(3) // Roll a 3-sided dice (0: Attack, 1: Defend, 2: Heal)
            if (action == 0) {
                val damageDealt = if (isPlayerDefending) {
                    rollDice(10) + 5 // Reduced damage if player is defending
                } else {
                    rollDice(15) + 10 // Roll a 15-sided dice and add 10 for damage
                }
                playerHealth -= damageDealt
                if (playerHealth < 0) {
                    playerHealth = 0
                }
                isPlayerDefending = false // Reset defense state after the enemy's turn
                updateHealthViews()
                checkGameStatus()
                currentTurn = "Player" // Switch to the player's turn
                displayCurrentTurn()
                displayEnemyAction("Enemy attacks for $damageDealt damage.")
                enableButtons() // Enable buttons after rolling the dice
            } else if (action == 1) {
                displayEnemyAction("Enemy defends.")
                currentTurn = "Player" // Switch to the player's turn
                displayCurrentTurn()
                enableButtons() // Enable buttons after rolling the dice
            } else {
                val healing = rollDice(15) + 5 // Roll a 15-sided dice and add 5 for healing
                enemyHealth += healing
                updateHealthViews()
                displayCurrentTurn()
                displayEnemyAction("Enemy heals for $healing health.")
                currentTurn = "Player" // Switch to the player's turn
                enableButtons() // Enable buttons after rolling the dice
            }
        }
    }

    private fun updateHealthViews() {
        playerHealthTextView.text = "Player Health: ${this.playerHealth}"
        enemyHealthTextView.text = "Enemy Health: ${this.enemyHealth}"
    }

    private fun checkGameStatus() {
        if (playerHealth <= 0 || enemyHealth <= 0) {
            gameOver = true // Set the game over flag
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
    }

    private fun disableAllButtonsExceptReset() {
        attackButton.isEnabled = false
        defendButton.isEnabled = false
        healButton.isEnabled = false
        rollDiceButton.isEnabled = false
        resetButton.isEnabled = true // Enable only the reset button
    }

    private fun displayEnemyAction(action: String) {
        this.gameStatusTextView.text = action
    }

    private fun displayPlayerAction(action: String) {
        this.gameStatusTextView.text = action
    }

    private fun resetGame() {
        gameOver = false // Reset the game over flag
        playerHealth = 100
        enemyHealth = 100
        updateHealthViews()
        enableButtons()
        gameStatusTextView.text = ""
        currentTurn = "Player" // Set the initial turn to player
        displayCurrentTurn()
    }
}
