/* (c) https://github.com/MontiCore/monticore */
package 'mySeats' { // Not equal to artifact name.
	value type Euro;

	block Seat {
		value price: Euro;
	}

	block LuxusSeat :> Seat;

	block 'Car.Seat';
}
