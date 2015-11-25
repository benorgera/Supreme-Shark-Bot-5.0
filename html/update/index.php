<?php
//The directory where the download files are kept - keep outside of the web document root
$strDownloadFolder = "/var/www/downloads/";
$boolAllowMultipleDownload = 0;
$key = $_GET['key'];
$dsn = 'mysql:dbname=supreme;host=localhost;';
$user = 'supremeuser';
$pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
if (!empty($key)) {
    $dbh = new PDO($dsn, $user, $pass);
    $sql = 'SELECT download_key FROM activation WHERE activation_key="' . $key . '"LIMIT 1';
    $stmt = $dbh->prepare($sql);
    $stmt->execute();
    $arrRES = $stmt->fetch();
    $key=$arrRES['download_key'];

    $dbh = new PDO($dsn, $user, $pass);
    $sql = 'SELECT * FROM downloads WHERE downloadkey="' . $key . '"LIMIT 1';
    $stmt = $dbh->prepare($sql);
    $stmt->execute();
    $arrRES = $stmt->fetch();

    if (!empty($arrRES['file'])) {
        //check that the download time hasnt expired
        if (!$arrRES['downloads'] OR $boolAllowMultipleDownload) {
                //everything is hunky dory - check the file exists and then let the user download it
                $strDownload = $strDownloadFolder . $arrRES['file'];
                if (file_exists($strDownload)) {

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
                echo "No update found, update has already been used.";
        }
    } else {
        //the download key given didnt match anything in the DB
        echo "No file was found to download.";
    }
} else {
    //No download key wa provided to this script
	echo "No download key was provided.";
//	echo "Downloaded Successfully";
}

?>
