var express = require("express");
var app = express();
var nodemailer = require('nodemailer');
var bodyParser = require('body-parser');

app.use(bodyParser.urlencoded({ extended: false }));


app.use('/assets', express.static(__dirname + '/assets'));

app.get('/', function(req, res) {
	res.sendFile(__dirname + '/index.html')
});

app.post('/sendMail', function(req, res) {
var sub = req.body.sub,
	email = req.body.email,
	msg = req.body.msg;

  console.log('Sent from: ' + email + '. Subject: ' + sub + '. Message: ' + msg + '.');

  sendMail(email, sub, msg);
  res.end('yes');
})

app.get('/thankyou', function(req, res) {
    res.sendFile('/opt/website/thankyou.html')
})

app.get('/testimonials', function(req, res) {
    res.sendFile('/opt/website/testimonials.html')
})

app.get('/mobilecontact', function(req, res) {
    res.sendFile('/opt/website/mobilecontact.html')
})

app.get('*', function(req, res) {
	res.sendFile(__dirname + '/404.html')
});


// create reusable transporter object using SMTP transport
var transporter = nodemailer.createTransport({
    service: 'Gmail',
    auth: {
        user: 'team@supremesharkbot.com',
        pass: '***REMOVED***'
    }
});

// NB! No need to recreate the transporter object. You can use
// the same transporter object for all e-mails

// setup e-mail data with unicode symbols

// send mail with defined transport object
var sendMail = function(contact, sub, msg) { 
	var mailOptions = {
    from: 'team@supremsharkbot.com', // sender address
    to: 'team@supremesharkbot.com', // list of receivers
    subject: 'Contact email from: ' + contact, // Subject line
    text: 'Subject: ' + sub + '\n \nMessage: ' + msg // plaintext body
};

	transporter.sendMail(mailOptions, function(error, info){
    if(error){
        console.log(error);
    }else{
        console.log('Message sent: ' + info.response);
    }
});
}


//listening on 
app.listen(80, function() {
	console.log("listening on port 80");
});

