<?php
    $key = $_GET['key'];
    $dsn = 'mysql:dbname=supreme;host=localhost';
    $user = 'supremeuser';
    $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
    $dbh = new PDO($dsn, $user, $pass);
    $sql = 'SELECT license_type FROM activation WHERE activation_key="' . $key . '"';
    $stmt = $dbh->prepare($sql);
    $stmt->execute();
    $arrRES = $stmt->fetch();
 
    $result = array('licenseType' => $arrRES[0]);
    header('Content-type: application/json');
    print json_encode($result);
?>  
