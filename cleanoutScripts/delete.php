<?php
        $dsn = 'mysql:dbname=supreme;host=45.55.240.241;';
        $user = 'supremeuser';
        $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
        $dbh = new PDO($dsn, $user, $pass);
        $sql = 'SELECT download_key FROM activation';
        $stmt = $dbh->prepare($sql);
        $stmt->execute();
        $row = $stmt->fetchAll();
	print_r($row);
	$blah = "";
        foreach($row as $nip) {
		$blah = $blah . $nip[0];
	}
	print($blah);
	$sql = "Select downloadkey from downloads";
	$stms = $dbh->prepare($sql);
	$stms->execute();
	$row = $stms->fetchAll();
	foreach ($row as $bl) {
		if (!strpos($blah, $bl[0])) {
			echo "Delete from downloads where downloadkey = `".$bl[0]."`";
			$sql = "DELETE FROM downloads WHERE downloadkey = '".$bl[0]."'";
		        $stms = $dbh->prepare($sql);
       			$stms->execute();
	        	$row = $stms->fetchAll();	
		}
	}
?>
