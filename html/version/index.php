<?php
    error_reporting(E_ALL);
    ini_set('display_errors', true);
    $dsn = 'mysql:dbname=supreme;host=localhost;';
    $user = 'supremeuser';
    $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
    $dbh = new PDO($dsn, $user, $pass);
    $sql = 'SELECT version, release_date, message FROM version WHERE id=1;';
    $stmt = $dbh->prepare($sql);
    $stmt->execute();
    $row = $stmt->fetch();
    $result = ($row);
    header('Content-type: application/json');
    print json_encode($result);
?>
