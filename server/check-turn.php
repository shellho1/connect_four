<?php
/**
 * Created by PhpStorm.
 * User: Weston
 * Date: 11/25/2018
 * Time: 5:06 PM
 */

require_once "db.inc.php";

echo '<?xml version="1.0" encoding="UTF-8" ?>';

if (!isset($_GET['magic']) || $_GET['magic'] !=  "NechAtHa6RuzeR8x") {
    echo '<connect4 status="no" msg="magic" />';
    exit;
}

$pdo = pdo_connect();
$game = $pdo->query("SELECT player1id, player2id, currPlayer, winner, timestamp FROM connect4game")->fetch();

if ($game['currPlayer'] == 1) {
    $username = $game['player1id'];
} else {
    $username = $game['player2id'];
}

$winner = $game['winner'];
$player1 = $game['player1id'];
$player2 = $game['player2id'];
$timestamp = $game['timestamp'];

echo "<connect4 status='yes' currPlayer='$username' player1='$player1' player2='$player2' winner='$winner' timestamp=$timestamp />";
exit;