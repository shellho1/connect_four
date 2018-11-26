<?php
/**
 * Created by PhpStorm.
 * User: Weston
 * Date: 11/25/2018
 * Time: 2:13 PM
 */

require_once  "db.inc.php";

echo '<?xml version="1.0" encoding="UTF-8" ?>';

if(!isset($_GET['magic']) || !isset($_GET['user']) || !isset($_GET['boardstate']) || $_GET['magic'] != "NechAtHa6RuzeR8x") {
    echo '<connect4 status="no" msg="magic" />';
    exit;
}

process($_GET['user'], $_GET['currplayer'], $_GET['boardstate']);

function process($username, $curr, $boardState){
    $pdo = pdo_connect();

    $game = $pdo->query("SELECT player1id, player2id, currPlayer FROM connect4game")->fetch();

    if ($game['currPlayer'] == 1) {
        $currUsername = $game['player1id'];
    } else {
        $currUsername = $game['player2id'];
    }

    if ($currUsername != $username) {
        echo '<connect4 status="no" msg="wrong user updating" />';
        exit;
    }

    $query = "UPDATE connect4game SET currPlayer=$curr, boardState=$boardState";
    $pdo->query($query);

    if ($pdo){
        echo '<connect4 status="yes" />';
        exit;
    }
    else {
        echo '<connect4 status="no" />';
        exit;
    }
}