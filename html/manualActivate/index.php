<?php
$strDownload = "/var/www/downloads/" . "manualActivate.jar.zip";
$strFile = file_get_contents($strDownload);
$arrRES['file'] = "manualActivate.jar.zip";
header("Content-type: application/force-download");
header("Content-Disposition: attachment; filename=\"" . str_replace(" ", "_", $arrRES['file']) . "\"");
echo $strFile;
?>

