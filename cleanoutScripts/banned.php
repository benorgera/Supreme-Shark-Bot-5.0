<?php
        $dsn = 'mysql:dbname=supreme;host=45.55.240.241;';
        $user = 'supremeuser';
        $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
        $dbh = new PDO($dsn, $user, $pass);
        $sql = 'SELECT activation_key FROM activation WHERE isBeta = "1"';
        $stmt = $dbh->prepare($sql);
        $stmt->execute();
        $row = $stmt->fetchAll();
        foreach($row as $nip) {
	    print("Nip" . $nip[0]);
	    $sql = 'INSERT INTO banned (activation_key) VALUES ("'.$nip[0].'")';
	    $stmt = $dbh->prepare($sql);
            $stmt->execute();
	}
?>
