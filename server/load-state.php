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
$username = $pdo->query("SELECT username FROM connect4state");
$row = $pdo->query("SELECT row FROM connect4state");
$column = $pdo->query("SELECT col FROM connect4state");

$username = $username->fetch();
$row = $row->fetch();
$column = $column->fetch();

$username = $username['username'];
$row = $row['row'];
$column = $column['col'];

echo "<connect4 status='yes' user=\"$username\" row=\"$row\" col=\"$column\" />";
