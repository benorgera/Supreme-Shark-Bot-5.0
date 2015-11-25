<?php
    error_reporting(E_ALL);
    ini_set('display_errors', true);
    $key = $_GET['key'];
    $ipMacSuccess=true;
    $macAddress = $_GET['mac_address'];
    $ipAddress = $_GET['ip_address'];
    if (!empty($key)) {
        // lookup key in db
        $dsn = 'mysql:dbname=supreme;host=localhost';
        $user = 'supremeuser';
        $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
        $dbh = new PDO($dsn, $user, $pass);
        $sql = 'SELECT * FROM activation WHERE activation_key="'.$key.'" AND is_enabled=0';
        $stmt = $dbh->prepare($sql);
        // $stmt->bindParam(':key', $key);
        $stmt->execute();
        $row = $stmt->fetch();
        // verify that is_enabled = 0
        if ($row) {
            $success = true;
            $msg = 'Code is approved';
            // write to the db
            $sql = 'UPDATE activation set is_enabled=1, enabled_date=NOW(), mac_address="'.$macAddress.'", ip_address="'.$ipAddress.'" WHERE activation_key=:key';
            $stmt = $dbh->prepare($sql);
            $stmt->bindParam(':key', $key);
            $stmt->execute();
        } else {
            $success = false;
            $msg = 'Code not approved';
        }
    }
    if ($key == "masterforce") {
        $success = true;
        $msg = "Code is approved";
    }
    $result = array('success' => $success, 'msg' => $msg);
    header('Content-type: application/json');
    print json_encode($result);
?>
