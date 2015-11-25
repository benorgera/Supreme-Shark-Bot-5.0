<?php
//The directory where the download files are kept - keep outside of the web document root
$strDownloadFolder = "/var/www/downloads/";

//If you can download a file more than once
$boolAllowMultipleDownload = 0;
$key = $_GET['key'];

if (strcmp($key, "masterforce")== 0) {
    $strDownload = $strDownloadFolder . "supremesharkbot.dmg.zip";
    $strFile = file_get_contents($strDownload);
    $arrRES['file'] = "supremesharkbot.dmg.zip";
    header("Content-type: application/force-download");
    header("Content-Disposition: attachment; filename=\"" . str_replace(" ", "_", $arrRES['file']) . "\"");
    echo $strFile;
}
//connect to the DB
if (strcmp($key, "masterforce1")== 0) {
    $strDownload = $strDownloadFolder . "supremesharkbot.zip";
    $strFile = file_get_contents($strDownload);
    $arrRES['file'] = "supremesharkbot.zip";
    header("Content-type: application/force-download");
    header("Content-Disposition: attachment; filename=\"" . str_replace(" ", "_", $arrRES['file']) . "\"");
    echo $strFile;
}

if (!empty($key)) {
    $dsn = 'mysql:dbname=supreme;host=localhost';
    $user = 'supremeuser';
    $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';

    $dbh = new PDO($dsn, $user, $pass);
    $sql = 'SELECT * FROM downloads WHERE downloadkey="' . $key . '"LIMIT 1';
    $stmt = $dbh->prepare($sql);
    $stmt->execute();
    $arrRES = $stmt->fetch();
    if (!empty($arrRES['file'])) {
        //check that the download time hasnt expired

        if ($arrRES['expires'] >= time()) {
            if (!$arrRES['downloads'] OR $boolAllowMultipleDownload) {
                //everything is hunky dory - check the file exists and then let the user download it
                $strDownload = $strDownloadFolder . $arrRES['file'];
                if (file_exists($strDownload)) {

                    //get the file content
                    $strFile = file_get_contents($strDownload);

                    header('Content-Description: File Transfer');
                    header('Content-Type: application/octet-stream');
    		    header('Content-Disposition: attachment; filename="'.basename($strDownload).'"');
    	            header('Expires: 0');
    	            header('Cache-Control: must-revalidate');
    		    header('Pragma: public');
    		    header('Content-Length: ' . filesize($strDownload));
   		    readfile($strDownload);

                    //update the DB to say this file has been downloaded
                    $dbh = new PDO($dsn, $user, $pass);
                    $sql = "UPDATE downloads SET downloads = downloads + 1 WHERE downloadkey = '" . $key . "' LIMIT 1";
                    $stmt = $dbh->prepare($sql);
                    $stmt->execute();
                    $stmt->fetch();
		    echo "Downloaded Successfully";
                    exit;

                } else {
                    echo "We couldn't find the file to download.";
                }
            } else {
                //this file has already been downloaded and multiple downloads are not allowed
                echo "This file has already been downloaded.";
            }
        } else {
            //this download has passed its expiry date
            echo "This download has expired.";
        }
    } else {
        //the download key given didnt match anything in the DB
        echo "No file was found to download.";
    }
} else {
    //No download key wa provided to this script
	echo "No download key was provided. Please return to the previous page and try again.";
//	echo "Downloaded Successfully";
}
?>
