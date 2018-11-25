<?php
/**
 * Created by PhpStorm.
 * User: Weston
 * Date: 11/25/2018
 * Time: 2:13 PM
 */

require_once  "db.inc.php";

echo '<?xml version="1.0" encoding="UTF-8" ?>';

if(!isset($_GET['magic']) || !isset($_GET['user']) || !isset($_GET['row']) || !isset($_GET['column']) || $_GET['magic'] != "NechAtHa6RuzeR8x") {
    echo '<connect4 status="no" msg="magic" />';
    exit;
}

process($_GET['user'],$_GET['row'],$_GET['column']);

function process($username, $row, $column){
    $pdo = pdo_connect();
    $usernameQ = $pdo->quote($username);
    $query = "UPDATE connect4state SET username=$usernameQ,row=$row,col=$column";
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