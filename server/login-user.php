<?php
require_once "db.inc.php";
echo '<?xml version="1.0" encoding="UTF-8" ?>';

if(!isset($_GET['magic']) || $_GET['magic'] != "NechAtHa6RuzeR8x") {
    echo '<connect4 status="no" msg="magic" />';
    exit;
}

process($_GET['user'], $_GET['pw']);

/**
 * Process the query
 * @param $user the user to look for
 * @param $password the user password
 */
function process($user, $password) {
    // Connect to the database
    $pdo = pdo_connect();

    getUser($pdo, $user, $password);

    echo '<connect4 status="yes" />';
    exit;
}

/**
 * Ask the database for the user ID. If the user exists, the password
 * must match.
 * @param $pdo PHP Data Object
 * @param $user The user name
 * @param $password Password
 * @return id if successful or exits if not
 */
function getUser($pdo, $user, $password) {
    // Does the user exist in the database?
    $userQ = $pdo->quote($user);
    $query = "SELECT id, password from connect4user where user=$userQ";

    $query2 = "SELECT player1id, player2id FROM connect4game";
    $players = $pdo->query($query2)->fetch();

    if ($players['player1id'] != NULL && $players['player2id'] != NULL) {
        echo '<connect4 status="no" msg="busy" />';
        exit;
    }

    $rows = $pdo->query($query);
    if($row = $rows->fetch()) {
        // We found the record in the database
        // Check the password
        if($row['password'] != $password) {
            echo '<connect4 status="no" msg="password error" />';
            exit;
        }

        // Start the PHP session system
        session_start();

        $_SESSION['user'] = $user;

        return $row['id'];
    }

    echo '<connect4 status="no" msg="user error" />';
    exit;
}