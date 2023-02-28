package AdderExample {
	import ScalarValues::*;


	  port def ZahlenPort{
	    out attribute value : Integer;
	  }
    part GenerateOneToThree{
      port outputPort : ZahlenPort;
      exhibit state verhalten{
          entry;
          attribute zahl : Integer;
        transition start
        do action send_11 send 0 to outputPort
        then eins;

        state eins;

        transition first eins
        do action send_10 send 1 to outputPort
        then zwei;
        state zwei;

        transition zwei
        do action send_9 send 2 to outputPort
        then drei;
        state drei;

        transition zwei
        do  action send_8 send 3 to outputPort
        then start;
      }
    }
        part GenerateOneToFive{
          port outputPort : ZahlenPort;

          exhibit state behaviourTemplate{
              entry;
            transition start
            do action send_0 send 0 to outputPort
            then eins;

            state eins;

            transition first eins
            do  action send_1  send 1 to outputPort
            then zwei;
            state zwei;

            transition zwei
            do  action send_2 send 2 to outputPort
            then drei;
            state drei;

            transition drei
            do  action send_3 send 3 to outputPort
            then vier;
            state vier;
            transition vier
            do  action send_4 send 4 to outputPort
            then fuenf;
            state fuenf;
            transition fuenf
            do  action send_5 send 5 to outputPort
            then start;
          }
        }

            part Adder{
              port number1 : ~ZahlenPort;
              port number2 : ~ZahlenPort;
              port outputPort : ZahlenPort;
              exhibit state adderBehaviour{
                  entry action send_611 send number1.value+number2.value to outputPort;
                transition start
                do action send_6 send number1.value+number2.value to outputPort
                then Idle;
                state Idle;
                transition Idle
                do action send_7  send number1.value+number2.value to outputPort
                then Idle;
              }
            }
    flow GenerateOneToThree.outputPort to Adder.number1;
    flow GenerateOneToFive.outputPort to Adder.number2;
    flow Adder.outputPort to Drain.drainPort;
    part Drain{
      port drainPort : ~ZahlenPort;
    }




}
