<?php
/**
 * Created by PhpStorm.
 * User: Weston
 * Date: 11/24/2018
 * Time: 8:18 PM
 */

require_once "db.inc.php";

echo '<?xml version="1.0" encoding="UTF-8" ?>';

if(!isset($_GET['magic']) || !isset($_GET['user']) || $_GET['magic'] != "NechAtHa6RuzeR8x") {
    echo '<connect4 status="no" msg="magic" />';
    exit;
}

// Process in a function
process($_GET['user']);

/*
 * Process the query
 * @param $username of the user to look for
 */
function process($username){
    // Connect to the database
    $pdo = pdo_connect();

    $player1 = $pdo->query("SELECT player1id FROM connect4game");
    $player2 = $pdo->query("SELECT player2id FROM connect4game");

    $player1 = $player1->fetch();
    $player2 = $player2->fetch();

    $player2 = $player2['player2id'];
    if ($player1['player1id'] != NULL and $player2['player2id'] != NULL){

        init_game($pdo,$player2);

        if ($player1['player1id'] == $username){
            $player2 = $player2['player2id'];
            echo "<connect4 status='yes' message='start' opponent=\"$player2\"/>";
        }
        else {
            $player1 = $player1['player1id'];
            echo "<connect4 status='yes' message='start' opponent=\"$player1\"/>";
        }
        exit;
    }
    else {
        echo "<connect4 status='yes' message='needPlayer'/>";
        exit;
    }
    exit;

}

function init_game($pdo,$player2){
    $reset = "TRUNCATE TABLE connect4state";
    $default = "INSERT INTO connect4state(username,row,col) VALUES(\"$player2\",-1,-1)";
    $pdo->query($reset);
    $pdo->query($default);
}