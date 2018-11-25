<?php
/**
 * Created by PhpStorm.
 * User: Weston
 * Date: 11/24/2018
 * Time: 10:51 PM
 */

require_once "db.inc.php";

echo '<?xml version="1.0" encoding="UTF-8" ?>';

if (!isset($_GET['magic']) || $_GET['magic'] !=  "NechAtHa6RuzeR8x") {
    echo '<connect4 status="no" msg="magic" />';
    exit;
}

// Connect to the database
$pdo = pdo_connect();
$query = "TRUNCATE TABLE connect4game";
$disconnect = $pdo->query($query);
if ($disconnect){
    echo "<connect status='yes' message='disconnected'/>";
    exit;
}
else {
    echo "<connect status='no' message='connected'/>";
}