<?php
    //Include the database credentials
    include 'constants.php';

    set_headers();   

    //The below statement decodes the json object from the received POST request
    /*json can contain keys : rollno - string stored in lowercase in DB,*/
    $post_json = json_decode(file_get_contents('php://input'), true);
    
    //JSONArray Request
    $post_json = $post_json[0];
    
    //We need this to be a global variable so that we can access them even after db_connection is closed.
    $query_result = NULL;
    $final_result = [];

    try{
      
      //Open database connection
      $db_connection = get_db_connection();

      $query_rollno = $post_json['rollno'];

      date_default_timezone_set('Asia/Kolkata');
      $now = date($datetime_format, strtotime(date($datetime_format) . " -4 hours"));
     
      //Prepare and execute query to get all records later than 4 hours ago from now
      $statement = $db_connection->prepare("SELECT * FROM `rides` WHERE `waittill` >= ?;");
      $statement->execute(array($now));
      $query_result = $statement->fetchAll(PDO::FETCH_ASSOC);
      //var_dump($query_result);
      
      //Check each query for the presence of the user and add in final result
      foreach($query_result as $i => $row){
        //var_dump($query_result[$i]['people']);
        $query_result[$i]['people'] = unserialize($query_result[$i]['people']);
        //var_dump($query_result[$i]['people']);
        $exists = false;
        
        foreach($query_result[$i]['people'] as $person){
          //var_dump($person, $query_rollno);
          if(strcmp($person['rollno'], $query_rollno) == 0){
            $exists = true;
            break;
          }
        }
        if($exists){
          array_push($final_result, $query_result[$i]);
        }
      }
    }catch(PDOException $ex){
      echo $ex->getMessage();
    }
    

    //Prepare a json object for responding to the POST request
    $json_output = json_encode($final_result); 
    //Output the json object
    echo $json_output;
  ?>