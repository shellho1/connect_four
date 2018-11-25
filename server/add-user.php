<?php
/**
 * Created by PhpStorm.
 * User: Weston
 * Date: 11/24/2018
 * Time: 4:47 PM
 */

require_once "db.inc.php";

echo '<?xml version="1.0" encoding="UTF-8" ?>';

if(!isset($_GET['magic']) || !isset($_GET['user']) || $_GET['magic'] != "NechAtHa6RuzeR8x") {
    echo '<connect4 status="no" msg="magic" />';
    exit;
}

// Process in a function
process($_GET['user']);

/**
 * Process the query
 * @param $username of the user to add to the game
 */
function process($username) {
    // Connect to the database
    $pdo = pdo_connect();

    $userQ = $pdo->quote($username);

    $queryPlayer1 = "SELECT player1id FROM connect4game";
    $player1 = $pdo->query($queryPlayer1);
    $player1 = $player1->fetch();
    if ($player1['player1id'] == NULL) {
        // Player 1 hasn't been added to update player1id entry
        $update = $pdo->query("INSERT INTO connect4game (player1id) VALUES ($userQ)");

        if (!$update) {
            echo '<connect4 status="no" msg="failed to add user to game" />';
            exit;
        }
        echo '<connect4 player1=\"userQ\" />';
        exit;
    } else {
        // Player 1 slot has already been filled so fill player 2 slot
        $update = $pdo->query("UPDATE connect4game SET player2id = $userQ");

        if (!$update) {
            echo '<connect4 status="no" msg="failed to add user to game" />';
            exit;
        }
        echo '<connect4 player2=\"userQ\" />';
        exit;
    }
}