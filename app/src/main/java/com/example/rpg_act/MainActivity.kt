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
    private var gameOver = false // Flag to track game over
    private var isFirstTurn = true // Flag to track the first turn

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
            if (isFirstTurn) {
                isFirstTurn = false
                disableButtonsExcept(rollDiceButton)
                rollDiceToDetermineFirstTurn()
            }
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

    private fun rollDice(sides: Int): Int {
        return Random().nextInt(sides) + 1
    }

    private fun rollDiceToDetermineFirstTurn() {
        val diceResult = rollDice(2)
        if (diceResult == 1) {
            currentTurn = "Player"
        }
        else {
            currentTurn = "Enemy"
        }
        displayCurrentTurn()
        enableButtons()
    }

    private fun displayCurrentTurn() {
        gameStatusTextView.text = "Current Turn: $currentTurn"
    }

    private fun playerTurn() {

        if (!gameOver && currentTurn == "Player") {
            disableButtons() // Disable buttons at the start of the player's turn
            displayPlayerAction("It's your turn.")
            displayPlayerAction("You use a normal attack.")
            val damageDealt = rollDice(20) + 10
            enemyHealth -= damageDealt
            if (enemyHealth < 0) {
                enemyHealth = 0
            }
            updateHealthViews()
            checkGameStatus()
            currentTurn = "Enemy"
            displayCurrentTurn()
            enemyTurn()
        }
    }

    private fun playerDefend() {
        isPlayerDefending = true
        displayPlayerAction("You defend against the enemy's attack.")
        currentTurn = "Enemy"
        displayCurrentTurn()
        enemyTurn()
    }

    private fun playerHeal() {
        if (!gameOver && playerHealth < 100) {
            val healingAmount = rollDice(15) + 10
            playerHealth += healingAmount
            displayPlayerAction("You heal for $healingAmount health.")
            currentTurn = "Enemy"
            displayCurrentTurn()
            enemyTurn()
        }
    }

    private fun enemyTurn() {

        if (!gameOver && currentTurn == "Enemy") {
            disableButtons()
            val action = rollDice(3)
            if (action == 1) {
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
                updateHealthViews()
                checkGameStatus()
                currentTurn = "Player"
                displayCurrentTurn()
                displayEnemyAction("Enemy attacks for $damageDealt damage.")
                enableButtons()
            } else if (action == 2) {
                displayEnemyAction("Enemy defends.")
                currentTurn = "Player"
                displayCurrentTurn()
                enableButtons()
            } else {
                val healing = rollDice(15) + 5
                enemyHealth += healing
                updateHealthViews()
                displayCurrentTurn()
                displayEnemyAction("Enemy heals for $healing health.")
                currentTurn = "Player"
                enableButtons()
            }
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
            }
            else {
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
        if (currentTurn == "Player") {
            attackButton.isEnabled = !gameOver
        }
        rollDiceButton.isEnabled = true
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
    }
}
