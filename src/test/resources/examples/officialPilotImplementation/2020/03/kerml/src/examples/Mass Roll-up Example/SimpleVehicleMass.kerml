package SimpleVehicleMass {
	import ISQ::*;
	
	class Engine {
		feature m subsets mass;
	}
	
	class Transmission {
		feature m subsets mass;
	}
	
	class Vehicle {
		feature m = eng::m + trans::m;
		
		composite feature eng: Engine {
			feature redefines m;
		}
		
		composite feature trans: Transmission {
			feature redefines m;
		}
	}
	
}
