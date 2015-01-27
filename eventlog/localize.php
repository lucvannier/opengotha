<?php
echo "17h29";

$ip = $_GET['ip'];
echo "ip = " . $ip;
$geo = localizeFromGeobyte ($ip);
echo " geo = " . $geo["DURATION"]. " " . $geo[1];


function localizeFromGeobyte ($ip) {
list($usec, $sec) = explode(" ",microtime());
$s = (float)$usec + (float)$sec;
$d = "GetLocation=&cid=0&c=&Tempate=".urlencode("iplocator.htm")."&ipaddress=".urlencode($ip)."&submit=Submit";
$fp = fsockopen("www.geobytes.com", 80);
fputs($fp, "POST /IpLocator.htm HTTP/1.1
");
fputs($fp, "Accept: text/*, image/jpeg, image/png, image/*, */*
");
fputs($fp, "Host: www.geobytes.com
");
fputs($fp, "Referer: http://www.geobytes.com/IpLocator.htm
");
fputs($fp, "Content-type: application/x-www-form-urlencoded
");
fputs($fp, "Content-length: ". strlen($d) ."
");
fputs($fp, "Connection: Keep-alive

");
fputs($fp, "User-Agent: Mozilla/5.0 (compatible; Konqueror/3.1; Linux
");
fputs($fp, "$d
");
while(is_resource($fp) && !feof($fp)) {
$res .= fgets($fp, 128 );
}
fclose($fp);
preg_match_all('/>([^<]+)<input name="[^"]+" value="([^"]+)"[^>]+>/i', $res, $matches);
var_dump($matches);
for($x=0; $x<count($matches[1]); $x++) {
$ret[$matches[1][$x]] = $matches[2][$x];
}
list($usec, $sec) = explode(" ",microtime());
$e = (float)$usec + (float)$sec;
$t = sprintf("%.4f", ($e - $s));
$ret["DURATION"] = $t;
if(count($ret)==1) {
preg_match_all("@body>(.*)</body@iU", $res,$matches);
return array("ERROR" => $matches[1][0],
"DURATION" => $t);
}
return $ret;
}

?>