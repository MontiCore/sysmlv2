/* (c) https://github.com/MontiCore/monticore */
package Vehicles{
	package Automobile{
		package ModellY{
			import 'Symbol Definition2'::*; //should be public


			/*
			The symbols in 'NoTransitivImport' should not be in scope because thy are a private import
			in Symbol Definition2
			block VehicleDef1;
      block BusDef1;
      // import 'Symbol Definition'::Vehicle; //should be public => part of scope
      // import 'Symbol Definition'; //should be public => part of scope
      // import 'Symbol Definition2'::VehiclePrivate; //should be private => not part of scope
      */
      block DefinedInScope;

		}
	}
}
