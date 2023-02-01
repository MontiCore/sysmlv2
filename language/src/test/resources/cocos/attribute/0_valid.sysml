package RedefinitionExample {
	import ScalarValues::*;
	part def Vehicle {
	}
	part def SmallVehicle :> Vehicle {
		attribute eng : SmallEngine;
	}
	part def BigVehicle :> Vehicle {
		attribute eng : BigEngine;
	}
		part def BigDVehicle :> BigVehicle,brumm {
  		attribute test : Boolean;
  		attribute brembrem : Boolean :>> brembrem;
  	}
  	part def brumm{
  		attribute brembrem : Boolean;
      	}

  part audi : BigVehicle;
  part vw : BigVehicle;
  part bmw :> audi : brumm;
	attribute def Engine {

        attribute horsePowers : Real;
	}
	attribute def SmallEngine :> Engine {
        attribute benzin : Boolean;
	}
	attribute def BigEngine :> Engine {
        attribute turbopump : Boolean;
	}

	part def A;

  part def A2 :> A;
 part def Z{
   attribute turbopump : X;
 }
	part def B{
   attribute turbopump : Y;

	part a : A;
	}

	part def C :> B, Z{
   attribute turbopump : Y :>> turbopump;
	part a : F :>> a;
	}

		part def F;
 attribute def X{

 }

 attribute def Y :> X;
}
