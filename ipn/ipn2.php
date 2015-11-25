<?php
echo "began";
error_log("\n\nBegan: " . gmdate('Y-m-d h:i:s \G\M\T'), 3, dirname(__FILE__) . "/_lib/error.log");

// STEP 1: read POST data
// Reading POSTed data directly from $_POST causes serialization issues with array data in the POST.
// Instead, read raw POST data from the input stream. 

// Set to 0 once you're ready to go live
define("USE_SANDBOX", 1);
$raw_post_data = file_get_contents('php://input');
$raw_post_array = explode('&', $raw_post_data);
$myPost = array();

foreach ($raw_post_array as $keyval) {
    $keyval = explode('=', $keyval);
    if (count($keyval) == 2)
        $myPost[$keyval[0]] = urldecode($keyval[1]);
}
// read the IPN message sent from PayPal and prepend 'cmd=_notify-validate'
error_log("\nIPN Read Began", 3, dirname(__FILE__) . "/_lib/error.log");
$req = 'cmd=_notify-validate';
if (function_exists('get_magic_quotes_gpc')) {
    $get_magic_quotes_exists = true;
}
foreach ($myPost as $key => $value) {
    if ($get_magic_quotes_exists == true && get_magic_quotes_gpc() == 1) {
        $value = urlencode(stripslashes($value));
    } else {
        $value = urlencode($value);
    }
    $req .= "&$key=$value";
}
error_log("\nIPN Read Finished", 3, dirname(__FILE__) . "/_lib/error.log");

// Step 2: POST IPN data back to PayPal to validate
if (USE_SANDBOX == true) {
    $paypal_url = "https://www.sandbox.paypal.com/cgi-bin/webscr";
} else {
    $paypal_url = "https://www.paypal.com/cgi-bin/webscr";
}
error_log("\nCurl Began", 3, dirname(__FILE__) . "/_lib/error.log");

$ch = curl_init($paypal_url);

curl_setopt($ch, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_1);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $req);
curl_setopt($link, CURLOPT_SSL_VERIFYPEER, TRUE);
curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, TRUE);
//end here
curl_setopt($ch, CURLOPT_FORBID_REUSE, 1);
//and this one
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Connection: Close'));
//end here
// In wamp-like environments that do not come bundled with root authority certificates,
// please download 'cacert.pem' from "http://curl.haxx.se/docs/caextract.html" and set 
// the directory path of the certificate as shown below:
curl_setopt($ch, CURLOPT_CAINFO, dirname(__FILE__) . '/_lib/cacert.pem');
error_log("\nCurl Finished", 3, dirname(__FILE__) . "/_lib/error.log");

if (!($res = curl_exec($ch))) {
    error_log("\nCurl " . curl_error($ch) . " when processing IPN data", 3, dirname(__FILE__) . "/_lib/error.log");
    curl_close($ch);
    exit;
}
curl_close($ch);

if (strcmp($res, "VERIFIED") == 0) {

    error_log("\nVERIFIED", 3, dirname(__FILE__) . "/_lib/error.log");
    // The IPN is verified, process it:
    // check whether the payment_status is Completed
    // check that txn_id has not been previously processed
    // check that receiver_email is your Primary PayPal email
    // check that payment_amount/payment_currency are correct
    // process the notification

    // assign posted variables to local variables
    $item_name = $_POST['item_name'];
    $item_number = $_POST['item_number'];
    if (empty($item_name)) {
        $item_name = $_POST['item_name1'];
    }
    if (empty($item_number)) {
        $item_number = $_POST['item_number1'];
    }
    $payment_status = $_POST['payment_status'];
    $payment_amount = $_POST['mc_gross'];
    $payment_currency = $_POST['mc_currency'];
    $txn_id = $_POST['txn_id'];
    $receiver_email = $_POST['receiver_email'];
    $payer_email = $_POST['payer_email'];
    $first_name = $_POST['first_name'];
    $last_name = $_POST['last_name'];
    $name = $first_name . " " . $last_name;

    if (strcmp($payment_status,"Completed") == 0) {
        $length = 25;
        $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        $charactersLength = strlen($characters);
        $randomString = '';
        for ($i = 0; $i < $length; $i++) {
            $randomString .= $characters[rand(0, $charactersLength - 1)];
        }
        $ip = '';
    
    
        if (!empty($_SERVER['HTTP_CLIENT_IP'])) {
            $ip = $_SERVER['HTTP_CLIENT_IP'];
        } elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR'])) {
            $ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
        } else {
            $ip = $_SERVER['REMOTE_ADDR'];
        }
        if (empty($ip)) {
            $ip = "unknown";
        }
        $dsn = 'mysql:dbname=supreme;host=45.55.189.185;';
        $user = 'supremeuser';
        $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
        try {
            $dbh = new PDO($dsn, $user, $pass);
            $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $e) {
            error_log("\nDB Connection failed: " . $e->getMessage(), 3, dirname(__FILE__) . "/_lib/error.log");
        }
    
    
    
    //download table
    
    
    
        function createKey()
        {
            $dsn = 'mysql:dbname=supreme;host=45.55.189.185;';
            $user = 'supremeuser';
            $pass = 'hsjkdafsfkjhldseiuwryasdpr21308762';
            try {
                $dbh = new PDO($dsn, $user, $pass);
                $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            } catch (PDOException $e) {
                error_log("\nDB Connection failed: " . $e->getMessage(), 3, dirname(__FILE__) . "/_lib/error.log");
            }
            //create a random key
            $strKey = md5(microtime());
            $sql = 'SELECT count(*) FROM downloads WHERE downloadkey = "' . $strKey . '" LIMIT 1';
            $stmt = $dbh->prepare($sql);
            try {
                $stmt->execute();
                $row = $stmt->fetch();
            } catch (PDOException $e) {
                error_log("\nDownloads Table Check Failed: " . $e->getMessage(), 3, dirname(__FILE__) . "/_lib/error.log");
                echo "\nDownloads Table Check Failed: " . $e->getMessage();
            }
            //check to make sure this key isnt already in use
    //    $resCheck = mysql_query("SELECT count(*) FROM downloads WHERE downloadkey = '{$strKey}' LIMIT 1");
            $arrCheck = mysql_fetch_assoc($row);
            if ($arrCheck['count(*)']) {
                //key already in use
                return createKey();
            } else {
                //key is OK
                return $strKey;
            }
        }
        if (strcmp($item_number,"1") == 0){
            $file = 'supremesharkbot.dmg.zip'; 
        } else {
            $file = 'supremesharkbot.zip';
        }
    //get a unique download key
        $strKey = createKey();
        $sql = "INSERT INTO `downloads` (downloadkey, file, expires) VALUES ('" . $strKey . "', '" . $file . "', '" . time() . (60 * 60 * 24 * 10) . "')";
        $stmt = $dbh->prepare($sql);
        try {
            $stmt->execute();
    //        $row = $stmt->fetch();
        } catch (PDOException $e) {
            error_log("\nDownloads Table Insert Failed: " . $e->getMessage(), 3, dirname(__FILE__) . "/_lib/error.log");
            echo "\nDownloads Table Insert Failed: " . $e->getMessage();
        }
        $link = "http://supremesharkbot.com:8080/download/?key=" . $strKey;
    
    
    //activation table
    
        $sql = 'INSERT INTO `activation` (activation_key, created_date, ip_address, download_key) VALUES ("' . $randomString . '", NOW(), "' . $ip . '", "'.$strKey.'")';
        $stmt = $dbh->prepare($sql);
        try {
            $stmt->execute();
        } catch (PDOException $e) {
            error_log("\nActivation Table Update failed: " . $e->getMessage(), 3, dirname(__FILE__) . "/_lib/error.log");
        }
    
    
    //purchases table
    
    
        $sql = 'INSERT INTO `purchases` (item_name, item_number, payer_email, receiver_email, purchase_date, payment_status, payment_amount, name) VALUES ("' . $item_name . '", "' . $item_number . '", "' . $payer_email . '", "' . $receiver_email . '", NOW(), "' . $payment_status . '", "' . $payment_amount . '", "' . $name . '")';
        $stmt = $dbh->prepare($sql);
        try {
            $stmt->execute();
        } catch (PDOException $e) {
            error_log("\nPurchases Table Update Failed: " . $e->getMessage(), 3, dirname(__FILE__) . "/_lib/error.log");
        }
    
    //email
    
    
        require('_lib/class.phpmailer.php');
        require('_lib/class.smtp.php');
    
        $mail = new PHPMailer();
        $body = "Dear " . $name . ",\n\nThank you for placing your order with Supreme Shark Bot! This email is to confirm your recent purchase. \n\nBelow you'll find a download link to the bot file itself along with  a README.pdf, which you should definitely view before running the bot. It explains setup and usage and will hopefully answer all of your questions. This download link EXPIRES in 10 days and can only be used ONCE for download, so DON'T click it until you're on the proper computer. Be sure to choose to save the file instead of opening it if prompted by your browser, otherwise you won't permanently receive it.\n\n" . $link . " \n\nBelow you'll find your one-time-use activation key. Do not lose this key, the bot will not run without it. \n\nActivation key: " . $randomString . "\n\nIf you run into any difficulties email us at team@supremesharkbot.com; we'll respond within 24 hours.\nThank you!";
        $mail->IsSMTP(); // telling the class to use SMTP
        $mail->Host = "mail.supremesharkbot.com"; // SMTP server
        $mail->SMTPDebug = 1;                     // enables SMTP debug information (for testing)
    // 1 = errors and messages
    // 2 = messages only
        $mail->SMTPAuth = true;                  // enable SMTP authentication
        $mail->SMTPSecure = "tls";                 // sets the prefix to the servier
        $mail->Host = "smtp.gmail.com";      // sets GMAIL as the SMTP server
        $mail->Port = 587;                   // set the SMTP port for the GMAIL server
        $mail->Username = "team@supremesharkbot.com";  // GMAIL username
        $mail->Password = "sharkbotissupreme3.0";
        $mail->SetFrom('team@supremesharkbot.com', 'Supreme Shark Bot Auto-Responder');
        $mail->Subject = "Thank you for Your Purchase: Supreme Bot Firefox 4.0";
        $mail->Body = $body;
        $mail->AddAddress($payer_email, $name);
    //
    //    $mail->AddAttachment("_lib/supremesharkbot.dmg");
    //    $mail->AddAttachment("_lib/tutorial.pdf");     //
    
    
        if (!$mail->Send()) {
            error_log("\nMailer Error:" . $mail->ErrorInfo, 3, dirname(__FILE__) . "/_lib/error.log");
        } else {
            error_log("\nMessage Sent!", 3, dirname(__FILE__) . "/_lib/error.log");
        }
    } else {
        //if the payment wasnt sent    
        require('_lib/class.phpmailer.php');
        require('_lib/class.smtp.php');

        $mail = new PHPMailer();
        $body = "Dear " . $name . ",\n\nThank you for placing your order with Supreme Shark Bot! This email is to confirm your recent purchase.\n\nUnfortunately, your Payment has not yet been processed, so you have not yet received the bot. As of right now your payment status is '".$payment_status."'. Once Paypal notifies us that your payment was completed, you will receive your bot.\n\nOnce again, thank you for your purchase. If you have any questions, email us at team@supremesharkbot.com";
        $mail->IsSMTP(); // telling the class to use SMTP
        $mail->Host = "mail.supremesharkbot.com"; // SMTP server
        $mail->SMTPDebug = 1;                     // enables SMTP debug information (for testing)
    // 1 = errors and messages
    // 2 = messages only
        $mail->SMTPAuth = true;                  // enable SMTP authentication
        $mail->SMTPSecure = "tls";                 // sets the prefix to the servier
        $mail->Host = "smtp.gmail.com";      // sets GMAIL as the SMTP server
        $mail->Port = 587;                   // set the SMTP port for the GMAIL server
        $mail->Username = "team@supremesharkbot.com";  // GMAIL username
        $mail->Password = "sharkbotissupreme3.0";
        $mail->SetFrom('team@supremesharkbot.com', 'Supreme Shark Bot Auto-Responder');
        $mail->Subject = "Thank you for Your Purchase: Supreme Bot Firefox 4.0";
        $mail->Body = $body;
        $mail->AddAddress($payer_email, $name);
            if (!$mail->Send()) {
                error_log("\nMailer Error:" . $mail->ErrorInfo, 3, dirname(__FILE__) . "/_lib/error.log");
            } else {
                error_log("\nMessage Sent!", 3, dirname(__FILE__) . "/_lib/error.log");
            }
    
    }
    // IPN message values depend upon the type of notification sent.
    // To loop through the &_POST array and print the NV pairs to the screen:
    foreach ($_POST as $key => $value) {
        echo $key . " = " . $value . "<br>";
    }
} elseif
(strcmp($res, "INVALID") == 0
) {
    // IPN invalid, log for manual investigation
    error_log("\nINVALID", 3, dirname(__FILE__) . "/_lib/error.log");
    echo "The response from IPN was: <b>" . $res . "</b>";
} else {
    error_log("\nNeither", 3, dirname(__FILE__) . "/_lib/error.log");
}
