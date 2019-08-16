<?php
echo "upload.php";
foreach($_POST as $key => $value) {
//    echo ("\nkey = " .$key) ;
//    echo ("\nvalue = " .$value) ;
}
// Parse value

$value = str_replace("\n", "", $value);
$lenValue = Strlen($value);
echo("\n\n *** lenValue = " .$lenValue);

echo ("\n\n *** shrinkedshortname");
$debKey = Strpos($value, "shrinkedshortname:");
echo ("\n *** debKey = " .$debKey);
$debVal = Strpos($value, ":") + 1;
echo ("\n *** debVal = " .$debVal);
$finVal = Strpos($value, ";") - 1;
echo ("\n *** finVal = " .$finVal);
$lenVal = $finVal - $debVal + 1;
echo ("\n *** lenVal = " .$lenVal);
$shrinkedShortName = substr($value, $debVal, $lenVal);
echo ("\n *** shrinkedShortName = " .$shrinkedShortName);

$value = substr($value, $finVal + 2, $lenValue - $finVal - 2);
$lenValue = Strlen($value);
echo("\n\n *** lenValue = " .$lenValue);

echo ("\n\n *** begindate");
$debKey = Strpos($value, "begindate:");
echo ("\n *** debKey = " .$debKey);
$debVal = Strpos($value, ":") + 1;
echo ("\n *** debVal = " .$debVal);
$finVal = Strpos($value, ";") - 1;
echo ("\n *** finVal = " .$finVal);
$lenVal = $finVal - $debVal + 1;
echo ("\n *** lenVal = " .$lenVal);
$beginDate = substr($value, $debVal, $lenVal);
echo ("\n *** beginDate = " .$beginDate);

$value = substr($value, $finVal + 2, $lenValue - $finVal - 2);
$lenValue = Strlen($value);
echo("\n\n *** lenValue = " .$lenValue);

echo ("\n\n *** currentdate");
$debKey = Strpos($value, "currentdate:");
echo ("\n *** debKey = " .$debKey);
$debVal = Strpos($value, ":") + 1;
echo ("\n *** debVal = " .$debVal);
$finVal = Strpos($value, ";") - 1;
echo ("\n *** finVal = " .$finVal);
$lenVal = $finVal - $debVal + 1;
echo ("\n *** lenVal = " .$lenVal);
$currentDate = substr($value, $debVal, $lenVal);
echo ("\n *** currentDate = " .$currentDate);

$value = substr($value, $finVal + 2, $lenValue - $finVal - 2);
$lenValue = Strlen($value);
echo("\n\n *** lenValue = " .$lenValue);

echo ("\n\n *** filecontent");
$debKey = Strpos($value, "filecontent:");
echo ("\n *** debKey = " .$debKey);
$debVal = Strpos($value, ":") + 1;
echo ("\n *** debVal = " .$debVal);
$finVal = $lenValue;
echo ("\n *** finVal = " .$finVal);
$lenVal = $finVal - $debVal + 1;
echo ("\n *** lenVal = " .$lenVal);
$fileContent = substr($value, $debVal, $lenVal);
echo ("\n *** fileContent = " .$fileContent);

// Dir creation
$dirName = $beginDate . "_" . $shrinkedShortName;
$fullDirName = './tournaments/'.$dirName;
$ret = mkdir($fullDirName);
$ret = chmod($fullDirName, 0777);

// File save
$fileName = $currentDate . "_" . $shrinkedShortName . ".xml";
// unlink($fileName);
$ret = fopen($fullDirName . "/" .$fileName, "w");
echo ("\n *** fopen ret = " .$ret);
$fullFileName = $fullDirName . '/' . $fileName;
$ret = file_put_contents ($fullFileName, $fileContent);
echo ("\n *** file_put_contents ret = " .$ret);

?>