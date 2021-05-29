/* (c) https://github.com/MontiCore/monticore */
package Vehicles{
	package Automobile{
		package ModellY{
			import Planes::ModellX; //Should be in scope Planes and then scope Automobile
			import Vehicles::Planes::ModellX; //Should be in scope Planes and the Vehicles
			import 'Symbol Definition'::Vehicle; //should resolve (to other file)
			import 'Symbol Definition'; //Should resolve (to other file)
			import NotDefined ; //Should not resolve.
		}
		package Planes{
			package ModellX{

			}
  	}
	}
}
