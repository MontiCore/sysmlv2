package ActionTest2 {


  part zweiterPart{
	action whole {
	first start;
   then DoSomething;
   action DoSomething{
   first start;
   then done;
   }
        then decideNode;
        decide decideNode;
        if 3<4 then DoSomething2;
        if true then DoSomething3;
         action DoSomething3{
         first start;
         then done;
         }
            then DoSomething4;
            action DoSomething4{
            first start;
            then done;
            }
      then DoSomething2;
      merge DoSomething2;
then PluginLamp;
   action PluginLamp{
      first start;
      then done;
   }
   then done;
	}
	}


}
