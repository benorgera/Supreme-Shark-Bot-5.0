<?php
$key = $_GET['key'];
if (empty($key)) {
    $banned="no";
} else {
    $dsn = 'mysql:dbname=supreme;host=localhost';
    $user = 'supremeuser';
    $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
    $dbh = new PDO($dsn, $user, $pass);
    $sql = 'SELECT * FROM banned WHERE activation_key="' . $key . '"';
    $stmt = $dbh->prepare($sql);
    $stmt->execute();
    $arrRES = $stmt->fetch();
    if ($arrRES) {
    	$banned="yes";
    } else {
	$banned="no";
    }
}
    $result = array('banned' => $banned);
    header('Content-type: application/json');
    print json_encode($result);
?>
