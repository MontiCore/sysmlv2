/* (c) https://github.com/MontiCore/monticore */
package Vehicles{
	package useOfDefs{
	  block a :> Vehicles::defs::b;
	  block aPrivate:> Vehicles::defs::bPrivate; //Should not resolve
	}
	package defs{
  	  block b;
  	  private block bPrivate;
  	}
}
