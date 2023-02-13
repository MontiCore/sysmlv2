package AdderExample {
	import ScalarValues::*;
	part TestSystem {

	  port def ZahlenPort{
	    attribute value : Integer;
	  }
    part GenerateOneToThree{
      out port outputPort : ZahlenPort;
      exhibit state verhalten{
          entry;
          attribute zahl : Integer;
        transition start
        do send 0 to outputPort
        then eins;

        state eins;

        transition first eins
        do send 1 to outputPort
        then zwei;
        state zwei;

        transition zwei
        do send 2 to outputPort
        then drei;
        state drei;

        transition zwei
        do send 3 to outputPort
        then start;
      }
    }
        part GenerateOneToFive{
          out port outputPort : ZahlenPort;
          exhibit state behaviour{
              entry;
            transition start
            do send 0 to outputPort
            then eins;

            state eins;

            transition first eins
            do send 1 to outputPort
            then zwei;
            state zwei;

            transition zwei
            do send 2 to outputPort
            then drei;
            state drei;

            transition drei
            do send 3 to outputPort
            then vier;
            state vier;
            transition vier
            do send 4 to outputPort
            then fuenf;
            state fuenf;
            transition fuenf
            do send 5 to outputPort
            then start;
          }
        }

            part Adder{
              in port number1 : ZahlenPort;
              in port number2 : ZahlenPort;
              out port outputPort : ZahlenPort;
              exhibit state adderBehaviour{
                  entry;
                transition start
                do send number1.value+number2.value to outputPort
                then Idle;
                state Idle;
                transition Idle
                do send number1.value+number2.value to outputPort
                then Idle;
              }
            }
    flow GenerateOneToThree.outputPort to Adder.number1;
    flow GenerateOneToFive.outputPort to Adder.number2;
    flow Adder.outputPort to Drain.drainPort;
    part Drain{
      in port drainPort : ZahlenPort;
    }

	}


}
