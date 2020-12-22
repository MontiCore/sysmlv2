package AnalysisExample {
	import ISQ::*;
	import ScalarFunctions::*;
	
	part vehicle {
		value m : MassValue;
		
		part eng {
			value m : MassValue;
		}
		
		part trans {
			value m : MassValue;
		}
	}
	
	constraint MassSum (
		partMasses : MassValue[0..*],
		totalMass : MassValue 
	) {
		totalMass == sum(partMasses)
	}
	
	constraint ms: MassSum (
		totalMass = vehicle::m,
		partMasses = {vehicle::eng::m, vehicle::trans::m}
	);
}