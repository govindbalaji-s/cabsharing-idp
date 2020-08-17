<?php
  include 'constants.php';

  set_headers();

  //Decode the json object from POST data.
  /*JSON object must contain:
          sno - sno of the row in rides database
          rollno - roll number of the user who wants to leave the trip
  */
  $post_json = json_decode(file_get_contents('php://input'), true);

  //This success is set false if the catch block is reached, symbolising a database error.
  $success = true;
  $message = '';
  try{
    
    //Open database connection
    $db_connection = get_db_connection();

    $query_sno = $post_json['sno'];
    $query_rollno = $post_json['rollno'];

    //Check if such a trip exists
    $statement = $db_connection->prepare("SELECT * FROM `rides` WHERE `sno` = ?;");
    $statement->execute(array($query_sno));

    if($statement->rowCount() == 0){
      //No such trip exists
      $success = false;
      $message = 'No such trip exists.';
    }
    else{
      $db_row = $statement->fetch(PDO::FETCH_ASSOC);
      $array_people = unserialize($db_row['people']);
      //If this person is the only person in the ride, delete the whole ride
      $no_of_people = count($array_people);
      if($no_of_people == 1 && strcmp($array_people[0]['rollno'], $query_rollno) == 0){
        //Assuming this is reached only if the user is already in the trip.
        $statement = $db_connection->prepare("DELETE FROM `rides` WHERE `sno` = ?;");
        $statement->execute(array($query_sno));
      }
      //Else only remove this person from the ride and recompute starttime and waittill as per others' preferences
      else{
        //Search for this person on the $array_people
        $index = 0;
        for($index = 0; $index < $no_of_people; $index++){
          if(strcmp($array_people[$index]["rollno"], $query_rollno) == 0){
            break;
          }
        }
        if($index == $no_of_people){
          //This means the person is not there
          $success = false;
          $message = 'User not part of the trip.';
        }
        else{
          //var_dump($array_people);
          //Remove that person and reorder the array
          array_splice($array_people, $index, 1);
          //var_dump($array_people);
          //Updating starttime and waittill
          $max_starttime = new DateTime($array_people[0]['starttime']);
          $min_waittill = new DateTime($array_people[0]['waittill']);
          for($i = 1; $i < $no_of_people - 1; $i++){
            $max_starttime = max($max_starttime, new DateTime($array_people[$i]['starttime']));
            $min_waittill = min($min_waittill, new DateTime($array_people[$i]['waittill']));
          }
          $max_starttime = $max_starttime->format($datetime_format);
          $min_waittill = $min_waittill->format($datetime_format);
          //var_dump($array_people);
          $array_people = serialize($array_people);
          $statement = $db_connection->prepare("UPDATE `rides` SET `people` = ?, `starttime` = ?, `waittill` = ? WHERE `sno` = ?;");
          $statement->execute(array($array_people, $max_starttime, $min_waittill, $query_sno));
        }
      }
    }
  }catch(PDOException $e){
    $success = false;
    $message = $e->getMessage();
  }
  //Check if the UPDATE was successful and respond accordingly
  echo_success($success, $message);
  exit();
?>