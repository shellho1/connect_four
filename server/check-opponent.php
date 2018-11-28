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

    $query = $pdo->query("SELECT player1id, player2id, timestamp FROM connect4game")->fetch();

    $player1 = $query['player1id'];
    $player2 = $query['player2id'];
    $timestamp = $query['timestamp'];

    if ($player1 != NULL and $player2 != NULL){

        if ($player1 == $username){
            echo "<connect4 status='yes' message='start' opponent=\"$player2\"/>";
        }
        else {
            echo "<connect4 status='yes' message='start' opponent=\"$player1\"/>";
        }
        exit;
    }
    else if (time() - strtotime($timestamp) > 30) {
        echo "<connect4 status='yes' message='timeout'/>";
        exit;
    }
    else {
        echo "<connect4 status='yes' message='needPlayer'/>";
        exit;
    }
}