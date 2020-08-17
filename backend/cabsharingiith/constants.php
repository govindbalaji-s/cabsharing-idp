<?php
//Global constants 

$datetime_format = "Y-m-d H:i:s";

//These values are temporary and of the free web hosting.
$db_host = 'localhost';
$db_name = 'cabseqlr_rides';
$db_username = 'cabseqlr_app';
$db_password = 'YaarPetraMagano';

function get_db_connection(){
  global $db_host, $db_name, $db_username, $db_password;
  $db_connection = new PDO("mysql:dbname={$db_name};host={$db_host};charset=utf8;", $db_username, $db_password);
  $db_connection->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
  $db_connection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
  return $db_connection;
}
function set_headers(){
  //The following four lines are necessary for our script to be accessed from an outside app.
  header("Access-Control-Allow-Origin: *");
  header("Access-Control-Allow-Methods: PUT, GET, POST");
  header("Access-Control-Allow-Credentials: true");
  header("Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept");
  //Ouput format is JSON
  header('Content-type:application/json;charset=utf-8');
}
function echo_success($success, $message=''){
  if($success)
    echo "{\"result\":\"SUCCESS\"}";
  else
    echo "{\"result\":\"FAILED\", \"message\":\"{$message}\"}";
}
?>