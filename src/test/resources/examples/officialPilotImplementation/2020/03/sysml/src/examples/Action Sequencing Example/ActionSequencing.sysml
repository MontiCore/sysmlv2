package ActionSequencing {
	
	action sequence {
		action sendRequest1 (out request1);
		then action receiveReply1 (in reply1);
		then action receiveRequest2 (in request2);
		then action sendRequest3 (out request3);
		then action receiveRequest3 (in request3);
		
	 	flow sendRequest1::request1 to receiveRequest1::request1;
	 	flow sendReply1::reply1 to receiveReply1::reply1;
	 	flow sendRequest2::request2 to receiveRequest2::request2;
	 	flow sendRequest3::request3 to receiveRequest3::request3;

		action receiveRequest1 (in request1);
	 	then action sendReply1 (out reply1);
	 	then action sendRequest2 (out request2);	 	
	}
	
	port def Requests {
		out request1;
		in reply1;	
		in request2;
	}
	
	part system {
		import sequence::*;
		
		part part1 {
			port p1 {
				out request1;
				in reply1;	
				in request2;			
			}
			
			perform sendRequest1 (out request1 = p1::request1);
			perform receiveReply1 (in reply1 = p1::reply1);
			perform receiveRequest2 (in request2 = p1::request2);
			perform sendRequest3;
			perform receiveRequest3;
		}
		
		part part2 {
			port p2 {
				in request1;
				out reply1;	
				out request2;				
			}
			
			perform receiveRequest1 (in request1 = p2::request1);
			perform sendReply1 (out reply1 = p2::reply1);
			perform sendRequest2 (out request2 = p2::request2);
		}
		
		connect part1::p1 to part2::p2;
	}
}