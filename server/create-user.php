<?php
require_once "db.inc.php";
echo '<?xml version="1.0" encoding="UTF-8" ?>';

if(!isset($_GET['magic']) || $_GET['magic'] != "NechAtHa6RuzeR8x") {
    echo '<connect4 status="no" msg="magic" />';
    exit;
}

// Process in a function
process($_GET['user'], $_GET['pw']);

echo '<connect4 status="yes"/>';

/**
 * Process the query
 * @param $user the user to look for
 * @param $password the user password
 */
function process($user, $password) {
    // Connect to the database
    $pdo = pdo_connect();

    $userQ = $pdo->quote($user);
    $pwQ = $pdo->quote($password);

    $query = "insert into connect4user(user, password) VALUES($userQ, $pwQ)";

    if(!$pdo->query($query)) {
        echo '<connect4 status="no" msg="failed to create user" />';
        exit;
    }

    echo '<connect4 status="yes"/>';
    exit;
}