package RedefinitionExample {
	import ScalarValues::*;
	part def Vehicle {
		attribute eng : Engine;
	}
	part def SmallVehicle :> Vehicle {
		attribute eng : SmallEngine redefines eng;
	}
	part def BigVehicle :> Vehicle {
		attribute eng : BigEngine :>> eng;
	}

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
