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
		part def MonsterTruck :> BigVehicle {
  		attribute vielBrummBrumm : Boolean;
  	}
  part audi : BigVehicle;
  part vw : BigVehicle;
  part bmw :> audi;
	attribute def Engine {

        attribute horsePowers : Real;
	}
	attribute def SmallEngine :> Engine {
        attribute benzin : Boolean;
	}
	attribute def BigEngine :> Engine {
        attribute turbopump : Boolean;
	}
}
