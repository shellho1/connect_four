<?php

function pdo_connect() {
    try {
        // Production server
        $dbhost="mysql:host=mysql-user.cse.msu.edu;dbname=dennis57";
        $user = "dennis57";
        $password = "catsrule";
        return new PDO($dbhost, $user, $password);
    } catch(PDOException $e) {
        die( "Unable to select database");
    }
}