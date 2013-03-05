<html>
<head>
<title>QR Code in Java Servlet - viralpatel.net</title>
	<TITLE>Clearable Textboxes in jQuery</TITLE>
<STYLE>
body, input{
	font-family: Calibri, Arial;
	margin: 0px;
	padding: 0px;
}
a {
	color: #0254EB
}
a:visited {
	color: #0254EB
}
#header h2 {
	color: white;
	background-color: #3275A8;
	margin:0px;
	padding: 5px;
	height: 40px;
	padding: 15px;
}

html, body, #container { height: 100%; }
body > #container { height: auto; min-height: 100%; }

#footer {
	font-size: 12px;
	clear: both;
	position: relative;
	z-index: 10;
	height: 3em;
	margin-top: -3em;
	text-align:center;
}
#content { padding-bottom: 3em; padding: 10px;}


input {
	font-size: 15px;
}
.style1 {
	border: 3px solid #ffaa00;
	font-size: 20px;
}

.style2 {
	border: 2px solid #aaff77;
	font-size: 18px;
}

</STYLE>
</HEAD>

<BODY>

<div id="container">
<div id="content">

	<form action="opt/qrcode" method="get">
		<p>Enter Text to create QR Code</p>
		<input type="text" name="qrtext" />
		<input type="submit" value="Generate QR Code" />
	</form>

<br/>

</div>
</BODY>
</HTML>
