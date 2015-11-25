<?php
    $key = $_GET["key"];

    function queries($key) {//returns false if failed, true if success

    $dsn = 'mysql:dbname=supreme;host=localhost';
    $user = 'supremeuser';
    $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
    $dbh = new PDO($dsn, $user, $pass);
    $sql = 'UPDATE activation SET is_enabled = 0 WHERE activation_key = "'.$key.'";'; 
    $stmt = $dbh->prepare($sql);
    $stmt->execute();
    $row = $stmt->fetch(); 


    if ($stmt->rowCount() == 0) {// on issue, if they already 0 as is_enabled this returns false becasue 0 rows affected. same applies to downloads update
	//return false; commented out because as long as next query is good this one wouldve worked
    }

    $sql = 'SELECT download_key FROM activation WHERE activation_key = "' . $key . '";';
    $stmt2 = $dbh->prepare($sql);
    $stmt2->execute();
    $res = $stmt2->fetch();

    if ($stmt2->rowCount() == 0) {
	return false;
    } else {
        $sql = 'UPDATE downloads SET downloads = 0 WHERE downloadkey = "' . $res[0] . '";';
        $stmt3 = $dbh->prepare($sql);
        $stmt3->execute();
        $res2 = $stmt3->fetch(); 
    }

    if ($stmt3->rowCount() == 0) {
//	return false;
    }
    return true; 
    } //end queries funciton


    $result = array('success' => queries($key));
    header('Content-type: application/json');
    print json_encode($result);
?>
