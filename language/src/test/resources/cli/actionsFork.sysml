package ActionTest2 {

port def StdPortDef{
out value : Real;
}

  part zweiterPart{
  port testPort : StdPortDef;

	action whole {
	attribute zahl : Real = 90;

	first start;
	then sendTest;
	action sendTest send 90 to testPort;
		then assignTest;
  	action assignTest assign zahl := 9823;
   then DoSomething;
   action DoSomething{
   in attribute zahlInner : Real = zahl;
   first start;
   then done;
   }
        then forkNode;
        fork forkNode;
        if 3<4 then MachMalWas;
        if true then DoSomething3;
         action DoSomething3{

	        out attribute zahlBlaBla : Real;
         first start;
         then done;
         }
            then DoSomething4;
            action DoSomething4{
            first start;
            then done;
            }
       then JoinNode;
         action MachMalWas{
         first start;
         then done;
         }
            then hierAuch;
            action hierAuch{
            first start;
            then done;
            }
      then JoinNode;
      join JoinNode;
then PluginLamp;
   action PluginLamp{
      first start;
      then done;
   }
   then done;
	}
	}


}
