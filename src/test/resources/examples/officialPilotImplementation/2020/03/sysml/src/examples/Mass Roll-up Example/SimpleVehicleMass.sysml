/* (c) https://github.com/MontiCore/monticore */
package SimpleVehicleMass {
	import ISQ::*;
	
	block Engine {
		value m :> mass;
	}
	
	block Transmission {
		value m :> mass;
	}
	
	block Vehicle {
		value m = eng::m + trans::m;
		
		part eng: Engine {
			value redefines m;
		}
		
		part trans: Transmission {
			value redefines m;
		}
	}
	
}
